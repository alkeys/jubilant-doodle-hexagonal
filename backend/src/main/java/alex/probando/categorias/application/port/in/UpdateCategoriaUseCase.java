package alex.probando.categorias.application.port.in;

import java.util.UUID;

import alex.probando.categorias.domain.Categoria;

public interface UpdateCategoriaUseCase {
    Categoria updateCategoria(UUID id, Categoria categoria);
}
