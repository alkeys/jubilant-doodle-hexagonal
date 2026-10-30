package alex.probando.tiendas.infra.adapter.out.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import alex.probando.tiendas.domain.Tienda;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.entity.TiendaEntity;

@Component
public class TiendaJpaMapper {

    public TiendaEntity toEntity(Tienda tienda) {
        if (tienda == null) {
            return null;
        }
        TiendaEntity entity = new TiendaEntity();
        entity.setId(tienda.id());
        entity.setNombre(tienda.nombre());
        entity.setDescripcion(tienda.descripcion());
        entity.setDireccion(tienda.direccion());
        entity.setActivo(tienda.activo());
        if (tienda.creadoEn() != null) {
            entity.setCreadoEn(tienda.creadoEn());
        }
        return entity;
    }

    public Tienda toDomain(TiendaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Tienda(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getDireccion(),
                entity.isActivo(),
                entity.getCreadoEn()
        );
    }
}
