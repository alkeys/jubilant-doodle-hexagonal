// src/lib/catalogFilterClient.ts

const $ = <T extends HTMLElement = HTMLElement>(s: string) =>
  document.querySelector(s) as T | null;

const $$ = <T extends HTMLElement = HTMLElement>(s: string) =>
  [...document.querySelectorAll(s)] as T[];

export function initCatalogFilters() {
  const selTienda = $("[data-f-tienda]") as HTMLSelectElement | null;
  const selCategoria = $("[data-f-categoria]") as HTMLSelectElement | null;
  const selTalla = $("[data-f-talla]") as HTMLSelectElement | null;
  const rngPrecio = $("[data-f-price]") as HTMLInputElement | null;
  const lblPrecio = $("[data-f-price-label]");
  const selSort = $("[data-f-sort]") as HTMLSelectElement | null;
  const btnReset = $("[data-f-reset]");
  const grid = $("[data-products-grid]");
  const emptyMsg = $("[data-empty-grid]");
  const countEl = $("[data-catalog-count]");
  const titleEl = $("[data-catalog-title]");
  const crumbEl = $("[data-catalog-crumb]");
  const cards = $$("[data-prod-item]");

  const maxPriceDefault = rngPrecio ? Number(rngPrecio.max) : 500;

  function applyFilters() {
    const tiendaId = selTienda?.value || "";
    const categoriaId = selCategoria?.value || "";
    const talla = selTalla?.value || "";
    const maxPrice = rngPrecio ? Number(rngPrecio.value) : maxPriceDefault;
    const sort = selSort?.value || "recent";

    if (lblPrecio) lblPrecio.textContent = "$" + maxPrice;

    let visibleCount = 0;

    cards.forEach((card) => {
      const cCat = card.dataset.categoriaId || "";
      const cTiendas = (card.dataset.tiendasIds || "").split(",");
      const cTallas = (card.dataset.tallas || "").split(",");
      const cPrecio = Number(card.dataset.precio || 0);

      const matchTienda = !tiendaId || cTiendas.includes(tiendaId);
      const matchCat = !categoriaId || cCat === categoriaId;
      const matchTalla = !talla || cTallas.includes(talla);
      const matchPrecio = cPrecio <= maxPrice;

      const show = matchTienda && matchCat && matchTalla && matchPrecio;
      card.classList.toggle("hidden", !show);
      if (show) visibleCount++;
    });

    // Ordenar tarjetas en el DOM
    if (grid) {
      const sorted = [...cards].sort((a, b) => {
        const pa = Number(a.dataset.precio || 0);
        const pb = Number(b.dataset.precio || 0);
        const ia = Number(a.dataset.index || 0);
        const ib = Number(b.dataset.index || 0);
        if (sort === "low") return pa - pb;
        if (sort === "high") return pb - pa;
        return ia - ib;
      });
      sorted.forEach((c) => grid.appendChild(c));
    }

    // Actualizar encabezados según filtro activo
    const tiendaText =
      selTienda && selTienda.selectedIndex > 0
        ? selTienda.options[selTienda.selectedIndex].text
        : "";
    const catText =
      selCategoria && selCategoria.selectedIndex > 0
        ? selCategoria.options[selCategoria.selectedIndex].text
        : "";

    if (titleEl && crumbEl) {
      if (tiendaText && catText) {
        titleEl.textContent = `${catText.toUpperCase()} — ${tiendaText.toUpperCase()}`;
        crumbEl.textContent = `INICIO / ${tiendaText.toUpperCase()} / ${catText.toUpperCase()}`;
      } else if (tiendaText) {
        titleEl.textContent = tiendaText.toUpperCase();
        crumbEl.textContent = `INICIO / SUCURSAL / ${tiendaText.toUpperCase()}`;
      } else if (catText) {
        titleEl.textContent = catText.toUpperCase();
        crumbEl.textContent = `INICIO / CATEGORÍA / ${catText.toUpperCase()}`;
      } else {
        titleEl.textContent = "NUESTROS PRODUCTOS";
        crumbEl.textContent = "INICIO / CATÁLOGO COMPLETO";
      }
    }

    const hasActiveFilter =
      Boolean(tiendaId) ||
      Boolean(categoriaId) ||
      Boolean(talla) ||
      maxPrice < maxPriceDefault;

    btnReset?.classList.toggle("hidden", !hasActiveFilter);
    emptyMsg?.classList.toggle("hidden", visibleCount > 0);
    if (countEl) {
      countEl.textContent = `${visibleCount} ${
        visibleCount === 1 ? "pieza disponible" : "piezas disponibles"
      }`;
    }
  }

  // Leer parámetros iniciales de la URL (?tiendaId=...&categoriaId=...)
  const params = new URLSearchParams(location.search);
  if (params.get("tiendaId") && selTienda) {
    selTienda.value = params.get("tiendaId")!;
  }
  if (params.get("categoriaId") && selCategoria) {
    selCategoria.value = params.get("categoriaId")!;
  }

  selTienda?.addEventListener("change", applyFilters);
  selCategoria?.addEventListener("change", applyFilters);
  selTalla?.addEventListener("change", applyFilters);
  rngPrecio?.addEventListener("input", applyFilters);
  selSort?.addEventListener("change", applyFilters);

  btnReset?.addEventListener("click", () => {
    if (selTienda) selTienda.value = "";
    if (selCategoria) selCategoria.value = "";
    if (selTalla) selTalla.value = "";
    if (rngPrecio) rngPrecio.value = String(maxPriceDefault);
    if (selSort) selSort.value = "recent";
    history.replaceState(null, "", "/#catalogo");
    applyFilters();
  });

  // Escuchar eventos desde el Mega Menú, Mosaico de Categorías, Footer o Tarjetas de Sucursales
  document.addEventListener("njx:filter", ((e: CustomEvent) => {
    const { tiendaId, categoriaId } = e.detail || {};
    if (tiendaId !== undefined && selTienda) {
      selTienda.value = tiendaId;
      if (categoriaId === undefined && selCategoria) selCategoria.value = "";
    }
    if (categoriaId !== undefined && selCategoria) {
      selCategoria.value = categoriaId;
      if (tiendaId === undefined && selTienda) selTienda.value = "";
    }
    applyFilters();
  }) as EventListener);

  applyFilters();
}

