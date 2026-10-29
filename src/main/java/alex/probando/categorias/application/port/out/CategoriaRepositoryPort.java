package alex.probando.categorias.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.categorias.domain.Categoria;

public interface CategoriaRepositoryPort {
    Categoria save(Categoria categoria);
    Optional<Categoria> findById(UUID id);
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findAllActivas();
    List<Categoria> findAll();
    boolean existsByNombre(String nombre);
    boolean existsByNombreAndIdNot(String nombre, UUID id);
    void deleteById(UUID id);
}
