package alex.probando.productos.infra.adapter.out.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoImagenEntity;

public interface SpringDataProductoImagenRepository extends JpaRepository<ProductoImagenEntity, UUID> {
    List<ProductoImagenEntity> findByProductoIdOrderByOrdenAsc(UUID productoId);
}
