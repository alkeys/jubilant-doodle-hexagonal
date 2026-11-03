-- =========================================================================
-- V7: MIGRACIÓN DE CATÁLOGO A TIENDA DE ROPA, CALZADO Y ACCESORIOS
-- =========================================================================

-- 1. Limpiar registros previos de ventas, inventario e imágenes de ejemplo (panadería)
DELETE FROM ventas;
DELETE FROM tienda_productos;
DELETE FROM producto_imagenes;
DELETE FROM productos;
DELETE FROM categorias;

-- 2. Actualizar correo del administrador al giro de moda (conservando usuario 'admin' y contraseña)
UPDATE usuarios
SET email = 'admin@modaurbana.com'
WHERE id = '11111111-1111-1111-1111-111111111111';

-- 3. Actualizar las sucursales existentes y agregar una tercera sucursal (Outlet)
UPDATE tiendas
SET nombre = 'Boutique Centro Histórico',
    descripcion = 'Tienda insignia de moda urbana, ropa casual y calzado de temporada.',
    direccion = 'Av. Madero 205, Centro Histórico'
WHERE id = '33333333-3333-3333-3333-333333333331';

UPDATE tiendas
SET nombre = 'Sucursal Plaza Norte',
    descripcion = 'Especialistas en sneakers, streetwear, ropa deportiva y accesorios.',
    direccion = 'Blvd. de las Naciones 840, Local 12B, Zona Norte'
WHERE id = '33333333-3333-3333-3333-333333333332';

INSERT INTO tiendas (id, nombre, descripcion, direccion, activo)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'Fashion Outlet Sur',
    'Sucursal outlet con descuentos en calzado, chamarras y colecciones pasadas.',
    'Calzada del Sur 1520, Plaza Outlet Sur',
    TRUE
)
ON CONFLICT (id) DO UPDATE
SET nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion,
    direccion = EXCLUDED.direccion,
    activo = EXCLUDED.activo;

-- 4. Crear categorías de ropa, calzado y accesorios
INSERT INTO categorias (id, nombre, descripcion, activo)
VALUES
    (
        '44444444-4444-4444-4444-444444444441',
        'Camisetas y Tops',
        'Playeras básicas, gráficas, camisas casuales y polos de algodón.',
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444442',
        'Pantalones y Jeans',
        'Jeans de mezclilla, pantalones cargo, joggers y chinos.',
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444443',
        'Calzado y Sneakers',
        'Tenis urbanos, botas de piel, calzado deportivo y zapatos casuales.',
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        'Chamarras y Sudaderas',
        'Hoodies, chamarras de mezclilla, cazadoras de cuero y rompevientos.',
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444445',
        'Accesorios',
        'Gorras, mochilas, cinturones, lentes de sol y bolsos urbanos.',
        TRUE
    )
ON CONFLICT (id) DO UPDATE
SET nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion,
    activo = EXCLUDED.activo;

-- 5. Insertar catálogo global de productos (Ropa, Zapatos y Accesorios)
INSERT INTO productos (id, categoria_id, nombre, descripcion, activo)
VALUES
    -- Camisetas y Tops
    (
        '55555555-5555-5555-5555-555555555551',
        '44444444-4444-4444-4444-444444444441',
        'Camiseta Oversize Algodón Premium',
        'Playera de corte holgado confeccionada en algodón peinado de 240g, cuello redondo reforzado.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555552',
        '44444444-4444-4444-4444-444444444441',
        'Camisa Oxford Manga Larga',
        'Camisa casual de corte slim fit en tejido Oxford transpirable, ideal para look casual o semiformal.',
        TRUE
    ),
    -- Pantalones y Jeans
    (
        '55555555-5555-5555-5555-555555555553',
        '44444444-4444-4444-4444-444444444442',
        'Jeans Slim Fit Denim Oscuro',
        'Pantalón de mezclilla elástica con lavado índigo oscuro, cinco bolsillos y costuras reforzadas.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555554',
        '44444444-4444-4444-4444-444444444442',
        'Pantalón Cargo Streetwear',
        'Pantalón cargo de gabardina resistente con múltiples bolsillos laterales y ajuste en tobillos.',
        TRUE
    ),
    -- Calzado y Sneakers
    (
        '55555555-5555-5555-5555-555555555555',
        '44444444-4444-4444-4444-444444444443',
        'Sneakers Urban Blancos Classic',
        'Tenis casuales minimalistas de piel sintética con suela vulcanizada antiderrapante y plantilla confort.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555556',
        '44444444-4444-4444-4444-444444444443',
        'Tenis Deportivos Runner Pro',
        'Calzado deportivo ultraligero con malla transpirable y entresuela de amortiguación reactiva.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555557',
        '44444444-4444-4444-4444-444444444443',
        'Botas Chelsea de Piel',
        'Botines estilo Chelsea en piel genuina con paneles elásticos laterales y suela de caucho cosida.',
        TRUE
    ),
    -- Chamarras y Sudaderas
    (
        '55555555-5555-5555-5555-555555555558',
        '44444444-4444-4444-4444-444444444444',
        'Chamarra de Mezclilla Vintage',
        'Clásica chamarra trucker de denim con botones metálicos y bolsillos frontales con solapa.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555559',
        '44444444-4444-4444-4444-444444444444',
        'Sudadera Hoodie Essential Gris',
        'Sudadera con capucha y bolsillo canguro, interior afelpado suave de algodón y poliéster.',
        TRUE
    ),
    -- Accesorios
    (
        '55555555-5555-5555-5555-555555555560',
        '44444444-4444-4444-4444-444444444445',
        'Mochila Urbana Impermeable',
        'Mochila minimalista con compartimento acolchado para laptop de 15 pulgadas y tela repelente al agua.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555561',
        '44444444-4444-4444-4444-444444444445',
        'Gorra Snapback Bordada',
        'Gorra estructurada de 6 paneles con visera curva y cierre ajustable trasero.',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555562',
        '44444444-4444-4444-4444-444444444445',
        'Lentes de Sol Retro Polarizados',
        'Gafas de sol con armazón de acetato ligero y lentes con protección UV400 polarizada.',
        TRUE
    )
ON CONFLICT (id) DO UPDATE
SET categoria_id = EXCLUDED.categoria_id,
    nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion,
    activo = EXCLUDED.activo;

-- 6. Imágenes de los productos
INSERT INTO producto_imagenes (id, producto_id, url, es_principal, orden)
VALUES
    -- Camiseta Oversize
    ('66666666-6666-6666-6666-666666666601', '55555555-5555-5555-5555-555555555551', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    ('66666666-6666-6666-6666-666666666602', '55555555-5555-5555-5555-555555555551', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=800&q=80', FALSE, 2),
    -- Camisa Oxford
    ('66666666-6666-6666-6666-666666666603', '55555555-5555-5555-5555-555555555552', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Jeans Slim Fit
    ('66666666-6666-6666-6666-666666666604', '55555555-5555-5555-5555-555555555553', 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Pantalón Cargo
    ('66666666-6666-6666-6666-666666666605', '55555555-5555-5555-5555-555555555554', 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Sneakers Urban Blancos
    ('66666666-6666-6666-6666-666666666606', '55555555-5555-5555-5555-555555555555', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    ('66666666-6666-6666-6666-666666666607', '55555555-5555-5555-5555-555555555555', 'https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?auto=format&fit=crop&w=800&q=80', FALSE, 2),
    -- Tenis Deportivos Runner Pro
    ('66666666-6666-6666-6666-666666666608', '55555555-5555-5555-5555-555555555556', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Botas Chelsea
    ('66666666-6666-6666-6666-666666666609', '55555555-5555-5555-5555-555555555557', 'https://images.unsplash.com/photo-1638247025967-b4e38f787b76?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Chamarra de Mezclilla
    ('66666666-6666-6666-6666-666666666610', '55555555-5555-5555-5555-555555555558', 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Sudadera Hoodie
    ('66666666-6666-6666-6666-666666666611', '55555555-5555-5555-5555-555555555559', 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Mochila Urbana
    ('66666666-6666-6666-6666-666666666612', '55555555-5555-5555-5555-555555555560', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Gorra Snapback
    ('66666666-6666-6666-6666-666666666613', '55555555-5555-5555-5555-555555555561', 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=800&q=80', TRUE, 1),
    -- Lentes de Sol
    ('66666666-6666-6666-6666-666666666614', '55555555-5555-5555-5555-555555555562', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80', TRUE, 1)
ON CONFLICT (id) DO NOTHING;

-- 7. Asignar inventario y precios por sucursal (tienda_productos)
INSERT INTO tienda_productos (id, tienda_id, producto_id, precio, stock, activo)
VALUES
    -- Boutique Centro Histórico (33333333-3333-3333-3333-333333333331)
    ('88888888-8888-8888-8888-888888888801', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555551', 24.99, 45, TRUE),
    ('88888888-8888-8888-8888-888888888802', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555552', 39.90, 30, TRUE),
    ('88888888-8888-8888-8888-888888888803', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555553', 49.99, 35, TRUE),
    ('88888888-8888-8888-8888-888888888804', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555555', 79.99, 25, TRUE),
    ('88888888-8888-8888-8888-888888888805', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555557', 119.00, 15, TRUE),
    ('88888888-8888-8888-8888-888888888806', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555558', 69.50, 20, TRUE),
    ('88888888-8888-8888-8888-888888888807', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555560', 54.00, 18, TRUE),
    ('88888888-8888-8888-8888-888888888808', '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555562', 29.99, 40, TRUE),

    -- Sucursal Plaza Norte (33333333-3333-3333-3333-333333333332)
    ('88888888-8888-8888-8888-888888888809', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555551', 24.99, 60, TRUE),
    ('88888888-8888-8888-8888-888888888810', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555554', 54.99, 28, TRUE),
    ('88888888-8888-8888-8888-888888888811', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555555', 79.99, 32, TRUE),
    ('88888888-8888-8888-8888-888888888812', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555556', 94.99, 40, TRUE),
    ('88888888-8888-8888-8888-888888888813', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555559', 44.99, 50, TRUE),
    ('88888888-8888-8888-8888-888888888814', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555560', 54.00, 22, TRUE),
    ('88888888-8888-8888-8888-888888888815', '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555561', 21.50, 55, TRUE),

    -- Fashion Outlet Sur (33333333-3333-3333-3333-333333333333) - Precios de outlet
    ('88888888-8888-8888-8888-888888888816', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555553', 39.99, 15, TRUE),
    ('88888888-8888-8888-8888-888888888817', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', 64.99, 12, TRUE),
    ('88888888-8888-8888-8888-888888888818', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555556', 79.99, 18, TRUE),
    ('88888888-8888-8888-8888-888888888819', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555558', 55.00, 10, TRUE),
    ('88888888-8888-8888-8888-888888888820', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555559', 34.99, 25, TRUE)
ON CONFLICT (tienda_id, producto_id) DO UPDATE
SET precio = EXCLUDED.precio,
    stock = EXCLUDED.stock,
    activo = EXCLUDED.activo;

-- 8. Registrar ventas de ejemplo con el nuevo catálogo y sucursales
INSERT INTO ventas (id, cliente_id, tienda_id, producto_id, cantidad, precio_unitario, total, created_at)
VALUES
    (
        '77777777-7777-7777-7777-777777777771',
        '22222222-2222-2222-2222-222222222222',
        '33333333-3333-3333-3333-333333333331',
        '55555555-5555-5555-5555-555555555555',
        1,
        79.99,
        79.99,
        CURRENT_TIMESTAMP - INTERVAL '2 days'
    ),
    (
        '77777777-7777-7777-7777-777777777772',
        '22222222-2222-2222-2222-222222222222',
        '33333333-3333-3333-3333-333333333332',
        '55555555-5555-5555-5555-555555555551',
        2,
        24.99,
        49.98,
        CURRENT_TIMESTAMP - INTERVAL '1 day'
    ),
    (
        '77777777-7777-7777-7777-777777777773',
        '22222222-2222-2222-2222-222222222222',
        '33333333-3333-3333-3333-333333333332',
        '55555555-5555-5555-5555-555555555556',
        1,
        94.99,
        94.99,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;

