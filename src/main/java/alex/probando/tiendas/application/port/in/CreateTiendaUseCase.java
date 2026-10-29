package alex.probando.tiendas.application.port.in;

import alex.probando.tiendas.domain.Tienda;

public interface CreateTiendaUseCase {
    Tienda createTienda(Tienda tienda);
}
