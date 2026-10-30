package alex.probando.usuarios.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Solicitud para actualizar los datos de un usuario")
public class UsuarioUpdateRequest {

    @Schema(description = "Nombre del usuario", example = "Juan Carlos")
    @NotBlank(message = "El nombre no puede ser nulo o vacío")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Bodoque")
    @NotBlank(message = "El apellido no puede ser nulo o vacío")
    private String apellido;

    @Schema(description = "Correo electrónico del usuario", example = "juan.bodoque@example.com")
    @NotBlank(message = "El email no puede ser nulo o vacío")
    private String email;

    @Schema(description = "Nombre de usuario", example = "juanbodoque")
    @NotBlank(message = "El nombre de usuario no puede ser nulo o vacío")
    private String nombreDeUsuario;

    @Schema(description = "Edad del usuario", example = "31")
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @Schema(description = "Estado activo del usuario", example = "true")
    private Boolean activo = true;

    @Schema(description = "Nueva contraseña (opcional)", example = "NuevaContraseñaSegura123")
    private String password;
}
