

--registro de ventas realizadas
CREATE TABLE IF NOT EXISTS ventas(
	id UUID PRIMARY KEY,
	cliente_id UUID NOT NULL,
	producto_id UUID NOT NULL,
	cantidad INT NOT NULL,
	precio_unitario DECIMAL(10, 2) NOT NULL,
	total DECIMAL(10, 2) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_ventas_cliente FOREIGN KEY (cliente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
	CONSTRAINT fk_ventas_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
);


--Índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_ventas_cliente ON ventas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ventas_producto ON ventas(producto_id);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(created_at);
