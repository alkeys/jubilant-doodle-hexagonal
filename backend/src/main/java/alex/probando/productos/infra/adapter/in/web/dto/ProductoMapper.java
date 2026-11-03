package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;

public class ProductoMapper {

    public static ProductoResponse toResponse(Producto domain) {
        if (domain == null) {
            return null;
        }

        List<ProductoImagenResponse> imagenesResponse = domain.imagenes() != null
                ? domain.imagenes().stream().map(ProductoMapper::toResponse).collect(Collectors.toList())
                : List.of();

        List<TiendaInventarioResponse> tiendasResponse = domain.inventarios() != null
                ? domain.inventarios().stream().map(ProductoMapper::toResponse).collect(Collectors.toList())
                : List.of();

        BigDecimal precioMin = domain.precioMinimo();
        BigDecimal precioMax = domain.precioMaximo();
        int stockTotal = domain.stockTotal();

        return new ProductoResponse(
                domain.id() != null ? domain.id().toString() : null,
                domain.categoriaId() != null ? domain.categoriaId().toString() : null,
                domain.nombre(),
                domain.descripcion(),
                domain.talla(),
                precioMin,
                precioMin,
                precioMax,
                stockTotal,
                stockTotal,
                domain.activo(),
                imagenesResponse,
                tiendasResponse,
                domain.creadoEn() != null ? domain.creadoEn().toString() : null,
                domain.actualizadoEn() != null ? domain.actualizadoEn().toString() : null);
    }

    public static TiendaInventarioResponse toResponse(TiendaProducto domain) {
        if (domain == null) {
            return null;
        }
        return new TiendaInventarioResponse(
                domain.id() != null ? domain.id().toString() : null,
                domain.tiendaId() != null ? domain.tiendaId().toString() : null,
                domain.tiendaNombre(),
                domain.precio(),
                domain.stock(),
                domain.activo());
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
        List<TiendaProducto> inventariosIniciales = List.of();
        if (request.getTiendaId() != null && request.getPrecio() != null) {
            int stock = request.getStock() != null ? request.getStock() : 0;
            inventariosIniciales = List.of(new TiendaProducto(
                    null,
                    request.getTiendaId(),
                    null,
                    request.getPrecio(),
                    stock,
                    activo,
                    Instant.now(),
                    Instant.now()));
        }

        return new Producto(
                null,
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getTalla(),
                activo,
                Instant.now(),
                Instant.now(),
                List.of(),
                inventariosIniciales);
    }
}
