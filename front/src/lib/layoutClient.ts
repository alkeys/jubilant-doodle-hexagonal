// src/lib/layoutClient.ts
import {
  cart,
  addToCart,
  setCartQty,
  clearCart,
  wishlist,
  toggleWishlist,
} from "./stores";
import { esc, fmt, cardHTML } from "./card";
import { UsuariosApi } from "../api/services/usuarios/UsuariosApi";
import { VentasApi } from "../api/services/ventas/VentasApi";
import { isAdminOrRh } from "./authGuard";

const $ = <T extends HTMLElement = HTMLElement>(
  s: string,
  r: ParentNode = document
) => r.querySelector(s) as T | null;

const $$ = <T extends HTMLElement = HTMLElement>(
  s: string,
  r: ParentNode = document
) => [...r.querySelectorAll(s)] as T[];

const FREE_SHIP = 150;

export function initLayoutClient() {
  const DATA = JSON.parse(
    document.body.dataset.muse || '{"cats":[],"products":[]}'
  );
  const PRODUCTS: any[] = DATA.products || [];
  const byHandle = new Map(PRODUCTS.map((p) => [p.handle, p]));

  (window as any).njxMuse = {
    PRODUCTS,
    cardHTML,
    wished: (h: string) => wishlist.get().includes(h),
  };

  // ── 1. Toast Notification ─────────────────────────────────────
  let toastT: ReturnType<typeof setTimeout>;
  function toast(msg: string) {
    const t = $("[data-toast]");
    const text = $("[data-toast-text]");
    if (!t || !text) return;
    text.textContent = msg;
    t.style.opacity = "1";
    clearTimeout(toastT);
    toastT = setTimeout(() => {
      t.style.opacity = "0";
    }, 2200);
  }

  // ── 2. Header, Mega Menú y Panel de Búsqueda ──────────────────
  const header = $("[data-header]")!;
  const mega = $("[data-mega]")!;
  const searchEl = $("[data-search]")!;
  const searchInput = $("[data-search-input]") as HTMLInputElement;
  const heroMode = document.body.dataset.heroHeader === "1";

  let hover = false;
  let megaOpen = false;
  let searchOpen = false;
  let scrolled = !heroMode;

  function paintHeader() {
    if (!header) return;
    const solid = scrolled || hover || megaOpen || searchOpen || !heroMode;
    header.classList.toggle("bg-white", solid);
    header.classList.toggle("text-ink", solid);
    header.classList.toggle("text-white", !solid);
    header.classList.toggle("border-line", solid);
    header.classList.toggle("border-transparent", !solid);
    header.style.transform =
      scrolled || !heroMode ? "translateY(0)" : "translateY(36px)";
  }

  function paintMega() {
    if (!mega) return;
    mega.classList.toggle("opacity-100", megaOpen);
    mega.classList.toggle("opacity-0", !megaOpen);
    mega.classList.toggle("translate-y-0", megaOpen);
    mega.classList.toggle("-translate-y-2.5", !megaOpen);
    mega.classList.toggle("pointer-events-auto", megaOpen);
    mega.classList.toggle("pointer-events-none", !megaOpen);
  }

  function paintSearch() {
    if (!searchEl) return;
    searchEl.classList.toggle("opacity-100", searchOpen);
    searchEl.classList.toggle("opacity-0", !searchOpen);
    searchEl.classList.toggle("translate-y-0", searchOpen);
    searchEl.classList.toggle("-translate-y-2.5", !searchOpen);
    searchEl.classList.toggle("pointer-events-auto", searchOpen);
    searchEl.classList.toggle("pointer-events-none", !searchOpen);
  }

  if (heroMode) {
    addEventListener(
      "scroll",
      () => {
        const s = scrollY > innerHeight * 0.72;
        if (s !== scrolled) {
          scrolled = s;
          paintHeader();
        }
      },
      { passive: true }
    );
  }

  header?.addEventListener("mouseenter", () => {
    hover = true;
    paintHeader();
  });

  header?.addEventListener("mouseleave", () => {
    hover = false;
    megaOpen = false;
    paintMega();
    paintHeader();
  });

  $("[data-mega-on]")?.addEventListener("mouseenter", () => {
    megaOpen = true;
    searchOpen = false;
    paintMega();
    paintSearch();
    paintHeader();
  });

  $("[data-mega-m]")?.addEventListener("click", (e) => {
    e.preventDefault();
    megaOpen = !megaOpen;
    searchOpen = false;
    paintMega();
    paintSearch();
    paintHeader();
  });

  $$("[data-mega-off]").forEach((el) =>
    el.addEventListener("mouseenter", () => {
      if (megaOpen) {
        megaOpen = false;
        paintMega();
        paintHeader();
      }
    })
  );

  // Interceptar clics en enlaces de filtro por sucursal o categoría cuando estamos en el Home
  document.addEventListener("click", (e) => {
    const t = e.target as HTMLElement;
    const tiendaLink = t.closest("[data-filter-tienda]") as HTMLElement | null;
    const catLink = t.closest("[data-filter-categoria]") as HTMLElement | null;
    if (!tiendaLink && !catLink) return;

    if (location.pathname === "/") {
      e.preventDefault();
      megaOpen = false;
      paintMega();
      paintHeader();

      const detail: { tiendaId?: string; categoriaId?: string } = {};
      if (tiendaLink) detail.tiendaId = tiendaLink.dataset.filterTienda ?? "";
      if (catLink) detail.categoriaId = catLink.dataset.filterCategoria ?? "";

      document.dispatchEvent(new CustomEvent("njx:filter", { detail }));
      document
        .getElementById("catalogo")
        ?.scrollIntoView({ behavior: "smooth" });
    }
  });

  // ── 3. Buscador en Vivo ───────────────────────────────────────
  $("[data-search-toggle]")?.addEventListener("click", () => {
    searchOpen = !searchOpen;
    megaOpen = false;
    paintMega();
    paintSearch();
    paintHeader();
    if (searchOpen) setTimeout(() => searchInput?.focus(), 60);
  });

  $("[data-search-close]")?.addEventListener("click", () => {
    searchOpen = false;
    paintSearch();
    paintHeader();
  });

  function renderSearchResults() {
    const q = searchInput.value.trim().toLowerCase();
    const box = $("[data-search-results]")!;
    if (!q) {
      box.classList.remove("show");
      box.innerHTML = "";
      return;
    }
    const hits = PRODUCTS.filter((p) =>
      (p.name + " " + (p.description || "") + " " + p.cat)
        .toLowerCase()
        .includes(q)
    ).slice(0, 6);
    box.classList.add("show");
    box.innerHTML = hits.length
      ? hits
        .map(
          (p) => `
          <a href="/Productos/${esc(p.handle)}" class="flex items-center gap-4 border-b border-linesoft py-3 hover:bg-tint/40 px-2 transition">
            <img src="${esc(p.image)}" alt="" class="h-[52px] w-[40px] object-cover" />
            <div class="flex flex-1 flex-col">
              <span class="cap-tag">${esc(p.name)}</span>
              <span class="text-[11px] text-muted">${esc(p.description || "")}</span>
            </div>
            <span class="text-[12px]">${fmt(p.price)}</span>
          </a>`
        )
        .join("")
      : `<div class="cap-tag py-4 text-muted">No se encontraron productos para esa búsqueda.</div>`;
  }

  searchInput?.addEventListener("input", renderSearchResults);

  $$("[data-pop-search]").forEach((b) =>
    b.addEventListener("click", () => {
      searchOpen = true;
      paintSearch();
      paintHeader();
      searchInput.value = b.dataset.popSearch!;
      renderSearchResults();
      searchInput.focus();
    })
  );

  // ── 4. Drawers (Bolsa y Favoritos) ────────────────────────────
  const overlay = $("[data-overlay]")!;
  const cartDrawer = $("[data-cart-drawer]")!;
  const wishDrawer = $("[data-wish-drawer]")!;

  function setDrawer(which: "cart" | "wish" | null) {
    overlay?.classList.toggle("on", !!which);
    cartDrawer?.classList.toggle("on", which === "cart");
    wishDrawer?.classList.toggle("on", which === "wish");
    document.body.style.overflow = which ? "hidden" : "";
  }

  overlay?.addEventListener("click", () => setDrawer(null));
  $$("[data-drawer-close]").forEach((b) =>
    b.addEventListener("click", () => setDrawer(null))
  );
  $$("[data-open-cart]").forEach((b) =>
    b.addEventListener("click", () => setDrawer("cart"))
  );
  $$("[data-open-wish]").forEach((b) =>
    b.addEventListener("click", () => setDrawer("wish"))
  );
  $$("[data-open-login]").forEach((b) => {
    let hasSession = false;
    try {
      const rawUser = localStorage.getItem("auth_user");
      if (rawUser) {
        const parsed = JSON.parse(rawUser);
        const span = b.querySelector("span");
        if (span && parsed?.username) {
          span.textContent = parsed.username;
          hasSession = true;
          if (b.tagName === "A") {
            b.setAttribute("href", "/user/account");
          }
        }
        if (isAdminOrRh(parsed?.roles)) {
          $$("[data-admin-nav]").forEach((el) => {
            el.classList.remove("hidden");
            el.classList.add("inline-flex");
          });
        }
      }
    } catch {
      // Ignorar si no hay JSON válido
    }
    b.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = hasSession ? "/user/account" : "/user/Login";
    });
  });

  // ── 5. Carrito de Compras ─────────────────────────────────────
  function renderCart() {
    const lines = cart.get();
    const count = lines.reduce((a, l) => a + l.quantity, 0);
    $$("[data-cart-count]").forEach((n) => (n.textContent = String(count)));
    const subtotal = lines.reduce(
      (a, l) => a + (byHandle.get(l.handle)?.price ?? 0) * l.quantity,
      0
    );
    const remaining = Math.max(0, FREE_SHIP - subtotal);
    const shipMsg = $("[data-ship-msg]");
    const shipBar = $("[data-ship-bar]");
    const cartSubtotal = $("[data-cart-subtotal]");
    const cartLines = $("[data-cart-lines]");

    if (shipMsg) {
      shipMsg.textContent =
        remaining > 0
          ? `Te faltan ${fmt(remaining)} para envío gratis`
          : "Tienes envío gratis en tu pedido";
    }
    if (shipBar) {
      shipBar.style.width = Math.min(100, (subtotal / FREE_SHIP) * 100) + "%";
    }
    if (cartSubtotal) {
      cartSubtotal.textContent = fmt(subtotal);
    }
    if (cartLines) {
      cartLines.innerHTML = lines.length
        ? lines
          .map((l) => {
            const p = byHandle.get(l.handle);
            if (!p) return "";
            return `<div class="flex gap-4 border-b border-linesoft py-6">
            <img src="${esc(p.image)}" alt="" class="h-[78px] w-[58px] flex-none object-cover" />
            <div class="flex min-w-0 flex-1 flex-col gap-2">
              <a href="/Productos/${esc(p.handle)}" class="cap-tag">${esc(p.name)}</a>
              <span class="cap-xs text-muted" style="letter-spacing:.08em">Talla ${esc(l.size)} · ${esc(l.color)}</span>
              <div class="mt-1.5 flex items-center justify-between">
                <span class="flex items-center border border-line">
                  <button data-l-dec="${esc(l.key)}" class="h-[30px] w-[30px] cursor-pointer">−</button>
                  <span class="w-[26px] text-center text-[11px]">${l.quantity}</span>
                  <button data-l-inc="${esc(l.key)}" class="h-[30px] w-[30px] cursor-pointer">+</button>
                </span>
                <span class="text-[12px]">${fmt(p.price * l.quantity)}</span>
              </div>
            </div>
          </div>`;
          })
          .join("")
        : `<div class="cap-tag py-16 text-center text-muted">Tu bolsa está vacía.</div>`;
    }
  }

  cart.subscribe(renderCart);

  $("[data-cart-lines]")?.addEventListener("click", (e) => {
    const t = (e.target as HTMLElement).closest(
      "button"
    ) as HTMLElement | null;
    if (!t) return;
    const line = (k: string) => cart.get().find((x) => x.key === k);
    if (t.dataset.lInc) {
      const l = line(t.dataset.lInc);
      if (l) setCartQty(l.key, l.quantity + 1);
    }
    if (t.dataset.lDec) {
      const l = line(t.dataset.lDec);
      if (l) setCartQty(l.key, l.quantity - 1);
    }
  });

  const checkoutBtn = $("[data-checkout]") as HTMLButtonElement | null;
  checkoutBtn?.addEventListener("click", async () => {
    const lines = cart.get();
    if (!lines.length) {
      toast("Tu bolsa de compra está vacía");
      return;
    }

    const usuariosApi = new UsuariosApi();
    const ventasApi = new VentasApi();

    checkoutBtn.disabled = true;
    const prevText = checkoutBtn.textContent;
    checkoutBtn.textContent = "PROCESANDO COMPRA...";

    try {
      // 1. Verificar sesión y obtener el UUID del cliente autenticado
      const perfil = await usuariosApi.getMiPerfil();

      // 2. Registrar cada línea del carrito en POST /api/v1/ventas
      const ventasRealizadas = [];
      for (const l of lines) {
        const p = byHandle.get(l.handle);
        if (!p) continue;
        const tiendaMatch =
          (p.tiendas || []).find(
            (t: any) => t.tiendaNombre === l.color && t.stock >= l.quantity
          ) ||
          (p.tiendas || []).find((t: any) => t.stock > 0) ||
          p.tiendas?.[0];

        if (!tiendaMatch?.tiendaId) {
          throw new Error(
            `El producto "${p.name}" no tiene sucursal asignada con stock disponible.`
          );
        }

        const venta = await ventasApi.registrarVenta({
          clienteId: perfil.id,
          tiendaId: tiendaMatch.tiendaId,
          productoId: p.id,
          cantidad: l.quantity,
        });
        ventasRealizadas.push(venta);
      }

      // 3. Vaciar el carrito y mostrar recibo en el modal de confirmación
      clearCart();
      setDrawer(null);

      const summaryEl = $("[data-thanks-summary]");
      const linesEl = $("[data-thanks-lines]");
      const totalPagado = ventasRealizadas.reduce(
        (acc, v) => acc + (v.totalPagado || 0),
        0
      );

      if (summaryEl) {
        summaryEl.textContent = `Se registraron ${ventasRealizadas.length} transaccion(es) a nombre de ${perfil.nombre} ${perfil.apellido} por un total de ${fmt(totalPagado)}.`;
      }
      if (linesEl) {
        linesEl.innerHTML = ventasRealizadas
          .map(
            (v) => `
            <div class="flex items-center justify-between border-b border-linesoft py-2 last:border-none">
              <div>
                <div class="cap-tag">${esc(v.productoNombre)} (x${v.cantidad})</div>
                <div class="note text-muted">${esc(v.tiendaNombre)} · folio ${esc(v.id.slice(0, 8))}</div>
              </div>
              <span class="font-medium">${fmt(v.totalPagado)}</span>
            </div>`
          )
          .join("");
      }

      $("[data-thanks-root]")?.classList.add("on");
    } catch (err: any) {
      const status = err?.response?.status;
      if (status === 401 || status === 403 || !localStorage.getItem("auth_user")) {
        toast("Inicia sesión para finalizar tu compra");
        setTimeout(() => {
          window.location.href = "/user/Login";
        }, 900);
      } else {
        const msg =
          err?.response?.data?.message ||
          err?.message ||
          "Error al procesar la compra.";
        toast(msg);
      }
    } finally {
      checkoutBtn.disabled = false;
      checkoutBtn.textContent = prevText || "Finalizar compra";
    }
  });

  $("[data-thanks-root]")?.addEventListener("click", (e) => {
    const t = e.target as HTMLElement;
    if (t === $("[data-thanks-root]") || t.closest("[data-thanks-close]")) {
      $("[data-thanks-root]")!.classList.remove("on");
    }
  });

  // ── 6. Lista de Favoritos (Wishlist) ──────────────────────────
  function renderWish() {
    const list = wishlist.get();
    $$("[data-wish-count]").forEach(
      (n) => (n.textContent = String(list.length))
    );
    const wishLines = $("[data-wish-lines]");
    if (wishLines) {
      wishLines.innerHTML = list.length
        ? list
          .map((h) => {
            const p = byHandle.get(h);
            if (!p) return "";
            return `<div class="flex gap-4 border-b border-linesoft py-6">
            <img src="${esc(p.image)}" alt="" class="h-[78px] w-[58px] flex-none object-cover" />
            <div class="flex min-w-0 flex-1 flex-col gap-2">
              <a href="/Productos/${esc(p.handle)}" class="cap-tag">${esc(p.name)}</a>
              <span class="text-[11px] text-soft">${fmt(p.price)}</span>
              <div class="mt-1.5 flex gap-4">
                <button data-w-move="${esc(p.handle)}" class="cap-xs link-u cursor-pointer" style="letter-spacing:.16em;text-underline-offset:4px">Mover a la bolsa</button>
                <button data-w-rm="${esc(p.handle)}" class="cap-xs cursor-pointer text-muted" style="letter-spacing:.16em">Quitar</button>
              </div>
            </div>
          </div>`;
          })
          .join("")
        : `<div class="cap-tag py-16 text-center text-muted">Aún no tienes piezas guardadas.</div>`;
    }

    $$("[data-card-wish]").forEach((b) => {
      const on = list.includes(b.dataset.cardWish!);
      b.classList.toggle("text-acc", on);
      b.classList.toggle("text-ink/45", !on);
    });
  }

  wishlist.subscribe(renderWish);

  $("[data-wish-lines]")?.addEventListener("click", (e) => {
    const t = (e.target as HTMLElement).closest(
      "button"
    ) as HTMLElement | null;
    if (!t) return;
    if (t.dataset.wMove) {
      const p = byHandle.get(t.dataset.wMove);
      if (p) {
        addToCart(
          p.handle,
          p.sizes[1] ?? "M",
          p.tiendas?.[0]?.tiendaNombre ?? "Sucursal Central"
        );
        toggleWishlist(p.handle);
        setDrawer("cart");
      }
    }
    if (t.dataset.wRm) toggleWishlist(t.dataset.wRm);
  });

  // ── 7. Vista Rápida (Quick View Modal) ────────────────────────
  const qvRoot = $("[data-qv-root]")!;
  let qvState = { handle: "", color: 0, size: "", acc: "desc" };
  const qvClose = () => qvRoot?.classList.remove("on");
  $("[data-qv-dim]")?.addEventListener("click", qvClose);

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      setDrawer(null);
      qvClose();
    }
  });

  function openQuickView(handle: string, keep = false) {
    const p = byHandle.get(handle);
    if (!p) return;
    if (!keep) qvState = { handle, color: 0, size: "", acc: "desc" };
    qvState.handle = handle;

    const tiendasHTML =
      p.tiendas && p.tiendas.length > 0
        ? p.tiendas
          .map(
            (t: any) =>
              `<div class="flex justify-between py-1 border-b border-linesoft"><span>${esc(t.tiendaNombre)}</span><span>${fmt(t.precio)} · ${t.stock > 0 ? t.stock + " disp." : "Agotado"}</span></div>`
          )
          .join("")
        : "Disponible en tienda principal";

    const accs = [
      ["desc", "Descripción", esc(p.description || "")],
      ["stores", "Disponibilidad por sucursal", tiendasHTML],
      [
        "ship",
        "Envío y devoluciones",
        "Envío gratuito en compras superiores a $150. Cambios sin costo dentro de 30 días.",
      ],
    ];

    $("[data-qv-panel]")!.innerHTML = `
      <div class="flex flex-col gap-2 bg-tint p-2">
        <img data-qv-main src="${esc(p.image)}" alt="${esc(p.name)}" class="aspect-[3/4] w-full object-cover" />
        ${p.image2
        ? `<div class="flex gap-2">
          <button data-qv-img="${esc(p.image)}" class="w-[64px] flex-none cursor-pointer border border-ink"><img src="${esc(p.image)}" alt="" class="aspect-[3/4] w-full object-cover" /></button>
          <button data-qv-img="${esc(p.image2)}" class="w-[64px] flex-none cursor-pointer border border-line opacity-80 hover:opacity-100"><img src="${esc(p.image2)}" alt="" class="aspect-[3/4] w-full object-cover" /></button>
        </div>`
        : ""
      }
      </div>
      <div class="flex flex-col gap-5 p-8 lg:p-12">
        <div class="flex items-start justify-between gap-5">
          <div class="flex flex-col gap-2.5">
            <div class="cap-xs text-muted">STOCK TOTAL: ${p.stockTotal} UNIDADES</div>
            <h3 class="m-0 text-[20px] font-normal tracking-[0.16em]">${esc(p.name.toUpperCase())}</h3>
          </div>
          <button data-qv-close class="cap cursor-pointer" style="letter-spacing:.18em">Cerrar</button>
        </div>
        <div class="text-[14px] tracking-[0.05em]">${fmt(p.price)}</div>
        <div class="flex flex-col gap-3">
          <div class="cap-xs text-muted">TALLA</div>
          <div class="flex flex-wrap gap-2">
            ${p.sizes.map((z: string) => `<button data-qv-size="${esc(z)}" class="cap cursor-pointer border px-4 py-2.5 transition ${qvState.size === z ? "border-ink bg-ink text-white" : "border-line"}" style="letter-spacing:.14em">${esc(z)}</button>`).join("")}
          </div>
        </div>
        <button data-qv-add class="btn-ink mt-1.5 p-4 ${qvState.size ? "" : "is-off"}">${qvState.size ? "Agregar a la bolsa — " + esc(qvState.size) : "Selecciona una talla"}</button>
        <div class="mt-2.5 border-t border-line">
          ${accs
        .map(
          (a) => `
            <div class="border-b border-line">
              <button data-qv-acc="${a[0]}" class="cap flex w-full cursor-pointer justify-between py-4" style="letter-spacing:.18em"><span>${a[1]}</span><span>${qvState.acc === a[0] ? "−" : "+"}</span></button>
              ${qvState.acc === a[0] ? `<div class="pb-5 text-[12px] leading-loose text-soft">${a[2]}</div>` : ""}
            </div>`
        )
        .join("")}
        </div>
        <a href="/Productos/${esc(p.handle)}" class="cap link-u self-start" style="letter-spacing:.2em">Ver página completa del producto</a>
      </div>`;
    qvRoot.classList.add("on");
  }

  $("[data-qv-panel]")?.addEventListener("click", (e) => {
    const t = e.target as HTMLElement;
    if (t.closest("[data-qv-close]")) {
      qvClose();
      return;
    }
    const gi = t.closest("[data-qv-img]") as HTMLElement | null;
    if (gi) {
      const main = $("[data-qv-main]") as HTMLImageElement | null;
      if (main) main.src = gi.dataset.qvImg!;
      $$("[data-qv-img]").forEach((x) => {
        const on = x === gi;
        x.classList.toggle("border-ink", on);
        x.classList.toggle("border-line", !on);
        x.classList.toggle("opacity-80", !on);
      });
      return;
    }
    const sz = t.closest("[data-qv-size]") as HTMLElement | null;
    if (sz) {
      qvState.size = sz.dataset.qvSize!;
      openQuickView(qvState.handle, true);
      return;
    }
    const ac = t.closest("[data-qv-acc]") as HTMLElement | null;
    if (ac) {
      qvState.acc = qvState.acc === ac.dataset.qvAcc ? "" : ac.dataset.qvAcc!;
      openQuickView(qvState.handle, true);
      return;
    }
    if (t.closest("[data-qv-add]")) {
      if (!qvState.size) return;
      const p = byHandle.get(qvState.handle)!;
      addToCart(
        p.handle,
        qvState.size,
        p.tiendas?.[0]?.tiendaNombre ?? "Sucursal Central"
      );
      qvClose();
      setDrawer("cart");
    }
  });

  // ── 8. Delegación Global de Tarjetas de Producto ──────────────
  document.addEventListener("click", (e) => {
    const t = e.target as HTMLElement;
    const wish = t.closest("[data-card-wish]") as HTMLElement | null;
    if (wish) {
      e.preventDefault();
      const added = toggleWishlist(wish.dataset.cardWish!);
      toast(added ? "Guardado en favoritos" : "Eliminado de favoritos");
      return;
    }
    const qv = t.closest("[data-card-qv]") as HTMLElement | null;
    if (qv) {
      e.preventDefault();
      openQuickView(qv.dataset.cardQv!);
      return;
    }
    const open = t.closest("[data-card-open-sizes]") as HTMLElement | null;
    if (open) {
      e.preventDefault();
      const card = open.closest("[data-card]")!;
      card.querySelector("[data-card-bar]")!.classList.add("hidden");
      const sizes = card.querySelector("[data-card-sizes]")!;
      sizes.classList.remove("hidden");
      sizes.classList.add("flex");
      return;
    }
    const size = t.closest("[data-card-size]") as HTMLElement | null;
    if (size) {
      e.preventDefault();
      const card = size.closest("[data-card]") as HTMLElement;
      const p = byHandle.get(size.dataset.h!);
      if (p) {
        addToCart(
          p.handle,
          size.dataset.cardSize!,
          p.tiendas?.[0]?.tiendaNombre ?? "Sucursal Central"
        );
        card.querySelector("[data-card-sizes]")!.classList.add("hidden");
        card.querySelector("[data-card-bar]")!.classList.remove("hidden");
        setDrawer("cart");
      }
      return;
    }
  });

  // ── 9. Inicialización de Estado Inicial ───────────────────────
  const yearEl = $("[data-year]");
  if (yearEl) yearEl.textContent = String(new Date().getFullYear());
  paintHeader();
  paintMega();
  paintSearch();
  renderCart();
  renderWish();
}

