package alex.probando.categorias.application.port.in;

import java.util.List;

import alex.probando.categorias.domain.Categoria;

public interface ListCategoriasUseCase {
    List<Categoria> listCategoriasActivas();
    List<Categoria> listTodasCategorias();
}
