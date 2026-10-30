package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para actualizar un producto existente")
public class ProductoUpdateRequest {

    @Schema(description = "ID de la sucursal de la empresa que vende el producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID tiendaId;

    @Schema(description = "ID de la categoría asignada", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID categoriaId;

    @Schema(description = "Nombre del producto", example = "Croissant de Mantequilla Especial")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Croissant francés horneado a diario con mantequilla extra")
    private String descripcion;

    @Schema(description = "Precio del producto", example = "2.75")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Schema(description = "Cantidad de existencias (stock)", example = "60")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Estado de publicación", example = "true")
    private Boolean activo;
}
