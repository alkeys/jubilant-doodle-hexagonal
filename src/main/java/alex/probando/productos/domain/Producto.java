package alex.probando.productos.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Producto(
        UUID id,
        UUID tiendaId,
        UUID categoriaId,
        String nombre,
        String descripcion,
        BigDecimal precio,
        int stock,
        boolean activo,
        Instant creadoEn,
        Instant actualizadoEn,
        List<ProductoImagen> imagenes) {

    public Producto {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if (tiendaId == null) {
            throw new IllegalArgumentException("El producto debe pertenecer a una sucursal/tienda");
        }
        if (categoriaId == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría asignada");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor o igual a 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = Instant.now();
        }
        imagenes = imagenes != null ? List.copyOf(imagenes) : List.of();
    }

    public Producto(
            UUID tiendaId,
            UUID categoriaId,
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stock) {
        this(null, tiendaId, categoriaId, nombre, descripcion, precio, stock, true, Instant.now(), Instant.now(),
                List.of());
    }

    // esta funcion se utilizara para actualizar las imagenes del producto
    public Producto withImagenes(List<ProductoImagen> nuevasImagenes) {
        return new Producto(
                this.id,
                this.tiendaId,
                this.categoriaId,
                this.nombre,
                this.descripcion,
                this.precio,
                this.stock,
                this.activo,
                this.creadoEn,
                Instant.now(),
                nuevasImagenes);
    }

    // esta funcion se utilizara para actualizar el stock del producto
    public Producto withStock(int nuevoStock) {
        return new Producto(
                this.id,
                this.tiendaId,
                this.categoriaId,
                this.nombre,
                this.descripcion,
                this.precio,
                nuevoStock,
                this.activo,
                this.creadoEn,
                Instant.now(),
                this.imagenes);
    }
}
