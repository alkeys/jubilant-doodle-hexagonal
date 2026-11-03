/**
 * Shared product card renderer — Noble Muse theme adapted for ProductosApi.
 * All interactivity (hover bar, quick size add, wishlist, quick view)
 * is delegated globally in Layout.astro via data-card-* attributes.
 */
export const esc = (s: string) =>
  String(s ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]!));

export const fmt = (n: number) => '$' + Number(n ?? 0).toFixed(2);

export function cardHTML(p: any, wished: boolean): string {
  const hasRange = p.precioMin != null && p.precioMax != null && p.precioMin !== p.precioMax;
  const priceText = hasRange ? `${fmt(p.precioMin)} – ${fmt(p.precioMax)}` : fmt(p.price);

  return `
  <article class="group relative" data-card="${esc(p.handle)}" data-color="0">
    <div class="relative aspect-[3/4] w-full overflow-hidden bg-tint">
      <a href="/Productos/${esc(p.handle)}" class="absolute inset-0 block">
        <img src="${esc(p.image)}" alt="${esc(p.name)}" loading="lazy" class="absolute inset-0 h-full w-full object-cover transition-[opacity,transform] duration-700 group-hover:scale-[1.04] ${p.image2 ? 'group-hover:opacity-0' : ''}" />
        ${p.image2 ? `<img src="${esc(p.image2)}" alt="" loading="lazy" class="absolute inset-0 h-full w-full object-cover opacity-0 transition-[opacity,transform] duration-700 group-hover:scale-[1.04] group-hover:opacity-100" />` : ''}
      </a>
      <span class="note pointer-events-none absolute bottom-2.5 left-3 text-white/80 mix-blend-difference">${esc(p.name.toLowerCase())} · <span class="hidden group-hover:inline">ver detalle</span><span class="group-hover:hidden">stock: ${p.stockTotal ?? 0}</span></span>
      ${p.badge ? `<span class="cap-xs absolute left-3 top-3 px-2.5 py-1.5 text-white" style="background:${p.badge === 'OUTLET' ? '#6f5bd6' : '#191936'}">${esc(p.badge)}</span>` : ''}
      <button data-card-wish="${esc(p.handle)}" aria-label="Wishlist" class="absolute right-2 top-2 flex h-8 w-8 cursor-pointer items-center justify-center text-[14px] transition ${wished ? 'text-acc' : 'text-ink/45'}"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg></button>
      <div class="absolute inset-x-0 bottom-0 translate-y-full border-t border-line bg-white/95 px-3.5 py-3 opacity-0 transition-all duration-300 group-hover:translate-y-0 group-hover:opacity-100">
        <div class="flex w-full items-center justify-between" data-card-bar>
          <button data-card-qv="${esc(p.handle)}" class="cap cursor-pointer">Vista rápida</button>
          <button data-card-open-sizes="${esc(p.handle)}" class="cap cursor-pointer">+ Agregar</button>
        </div>
        <div class="hidden w-full items-center justify-center gap-3" data-card-sizes>
          <span class="cap-xs text-muted">Talla</span>
          ${(p.sizes ?? ['S', 'M', 'L', 'XL']).map((z: string) => `<button data-card-size="${esc(z)}" data-h="${esc(p.handle)}" class="cursor-pointer px-1 py-0.5 text-[11px] tracking-[0.1em] hover:text-acc">${esc(z)}</button>`).join('')}
        </div>
      </div>
    </div>
    <div class="flex flex-col gap-1.5 px-0.5 pt-3.5">
      <div class="flex items-start justify-between gap-2">
        <a href="/Productos/${esc(p.handle)}" class="cap-tag leading-relaxed hover:underline">${esc(p.name)}</a>
      </div>
      <div class="flex items-baseline justify-between gap-2.5 text-[12px] tracking-[0.04em]">
        <span class="font-normal text-ink">${priceText}</span>
        <span class="cap-xs text-muted">${p.tiendasCount ?? 1} ${(p.tiendasCount ?? 1) === 1 ? 'sucursal' : 'sucursales'}</span>
      </div>
    </div>
  </article>`;
}

