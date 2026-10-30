package alex.probando.ventas.infra.adapter.in.web;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import alex.probando.ventas.application.port.in.CreateVentaUseCase;
import alex.probando.ventas.application.port.in.GetProductosMasVendidosUseCase;
import alex.probando.ventas.application.port.in.GetVentaByIdUseCase;
import alex.probando.ventas.application.port.in.ListVentasUseCase;
import alex.probando.ventas.domain.Venta;
import alex.probando.ventas.infra.adapter.in.web.dto.ProductoMasVendidoResponse;
import alex.probando.ventas.infra.adapter.in.web.dto.VentaMapper;
import alex.probando.ventas.infra.adapter.in.web.dto.VentaRequest;
import alex.probando.ventas.infra.adapter.in.web.dto.VentaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Gestión de compras, registro de ventas y reportes analíticos de productos más vendidos")
public class VentaController {

    private final CreateVentaUseCase createVentaUseCase;
    private final GetVentaByIdUseCase getVentaByIdUseCase;
    private final ListVentasUseCase listVentasUseCase;
    private final GetProductosMasVendidosUseCase getProductosMasVendidosUseCase;

    @Operation(
        summary = "Registrar una nueva venta",
        description = "Registra una compra validando la existencia del cliente, la disponibilidad del producto y descontando automáticamente las existencias del stock."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrarVenta(@Valid @RequestBody VentaRequest request) {
        Venta venta = createVentaUseCase.createVenta(
                request.getClienteId(),
                request.getProductoId(),
                request.getCantidad()
        );
        return VentaMapper.toResponse(venta);
    }

    @Operation(
        summary = "Consultar productos más vendidos (Top ventas)",
        description = "Retorna el ranking de los productos con mayor volumen de unidades vendidas y el total recaudado acumulado."
    )
    @GetMapping("/top-productos")
    public List<ProductoMasVendidoResponse> getProductosMasVendidos(
            @Parameter(description = "Número máximo de productos en el ranking (1 a 50)", example = "10")
            @RequestParam(defaultValue = "10") int limite
    ) {
        return getProductosMasVendidosUseCase.getProductosMasVendidos(limite).stream()
                .map(VentaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "Listar historial de ventas",
        description = "Obtiene el listado histórico de ventas. Permite filtrar opcionalmente por cliente o por producto."
    )
    @GetMapping
    public List<VentaResponse> listVentas(
            @Parameter(description = "Filtrar por ID único del cliente", example = "22222222-2222-2222-2222-222222222222")
            @RequestParam(required = false) UUID clienteId,
            @Parameter(description = "Filtrar por ID único del producto", example = "55555555-5555-5555-5555-555555555551")
            @RequestParam(required = false) UUID productoId
    ) {
        return listVentasUseCase.listVentas(clienteId, productoId).stream()
                .map(VentaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "Obtener detalle de una venta por ID",
        description = "Recupera la información completa de una transacción de venta mediante su identificador único."
    )
    @GetMapping("/{id}")
    public VentaResponse getVentaById(
            @Parameter(description = "Identificador único (UUID) de la venta", example = "77777777-7777-7777-7777-777777777771")
            @PathVariable UUID id
    ) {
        return VentaMapper.toResponse(getVentaByIdUseCase.getVentaById(id));
    }
}
