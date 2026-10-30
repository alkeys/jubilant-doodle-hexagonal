package alex.probando.ventas.infra.adapter.out.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alex.probando.ventas.infra.adapter.out.persistence.jpa.entity.VentaEntity;

public interface SpringDataVentaRepository extends JpaRepository<VentaEntity, UUID> {

    List<VentaEntity> findByClienteIdOrderByCreatedAtDesc(UUID clienteId);

    List<VentaEntity> findByProductoIdOrderByCreatedAtDesc(UUID productoId);

    List<VentaEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT v.productoId as productoId, SUM(v.cantidad) as totalCantidad, SUM(v.total) as totalRecaudado " +
           "FROM VentaEntity v " +
           "GROUP BY v.productoId " +
           "ORDER BY SUM(v.cantidad) DESC")
    List<ProductoMasVendidoProjection> findTopVendidos(Pageable pageable);
}
