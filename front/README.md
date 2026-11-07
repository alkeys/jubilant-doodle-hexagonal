# 👗 Noble Muse — Frontend SSR en Astro 7 + TailwindCSS 4

Aplicación web de comercio electrónico y panel administrativo para la cadena de tiendas **Noble Muse (Moda Urbana, Calzado, Sneakers y Accesorios)**, construida con **Astro 7 en modo SSR**, **TailwindCSS 4** y **Axios**, conectada 100% en tiempo real al backend con Arquitectura Hexagonal (`http://localhost:8080/api/v1`).

---

## ✨ Arquitectura Modular del Frontend

El proyecto separa estrictamente la experiencia en dos módulos independientes siguiendo el diseño editorial **Noble Muse (`astro-njx-muse`)**:

### 1. Portal de Cliente (`src/layouts/Layout.astro`)
- **Componentes de Layout (`src/components/layout/`)**:
  - `TopBanner.astro`: Cinta superior informativa.
  - `Navbar.astro`: Barra de navegación con acceso a Catálogo, Tiendas, Mis Pedidos (`/user/pedidos`), botón dinámico `Admin` (`/admin` para roles `ADMIN`/`RH`) y estado de cuenta (`Iniciar Sesión` / `@usuario`).
  - `MegaMenu.astro`: Menú desplegable con sucursales, colecciones y pieza destacada.
  - `SearchPanel.astro`: Buscador predictivo en vivo por nombre, descripción o talla.
  - `Footer.astro`: Pie de página con filtros directos hacia el catálogo.
- **Componentes de Overlays (`src/components/overlays/`)**:
  - `CartDrawer.astro`: Bolsa de compras con selector de sucursal y checkout real conectado a `POST /api/v1/ventas`.
  - `WishlistDrawer.astro`: Lista de favoritos persistida en `localStorage`.
  - `QuickViewModal.astro`: Vista rápida de producto con selector de talla y sucursal.
  - `OrderThanksModal.astro`: Confirmación de pedido procesado.
- **Componentes del Home (`src/components/home/`)**:
  - `HeroSlider.astro`, `CategoriasGrid.astro`, `CatalogoFiltros.astro`, `ProductoCard.astro`, `TopVentasSection.astro`, `SucursalesSection.astro`.
- **Páginas de Usuario (`src/pages/user/`)**:
  - `/user/Login`: Inicio de sesión con cookie `HttpOnly` + respaldo `Bearer` token y redirección según rol.
  - `/user/register`: Registro de cuenta nueva (`POST /api/v1/usuarios`) con inicio de sesión automático.
  - `/user/account`: Ficha de perfil (`GET /api/v1/usuarios/perfil`), resumen de bolsa/favoritos y accesos rápidos.
  - `/user/pedidos`: Ventana dedicada de historial de pedidos del cliente (`GET /api/v1/ventas?clienteId=...`) con filtro por sucursal y recibo detallado.

### 2. Panel de Administración (`src/layouts/AdminLayout.astro`)
Protegido por el middleware SSR (`src/middleware.ts`) y guardias de rol (`src/lib/authGuard.ts`):
- **`/admin` (`src/pages/admin/index.astro`) — Subir Productos, Galería e Inventario**:
  - Creación de producto y subida múltiple de fotografías (`multipart/form-data`) en un solo flujo, con vista previa y botón `× Quitar` antes de publicar.
  - Edición de producto con galería integrada para **borrar fotografías individuales** (`DELETE /api/v1/productos/{id}/imagenes/{imagenId}`), **cambiar imagen de portada** (`PUT .../principal`) o agregar nuevas fotos.
  - Asignación de stock y precios diferenciados por sucursal (`POST` / `DELETE /api/v1/productos/{id}/tiendas`).
- **`/admin/tiendas` (`src/pages/admin/tiendas.astro`) — Administrar Tiendas**:
  - CRUD completo de sucursales y ventana modal **Ver Inventario** para inspeccionar las piezas y existencias de cada tienda.
- **`/admin/categorias` (`src/pages/admin/categorias.astro`) — Categorías**:
  - CRUD completo de categorías del catálogo.
- **`/admin/usuarios` (`src/pages/admin/usuarios.astro`) — Usuarios y Roles (`ADMIN` / `RH`)**:
  - Gestión de cuentas y asignación de roles (`ADMIN`, `RH`, `CLIENTE`).
- **`/admin/ventas` (`src/pages/admin/ventas.astro`) — Ver Pedidos y Ventas**:
  - KPIs de ingresos, unidades y ticket promedio, filtros por tienda/producto/cliente y modal de comprobante oficial.

---

## 🔌 Capa de Servicios API (`src/api/`)

- **`src/api/lib/ApiClient.ts`**:
  - Instancia Axios con `withCredentials: true` (envío de cookie `HttpOnly`).
  - Interceptor de petición que adjunta `Authorization: Bearer <token>` como respaldo para peticiones `PUT`, `DELETE` y `PATCH` cross-origin.
  - Helper `resolveImageUrl()` que convierte automáticamente rutas relativas `/uploads/...` al origen del backend (`http://localhost:8080/uploads/...`).
- **`src/api/services/`**:
  - `AuthApi.ts`, `UsuariosApi.ts`, `ProductosApi.ts`, `CategoriasApi.ts`, `TiendasApi.ts`, `VentasApi.ts`.

---

## 🚀 Comandos Disponibles

| Comando | Acción |
| :--- | :--- |
| `pnpm install` | Instala las dependencias del proyecto |
| `pnpm dev` | Inicia el servidor de desarrollo SSR en `http://localhost:4321` |
| `pnpm build` | Compila el proyecto para producción en `./dist/` |
| `pnpm preview` | Previsualiza localmente la versión compilada |
