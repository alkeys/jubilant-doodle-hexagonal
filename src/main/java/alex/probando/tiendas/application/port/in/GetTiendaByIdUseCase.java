package alex.probando.tiendas.application.port.in;

import java.util.UUID;

import alex.probando.tiendas.domain.Tienda;

public interface GetTiendaByIdUseCase {
    Tienda getTiendaById(UUID id);
}
