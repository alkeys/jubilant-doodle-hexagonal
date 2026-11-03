package alex.probando.productos.infra.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import alex.probando.productos.application.port.in.CreateProductoUseCase;
import alex.probando.productos.application.port.in.DeleteProductoUseCase;
import alex.probando.productos.application.port.in.GetProductoByIdUseCase;
import alex.probando.productos.application.port.in.GetProductosPrincipalesUseCase;
import alex.probando.productos.application.port.in.ListProductosUseCase;
import alex.probando.productos.application.port.in.ManageProductoImagenesUseCase;
import alex.probando.productos.application.port.in.ManageProductoInventarioUseCase;
import alex.probando.productos.application.port.in.UpdateProductoUseCase;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoImagenResponse;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoMapper;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoRequest;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoResponse;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoUpdateRequest;
import alex.probando.productos.infra.adapter.in.web.dto.TiendaInventarioRequest;
import alex.probando.productos.infra.adapter.in.web.dto.TiendaInventarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos, inventario por tienda y subida de imágenes")
public class ProductoController {

    private final CreateProductoUseCase createProductoUseCase;
    private final GetProductoByIdUseCase getProductoByIdUseCase;
    private final ListProductosUseCase listProductosUseCase;
    private final GetProductosPrincipalesUseCase getProductosPrincipalesUseCase;
    private final UpdateProductoUseCase updateProductoUseCase;
    private final DeleteProductoUseCase deleteProductoUseCase;
    private final ManageProductoImagenesUseCase manageProductoImagenesUseCase;
    private final ManageProductoInventarioUseCase manageProductoInventarioUseCase;

    @Operation(
        summary = "Listar catálogo de productos",
        description = "Consulta pública del catálogo. Permite filtrar por sucursal (?tiendaId=...), categoría (?categoriaId=...) o búsqueda por texto (?buscar=...)"
    )
    @GetMapping
    public List<ProductoResponse> listProductos(
            @RequestParam(required = false) UUID tiendaId,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "false") boolean incluirInactivos
    ) {
        return listProductosUseCase.listProductos(tiendaId, categoriaId, buscar, incluirInactivos).stream()
                .map(ProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "Obtener productos principales del catálogo (Portada)",
        description = "Consulta pública paginada para la portada de la tienda. Retorna productos únicos consolidados con disponibilidad en tiendas."
    )
    @GetMapping("/principales")
    public List<ProductoResponse> getProductosPrincipales(
            @Parameter(description = "Número de página (0-indexada)", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Cantidad máxima de productos a retornar (por defecto 10, máximo 50)", example = "10")
            @RequestParam(defaultValue = "10") int limite
    ) {
        return getProductosPrincipalesUseCase.getProductosPrincipales(pagina, limite).stream()
                .map(ProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener producto por ID", description = "Consulta pública del detalle de un producto, sus imágenes y su disponibilidad por sucursal")
    @GetMapping("/{id}")
    public ProductoResponse getProductoById(@PathVariable UUID id) {
        Producto producto = getProductoByIdUseCase.getProductoById(id);
        return ProductoMapper.toResponse(producto);
    }

    @Operation(
        summary = "Crear nuevo producto (Solo ADMIN)",
        description = "Da de alta la publicación de un producto en el catálogo asignándolo opcionalmente a una sucursal inicial con su precio y stock."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse createProducto(@RequestBody @Valid ProductoRequest request) {
        Producto producto = ProductoMapper.toDomain(request);
        Producto creado = createProductoUseCase.createProducto(producto);
        return ProductoMapper.toResponse(creado);
    }

    @Operation(summary = "Actualizar datos generales del producto (Solo ADMIN)", description = "Modifica los datos del catálogo general (nombre, descripción, categoría, estado)")
    @PutMapping("/{id}")
    public ProductoResponse updateProducto(
            @Parameter(description = "ID único (UUID) del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestBody @Valid ProductoUpdateRequest request
    ) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();

        Producto actualizado = new Producto(
                id,
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getTalla(),
                activo,
                null,
                null,
                List.of(),
                List.of()
        );

        Producto resultado = updateProductoUseCase.updateProducto(id, actualizado);
        return ProductoMapper.toResponse(resultado);
    }

    @Operation(summary = "Eliminar producto (Solo ADMIN)", description = "Elimina el producto del catálogo, sus inventarios en sucursales y sus imágenes")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducto(@PathVariable UUID id) {
        deleteProductoUseCase.deleteProducto(id);
    }

    @Operation(summary = "Asignar o actualizar precio y stock en una sucursal (Solo ADMIN)", description = "Define el precio y existencias del producto para una tienda específica")
    @PostMapping("/{id}/tiendas")
    public TiendaInventarioResponse asignarInventario(
            @PathVariable UUID id,
            @RequestBody @Valid TiendaInventarioRequest request
    ) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();
        TiendaProducto inventario = new TiendaProducto(
                null,
                request.getTiendaId(),
                null,
                request.getPrecio(),
                request.getStock(),
                activo,
                Instant.now(),
                Instant.now()
        );
        TiendaProducto guardado = manageProductoInventarioUseCase.asignarOActualizarInventario(id, inventario);
        return ProductoMapper.toResponse(guardado);
    }

    @Operation(summary = "Remover producto de una sucursal (Solo ADMIN)", description = "Quita el producto del inventario de una tienda específica")
    @DeleteMapping("/{id}/tiendas/{tiendaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarInventario(
            @PathVariable UUID id,
            @PathVariable UUID tiendaId
    ) {
        manageProductoInventarioUseCase.eliminarInventarioDeTienda(id, tiendaId);
    }

    @Operation(
        summary = "Subir imágenes para un producto (Solo ADMIN)",
        description = "Sube 1 o más fotos al producto. Formatos admitidos: JPEG, PNG, WEBP."
    )
    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductoImagenResponse> uploadImagenes(
            @PathVariable UUID id,
            @RequestPart("imagenes") List<MultipartFile> files
    ) {
        List<ProductoImagen> subidas = manageProductoImagenesUseCase.uploadImagenes(id, files);
        return subidas.stream()
                .map(ProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Eliminar una imagen de un producto (Solo ADMIN)", description = "Elimina una foto específica del producto y su archivo en el almacenamiento")
    @DeleteMapping("/{id}/imagenes/{imagenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImagen(@PathVariable UUID id, @PathVariable UUID imagenId) {
        manageProductoImagenesUseCase.deleteImagen(id, imagenId);
    }

    @Operation(summary = "Marcar una imagen como principal (Solo ADMIN)", description = "Define cuál imagen será la portada destacada del producto")
    @PutMapping("/{id}/imagenes/{imagenId}/principal")
    public ProductoResponse setPrincipalImagen(@PathVariable UUID id, @PathVariable UUID imagenId) {
        Producto producto = manageProductoImagenesUseCase.setImagenPrincipal(id, imagenId);
        return ProductoMapper.toResponse(producto);
    }
}
