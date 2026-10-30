package alex.probando.categorias.infra.adapter.out.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.categorias.infra.adapter.out.persistence.jpa.entity.CategoriaEntity;

public interface SpringDataCategoriaRepository extends JpaRepository<CategoriaEntity, UUID> {
    List<CategoriaEntity> findByActivoTrue();
    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);
}
