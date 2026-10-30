package alex.probando.usuarios.infra.adapter.in.web.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para asignar o modificar roles de un usuario")
public class AssignRolesRequest {

    @Schema(description = "Conjunto de roles a asignar (ej: ADMIN, RH, CLIENTE)", example = "[\"ADMIN\", \"RH\"]")
    @NotEmpty(message = "Debe proporcionar al menos un rol")
    private Set<String> roles;
}
