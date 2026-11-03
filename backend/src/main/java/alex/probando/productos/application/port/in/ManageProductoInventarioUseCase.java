package alex.probando.productos.application.port.in;

import java.util.UUID;

import alex.probando.productos.domain.TiendaProducto;

public interface ManageProductoInventarioUseCase {
    TiendaProducto asignarOActualizarInventario(UUID productoId, TiendaProducto inventario);
    void eliminarInventarioDeTienda(UUID productoId, UUID tiendaId);
}
