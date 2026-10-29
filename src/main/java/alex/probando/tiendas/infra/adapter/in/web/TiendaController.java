package alex.probando.tiendas.infra.adapter.in.web;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import alex.probando.tiendas.application.port.in.CreateTiendaUseCase;
import alex.probando.tiendas.application.port.in.DeleteTiendaUseCase;
import alex.probando.tiendas.application.port.in.GetTiendaByIdUseCase;
import alex.probando.tiendas.application.port.in.ListTiendasUseCase;
import alex.probando.tiendas.application.port.in.UpdateTiendaUseCase;
import alex.probando.tiendas.domain.Tienda;
import alex.probando.tiendas.infra.adapter.in.web.dto.TiendaMapper;
import alex.probando.tiendas.infra.adapter.in.web.dto.TiendaRequest;
import alex.probando.tiendas.infra.adapter.in.web.dto.TiendaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tiendas")
@RequiredArgsConstructor
@Tag(name = "Tiendas y Sucursales", description = "Gestión centralizada de las sucursales y puntos de venta de la cadena comercial")
public class TiendaController {

    private final CreateTiendaUseCase createTiendaUseCase;
    private final GetTiendaByIdUseCase getTiendaByIdUseCase;
    private final ListTiendasUseCase listTiendasUseCase;
    private final UpdateTiendaUseCase updateTiendaUseCase;
    private final DeleteTiendaUseCase deleteTiendaUseCase;

    @Operation(summary = "Listar sucursales de la cadena", description = "Endpoint público para consultar las sucursales y puntos de venta activos de la tienda")
    @GetMapping
    public List<TiendaResponse> listTiendasActivas() {
        return listTiendasUseCase.listTiendasActivas().stream()
                .map(TiendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener sucursal por ID", description = "Consulta pública del detalle de una sucursal física o punto de venta por su UUID")
    @GetMapping("/{id}")
    public TiendaResponse getTiendaById(@PathVariable UUID id) {
        Tienda tienda = getTiendaByIdUseCase.getTiendaById(id);
        return TiendaMapper.toResponse(tienda);
    }

    @Operation(summary = "Crear nueva sucursal (Solo ADMIN)", description = "Permite a los administradores de la cadena dar de alta una nueva sucursal de la empresa")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TiendaResponse createTienda(@RequestBody @Valid TiendaRequest request) {
        Tienda tienda = TiendaMapper.toDomain(request);
        Tienda creada = createTiendaUseCase.createTienda(tienda);
        return TiendaMapper.toResponse(creada);
    }

    @Operation(summary = "Actualizar datos de sucursal (Solo ADMIN)", description = "Modifica la información o dirección de una sucursal existente de la cadena")
    @PutMapping("/{id}")
    public TiendaResponse updateTienda(
            @PathVariable UUID id,
            @RequestBody @Valid TiendaRequest request
    ) {
        Tienda tiendaActualizada = new Tienda(request.getNombre(), request.getDescripcion(), request.getDireccion());
        Tienda resultado = updateTiendaUseCase.updateTienda(id, tiendaActualizada);
        return TiendaMapper.toResponse(resultado);
    }

    @Operation(summary = "Dar de baja sucursal (Solo ADMIN)", description = "Elimina o retira una sucursal de la cadena")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTienda(@PathVariable UUID id) {
        deleteTiendaUseCase.deleteTienda(id);
    }
}
