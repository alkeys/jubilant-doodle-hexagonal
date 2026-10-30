package alex.probando.usuarios.infra.adapter.out.persistence.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.RoleEntity;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.UsuarioEntity;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper.UsuarioJpaMapper;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository.SpringDataRoleRepository;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("sql")
public class JpaUsuarioAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;
    private final SpringDataRoleRepository roleRepository;
    private final UsuarioJpaMapper mapper;

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        
        // Asociar roles administrados desde la base de datos
        if (usuario.roles() != null && !usuario.roles().isEmpty()) {
            Set<RoleEntity> managedRoles = new HashSet<>();
            for (var role : usuario.roles()) {
                RoleEntity roleEntity = roleRepository.findByNombre(role.nombre())
                        .orElseGet(() -> roleRepository.save(new RoleEntity(role.nombre())));
                managedRoles.add(roleEntity);
            }
            entity.setRoles(managedRoles);
        }
        
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario) {
        return repository.findByNombreDeUsuario(nombreDeUsuario).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByNombreDeUsuario(String nombreDeUsuario) {
        return repository.existsByNombreDeUsuario(nombreDeUsuario);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
