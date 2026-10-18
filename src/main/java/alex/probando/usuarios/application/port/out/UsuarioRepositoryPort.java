package alex.probando.usuarios.application.port.out;

import alex.probando.usuarios.domain.Usuario;

public interface UsuarioRepositoryPort {
    Usuario save(Usuario usuario);
    
}
