package alex.probando.usuarios.application.service;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.usuarios.application.port.in.CreateUserCase;
import alex.probando.usuarios.application.port.out.PasswordEncoderPort;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;

@Service
@Transactional
public class UsuarioService implements CreateUserCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final int MIN_PASSWORD_LENGTH = 8;
    private final PasswordEncoderPort passwordEncoderPort;

    public UsuarioService(UsuarioRepositoryPort usuarioRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort; 
    }

    @Override
    public Usuario createUser(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }else if (usuario.email() == null || usuario.email().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo o vacío");
        } else if (usuario.password() == null || usuario.password().isEmpty()) {
            throw new IllegalArgumentException("La contraseña del usuario no puede ser nula o vacía");
        }
        if(usuario.password().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña del usuario debe tener al menos 8 caracteres");
        }

        // Codificar la contraseña antes de guardarla
        String encodedPassword = passwordEncoderPort.codificar(usuario.password());
        usuario = new Usuario(
            usuario.id(),
            usuario.nombre(),
            usuario.NombreDeUsuario(),  
            usuario.apellido(),
            usuario.email(),
            usuario.edad(),
            usuario.activo(),
            encodedPassword
        );
        return usuarioRepositoryPort.save(usuario);

    }

   

  

}
