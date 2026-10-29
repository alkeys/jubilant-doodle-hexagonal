package alex.probando.categorias.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.categorias.application.port.in.CreateCategoriaUseCase;
import alex.probando.categorias.application.port.in.DeleteCategoriaUseCase;
import alex.probando.categorias.application.port.in.GetCategoriaByIdUseCase;
import alex.probando.categorias.application.port.in.ListCategoriasUseCase;
import alex.probando.categorias.application.port.in.UpdateCategoriaUseCase;
import alex.probando.categorias.application.port.out.CategoriaRepositoryPort;
import alex.probando.categorias.domain.Categoria;
import alex.probando.shared.exception.DuplicateResourceException;
import alex.probando.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaService implements
        CreateCategoriaUseCase,
        GetCategoriaByIdUseCase,
        ListCategoriasUseCase,
        UpdateCategoriaUseCase,
        DeleteCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    @Override
    public Categoria createCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        if (categoria.nombre() == null || categoria.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo o vacío");
        }

        if (categoriaRepositoryPort.existsByNombre(categoria.nombre())) {
            throw new DuplicateResourceException("Ya existe una categoría registrada con el nombre: " + categoria.nombre());
        }

        return categoriaRepositoryPort.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria getCategoriaById(UUID id) {
        return categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listCategoriasActivas() {
        return categoriaRepositoryPort.findAllActivas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listTodasCategorias() {
        return categoriaRepositoryPort.findAll();
    }

    @Override
    public Categoria updateCategoria(UUID id, Categoria categoriaActualizada) {
        Categoria existente = getCategoriaById(id);

        if (categoriaActualizada.nombre() != null && !categoriaActualizada.nombre().isBlank()) {
            if (categoriaRepositoryPort.existsByNombreAndIdNot(categoriaActualizada.nombre(), id)) {
                throw new DuplicateResourceException("Ya existe otra categoría con el nombre: " + categoriaActualizada.nombre());
            }
        }

        Categoria nueva = new Categoria(
                existente.id(),
                categoriaActualizada.nombre() != null && !categoriaActualizada.nombre().isBlank() ? categoriaActualizada.nombre() : existente.nombre(),
                categoriaActualizada.descripcion() != null ? categoriaActualizada.descripcion() : existente.descripcion(),
                categoriaActualizada.activo()
        );

        return categoriaRepositoryPort.save(nueva);
    }

    @Override
    public void deleteCategoria(UUID id) {
        if (!categoriaRepositoryPort.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepositoryPort.deleteById(id);
    }
}
