package alex.probando.productos.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record Producto(
        UUID id,
        UUID categoriaId,
        String nombre,
        String descripcion,
        String talla,
        boolean activo,
        Instant creadoEn,
        Instant actualizadoEn,
        List<ProductoImagen> imagenes,
        List<TiendaProducto> inventarios) {

    public Producto {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if (categoriaId == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría asignada");
        }
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = Instant.now();
        }
        imagenes = imagenes != null ? List.copyOf(imagenes) : List.of();
        inventarios = inventarios != null ? List.copyOf(inventarios) : List.of();
    }

    public Producto(
            UUID id,
            UUID categoriaId,
            String nombre,
            String descripcion,
            boolean activo,
            Instant creadoEn,
            Instant actualizadoEn,
            List<ProductoImagen> imagenes,
            List<TiendaProducto> inventarios) {
        this(id, categoriaId, nombre, descripcion, null, activo, creadoEn, actualizadoEn, imagenes, inventarios);
    }

    public Producto(
            UUID categoriaId,
            String nombre,
            String descripcion) {
        this(null, categoriaId, nombre, descripcion, null, true, Instant.now(), Instant.now(),
                List.of(), List.of());
    }

    public BigDecimal precioMinimo() {
        return inventarios.stream()
                .filter(TiendaProducto::activo)
                .map(TiendaProducto::precio)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal precioMaximo() {
        return inventarios.stream()
                .filter(TiendaProducto::activo)
                .map(TiendaProducto::precio)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public int stockTotal() {
        return inventarios.stream()
                .filter(TiendaProducto::activo)
                .mapToInt(TiendaProducto::stock)
                .sum();
    }

    public Optional<TiendaProducto> obtenerInventario(UUID tiendaId) {
        if (tiendaId == null) return Optional.empty();
        return inventarios.stream()
                .filter(i -> tiendaId.equals(i.tiendaId()))
                .findFirst();
    }

    public Producto withImagenes(List<ProductoImagen> nuevasImagenes) {
        return new Producto(
                this.id,
                this.categoriaId,
                this.nombre,
                this.descripcion,
                this.talla,
                this.activo,
                this.creadoEn,
                Instant.now(),
                nuevasImagenes,
                this.inventarios);
    }

    public Producto withInventarios(List<TiendaProducto> nuevosInventarios) {
        return new Producto(
                this.id,
                this.categoriaId,
                this.nombre,
                this.descripcion,
                this.talla,
                this.activo,
                this.creadoEn,
                Instant.now(),
                this.imagenes,
                nuevosInventarios);
    }
}
