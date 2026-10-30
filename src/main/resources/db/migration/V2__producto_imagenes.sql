-- =========================================================================
-- V2: TABLA DE MÚLTIPLES IMÁGENES POR PRODUCTO
-- =========================================================================

CREATE TABLE IF NOT EXISTS producto_imagenes (
    id UUID PRIMARY KEY,
    producto_id UUID NOT NULL,
    url VARCHAR(1000) NOT NULL,
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,
    orden INT NOT NULL DEFAULT 0,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_imagenes_producto 
        FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_producto_imagenes_producto ON producto_imagenes(producto_id);
