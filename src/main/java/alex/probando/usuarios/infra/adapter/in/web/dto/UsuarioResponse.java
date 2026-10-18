
package alex.probando.usuarios.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
    name = "UsuarioResponse",
    description = "Representación de un usuario en la API"
)
@Builder
public record UsuarioResponse(

    @Schema(
        description = "Identificador único (UUID)",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    String id,

    @Schema(
        description = "Nombre del usuario",
        example = "Juan Carlos"
    )
    String nombre,

    @Schema(
        description = "Apellido del usuario",
        example = "Bodoque"
    )
    String apellido,

    @Schema(
        description = "Correo electrónico del usuario",
        example = "juan.bodoque@example.com"
    )
    String email,
    @Schema(
        description = "Edad del usuario",
        example = "30"
    )
    int edad
) {
}
