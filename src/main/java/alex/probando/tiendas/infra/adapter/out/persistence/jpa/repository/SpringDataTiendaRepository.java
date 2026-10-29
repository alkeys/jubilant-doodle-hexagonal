package alex.probando.tiendas.infra.adapter.out.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import alex.probando.tiendas.infra.adapter.out.persistence.jpa.entity.TiendaEntity;

public interface SpringDataTiendaRepository extends JpaRepository<TiendaEntity, UUID> {
    List<TiendaEntity> findByActivoTrue();
}
