package alex.probando.productos.application.port.in;

import java.util.List;

import alex.probando.productos.domain.Producto;

public interface GetProductosPrincipalesUseCase {
    /**
     * Obtiene los productos principales y destacados activos para la portada o catálogo principal,
     * aplicando límites de rango para optimizar el rendimiento y proteger la base de datos.
     *
     * @param pagina Número de página (0-indexada)
     * @param limite Cantidad de productos a retornar (con tope máximo)
     * @return Lista de productos principales con sus imágenes asociadas
     */
    List<Producto> getProductosPrincipales(int pagina, int limite);
}
