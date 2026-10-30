package alex.probando.productos.application.port.in;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;

public interface ManageProductoImagenesUseCase {
    List<ProductoImagen> uploadImagenes(UUID productoId, List<MultipartFile> files);
    void deleteImagen(UUID productoId, UUID imagenId);
    Producto setImagenPrincipal(UUID productoId, UUID imagenId);
}
