package alex.probando.ventas.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.shared.exception.ResourceNotFoundException;
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

    private static final int DEFAULT_TOP_LIMIT = 10;
    private static final int MAX_TOP_LIMIT = 50;

    @Override
    public Venta createVenta(UUID clienteId, UUID productoId, int cantidad) {
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

        // 2. Validar existencia y estado del producto
        Producto producto = productoRepositoryPort.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        if (!producto.activo()) {
            throw new IllegalArgumentException(
                    "El producto '" + producto.nombre() + "' no se encuentra activo para la venta");
        }

        // 3. Validar disponibilidad de stock
        if (producto.stock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto '" + producto.nombre()
                    + "'. Stock disponible: " + producto.stock() + ", solicitado: " + cantidad);
        }

        // 4. Descontar stock del producto de forma atómica
        Producto productoActualizado = producto.withStock(producto.stock() - cantidad);
        productoRepositoryPort.save(productoActualizado);

        // 5. Calcular totales a partir del precio oficial actual del producto
        BigDecimal precioUnitario = producto.precio();
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        Venta nuevaVenta = new Venta(
                null,
                clienteId,
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
        if (clienteId != null) {
            return ventaRepositoryPort.findByClienteId(clienteId);
        }
        if (productoId != null) {
            return ventaRepositoryPort.findByProductoId(productoId);
        }
        return ventaRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true) // el transaction sierve para evitar que el metodo no sea atómico osea
                                    // si hay un error no se guarda y si no hay ningun error se guarda y se hace
                                    // un tipo de rolback
    public List<ProductoMasVendido> getProductosMasVendidos(int limite) {
        int limiteEfectivo = limite <= 0 ? DEFAULT_TOP_LIMIT : Math.min(limite, MAX_TOP_LIMIT);
        return ventaRepositoryPort.findTopProductosVendidos(limiteEfectivo);
    }
}
