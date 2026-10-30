package alex.probando.ventas.infra.adapter.in.web.dto;

import alex.probando.ventas.domain.ProductoMasVendido;
import alex.probando.ventas.domain.Venta;

public final class VentaMapper {

    private VentaMapper() {
    }

    public static VentaResponse toResponse(Venta domain) {
        if (domain == null) {
            return null;
        }

        return VentaResponse.builder()
                .id(domain.id())
                .clienteId(domain.clienteId())
                .productoId(domain.productoId())
                .cantidad(domain.cantidad())
                .precioUnitario(domain.precioUnitario())
                .total(domain.total())
                .createdAt(domain.createdAt() != null ? domain.createdAt().toString() : null)
                .build();
    }

    public static ProductoMasVendidoResponse toResponse(ProductoMasVendido domain) {
        if (domain == null) {
            return null;
        }

        return ProductoMasVendidoResponse.builder()
                .productoId(domain.productoId())
                .nombreProducto(domain.nombreProducto())
                .totalCantidadVendida(domain.totalCantidadVendida())
                .totalRecaudado(domain.totalRecaudado())
                .build();
    }
}
