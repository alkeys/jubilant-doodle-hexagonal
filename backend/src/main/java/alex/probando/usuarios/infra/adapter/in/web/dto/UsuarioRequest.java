package alex.probando.usuarios.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Solicitud para el registro y creación de un nuevo usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @Schema(description = "Nombre del usuario", example = "Juan Carlos")
    @NotBlank(message = "El nombre no puede ser nulo, vacío o contener solo espacios")
    private String nombre;
    @Schema(description = "Apellido del usuario", example = "Bodoque")
    @NotBlank(message = "El apellido no puede ser nulo, vacío o contener solo espacios")
    private String apellido;
    @Schema(description = "Correo electrónico del usuario", example = "juan.bodoque@example.com")
    @NotBlank(message = "El email no puede ser nulo, vacío o contener solo espacios")
    private String email;
    @Schema(description = "Edad del usuario", example = "30")
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;
    @Schema(description = "Nombre de usuario único", example = "juanbodoque")
    @NotBlank(message = "El nombre de usuario no puede ser nulo, vacío o contener solo espacios")
    private String nombreDeUsuario;
    @Schema(description = "Contraseña del usuario", example = "MiContraseñaSegura123")
    @NotBlank(message = "La contraseña no puede ser nula, vacía o contener solo espacios")
    private String password;
}
