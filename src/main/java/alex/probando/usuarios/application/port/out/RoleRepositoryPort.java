package alex.probando.usuarios.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.usuarios.domain.Role;

public interface RoleRepositoryPort {
    Role save(Role role);
    Optional<Role> findById(UUID id);
    Optional<Role> findByNombre(String nombre);
    List<Role> findAll();
}
