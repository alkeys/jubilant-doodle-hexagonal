package alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.UsuarioEntity;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByNombreDeUsuario(String nombreDeUsuario);
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNombreDeUsuario(String nombreDeUsuario);
}
