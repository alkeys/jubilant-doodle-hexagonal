package alex.probando.usuarios.application.port.in;

import alex.probando.usuarios.domain.Usuario;

public interface CreateUserCase {
    Usuario createUser(Usuario usuario);

}
