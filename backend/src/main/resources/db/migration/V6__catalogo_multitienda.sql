-- =========================================================================
-- V6: DESACOPLAR CATÁLOGO DE PRODUCTOS E INVENTARIO POR TIENDA
-- =========================================================================

-- 1. Tabla intermedia de inventario por tienda
CREATE TABLE IF NOT EXISTS tienda_productos (
    id UUID PRIMARY KEY,
    tienda_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tienda_productos_tienda FOREIGN KEY (tienda_id) REFERENCES tiendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_tienda_productos_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT uq_tienda_producto UNIQUE (tienda_id, producto_id)
);

-- 2. Actualizar tabla ventas para registrar la sucursal de compra
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS tienda_id UUID;

UPDATE ventas v
SET tienda_id = p.tienda_id
FROM productos p
WHERE v.producto_id = p.id AND v.tienda_id IS NULL;

-- En caso de registros huérfanos sin tienda asignada
UPDATE ventas
SET tienda_id = (SELECT id FROM tiendas ORDER BY creado_en ASC LIMIT 1)
WHERE tienda_id IS NULL;

ALTER TABLE ventas ALTER COLUMN tienda_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_ventas_tienda'
    ) THEN
        ALTER TABLE ventas ADD CONSTRAINT fk_ventas_tienda 
            FOREIGN KEY (tienda_id) REFERENCES tiendas(id) ON DELETE RESTRICT;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_ventas_tienda ON ventas(tienda_id);

-- 3. Migrar inventarios existentes desde productos hacia tienda_productos
INSERT INTO tienda_productos (id, tienda_id, producto_id, precio, stock, activo, creado_en, actualizado_en)
SELECT 
    uuid_generate_v4(),
    tienda_id,
    id,
    precio,
    stock,
    activo,
    creado_en,
    actualizado_en
FROM productos
ON CONFLICT (tienda_id, producto_id) DO NOTHING;

-- 4. Eliminar columnas dependientes de sucursal en productos
DROP INDEX IF EXISTS idx_productos_tienda;
DROP INDEX IF EXISTS idx_productos_precio;

ALTER TABLE productos DROP CONSTRAINT IF EXISTS fk_producto_tienda;
ALTER TABLE productos DROP COLUMN IF EXISTS tienda_id;
ALTER TABLE productos DROP COLUMN IF EXISTS precio;
ALTER TABLE productos DROP COLUMN IF EXISTS stock;

-- 5. Crear índices de optimización para tienda_productos
CREATE INDEX IF NOT EXISTS idx_tienda_productos_tienda ON tienda_productos(tienda_id);
CREATE INDEX IF NOT EXISTS idx_tienda_productos_producto ON tienda_productos(producto_id);
CREATE INDEX IF NOT EXISTS idx_tienda_productos_precio ON tienda_productos(precio);

-- 6. Agregar ejemplo de producto presente en múltiples tiendas
-- El Croissant de Mantequilla también está disponible en la Sucursal Norte con su propio precio y stock
INSERT INTO tienda_productos (id, tienda_id, producto_id, precio, stock, activo)
VALUES (
    '88888888-8888-8888-8888-888888888881',
    '33333333-3333-3333-3333-333333333332',
    '55555555-5555-5555-5555-555555555552',
    3.25,
    20,
    TRUE
)
ON CONFLICT (tienda_id, producto_id) DO NOTHING;
