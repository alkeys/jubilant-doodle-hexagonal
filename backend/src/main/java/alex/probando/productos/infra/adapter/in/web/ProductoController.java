package alex.probando.productos.infra.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import alex.probando.productos.application.port.in.UpdateProductoUseCase;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoImagenResponse;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoMapper;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoRequest;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoResponse;
import alex.probando.productos.infra.adapter.in.web.dto.ProductoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos y subida de imágenes para la tienda comercial")
public class ProductoController {

    private final CreateProductoUseCase createProductoUseCase;
    private final GetProductoByIdUseCase getProductoByIdUseCase;
    private final ListProductosUseCase listProductosUseCase;
    private final GetProductosPrincipalesUseCase getProductosPrincipalesUseCase;
    private final UpdateProductoUseCase updateProductoUseCase;
    private final DeleteProductoUseCase deleteProductoUseCase;
    private final ManageProductoImagenesUseCase manageProductoImagenesUseCase;

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
        description = "Consulta pública paginada para la portada de la tienda. " +
                      "Aplica un rango controlado para no sobrecargar la base de datos (límite máximo permitido: 50 productos)."
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

    @Operation(summary = "Obtener producto por ID", description = "Consulta pública del detalle de un producto y todas sus imágenes asociadas")
    @GetMapping("/{id}")
    public ProductoResponse getProductoById(@PathVariable UUID id) {
        Producto producto = getProductoByIdUseCase.getProductoById(id);
        return ProductoMapper.toResponse(producto);
    }

    @Operation(
        summary = "Crear nuevo producto (Solo ADMIN)",
        description = "Da de alta la publicación de un producto en el catálogo asignándolo a una sucursal y categoría. " +
                      "Retorna el producto creado con su identificador único (id), el cual se utiliza como ID de la publicación para adjuntarle fotos en POST /{id}/imagenes."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse createProducto(@RequestBody @Valid ProductoRequest request) {
        Producto producto = ProductoMapper.toDomain(request);
        Producto creado = createProductoUseCase.createProducto(producto);
        return ProductoMapper.toResponse(creado);
    }

    @Operation(summary = "Actualizar producto (Solo ADMIN)", description = "Modifica los datos generales, precio o stock de un producto existente")
    @PutMapping("/{id}")
    public ProductoResponse updateProducto(
            @Parameter(description = "ID único (UUID) de la publicación del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestBody @Valid ProductoUpdateRequest request
    ) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();
        BigDecimal precio = request.getPrecio() != null ? request.getPrecio() : BigDecimal.ZERO;
        int stock = request.getStock() != null ? request.getStock() : 0;

        Producto actualizado = new Producto(
                id,
                request.getTiendaId(),
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                precio,
                stock,
                activo,
                null,
                null,
                List.of()
        );

        Producto resultado = updateProductoUseCase.updateProducto(id, actualizado);
        return ProductoMapper.toResponse(resultado);
    }

    @Operation(summary = "Eliminar producto (Solo ADMIN)", description = "Elimina la publicación del producto y todas sus imágenes físicas del servidor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducto(
            @Parameter(description = "ID único (UUID) de la publicación del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {
        deleteProductoUseCase.deleteProducto(id);
    }

    @Operation(
        summary = "Subir imágenes para la publicación del producto (Solo ADMIN)",
        description = "Permite subir múltiples fotos para la publicación (mínimo recomendado: 3 imágenes). " +
                      "El parámetro '{id}' en la URL es el identificador (UUID) de la publicación del producto al que pertenecen estas fotos. " +
                      "Formatos admitidos: JPEG, PNG, WEBP."
    )
    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductoImagenResponse> uploadImagenes(
            @Parameter(description = "ID único (UUID) de la publicación del producto donde se adjuntarán las fotos", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @Parameter(description = "Lista de archivos de fotos a subir")
            @RequestPart("imagenes") List<MultipartFile> files
    ) {
        List<ProductoImagen> subidas = manageProductoImagenesUseCase.uploadImagenes(id, files);
        return subidas.stream()
                .map(ProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Eliminar una imagen de un producto (Solo ADMIN)", description = "Elimina la foto seleccionada de la publicación y remueve el archivo físico del disco")
    @DeleteMapping("/{id}/imagenes/{imagenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImagen(
            @Parameter(description = "ID único (UUID) de la publicación del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @Parameter(description = "ID único (UUID) de la imagen a eliminar", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7")
            @PathVariable UUID imagenId
    ) {
        manageProductoImagenesUseCase.deleteImagen(id, imagenId);
    }

    @Operation(summary = "Establecer imagen como principal (Solo ADMIN)", description = "Configura cuál de las fotos asociadas a la publicación será la portada o foto principal")
    @PatchMapping("/{id}/imagenes/{imagenId}/principal")
    public ProductoResponse setImagenPrincipal(
            @Parameter(description = "ID único (UUID) de la publicación del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @Parameter(description = "ID único (UUID) de la imagen que será la nueva portada", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7")
            @PathVariable UUID imagenId
    ) {
        Producto producto = manageProductoImagenesUseCase.setImagenPrincipal(id, imagenId);
        return ProductoMapper.toResponse(producto);
    }
}
