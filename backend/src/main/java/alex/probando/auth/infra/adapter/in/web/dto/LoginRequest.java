package alex.probando.auth.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para inicio de sesión")
public class LoginRequest {

    @NotBlank(message = "El nombre de usuario o email es obligatorio")
    @Schema(description = "Nombre de usuario o email registrado", example = "juanbodoque")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "MiContraseñaSegura123")
    private String password;
}
