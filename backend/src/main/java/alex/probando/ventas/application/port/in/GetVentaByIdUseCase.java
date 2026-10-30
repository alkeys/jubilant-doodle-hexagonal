package alex.probando.ventas.application.port.in;

import java.util.UUID;

import alex.probando.ventas.domain.Venta;

public interface GetVentaByIdUseCase {
    Venta getVentaById(UUID id);
}
