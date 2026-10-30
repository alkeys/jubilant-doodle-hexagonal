package alex.probando.auth.infra.adapter.in.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación exitosa")
public class AuthResponse {

    @Schema(description = "Mensaje informativo", example = "Inicio de sesión exitoso. Cookie configurada.")
    private String mensaje;

    @Schema(description = "Token JWT (para usar en Swagger 'Authorize', Postman o clientes móviles)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de autorización", example = "Bearer")
    private String tipoToken;

    @Schema(description = "Nombre de usuario", example = "juanbodoque")
    private String username;

    @Schema(description = "Roles asignados", example = "[\"ROLE_CLIENTE\"]")
    private List<String> roles;
}
