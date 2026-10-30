package alex.probando.productos.application.port.in;

import java.util.List;
import java.util.UUID;

import alex.probando.productos.domain.Producto;

public interface ListProductosUseCase {
    List<Producto> listProductos(UUID tiendaId, UUID categoriaId, String buscar, boolean incluirInactivos);
}
