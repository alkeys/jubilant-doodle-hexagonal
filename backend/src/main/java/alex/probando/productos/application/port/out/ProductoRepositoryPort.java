package alex.probando.productos.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;

public interface ProductoRepositoryPort {
    Producto save(Producto producto);
    Optional<Producto> findById(UUID id);
    List<Producto> findAllActivos();
    List<Producto> findAll();
    List<Producto> findByTiendaId(UUID tiendaId);
    List<Producto> findByCategoriaId(UUID categoriaId);
    List<Producto> buscarPorNombre(String query);
    List<Producto> findProductosPrincipales(int pagina, int limite);
    void deleteById(UUID id);

    ProductoImagen saveImagen(UUID productoId, ProductoImagen imagen);
    void deleteImagen(UUID productoId, UUID imagenId);
    Optional<ProductoImagen> findImagenById(UUID imagenId);

    TiendaProducto saveInventario(UUID productoId, TiendaProducto inventario);
    void deleteInventario(UUID productoId, UUID tiendaId);
    Optional<TiendaProducto> findInventario(UUID productoId, UUID tiendaId);
}
