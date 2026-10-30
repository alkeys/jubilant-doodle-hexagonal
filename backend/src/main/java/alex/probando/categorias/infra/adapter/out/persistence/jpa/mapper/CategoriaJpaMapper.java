package alex.probando.categorias.infra.adapter.out.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import alex.probando.categorias.domain.Categoria;
import alex.probando.categorias.infra.adapter.out.persistence.jpa.entity.CategoriaEntity;

@Component
public class CategoriaJpaMapper {

    public CategoriaEntity toEntity(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(categoria.id());
        entity.setNombre(categoria.nombre());
        entity.setDescripcion(categoria.descripcion());
        entity.setActivo(categoria.activo());
        return entity;
    }

    public Categoria toDomain(CategoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Categoria(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActivo()
        );
    }
}
