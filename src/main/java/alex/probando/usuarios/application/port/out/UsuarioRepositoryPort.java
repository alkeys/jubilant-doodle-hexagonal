package alex.probando.usuarios.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.usuarios.domain.Usuario;

public interface UsuarioRepositoryPort {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAll();
    boolean existsByEmail(String email);
    boolean existsByNombreDeUsuario(String nombreDeUsuario);
    void deleteById(UUID id);
}
