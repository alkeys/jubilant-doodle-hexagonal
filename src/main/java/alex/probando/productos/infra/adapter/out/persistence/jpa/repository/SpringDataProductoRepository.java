package alex.probando.productos.infra.adapter.out.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;

public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, UUID> {
    List<ProductoEntity> findByActivoTrue();
    Page<ProductoEntity> findByActivoTrueOrderByCreadoEnDesc(Pageable pageable);
    List<ProductoEntity> findByTiendaId(UUID tiendaId);
    List<ProductoEntity> findByCategoriaId(UUID categoriaId);

    @Query("SELECT p FROM ProductoEntity p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ProductoEntity> searchByNombre(@Param("query") String query);
}
