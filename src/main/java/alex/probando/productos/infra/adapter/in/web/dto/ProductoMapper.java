package alex.probando.productos.infra.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;

public class ProductoMapper {

    public static ProductoResponse toResponse(Producto domain) {
        if (domain == null) {
            return null;
        }

        List<ProductoImagenResponse> imagenesResponse = domain.imagenes() != null
                ? domain.imagenes().stream().map(ProductoMapper::toResponse).collect(Collectors.toList())
                : List.of();

        return new ProductoResponse(
                domain.id() != null ? domain.id().toString() : null,
                domain.tiendaId() != null ? domain.tiendaId().toString() : null,
                domain.categoriaId() != null ? domain.categoriaId().toString() : null,
                domain.nombre(),
                domain.descripcion(),
                domain.precio(),
                domain.stock(),
                domain.activo(),
                imagenesResponse,
                domain.creadoEn() != null ? domain.creadoEn().toString() : null,
                domain.actualizadoEn() != null ? domain.actualizadoEn().toString() : null);
    }

    public static ProductoImagenResponse toResponse(ProductoImagen domain) {
        if (domain == null) {
            return null;
        }
        return new ProductoImagenResponse(
                domain.id() != null ? domain.id().toString() : null,
                domain.url(),
                domain.esPrincipal(),
                domain.orden());
    }

    public static Producto toDomain(ProductoRequest request) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();
        return new Producto(
                null,
                request.getTiendaId(),
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                activo,
                Instant.now(),
                Instant.now(),
                List.of());
    }
}
