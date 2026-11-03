package alex.probando.productos.application.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import alex.probando.categorias.application.port.out.CategoriaRepositoryPort;
import alex.probando.productos.application.port.in.CreateProductoUseCase;
import alex.probando.productos.application.port.in.DeleteProductoUseCase;
import alex.probando.productos.application.port.in.GetProductoByIdUseCase;
import alex.probando.productos.application.port.in.GetProductosPrincipalesUseCase;
import alex.probando.productos.application.port.in.ListProductosUseCase;
import alex.probando.productos.application.port.in.ManageProductoImagenesUseCase;
import alex.probando.productos.application.port.in.ManageProductoInventarioUseCase;
import alex.probando.productos.application.port.in.UpdateProductoUseCase;
import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.shared.storage.port.FileStoragePort;
import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductoService implements
        CreateProductoUseCase,
        GetProductoByIdUseCase,
        ListProductosUseCase,
        GetProductosPrincipalesUseCase,
        UpdateProductoUseCase,
        DeleteProductoUseCase,
        ManageProductoImagenesUseCase,
        ManageProductoInventarioUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final TiendaRepositoryPort tiendaRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final FileStoragePort fileStoragePort;

    private static final int DEFAULT_LIMITE_PAGINA = 10;
    private static final int MAX_LIMITE_PAGINA = 50;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/jpg"
    );

    @Override
    public Producto createProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        // Validar existencia de la categoría
        if (categoriaRepositoryPort.findById(producto.categoriaId()).isEmpty()) {
            throw new ResourceNotFoundException("La categoría indicada no existe con ID: " + producto.categoriaId());
        }

        // Validar sucursales de inventario inicial si se proporcionan
        if (producto.inventarios() != null) {
            for (TiendaProducto inv : producto.inventarios()) {
                if (tiendaRepositoryPort.findById(inv.tiendaId()).isEmpty()) {
                    throw new ResourceNotFoundException("La sucursal indicada no existe con ID: " + inv.tiendaId());
                }
            }
        }

        return productoRepositoryPort.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto getProductoById(UUID id) {
        return productoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listProductos(UUID tiendaId, UUID categoriaId, String buscar, boolean incluirInactivos) {
        List<Producto> lista = incluirInactivos 
                ? productoRepositoryPort.findAll() 
                : productoRepositoryPort.findAllActivos();

        return lista.stream()
                .filter(p -> tiendaId == null || p.inventarios().stream().anyMatch(i -> i.tiendaId().equals(tiendaId) && i.activo()))
                .filter(p -> categoriaId == null || p.categoriaId().equals(categoriaId))
                .filter(p -> buscar == null || buscar.isBlank() 
                        || p.nombre().toLowerCase().contains(buscar.toLowerCase().trim())
                        || (p.descripcion() != null && p.descripcion().toLowerCase().contains(buscar.toLowerCase().trim())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> getProductosPrincipales(int pagina, int limite) {
        if (pagina < 0) {
            pagina = 0;
        }
        if (limite <= 0) {
            limite = DEFAULT_LIMITE_PAGINA;
        } else if (limite > MAX_LIMITE_PAGINA) {
            limite = MAX_LIMITE_PAGINA;
        }

        return productoRepositoryPort.findProductosPrincipales(pagina, limite);
    }

    @Override
    public Producto updateProducto(UUID id, Producto actualizado) {
        Producto existente = getProductoById(id);

        if (actualizado.categoriaId() != null && !actualizado.categoriaId().equals(existente.categoriaId())) {
            if (categoriaRepositoryPort.findById(actualizado.categoriaId()).isEmpty()) {
                throw new ResourceNotFoundException("La categoría indicada no existe con ID: " + actualizado.categoriaId());
            }
        }

        Producto nuevo = new Producto(
                existente.id(),
                actualizado.categoriaId() != null ? actualizado.categoriaId() : existente.categoriaId(),
                actualizado.nombre() != null && !actualizado.nombre().isBlank() ? actualizado.nombre() : existente.nombre(),
                actualizado.descripcion() != null ? actualizado.descripcion() : existente.descripcion(),
                actualizado.talla() != null ? actualizado.talla() : existente.talla(),
                actualizado.activo(),
                existente.creadoEn(),
                Instant.now(),
                existente.imagenes(),
                existente.inventarios()
        );

        return productoRepositoryPort.save(nuevo);
    }

    @Override
    public void deleteProducto(UUID id) {
        Producto existente = getProductoById(id);
        
        for (ProductoImagen img : existente.imagenes()) {
            fileStoragePort.deleteFile(img.url());
        }

        productoRepositoryPort.deleteById(id);
    }

    @Override
    public TiendaProducto asignarOActualizarInventario(UUID productoId, TiendaProducto inventario) {
        if (inventario == null) {
            throw new IllegalArgumentException("El inventario no puede ser nulo");
        }
        getProductoById(productoId);
        if (tiendaRepositoryPort.findById(inventario.tiendaId()).isEmpty()) {
            throw new ResourceNotFoundException("La sucursal indicada no existe con ID: " + inventario.tiendaId());
        }

        return productoRepositoryPort.saveInventario(productoId, inventario);
    }

    @Override
    public void eliminarInventarioDeTienda(UUID productoId, UUID tiendaId) {
        getProductoById(productoId);
        productoRepositoryPort.deleteInventario(productoId, tiendaId);
    }

    @Override
    public List<ProductoImagen> uploadImagenes(UUID productoId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una imagen");
        }

        Producto producto = getProductoById(productoId);

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Uno de los archivos subidos se encuentra vacío");
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Formato no soportado: " + contentType + ". Formatos válidos: JPEG, PNG, WEBP");
            }
        }

        int siguienteOrden = producto.imagenes().stream()
                .mapToInt(ProductoImagen::orden)
                .max()
                .orElse(0) + 1;

        boolean yaTienePrincipal = producto.imagenes().stream()
                .anyMatch(ProductoImagen::esPrincipal);

        List<ProductoImagen> subidas = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String url = fileStoragePort.storeFile(file, "productos/" + productoId);

            boolean principal = !yaTienePrincipal && (i == 0);
            ProductoImagen nuevaImagen = new ProductoImagen(
                    null,
                    url,
                    principal,
                    siguienteOrden + i,
                    Instant.now()
            );

            ProductoImagen guardada = productoRepositoryPort.saveImagen(productoId, nuevaImagen);
            subidas.add(guardada);
        }

        return subidas;
    }

    @Override
    public void deleteImagen(UUID productoId, UUID imagenId) {
        Producto producto = getProductoById(productoId);

        ProductoImagen imagen = producto.imagenes().stream()
                .filter(img -> imagenId.equals(img.id()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));

        fileStoragePort.deleteFile(imagen.url());
        productoRepositoryPort.deleteImagen(productoId, imagenId);

        if (imagen.esPrincipal()) {
            List<ProductoImagen> restantes = producto.imagenes().stream()
                    .filter(img -> !imagenId.equals(img.id()))
                    .collect(Collectors.toList());

            if (!restantes.isEmpty()) {
                ProductoImagen nuevaPrincipal = restantes.get(0).withPrincipal(true);
                productoRepositoryPort.saveImagen(productoId, nuevaPrincipal);
            }
        }
    }

    @Override
    public Producto setImagenPrincipal(UUID productoId, UUID imagenId) {
        Producto producto = getProductoById(productoId);

        boolean existe = producto.imagenes().stream()
                .anyMatch(img -> imagenId.equals(img.id()));

        if (!existe) {
            throw new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId);
        }

        for (ProductoImagen img : producto.imagenes()) {
            boolean esEsta = imagenId.equals(img.id());
            if (img.esPrincipal() != esEsta) {
                ProductoImagen actualizada = img.withPrincipal(esEsta);
                productoRepositoryPort.saveImagen(productoId, actualizada);
            }
        }

        return getProductoById(productoId);
    }
}
