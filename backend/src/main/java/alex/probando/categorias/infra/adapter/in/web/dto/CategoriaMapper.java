package alex.probando.categorias.infra.adapter.in.web.dto;

import alex.probando.categorias.domain.Categoria;

public class CategoriaMapper {

    public static CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaResponse(
                categoria.id() != null ? categoria.id().toString() : null,
                categoria.nombre(),
                categoria.descripcion(),
                categoria.activo()
        );
    }

    public static Categoria toDomain(CategoriaRequest request) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();
        return new Categoria(
                null,
                request.getNombre(),
                request.getDescripcion(),
                activo
        );
    }
}
