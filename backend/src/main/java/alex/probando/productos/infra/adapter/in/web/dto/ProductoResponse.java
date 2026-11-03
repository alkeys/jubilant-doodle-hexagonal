package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "ProductoResponse", description = "Información completa de un producto del catálogo con su disponibilidad multitienda")
@Builder
public record ProductoResponse(
    @Schema(description = "Identificador único (UUID) del producto en el catálogo", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "ID de la categoría a la que pertenece", example = "b2c3d4e5-e89b-12d3-a456-426614174000")
    String categoriaId,

    @Schema(description = "Nombre del producto", example = "Camiseta Oversize Algodón Premium")
    String nombre,

    @Schema(description = "Descripción detallada del producto", example = "Playera de corte holgado confeccionada en algodón peinado")
    String descripcion,

    @Schema(description = "Tallas disponibles del producto", example = "XS, S, M, L, XL")
    String talla,

    @Schema(description = "Precio base/referencial (mínimo disponible entre las tiendas activas)", example = "24.99")
    BigDecimal precio,

    @Schema(description = "Precio mínimo entre las sucursales donde se vende", example = "24.99")
    BigDecimal precioMin,

    @Schema(description = "Precio máximo entre las sucursales donde se vende", example = "29.99")
    BigDecimal precioMax,

    @Schema(description = "Cantidad de existencias totales sumadas de todas las sucursales", example = "45")
    int stock,

    @Schema(description = "Total acumulado de stock en todas las sucursales", example = "45")
    int stockTotal,

    @Schema(description = "Estado de publicación general para clientes", example = "true")
    boolean activo,

    @Schema(description = "Lista de imágenes asociadas a la ficha del producto", example = "[]")
    List<ProductoImagenResponse> imagenes,

    @Schema(description = "Sucursales donde el producto se encuentra disponible con su precio y stock particular", example = "[]")
    List<TiendaInventarioResponse> tiendas,

    @Schema(description = "Fecha de creación ISO-8601", example = "2026-10-28T20:00:00Z")
    String creadoEn,

    @Schema(description = "Fecha de última modificación ISO-8601", example = "2026-10-28T22:00:00Z")
    String actualizadoEn
) {
}
