-- =========================================================================
-- V4: DATOS DE EJEMPLO (SEED DATA)
-- =========================================================================

-- 1. Usuarios de ejemplo
-- Contraseñas:
-- admin / admin123
-- mariaperez / cliente123
INSERT INTO usuarios (id, nombre, apellido, email, nombre_de_usuario, password, edad, activo)
VALUES 
    (
        '11111111-1111-1111-1111-111111111111', 
        'Carlos', 
        'Administrador', 
        'admin@panfrances.com', 
        'admin', 
        '$2a$10$9OIH1Sa39q7vLbUCbj.p3.h8IUNrONznPKC6e3JF6utwVDW.kubBi', 
        32, 
        TRUE
    ),
    (
        '22222222-2222-2222-2222-222222222222', 
        'María', 
        'Pérez', 
        'maria.perez@example.com', 
        'mariaperez', 
        '$2a$10$wMNx12AiU0dQvcR5wPXYMOUseef2rtNnya8S0y5CDHGIyBQ1evgx2', 
        28, 
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

-- 2. Asignación de Roles
INSERT INTO usuario_roles (usuario_id, role_id)
SELECT '11111111-1111-1111-1111-111111111111'::uuid, r.id 
FROM roles r WHERE r.nombre = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
ON CONFLICT DO NOTHING;

-- 3. Tiendas de ejemplo
INSERT INTO tiendas (id, nombre, descripcion, direccion, activo)
VALUES 
    (
        '33333333-3333-3333-3333-333333333331', 
        'Panadería Central', 
        'Panadería artesanal y repostería fina recién salida del horno.', 
        'Av. Central 101, Centro', 
        TRUE
    ),
    (
        '33333333-3333-3333-3333-333333333332', 
        'Sucursal Norte', 
        'Especialidad en café de altura y panadería tradicional.', 
        'Calle Flores 456, Norte', 
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

-- 4. Categorías de ejemplo
INSERT INTO categorias (id, nombre, descripcion, activo)
VALUES 
    (
        '44444444-4444-4444-4444-444444444441', 
        'Panadería Artesanal', 
        'Variedad de panes rústicos con masa madre.', 
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444442', 
        'Pastelería y Repostería', 
        'Croissants, pasteles y delicias dulces.', 
        TRUE
    ),
    (
        '44444444-4444-4444-4444-444444444443', 
        'Bebidas y Café', 
        'Café de grano, espresso y bebidas calientes o frías.', 
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

-- 5. Productos de ejemplo
INSERT INTO productos (id, tienda_id, categoria_id, nombre, descripcion, precio, stock, imagen_url, activo)
VALUES 
    (
        '55555555-5555-5555-5555-555555555551',
        '33333333-3333-3333-3333-333333333331',
        '44444444-4444-4444-4444-444444444441',
        'Baguette Artesanal',
        'Pan tradicional francés crujiente elaborado con fermentación lenta.',
        2.50,
        50,
        'https://images.unsplash.com/photo-1549931319-a545dcf3bc73',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555552',
        '33333333-3333-3333-3333-333333333331',
        '44444444-4444-4444-4444-444444444442',
        'Croissant de Mantequilla',
        'Clásico croissant hojaldrado horneado con mantequilla de primera calidad.',
        3.00,
        35,
        'https://images.unsplash.com/photo-1555507036-ab1f4038808a',
        TRUE
    ),
    (
        '55555555-5555-5555-5555-555555555553',
        '33333333-3333-3333-3333-333333333332',
        '44444444-4444-4444-4444-444444444443',
        'Café Latte',
        'Doble shot de café espresso con leche vaporizada y microespuma cremosa.',
        3.50,
        80,
        'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd',
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

-- 6. Imágenes adicionales de productos
INSERT INTO producto_imagenes (id, producto_id, url, es_principal, orden)
VALUES 
    (
        '66666666-6666-6666-6666-666666666661',
        '55555555-5555-5555-5555-555555555552',
        'https://images.unsplash.com/photo-1555507036-ab1f4038808a',
        TRUE,
        1
    )
ON CONFLICT (id) DO NOTHING;

-- 7. Ventas de ejemplo
INSERT INTO ventas (id, cliente_id, producto_id, cantidad, precio_unitario, total, created_at)
VALUES 
    (
        '77777777-7777-7777-7777-777777777771',
        '22222222-2222-2222-2222-222222222222',
        '55555555-5555-5555-5555-555555555551',
        2,
        2.50,
        5.00,
        CURRENT_TIMESTAMP - INTERVAL '1 day'
    ),
    (
        '77777777-7777-7777-7777-777777777772',
        '22222222-2222-2222-2222-222222222222',
        '55555555-5555-5555-5555-555555555553',
        1,
        3.50,
        3.50,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;
