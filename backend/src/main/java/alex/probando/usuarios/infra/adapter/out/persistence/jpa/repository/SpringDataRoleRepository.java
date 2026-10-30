package alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.RoleEntity;

public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByNombre(String nombre);
}
