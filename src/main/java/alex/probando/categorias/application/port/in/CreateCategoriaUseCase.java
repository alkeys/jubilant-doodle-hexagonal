package alex.probando.categorias.application.port.in;

import alex.probando.categorias.domain.Categoria;

public interface CreateCategoriaUseCase {
    Categoria createCategoria(Categoria categoria);
}
