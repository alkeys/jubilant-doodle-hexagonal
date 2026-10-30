package alex.probando.categorias.application.port.in;

import java.util.UUID;

import alex.probando.categorias.domain.Categoria;

public interface GetCategoriaByIdUseCase {
    Categoria getCategoriaById(UUID id);
}
