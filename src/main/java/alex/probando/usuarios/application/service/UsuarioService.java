package alex.probando.usuarios.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.usuarios.application.port.in.CreateUserCase;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;

@Service
@Transactional
public class UsuarioService implements CreateUserCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public Usuario createUser(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        return usuarioRepositoryPort.save(usuario);
    }
}
