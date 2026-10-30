package alex.probando.categorias.infra.adapter.in.web;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import alex.probando.categorias.application.port.in.CreateCategoriaUseCase;
import alex.probando.categorias.application.port.in.DeleteCategoriaUseCase;
import alex.probando.categorias.application.port.in.GetCategoriaByIdUseCase;
import alex.probando.categorias.application.port.in.ListCategoriasUseCase;
import alex.probando.categorias.application.port.in.UpdateCategoriaUseCase;
import alex.probando.categorias.domain.Categoria;
import alex.probando.categorias.infra.adapter.in.web.dto.CategoriaMapper;
import alex.probando.categorias.infra.adapter.in.web.dto.CategoriaRequest;
import alex.probando.categorias.infra.adapter.in.web.dto.CategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión del catálogo de categorías para clasificación de productos de la tienda")
public class CategoriaController {

    private final CreateCategoriaUseCase createCategoriaUseCase;
    private final GetCategoriaByIdUseCase getCategoriaByIdUseCase;
    private final ListCategoriasUseCase listCategoriasUseCase;
    private final UpdateCategoriaUseCase updateCategoriaUseCase;
    private final DeleteCategoriaUseCase deleteCategoriaUseCase;

    @Operation(
        summary = "Listar categorías de productos",
        description = "Consulta pública del catálogo de categorías activas para navegar los productos de la tienda (o todas con ?todas=true si eres admin)"
    )
    @GetMapping
    public List<CategoriaResponse> listCategorias(@RequestParam(defaultValue = "false") boolean todas) {
        List<Categoria> categorias = todas 
                ? listCategoriasUseCase.listTodasCategorias() 
                : listCategoriasUseCase.listCategoriasActivas();

        return categorias.stream()
                .map(CategoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener categoría por ID", description = "Consulta pública del detalle de una categoría por su UUID")
    @GetMapping("/{id}")
    public CategoriaResponse getCategoriaById(@PathVariable UUID id) {
        Categoria categoria = getCategoriaByIdUseCase.getCategoriaById(id);
        return CategoriaMapper.toResponse(categoria);
    }

    @Operation(summary = "Crear nueva categoría (Solo ADMIN)", description = "Permite a los administradores registrar una nueva categoría de productos")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse createCategoria(@RequestBody @Valid CategoriaRequest request) {
        Categoria categoria = CategoriaMapper.toDomain(request);
        Categoria creada = createCategoriaUseCase.createCategoria(categoria);
        return CategoriaMapper.toResponse(creada);
    }

    @Operation(summary = "Actualizar categoría (Solo ADMIN)", description = "Permite modificar el nombre, descripción o estado de una categoría existente")
    @PutMapping("/{id}")
    public CategoriaResponse updateCategoria(
            @PathVariable UUID id,
            @RequestBody @Valid CategoriaRequest request
    ) {
        boolean activo = request.getActivo() == null ? true : request.getActivo();
        Categoria categoriaActualizada = new Categoria(id, request.getNombre(), request.getDescripcion(), activo);
        Categoria resultado = updateCategoriaUseCase.updateCategoria(id, categoriaActualizada);
        return CategoriaMapper.toResponse(resultado);
    }

    @Operation(summary = "Eliminar categoría (Solo ADMIN)", description = "Elimina una categoría del sistema por su UUID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoria(@PathVariable UUID id) {
        deleteCategoriaUseCase.deleteCategoria(id);
    }
}
