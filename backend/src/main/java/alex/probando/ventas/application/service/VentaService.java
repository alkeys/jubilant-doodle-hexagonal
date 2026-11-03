package alex.probando.ventas.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.ventas.application.port.in.CreateVentaUseCase;
import alex.probando.ventas.application.port.in.GetProductosMasVendidosUseCase;
import alex.probando.ventas.application.port.in.GetVentaByIdUseCase;
import alex.probando.ventas.application.port.in.ListVentasUseCase;
import alex.probando.ventas.application.port.out.VentaRepositoryPort;
import alex.probando.ventas.domain.ProductoMasVendido;
import alex.probando.ventas.domain.Venta;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VentaService implements
        CreateVentaUseCase,
        GetVentaByIdUseCase,
        ListVentasUseCase,
        GetProductosMasVendidosUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TiendaRepositoryPort tiendaRepositoryPort;

    private static final int DEFAULT_TOP_LIMIT = 10;
    private static final int MAX_TOP_LIMIT = 50;

    @Override
    public Venta createVenta(UUID clienteId, UUID tiendaId, UUID productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // 1. Validar existencia y estado del cliente
        Usuario cliente = usuarioRepositoryPort.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        if (!cliente.activo()) {
            throw new IllegalArgumentException(
                    "El cliente con ID " + clienteId + " está inactivo y no puede realizar compras");
        }

        // 2. Validar existencia de la sucursal/tienda
        if (tiendaRepositoryPort.findById(tiendaId).isEmpty()) {
            throw new ResourceNotFoundException("La sucursal no existe con ID: " + tiendaId);
        }

        // 3. Validar existencia y estado del producto
        Producto producto = productoRepositoryPort.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        if (!producto.activo()) {
            throw new IllegalArgumentException(
                    "El producto '" + producto.nombre() + "' no se encuentra activo para la venta");
        }

        // 4. Obtener y validar disponibilidad en la sucursal especificada
        TiendaProducto inventario = producto.obtenerInventario(tiendaId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto '" + producto.nombre()
                        + "' no está disponible para la venta en la sucursal indicada"));

        if (!inventario.activo()) {
            throw new IllegalArgumentException("El producto '" + producto.nombre()
                    + "' está desactivado en la sucursal seleccionada");
        }

        // 5. Validar disponibilidad de stock en dicha sucursal
        if (inventario.stock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en esta sucursal para el producto '" + producto.nombre()
                    + "'. Stock disponible: " + inventario.stock() + ", solicitado: " + cantidad);
        }

        // 6. Descontar stock de la sucursal
        TiendaProducto inventarioActualizado = inventario.withStock(inventario.stock() - cantidad);
        productoRepositoryPort.saveInventario(productoId, inventarioActualizado);

        // 7. Calcular totales a partir del precio unitario específico de esta tienda
        BigDecimal precioUnitario = inventario.precio();
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        Venta nuevaVenta = new Venta(
                null,
                clienteId,
                tiendaId,
                productoId,
                cantidad,
                precioUnitario,
                total,
                Instant.now());

        return ventaRepositoryPort.save(nuevaVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta getVentaById(UUID id) {
        return ventaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listVentas(UUID clienteId, UUID productoId) {
        return ventaRepositoryPort.findAll().stream()
                .filter(v -> clienteId == null || v.clienteId().equals(clienteId))
                .filter(v -> productoId == null || v.productoId().equals(productoId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendido> getProductosMasVendidos(int limite) {
        int limiteEfectivo = limite <= 0 ? DEFAULT_TOP_LIMIT : Math.min(limite, MAX_TOP_LIMIT);
        return ventaRepositoryPort.findTopProductosVendidos(limiteEfectivo);
    }
}
