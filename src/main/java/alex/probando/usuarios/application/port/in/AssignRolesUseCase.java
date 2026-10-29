package alex.probando.usuarios.application.port.in;

import java.util.Set;
import java.util.UUID;

import alex.probando.usuarios.domain.Usuario;

public interface AssignRolesUseCase {
    Usuario assignRoles(UUID id, Set<String> roles);
}
