package alex.probando.tiendas.application.port.in;

import java.util.UUID;

import alex.probando.tiendas.domain.Tienda;

public interface UpdateTiendaUseCase {
    Tienda updateTienda(UUID id, Tienda tiendaActualizada);
}
