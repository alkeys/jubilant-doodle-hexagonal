package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para asignar o actualizar el precio y existencias de un producto en una sucursal")
public class TiendaInventarioRequest {

    @Schema(description = "ID de la sucursal", example = "33333333-3333-3333-3333-333333333331")
    @NotNull(message = "El ID de la tienda es obligatorio")
    private UUID tiendaId;

    @Schema(description = "Precio del producto en esta sucursal", example = "2.50")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Schema(description = "Cantidad de existencias en esta sucursal", example = "50")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Estado de disponibilidad en la sucursal (true por defecto)", example = "true")
    private Boolean activo;
}
