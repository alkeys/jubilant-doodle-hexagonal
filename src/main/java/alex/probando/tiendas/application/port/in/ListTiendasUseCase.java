package alex.probando.tiendas.application.port.in;

import java.util.List;

import alex.probando.tiendas.domain.Tienda;

public interface ListTiendasUseCase {
    List<Tienda> listTiendasActivas();
}
