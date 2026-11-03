# 👗👟 Jubilant Doodle — Arquitectura Hexagonal & Vertical Slice (Noble Muse Store)

Sistema integral de gestión para cadena de tiendas de **Moda Urbana, Calzado, Sneakers y Accesorios** (Catálogo multi-sucursal con tallas, Inventario por tienda, Ventas, Usuarios y Autenticación), construido aplicando **Arquitectura Hexagonal (Ports & Adapters)** organizada en **Vertical Slices** en el backend y **Astro 7 SSR + TailwindCSS 4 (Tema Noble Muse)** en el frontend.

---

## 📁 Estructura del Repositorio (Monorepo)

El proyecto está dividido en dos módulos independientes dentro del mismo repositorio:

```text
jubilant-doodle-hexagonal/
├── backend/                  # API REST en Java 27 + Spring Boot 4.1.1 (Gradle)
│   ├── src/main/java/        # Código fuente organizado por Vertical Slices + Hexagonal
│   ├── src/main/resources/   # Configuración (application.yaml) y migraciones Flyway (V1..V8)
│   ├── Dockerfile            # Construcción multi-stage para despliegue en contenedor
│   └── build.gradle          # Dependencias y configuración de compilación
│
├── front/                    # Aplicación Web SSR en Astro 7 + TailwindCSS 4 + TypeScript
│   ├── src/api/
│   │   ├── client/           # Instancia base de Axios (BaseApi.ts)
│   │   ├── services/         # Servicios completos por dominio (Productos, Categorias, Tiendas, Auth, Ventas, Usuarios)
│   │   └── types/            # Contratos TypeScript (Request/Response DTOs de todos los endpoints)
│   ├── src/lib/              # Estado global reactivo en cliente (stores.ts) y renderizado de cards (card.ts)
│   ├── src/layouts/          # Layout editorial Noble Muse (Header, Search, Quick View, Cart & Wishlist Drawers)
│   ├── src/pages/            # Rutas SSR: Home (index.astro) y Detalle (/Productos/[id].astro)
│   └── package.json          # Scripts y dependencias de frontend
│
└── docker-compose.yml        # Orquestación de Base de Datos (PostgreSQL 16) y Backend
```

---

## 🏛️ Arquitectura del Backend (`backend/`)

El backend combina dos patrones de diseño de software modernos:

1. **Vertical Slice Architecture (Primer nivel)**: En lugar de agrupar todo el proyecto en carpetas técnicas globales (`controllers/`, `services/`, `repositories/`), el sistema se divide por **dominios de negocio independientes** (`productos`, `categorias`, `tiendas`, `ventas`, `usuarios`, `shared`).
2. **Arquitectura Hexagonal / Ports & Adapters (Dentro de cada Slice)**: Cada módulo protege su lógica de negocio aislándola de frameworks externos, bases de datos y protocolos HTTP.

```text
alex.probando.<modulo>
├── domain/                         # NÚCLEO: Modelos puros de negocio (Sin anotaciones de Spring/JPA)
├── application/
│   ├── port/
│   │   ├── in/                     # PUERTOS DE ENTRADA: Casos de uso (Interfaces que expone el negocio)
│   │   └── out/                    # PUERTOS DE SALIDA: Interfaces que el negocio necesita (ej. Repositorios)
│   └── service/                    # CASOS DE USO: Implementación de la lógica de negocio
└── infra/
    └── adapter/
        ├── in/web/                 # ADAPTADOR PRIMARIO: Controladores REST, Request/Response DTOs y Mappers Web
        └── out/persistence/jpa/    # ADAPTADOR SECUNDARIO: Entidades JPA, Spring Data Repositories y Mappers JPA
```

### Módulos del Sistema (Slices)

| Slice | Responsabilidad Principal |
| :--- | :--- |
| **`productos`** | Gestión del catálogo maestro de productos (incluyendo `talla`), galería de imágenes (subida local y URLs externas), e inventario/precios diferenciados por sucursal (`tienda_productos`). |
| **`categorias`** | Clasificación del catálogo de productos (Camisetas y Tops, Pantalones y Jeans, Calzado y Sneakers, Chamarras y Sudaderas, Accesorios). |
| **`tiendas`** | Administración de sucursales físicas (Boutique Centro Histórico, Sucursal Plaza Norte, Fashion Outlet Sur) y consulta de stock por tienda. |
| **`ventas`** | Registro transaccional de ventas por sucursal, descuento automático de stock local y reportes de productos más vendidos (ranking global o por tienda). |
| **`usuarios`** | Gestión de usuarios, perfiles, asignación de roles (`ROLE_ADMIN`, `ROLE_VENDEDOR`, `ROLE_CLIENTE`) y endpoints de autenticación (`/api/v1/auth`). |
| **`shared`** | Componentes transversales: Seguridad (Spring Security + sesiones HTTP), manejo global de excepciones (`GlobalExceptionHandler`), auditoría y configuración de almacenamiento de archivos (`WebConfig`). |

---

## 🛠️ Stack Tecnológico

### Backend
- **Lenguaje**: Java 27 (Toolchain configurado en Gradle)
- **Framework**: Spring Boot 4.1.1 (`spring-boot-starter-webmvc`, `data-jpa`, `security`, `validation`, `actuator`)
- **Base de Datos**: PostgreSQL 16
- **Migraciones**: Flyway (`V1` a `V8` ejecutadas automáticamente al iniciar)
- **Documentación API**: SpringDoc OpenAPI 3 / Swagger UI (`3.0.2`)
- **Herramientas**: Lombok

### Frontend
- **Framework**: Astro 7.0 (`output: 'server'` — SSR dinámico conectado 100% en tiempo real a la API)
- **Estilos y Tema**: TailwindCSS 4.2 + Tema Editorial **Noble Muse (`astro-njx-muse`)** con fuentes *Cormorant Garamond* y *Plus Jakarta Sans*
- **Cliente HTTP**: Axios 1.15
- **Lenguaje**: TypeScript

---

## 🚀 Requisitos Previos

- **Docker** y **Docker Compose** (para levantar PostgreSQL o el stack completo).
- **JDK 27** (si deseas ejecutar el backend localmente fuera de Docker).
- **Node.js 20+** y **npm** (para ejecutar el frontend en Astro).

---

## ⚙️ Ejecución del Proyecto

### Opción 1: Levantar Base de Datos con Docker y Backend/Frontend en modo desarrollo (Recomendado)

#### 1. Iniciar PostgreSQL
Desde la raíz del repositorio, levanta únicamente el contenedor de base de datos:

```bash
docker compose up -d postgres
```
*Esto expondrá PostgreSQL en `localhost:5432` con la base de datos `midb` (`usuario` / `secreto`).*

#### 2. Iniciar el Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
- La API estará disponible en: `http://localhost:8080`
- **Flyway** creará automáticamente las tablas y cargará los datos semilla (`V1` a `V8`).
- Documentación interactiva **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Especificación OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

#### 3. Iniciar el Frontend (Astro SSR)
En otra terminal:
```bash
cd front
npm install
npm run dev
```
- La aplicación web estará disponible en: `http://localhost:4321`

---

### Opción 2: Levantar Backend + Base de Datos con Docker Compose

Si prefieres compilar y correr el backend dentro de Docker junto con PostgreSQL:

```bash
docker compose up --build -d
```

---

## 🗄️ Modelo de Datos y Migraciones (Flyway)

Las migraciones se encuentran en `backend/src/main/resources/db/migration/` y se ejecutan en orden secuencial:

1. **`V1__init_schema.sql`**: Crea las tablas base `usuarios`, `roles`, `usuario_roles`, `categorias`, `productos` y `ventas`.
2. **`V2__insert_roles_and_admin.sql`**: Inserta roles iniciales y el usuario administrador por defecto.
3. **`V3__tiendas_e_imagenes_producto.sql`**: Añade soporte multi-sucursal (`tiendas`, `tienda_productos`), galería de imágenes (`producto_imagenes`) y vincula las ventas a una tienda específica.
4. **`V4__seed_data_completa.sql`**: Datos semilla iniciales de prueba.
5. **`V5__ajustar_precios_reales.sql`**: Ajuste de precios e inventario multi-sucursal.
6. **`V6__imagenes_reales_panaderia.sql`**: Migración histórica de imágenes de catálogo.
7. **`V7__catalogo_moda_productos.sql`**: Transforma el catálogo a una cadena de **Moda Urbana, Calzado, Sneakers y Accesorios** (3 boutiques/sucursales, 5 categorías de moda, 12 productos con fotografías editoriales, precios diferenciados por sucursal y ventas de ejemplo).
8. **`V8__agregar_talla_productos.sql`**: Añade la columna `talla VARCHAR(100)` a la tabla `productos` y asigna tallas reales según el tipo de artículo (ej. `S, M, L, XL` en ropa, `28, 30, 32, 34` en jeans, `25.5, 26, 27, 28 MX` en calzado y `Unitalla` en accesorios).

> **Nota sobre borrado lógico (Soft Delete):** Las entidades principales utilizan la columna `activo = true/false` para dar de baja registros sin perder integridad referencial histórica en las ventas.

---

## 🔐 Autenticación y Credenciales de Prueba

El backend utiliza **Spring Security** con sesiones HTTP (`JSESSIONID`) mediante el endpoint `/api/v1/auth/login`.

| Rol | Email | Contraseña | Permisos |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@correo.com` | `admin123` | Control total (`POST`, `PUT`, `DELETE` en categorías, productos, tiendas, inventario y usuarios). |
| **Vendedor** | `carlos.vendedor@correo.com` | `admin123` | Consulta de catálogo y registro de ventas. |
| **Cliente** | `ana.cliente@correo.com` | `admin123` | Consulta pública de catálogo, sucursales y productos principales. |

---

## 📡 Principales Endpoints de la API (`/api/v1`)

### Autenticación (`/api/v1/auth`)
- `POST /api/v1/auth/login` — Iniciar sesión con email y password.
- `POST /api/v1/auth/logout` — Cerrar sesión activa.
- `GET /api/v1/auth/me` — Obtener datos y roles del usuario en sesión.

### Productos (`/api/v1/productos`)
- `GET /api/v1/productos/principales` — Catálogo público enriquecido (incluye `talla`, rango `precioMin` / `precioMax`, `stockTotal`, galería de imágenes y desglose por tienda).
- `GET /api/v1/productos?tiendaId={uuid}` — Listar productos (opcionalmente filtrados por sucursal).
- `GET /api/v1/productos/{id}` — Detalle completo de un producto (incluye `talla`, categoría, imágenes y disponibilidad por sucursal).
- `POST /api/v1/productos` — Crear producto con nombre, descripción, precio base, stock, categoría, `talla` e imágenes *(Admin)*.
- `PUT /api/v1/productos/{id}` — Actualizar datos generales del producto incluyendo `talla` *(Admin)*.
- `DELETE /api/v1/productos/{id}` — Eliminar producto lógicamente *(Admin)*.
- `POST /api/v1/productos/{id}/tiendas` — Asignar o actualizar precio y stock de un producto en una sucursal *(Admin)*.
- `DELETE /api/v1/productos/{id}/tiendas/{tiendaId}` — Remover producto de una sucursal *(Admin)*.
- `POST /api/v1/productos/{id}/imagenes` — Subir archivos de imagen (`multipart/form-data`) para un producto *(Admin)*.
- `PUT /api/v1/productos/{id}/imagenes/{imagenId}/principal` — Marcar imagen como portada principal *(Admin)*.
- `DELETE /api/v1/productos/{id}/imagenes/{imagenId}` — Eliminar una imagen de un producto *(Admin)*.

### Categorías (`/api/v1/categorias`)
- `GET /api/v1/categorias` — Listar categorías activas.
- `GET /api/v1/categorias/{id}` — Obtener categoría por ID.
- `POST /api/v1/categorias` / `PUT /api/v1/categorias/{id}` / `DELETE /api/v1/categorias/{id}` — CRUD de categorías *(Admin)*.

### Tiendas / Sucursales (`/api/v1/tiendas`)
- `GET /api/v1/tiendas` — Listar todas las sucursales activas.
- `GET /api/v1/tiendas/{id}` — Obtener detalle de sucursal.
- `POST /api/v1/tiendas` / `PUT /api/v1/tiendas/{id}` / `DELETE /api/v1/tiendas/{id}` — CRUD de sucursales *(Admin)*.

### Ventas (`/api/v1/ventas`)
- `POST /api/v1/ventas` — Registrar una venta en una sucursal (descuenta stock automáticamente).
- `GET /api/v1/ventas/mas-vendidos?tiendaId={uuid}&limit=10` — Ranking de productos más vendidos (global o por sucursal).
- `GET /api/v1/ventas/tienda/{tiendaId}` — Historial de ventas de una sucursal.

### Usuarios (`/api/v1/usuarios`)
- `GET /api/v1/usuarios` — Listar todos los usuarios *(Admin)*.
- `GET /api/v1/usuarios/{id}` — Obtener usuario por ID *(Admin)*.
- `POST /api/v1/usuarios` / `PUT /api/v1/usuarios/{id}` / `DELETE /api/v1/usuarios/{id}` — CRUD de usuarios *(Admin)*.
- `POST /api/v1/usuarios/{id}/roles` — Asignar roles a un usuario *(Admin)*.

---

## 🖥️ Arquitectura del Frontend (`front/`)

El frontend está construido con **Astro 7 en modo SSR (`output: 'server'`)** e implementa el diseño editorial **Noble Muse (`astro-njx-muse`)**, alimentado al **100% por la API REST del backend** (sin productos ni imágenes estáticas locales):

- **`src/api/client/BaseApi.ts`**: Cliente centralizado con Axios apuntando a `http://localhost:8080/api/v1/`.
- **`src/api/services/`**: Servicios tipados para los 6 módulos del backend (`ProductosApi`, `CategoriasApi`, `TiendasApi`, `AuthApi`, `VentasApi`, `UsuariosApi`) exportados en un barril (`index.ts`).
- **`src/api/types/`**: Interfaces TypeScript completas para todos los DTOs de entrada y salida de la API (`ProductoResponse` con `talla`, `CategoriaResponse`, `TiendaResponse`, `VentaResponse`, `ProductoMasVendidoResponse`, `UsuarioResponse`, `AuthResponse`, etc.).
- **`src/lib/stores.ts` y `src/lib/card.ts`**: Gestión en cliente de **Carrito de compras (`CartStore`)**, **Lista de deseos (`WishlistStore`)**, **Vista Rápida (`QuickView`)** con selector interactivo de tallas y sucursales, y notificaciones **Toast**.
- **`src/pages/index.astro` y `src/pages/Productos/ListaProducto.astro`**:
  - **Hero Slider dinámico** con los productos destacados de la API.
  - **Mosaico de Colecciones por Categoría** y **Directorio de Sucursales** con filtrado directo en un clic.
  - **Barra de Filtros Interactiva**: filtra en tiempo real por **Sucursal** (actualizando el precio y stock específicos de esa tienda), **Categoría**, **Talla**, **Rango de Precio** y **Ordenamiento**.
  - **Ranking Top Ventas** conectado a `/api/v1/ventas/mas-vendidos`.
- **`src/pages/Productos/[id].astro` y `src/pages/Productos/Producto.astro`**:
  - Página de detalle SSR dinámica para cualquier UUID de producto, con galería interactiva de imágenes, selector de tallas (`producto.talla`), tabla comparativa de precios y stock por sucursal, productos relacionados y acciones de compra/favoritos.

---

## 🧪 Pruebas y Compilación

Para ejecutar los tests unitarios y verificar la compilación del backend:

```bash
cd backend
./gradlew test
```

Para verificar la compilación de producción del frontend:

```bash
cd front
npm run build
```

---

## 👤 Autor

**Alexander Aviles (Alkeys)**
- GitHub: [@alkeys](https://github.com/alkeys)
