package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para registrar un nuevo producto")
public class ProductoRequest {

    @Schema(description = "ID de la sucursal de la empresa que vende el producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @NotNull(message = "El ID de la tienda es obligatorio")
    private UUID tiendaId;

    @Schema(description = "ID de la categoría asignada", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @NotNull(message = "El ID de la categoría es obligatorio")
    private UUID categoriaId;

    @Schema(description = "Nombre del producto", example = "Croissant de Mantequilla")
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Croissant francés horneado a diario")
    private String descripcion;

    @Schema(description = "Precio del producto (mínimo 0.00)", example = "2.50")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Schema(description = "Cantidad de existencias (stock)", example = "50")
    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Estado de publicación inicial (opcional, true por defecto)", example = "true")
    private Boolean activo;
}
