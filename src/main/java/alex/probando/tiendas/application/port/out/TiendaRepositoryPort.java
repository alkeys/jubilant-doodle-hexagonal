package alex.probando.tiendas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.tiendas.domain.Tienda;

public interface TiendaRepositoryPort {
    Tienda save(Tienda tienda);
    Optional<Tienda> findById(UUID id);
    List<Tienda> findAllActivas();
    List<Tienda> findAll();
    void deleteById(UUID id);
}
