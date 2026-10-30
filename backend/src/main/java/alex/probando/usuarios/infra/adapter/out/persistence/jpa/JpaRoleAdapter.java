package alex.probando.usuarios.infra.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.RoleRepositoryPort;
import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.RoleEntity;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository.SpringDataRoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaRoleAdapter implements RoleRepositoryPort {

    private final SpringDataRoleRepository repository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = new RoleEntity(role.id(), role.nombre());
        RoleEntity saved = repository.save(entity);
        return new Role(saved.getId(), saved.getNombre());
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return repository.findById(id)
                .map(entity -> new Role(entity.getId(), entity.getNombre()));
    }

    @Override
    public Optional<Role> findByNombre(String nombre) {
        return repository.findByNombre(nombre)
                .map(entity -> new Role(entity.getId(), entity.getNombre()));
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll().stream()
                .map(entity -> new Role(entity.getId(), entity.getNombre()))
                .collect(Collectors.toList());
    }
}
