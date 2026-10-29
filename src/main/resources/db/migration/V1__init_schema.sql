-- =========================================================================
-- 1. CONFIGURACIÓN INICIAL
-- =========================================================================

-- Habilitar extensión para generación de UUIDs (opcional en PostgreSQL moderno)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- =========================================================================
-- 2. GESTIÓN DE USUARIOS Y ROLES
-- =========================================================================

-- Tabla de Roles
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    nombre_de_usuario VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    edad INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla Intermedia Many-to-Many (usuario_roles)
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    CONSTRAINT fk_usuario_roles_usuario 
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_roles_role 
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);


-- =========================================================================
-- 3. GESTIÓN DE TIENDAS Y CATÁLOGO
-- =========================================================================

-- Tabla de Sucursales / Tiendas de la cadena
CREATE TABLE IF NOT EXISTS tiendas (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Categorías (para filtrar productos por tipo)
CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de Productos (Cada producto pertenece a una tienda y a una categoría)
CREATE TABLE IF NOT EXISTS productos (
    id UUID PRIMARY KEY,
    tienda_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    imagen_url VARCHAR(508),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_tienda 
        FOREIGN KEY (tienda_id) REFERENCES tiendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_productos_categoria 
        FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);


-- =========================================================================
-- 4. ÍNDICES DE OPTIMIZACIÓN
-- =========================================================================

-- Índices para usuarios y autenticación
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(nombre_de_usuario);

-- Índices para tiendas y productos (búsquedas, filtros y relaciones)
CREATE INDEX IF NOT EXISTS idx_productos_tienda ON productos(tienda_id);
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_precio ON productos(precio);
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);


-- =========================================================================
-- 5. SEMILLA DE ROLES BÁSICOS
-- =========================================================================
INSERT INTO roles (id, nombre) VALUES 
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'RH'),
    (gen_random_uuid(), 'CLIENTE')
ON CONFLICT (nombre) DO NOTHING;
