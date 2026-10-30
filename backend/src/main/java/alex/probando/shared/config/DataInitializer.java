package alex.probando.shared.config;

import java.util.Set;

import alex.probando.usuarios.domain.Role;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.PasswordEncoderPort;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // para que este component se ejecute al iniciar la aplicacion, debemos inyectar
    // los repositorios que vamos a usar
    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void run(String... args) throws Exception {
        createUsers();
    }

    private void createUsers() {
        String username = "Alex123";
        if (usuarioRepository.existsByNombreDeUsuario(username)) {
            return;
        }

        Set<Role> roles = Set.of(new Role(Role.ADMIN));

        Usuario usuario = new Usuario(
                "Alex",
                username,
                "Avilés",
                "alexaviles@gmail.com",
                25,
                true,
                passwordEncoderPort.codificar("12345678"),
                roles);

        usuarioRepository.save(usuario);
    }

}
