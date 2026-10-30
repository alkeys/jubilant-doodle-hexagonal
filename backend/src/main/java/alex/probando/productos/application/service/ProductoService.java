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
import alex.probando.productos.application.port.in.UpdateProductoUseCase;
import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
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
        ManageProductoImagenesUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final TiendaRepositoryPort tiendaRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final FileStoragePort fileStoragePort;

    private static final int DEFAULT_LIMITE_PAGINA = 10;
    private static final int MAX_LIMITE_PAGINA = 50; // Límite máximo para proteger la base de datos

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

        // Validar existencia de la tienda / sucursal
        if (tiendaRepositoryPort.findById(producto.tiendaId()).isEmpty()) {
            throw new ResourceNotFoundException("La sucursal indicada no existe con ID: " + producto.tiendaId());
        }

        // Validar existencia de la categoría
        if (categoriaRepositoryPort.findById(producto.categoriaId()).isEmpty()) {
            throw new ResourceNotFoundException("La categoría indicada no existe con ID: " + producto.categoriaId());
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
                .filter(p -> tiendaId == null || p.tiendaId().equals(tiendaId))
                .filter(p -> categoriaId == null || p.categoriaId().equals(categoriaId))
                .filter(p -> buscar == null || buscar.isBlank() 
                        || p.nombre().toLowerCase().contains(buscar.toLowerCase().trim())
                        || (p.descripcion() != null && p.descripcion().toLowerCase().contains(buscar.toLowerCase().trim())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> getProductosPrincipales(int pagina, int limite) {
        // Protección contra desbordamiento y saturación de la base de datos
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

        if (actualizado.tiendaId() != null && !actualizado.tiendaId().equals(existente.tiendaId())) {
            if (tiendaRepositoryPort.findById(actualizado.tiendaId()).isEmpty()) {
                throw new ResourceNotFoundException("La sucursal indicada no existe con ID: " + actualizado.tiendaId());
            }
        }

        if (actualizado.categoriaId() != null && !actualizado.categoriaId().equals(existente.categoriaId())) {
            if (categoriaRepositoryPort.findById(actualizado.categoriaId()).isEmpty()) {
                throw new ResourceNotFoundException("La categoría indicada no existe con ID: " + actualizado.categoriaId());
            }
        }

        Producto nuevo = new Producto(
                existente.id(),
                actualizado.tiendaId() != null ? actualizado.tiendaId() : existente.tiendaId(),
                actualizado.categoriaId() != null ? actualizado.categoriaId() : existente.categoriaId(),
                actualizado.nombre() != null && !actualizado.nombre().isBlank() ? actualizado.nombre() : existente.nombre(),
                actualizado.descripcion() != null ? actualizado.descripcion() : existente.descripcion(),
                actualizado.precio() != null ? actualizado.precio() : existente.precio(),
                actualizado.stock() >= 0 ? actualizado.stock() : existente.stock(),
                actualizado.activo(),
                existente.creadoEn(),
                Instant.now(),
                existente.imagenes()
        );

        return productoRepositoryPort.save(nuevo);
    }

    @Override
    public void deleteProducto(UUID id) {
        Producto existente = getProductoById(id);
        
        // Eliminar archivos físicos asociados
        for (ProductoImagen img : existente.imagenes()) {
            fileStoragePort.deleteFile(img.url());
        }

        productoRepositoryPort.deleteById(id);
    }

    @Override
    public List<ProductoImagen> uploadImagenes(UUID productoId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una imagen");
        }

        Producto producto = getProductoById(productoId);
        List<ProductoImagen> nuevasImagenes = new ArrayList<>();

        int ordenInicio = producto.imagenes().size();
        boolean yaTienePrincipal = producto.imagenes().stream().anyMatch(ProductoImagen::esPrincipal);

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                continue;
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Formato no soportado para " + file.getOriginalFilename() + ". Solo se admiten JPEG, PNG y WEBP");
            }

            String urlPublica = fileStoragePort.storeFile(file, "productos");
            boolean esPrincipal = (!yaTienePrincipal && i == 0);
            if (esPrincipal) {
                yaTienePrincipal = true;
            }

            ProductoImagen imagen = new ProductoImagen(
                    null,
                    urlPublica,
                    esPrincipal,
                    ordenInicio + i,
                    Instant.now()
            );

            ProductoImagen guardada = productoRepositoryPort.saveImagen(productoId, imagen);
            nuevasImagenes.add(guardada != null ? guardada : imagen);
        }

        return nuevasImagenes;
    }

    @Override
    public void deleteImagen(UUID productoId, UUID imagenId) {
        Producto producto = getProductoById(productoId);
        ProductoImagen imagen = producto.imagenes().stream()
                .filter(img -> img.id().equals(imagenId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));

        fileStoragePort.deleteFile(imagen.url());
        productoRepositoryPort.deleteImagen(productoId, imagenId);
    }

    @Override
    public Producto setImagenPrincipal(UUID productoId, UUID imagenId) {
        Producto producto = getProductoById(productoId);
        boolean existe = producto.imagenes().stream().anyMatch(img -> img.id().equals(imagenId));
        if (!existe) {
            throw new ResourceNotFoundException("La imagen " + imagenId + " no pertenece al producto " + productoId);
        }

        List<ProductoImagen> actualizadas = producto.imagenes().stream()
                .map(img -> new ProductoImagen(
                        img.id(),
                        img.url(),
                        img.id().equals(imagenId), // Solo la seleccionada es true
                        img.orden(),
                        img.creadoEn()
                ))
                .collect(Collectors.toList());

        Producto conPrincipalActualizada = producto.withImagenes(actualizadas);
        return productoRepositoryPort.save(conPrincipalActualizada);
    }
}
