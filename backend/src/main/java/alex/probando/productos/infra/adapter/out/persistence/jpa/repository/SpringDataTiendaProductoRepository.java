package alex.probando.productos.infra.adapter.out.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.TiendaProductoEntity;

public interface SpringDataTiendaProductoRepository extends JpaRepository<TiendaProductoEntity, UUID> {
    Optional<TiendaProductoEntity> findByProductoIdAndTiendaId(UUID productoId, UUID tiendaId);
    void deleteByProductoIdAndTiendaId(UUID productoId, UUID tiendaId);
}
