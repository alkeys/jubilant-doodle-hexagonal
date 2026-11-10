# 👗👟 Jubilant Doodle — Arquitectura Hexagonal & Vertical Slice (Noble Muse Store)

Sistema integral de comercio electrónico y gestión empresarial para la cadena de tiendas **Noble Muse (Moda Urbana, Calzado, Sneakers y Accesorios)**, construido aplicando **Arquitectura Hexagonal (Ports & Adapters)** organizada en **Vertical Slices** en el backend y **Astro 7 SSR + TailwindCSS 4 (Tema Editorial Noble Muse)** en el frontend, separando estrictamente el **Portal de Cliente** y el **Panel de Administración**.

---

## 🖼️ Galería del sistema

<table>
  <tr>
    <td align="center">
      <a href="https://postimg.cc/BjMz1vbf">
        <img src="https://i.postimg.cc/VN8Pprcv/screenshot-2026-11-10-09-27-04.png" alt="Portal de cliente" width="420">
      </a>
      <br>🛍️ <strong>Portal de cliente</strong>
    </td>
    <td align="center">
      <a href="https://postimg.cc/HVmvZM7r">
        <img src="https://i.postimg.cc/hPQFGbD1/screenshot-2026-11-10-09-29-07.png" alt="Panel de administración" width="420">
      </a>
      <br>⚙️ <strong>Panel de administración</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://postimg.cc/FdcGGFrk">
        <img src="https://i.postimg.cc/85X3psx4/screenshot-2026-11-10-09-29-40.png" alt="Edición de producto" width="420">
      </a>
      <br>📦 <strong>Edición de productos</strong>
    </td>
    <td align="center">
      <a href="https://postimg.cc/pyfqFdcq">
        <img src="https://i.postimg.cc/G3NN68Fw/screenshot-2026-11-10-09-30-08.png" alt="Gestión de categorías" width="420">
      </a>
      <br>🏷️ <strong>Gestión de categorías</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://postimg.cc/pyfqFdcq">
        <img src="https://i.postimg.cc/G3NN68Fw/screenshot-2026-11-10-09-30-08.png" alt="Gestión de sucursales" width="420">
      </a>
      <br>🏪 <strong>Gestión de sucursales</strong>
    </td>
    <td align="center">
      <a href="https://postimg.cc/pyfqFdcq">
        <img src="https://i.postimg.cc/G3NN68Fw/screenshot-2026-11-10-09-30-08.png" alt="Usuarios y roles" width="420">
      </a>
      <br>👥 <strong>Usuarios y roles</strong>
    </td>
  </tr>
  <tr>
    <td align="center" colspan="2">
      <a href="https://postimg.cc/pyfqFdcq">
        <img src="https://i.postimg.cc/G3NN68Fw/screenshot-2026-11-10-09-30-08.png" alt="Ventas y reportes" width="420">
      </a>
      <br>📊 <strong>Ventas y reportes</strong>
    </td>
  </tr>
</table>

## 📁 Estructura del Repositorio (Monorepo)

```text
jubilant-doodle-hexagonal/
├── backend/                  # API REST en Java 25 + Spring Boot 4 (Gradle)
│   ├── src/main/java/        # Código fuente organizado por Vertical Slices + Hexagonal
│   ├── src/main/resources/   # Configuración (application.yaml) y migraciones Flyway (V1..V8)
│   ├── uploads/              # Almacenamiento local de imágenes de productos (/uploads/productos/**)
│   ├── Dockerfile            # Construcción multi-stage para despliegue en contenedor
│   └── build.gradle          # Dependencias y configuración de compilación
│
├── front/                    # Aplicación Web SSR en Astro 7 + TailwindCSS 4 + TypeScript
│   ├── src/api/
│   │   ├── lib/              # Cliente Axios con interceptor JWT + resolveImageUrl (ApiClient.ts)
│   │   ├── services/         # Servicios por dominio (Productos, Categorias, Tiendas, Auth, Ventas, Usuarios)
│   │   └── types/            # Contratos TypeScript (Request/Response DTOs de los 28 endpoints)
│   ├── src/components/
│   │   ├── layout/           # TopBanner, Navbar, MegaMenu, SearchPanel, Footer
│   │   ├── overlays/         # CartDrawer, WishlistDrawer, QuickViewModal, OrderThanksModal
│   │   └── home/             # HeroSlider, CategoriasGrid, CatalogoFiltros, ProductoCard, TopVentas, Sucursales
│   ├── src/layouts/          # Layout.astro (Portal Cliente) y AdminLayout.astro (Panel Admin RBAC)
│   ├── src/lib/              # Estado global (stores.ts), RBAC (authGuard.ts), scripts cliente (layoutClient.ts, catalogFilterClient.ts)
│   ├── src/middleware.ts     # Middleware SSR de protección de rutas (/admin/**) y proxy de /uploads/**
│   ├── src/pages/
│   │   ├── index.astro       # Portada del Portal de Cliente
│   │   ├── Productos/        # Catálogo y detalle dinámico (/Productos/[id])
│   │   ├── user/             # Login, Registro, Mi Cuenta y Mis Pedidos
│   │   └── admin/            # Panel de Administración (Productos, Tiendas, Categorías, Usuarios, Ventas)
│   └── package.json
│
└── docker-compose.yml        # Orquestación de Base de Datos (PostgreSQL + MongoDB) y Backend
```

---

## 🏛️ Arquitectura del Backend (`backend/`)

El backend combina dos patrones de diseño de software:

1. **Vertical Slice Architecture**: El sistema se divide por **dominios de negocio independientes** (`auth`, `productos`, `categorias`, `tiendas`, `ventas`, `usuarios`, `shared`).
2. **Arquitectura Hexagonal (Ports & Adapters)**: Cada módulo aísla su núcleo de dominio respecto a infraestructura web, seguridad y persistencia JPA.

```text
alex.probando.<modulo>
├── domain/                         # NÚCLEO: Modelos puros de negocio
├── application/
│   ├── port/
│   │   ├── in/                     # PUERTOS DE ENTRADA: Casos de uso
│   │   └── out/                    # PUERTOS DE SALIDA: Repositorios y almacenamiento
│   └── service/                    # Implementación transaccional de casos de uso
└── infra/
    └── adapter/
        ├── in/web/                 # Controladores REST, DTOs Request/Response y Mappers Web
        └── out/persistence/jpa/    # Entidades JPA, Spring Data Repositories y Mappers JPA
```

### Módulos del Sistema (Slices)

| Slice | Responsabilidad Principal |
| :--- | :--- |
| **`auth`** | Autenticación con JWT en cookie `HttpOnly` (`access_token`) + token `Bearer`, cierre de sesión y validación de sesión activa (`/api/v1/auth/me`). |
| **`productos`** | Catálogo maestro de productos (incluyendo `talla`), subida multipart de fotografías (`POST /api/v1/productos/{id}/imagenes`), selección de imagen de portada, borrado físico/lógico de fotos e inventario/precios diferenciados por sucursal. |
| **`categorias`** | Clasificación del catálogo de productos y filtrado por colecciones. |
| **`tiendas`** | Administración de sucursales físicas y consulta de inventario por tienda. |
| **`ventas`** | Checkout transaccional multi-artículo, descuento automático de stock en sucursal, historial de pedidos filtrable y ranking de productos más vendidos (`/api/v1/ventas/top-productos`). |
| **`usuarios`** | Registro público de clientes (`POST /api/v1/usuarios`), consulta de perfil propio (`GET /api/v1/usuarios/perfil`), administración de cuentas y asignación de roles (`ADMIN`, `RH`, `CLIENTE`). |
| **`shared`** | Seguridad centralizada (`SecurityConfig`, `EndpointSecurityManager`, `JwtCookieAuthenticationFilter`), CORS (`CorsConfig`), recursos estáticos (`WebMvcConfig` para `/uploads/**`), almacenamiento de archivos (`LocalFileStorageAdapter`) y manejo global de errores (`GlobalExceptionHandler`). |

---

## 🖥️ Módulos del Frontend (`front/`)

El frontend está separado en dos módulos independientes bajo la identidad visual **Noble Muse (`astro-njx-muse`)**:

### 1. Portal de Cliente (`Layout.astro`)
- **`/` (`index.astro` + `ListaProducto.astro`)**: Hero Slider dinámico, Mosaico de Colecciones, Filtros en tiempo real (por sucursal, categoría, talla, precio y orden), Grilla de Productos, Top Ventas y Directorio de Sucursales.
- **`/Productos/[id]`**: Ficha editorial del producto con galería interactiva, selector de sucursal con stock y precio en vivo, selector de talla y piezas relacionadas.
- **`/user/Login`**: Inicio de sesión con detección de sesión activa y redirección inteligente por rol (`ADMIN`/`RH` → `/admin`, `CLIENTE` → `/user/account`).
- **`/user/register`**: Registro público de nuevos clientes conectado a `POST /api/v1/usuarios` con auto-login tras crear la cuenta.
- **`/user/account`**: Ficha personal del usuario (`GET /api/v1/usuarios/perfil`), resumen de bolsa/wishlist, accesos rápidos y tabla de compras recientes.
- **`/user/pedidos`**: Ventana dedicada de **Mis Pedidos** (`GET /api/v1/ventas?clienteId=...`) con buscador, filtro por sucursal, KPIs de compras y modal de comprobante oficial (`GET /api/v1/ventas/{id}`).
- **Checkout Integrado (`CartDrawer.astro`)**: La bolsa de compras permite elegir sucursal de despacho y procesa la compra real contra `POST /api/v1/ventas`.

### 2. Panel de Administración (`AdminLayout.astro` + Protección RBAC)
Protegido mediante [`src/middleware.ts`](front/src/middleware.ts) y [`src/lib/authGuard.ts`](front/src/lib/authGuard.ts) para los roles **`ADMIN`** y **`RH`**:
- **`/admin` (Subir Productos, Galería e Inventario)**:
  - Alta de producto + asignación de sucursal/precio/stock inicial + subida múltiple de imágenes con vista previa y opción de descartar archivos antes de publicar.
  - Edición de producto con visualización y borrado directo de fotografías actuales (`DELETE /api/v1/productos/{id}/imagenes/{imagenId}`), cambio de portada (`PUT .../principal`) y carga de nuevas fotos.
  - Modal de inventario multitienda (`POST` / `DELETE /api/v1/productos/{id}/tiendas`).
- **`/admin/tiendas` (Administrar Tiendas / Sucursales)**:
  - CRUD completo de sucursales (`GET`, `POST`, `PUT`, `DELETE /api/v1/tiendas`).
  - Ventana modal **Ver Inventario** por sucursal con el listado de piezas, existencias y acceso directo para subir productos a esa tienda.
- **`/admin/categorias` (Gestión de Categorías)**:
  - CRUD completo de categorías del catálogo (`GET`, `POST`, `PUT`, `DELETE /api/v1/categorias`).
- **`/admin/usuarios` (Directorio de Usuarios y Roles — `ADMIN` y `RH`)**:
  - Listado y búsqueda de cuentas, alta/edición de usuarios, asignación de roles (`PATCH /api/v1/usuarios/{id}/roles`) y baja (`DELETE /api/v1/usuarios/{id}`).
- **`/admin/ventas` (Ver Pedidos y Auditoría de Ventas)**:
  - KPIs financieros en tiempo real, filtros combinados por sucursal, producto o UUID de cliente (`GET /api/v1/ventas`) y comprobante detallado por transacción (`GET /api/v1/ventas/{id}`).

---

## 🔐 Matriz de Roles y Seguridad (RBAC)

Configurada en [`EndpointSecurityManager.java`](backend/src/main/java/alex/probando/shared/security/EndpointSecurityManager.java):

| Rol | Permisos en el Sistema |
| :--- | :--- |
| **Público (Sin sesión)** | Consulta de catálogo (`/api/v1/productos/**`), categorías (`/api/v1/categorias/**`), tiendas (`/api/v1/tiendas/**`), ranking top ventas (`/api/v1/ventas/top-productos`), imágenes (`/uploads/**`), login (`/api/v1/auth/**`) y registro (`POST /api/v1/usuarios`). |
| **`CLIENTE`** | Todo lo público + consultar su perfil (`GET /api/v1/usuarios/perfil`), realizar compras (`POST /api/v1/ventas`) y consultar sus pedidos (`GET /api/v1/ventas/**`). |
| **`RH`** | Todo lo de `CLIENTE` + acceso al Panel Admin para consultar directorio y detalle de usuarios (`GET /api/v1/usuarios`, `GET /api/v1/usuarios/{id}`). |
| **`ADMIN`** | Control total de productos, subida/borrado de imágenes, inventario por tienda, sucursales, categorías, ventas, usuarios y asignación de roles. |

### Credenciales por Defecto (Inicializadas automáticamente)
- **Usuario Admin (`DataInitializer.java`)**:
  - **Usuario**: `Alex123`
  - **Contraseña**: `12345678`
  - **Rol**: `ADMIN`

---

## ⚙️ Ejecución del Proyecto

### 1. Iniciar Base de Datos
```bash
docker compose up -d
```

### 2. Iniciar el Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
- API REST: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Imágenes estáticas: `http://localhost:8080/uploads/...`

### 3. Iniciar el Frontend (Astro SSR)
```bash
cd front
pnpm install
pnpm dev
```
- Aplicación Web: `http://localhost:4321`

---

## 👤 Autor

**Alexander Aviles (Alkeys)**
- GitHub: [@alkeys](https://github.com/alkeys)
