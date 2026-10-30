package alex.probando.ventas.infra.adapter.in.web.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud para registrar una nueva venta")
public class VentaRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID único del cliente que realiza la compra", example = "22222222-2222-2222-2222-222222222222")
    private UUID clienteId;

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID único del producto que se va a comprar", example = "55555555-5555-5555-5555-555555555551")
    private UUID productoId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de unidades a comprar", example = "2")
    private int cantidad;
}
