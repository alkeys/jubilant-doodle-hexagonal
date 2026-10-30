package alex.probando.ventas.infra.adapter.out.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import alex.probando.ventas.domain.Venta;
import alex.probando.ventas.infra.adapter.out.persistence.jpa.entity.VentaEntity;

@Component
public class VentaJpaMapper {

    public VentaEntity toEntity(Venta domain) {
        if (domain == null) {
            return null;
        }

        VentaEntity entity = new VentaEntity();
        entity.setId(domain.id());
        entity.setClienteId(domain.clienteId());
        entity.setProductoId(domain.productoId());
        entity.setCantidad(domain.cantidad());
        entity.setPrecioUnitario(domain.precioUnitario());
        entity.setTotal(domain.total());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public Venta toDomain(VentaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Venta(
                entity.getId(),
                entity.getClienteId(),
                entity.getProductoId(),
                entity.getCantidad(),
                entity.getPrecioUnitario(),
                entity.getTotal(),
                entity.getCreatedAt()
        );
    }
}
