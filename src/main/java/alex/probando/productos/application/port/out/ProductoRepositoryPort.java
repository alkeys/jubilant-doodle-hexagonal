package alex.probando.productos.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;

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

    void saveImagen(UUID productoId, ProductoImagen imagen);
    void deleteImagen(UUID productoId, UUID imagenId);
    Optional<ProductoImagen> findImagenById(UUID imagenId);
}
