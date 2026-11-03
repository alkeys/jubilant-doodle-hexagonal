-- =========================================================================
-- V8: AGREGAR CAMPO TALLA A LA TABLA PRODUCTOS Y POBLAR TALLAS POR TIPO
-- =========================================================================

ALTER TABLE productos ADD COLUMN IF NOT EXISTS talla VARCHAR(100);

-- Camisetas y Tops (Tallas de ropa)
UPDATE productos
SET talla = 'XS, S, M, L, XL'
WHERE id IN (
    '55555555-5555-5555-5555-555555555551',
    '55555555-5555-5555-5555-555555555552'
);

-- Pantalones y Jeans (Tallas de cintura)
UPDATE productos
SET talla = '28, 30, 32, 34, 36'
WHERE id IN (
    '55555555-5555-5555-5555-555555555553',
    '55555555-5555-5555-5555-555555555554'
);

-- Calzado y Sneakers (Tallas de calzado en cm / MX / EU)
UPDATE productos
SET talla = '25, 26, 27, 28, 29'
WHERE id IN (
    '55555555-5555-5555-5555-555555555555',
    '55555555-5555-5555-5555-555555555556',
    '55555555-5555-5555-5555-555555555557'
);

-- Chamarras y Sudaderas (Tallas de ropa)
UPDATE productos
SET talla = 'S, M, L, XL'
WHERE id IN (
    '55555555-5555-5555-5555-555555555558',
    '55555555-5555-5555-5555-555555555559'
);

-- Accesorios (Mochila, Gorra, Lentes)
UPDATE productos
SET talla = 'ÚNICA'
WHERE id IN (
    '55555555-5555-5555-5555-555555555560',
    '55555555-5555-5555-5555-555555555561',
    '55555555-5555-5555-5555-555555555562'
);

-- Valor por defecto para cualquier otro producto sin talla definida
UPDATE productos
SET talla = 'ÚNICA'
WHERE talla IS NULL;

