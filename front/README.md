# 👗 Noble Muse — Frontend SSR en Astro 7 + TailwindCSS 4

Aplicación web de comercio electrónico para la cadena de tiendas **Noble Muse (Moda Urbana, Calzado, Sneakers y Accesorios)**, construida con **Astro 7 en modo SSR (`output: 'server'`)**, **TailwindCSS 4** y **Axios**, conectada 100% en tiempo real al backend con Arquitectura Hexagonal (`http://localhost:8080/api/v1`).

---

## ✨ Características Principales

- **SSR Dinámico (`output: 'server'`)**: Todas las páginas consultan la API REST en tiempo real sin depender de datos estáticos pre-compilados.
- **Tema Editorial Noble Muse (`astro-njx-muse`)**: Diseño minimalista de alta costura con tipografías *Cormorant Garamond* y *Plus Jakarta Sans*, animaciones fluidas, badges de stock y tallas.
- **Catálogo Multi-Sucursal Inteligente (`ListaProducto.astro`)**:
  - **Hero Slider** dinámico con productos destacados de la API.
  - **Mosaico de Colecciones por Categoría** (`/api/v1/categorias`) con conteo de piezas y filtrado automático al hacer clic.
  - **Barra de Filtros Interactiva**: filtra instantáneamente por **Sucursal**, **Categoría**, **Talla**, **Rango de Precio** y **Ordenamiento** (precio menor/mayor, nombre A-Z). Al elegir una sucursal, cada tarjeta muestra el precio y stock exclusivos de esa tienda.
  - **Sección Top Ventas**: ranking conectado a `/api/v1/ventas/mas-vendidos`.
  - **Directorio de Sucursales**: tarjetas de tiendas físicas (`/api/v1/tiendas`) con botón para explorar el inventario de cada sucursal.
- **Página de Detalle de Producto (`/Productos/[id]`)**:
  - Galería interactiva con cambio de imagen principal y zoom suave.
  - Selector interactivo de **Tallas disponibles** (`producto.talla`).
  - Tabla comparativa de **Precio y Disponibilidad por Sucursal** con selección de tienda activa.
  - Productos relacionados de la misma categoría.
- **Experiencia de Compra Completa (`Layout.astro` + `stores.ts`)**:
  - **Buscador en vivo** en la barra superior (filtra por nombre, descripción o talla directamente desde la API).
  - **Modal de Vista Rápida (Quick View)** desde cualquier tarjeta del catálogo.
  - **Drawers laterales de Carrito de Compras y Lista de Deseos (Wishlist)** persistidos en `localStorage` con notificaciones Toast.
  - **Footer dinámico** cuyos enlaces de Sucursales y Categorías aplican el filtro correspondiente y desplazan suavemente al catálogo.

---

## 📂 Estructura del Proyecto

```text
front/
├── public/                        # Activos públicos (favicon)
├── src/
│   ├── api/
│   │   ├── client/
│   │   │   └── BaseApi.ts         # Cliente HTTP Axios (baseURL: http://localhost:8080/api/v1/)
│   │   ├── services/              # Capa de servicios completa para todos los endpoints de la API
│   │   │   ├── auth/AuthApi.ts
│   │   │   ├── categorias/CategoriasApi.ts
│   │   │   ├── productos/ProductosApi.ts
│   │   │   ├── tiendas/TiendasApi.ts
│   │   │   ├── usuarios/UsuariosApi.ts
│   │   │   ├── ventas/VentasApi.ts
│   │   │   └── index.ts
│   │   └── types/                 # Contratos e interfaces TypeScript (DTOs Request/Response)
│   │       ├── auth/              # LoginRequest, AuthResponse
│   │       ├── categorias/        # CategoriaRequest, CategoriaResponse
│   │       ├── productos/         # ProductoRequest, ProductoUpdateRequest, ProductoResponse (con talla), etc.
│   │       ├── tiendas/           # TiendaRequest, TiendaResponse
│   │       ├── usuarios/          # UsuarioRequest, UsuarioUpdateRequest, UsuarioResponse, AssignRolesRequest
│   │       ├── ventas/            # VentaRequest, VentaResponse, ProductoMasVendidoResponse
│   │       ├── shared/            # ErrorResponse
│   │       └── index.ts
│   ├── layouts/
│   │   └── Layout.astro           # Layout global (Header, Buscador, QuickView Modal, Cart/Wishlist Drawers, Footer)
│   ├── lib/
│   │   ├── stores.ts              # Estado reactivo en cliente (CartStore, WishlistStore, QuickView, Toast)
│   │   └── card.ts                # Renderizado de tarjetas de producto con soporte de tallas y sucursales
│   ├── pages/
│   │   ├── index.astro            # Página de inicio SSR
│   │   └── Productos/
│   │       ├── ListaProducto.astro # Home completo (Hero, Categorías, Filtros, Grid, Top Ventas, Sucursales)
│   │       ├── [id].astro         # Ruta dinámica SSR para detalle de producto por UUID
│   │       └── Producto.astro     # Vista detallada del producto (Galería, Tallas, Sucursales, Relacionados)
│   └── styles/
│       └── global.css             # Configuración de TailwindCSS 4 y estilos editoriales Noble Muse
├── astro.config.mjs               # Configuración de Astro (output: 'server' + Vite Tailwind plugin)
├── tsconfig.json
└── package.json
```

---

## 🔌 Servicios de API Disponibles (`src/api/services/`)

Todos los servicios heredan de `BaseApi` y cuentan con tipado estricto en TypeScript:

| Servicio | Métodos Implementados |
| :--- | :--- |
| **`ProductosApi`** | `getPrincipalesProductos()`, `getAllProductos(tiendaId?)`, `getProductoById(id)`, `createProducto(data)`, `updateProducto(id, data)`, `deleteProducto(id)`, `asignarInventario(id, data)`, `eliminarInventario(id, tiendaId)`, `uploadImagenes(id, files)`, `setPrincipalImagen(id, imagenId)`, `deleteImagen(id, imagenId)` |
| **`CategoriasApi`** | `getAllCategorias()`, `getCategoriaById(id)`, `createCategoria(data)`, `updateCategoria(id, data)`, `deleteCategoria(id)` |
| **`TiendasApi`** | `getAllTiendas()`, `getTiendaById(id)`, `createTienda(data)`, `updateTienda(id, data)`, `deleteTienda(id)` |
| **`VentasApi`** | `registrarVenta(data)`, `getMasVendidos(tiendaId?, limit?)`, `getVentasByTienda(tiendaId)` |
| **`AuthApi`** | `login(credentials)`, `logout()`, `me()` |
| **`UsuariosApi`** | `getAllUsuarios()`, `getUsuarioById(id)`, `createUsuario(data)`, `updateUsuario(id, data)`, `deleteUsuario(id)`, `assignRoles(id, data)` |

---

## 🚀 Comandos Disponibles

Ejecuta los siguientes comandos desde el directorio `front/`:

| Comando | Acción |
| :--- | :--- |
| `npm install` | Instala todas las dependencias del proyecto |
| `npm run dev` | Inicia el servidor de desarrollo SSR en `http://localhost:4321` |
| `npm run build` | Compila el proyecto para producción en `./dist/` |
| `npm run preview` | Previsualiza localmente la versión compilada |

> **Importante:** Asegúrate de tener el backend de Spring Boot ejecutándose en `http://localhost:8080` antes de iniciar el frontend para que las páginas SSR puedan obtener el catálogo, las categorías, las sucursales y el ranking de ventas.
