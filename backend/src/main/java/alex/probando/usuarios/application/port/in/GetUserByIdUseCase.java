package alex.probando.usuarios.application.port.in;

import java.util.UUID;

import alex.probando.usuarios.domain.Usuario;

public interface GetUserByIdUseCase {
    Usuario getUserById(UUID id);
}
