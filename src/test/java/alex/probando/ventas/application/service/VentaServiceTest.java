package alex.probando.ventas.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.ventas.application.port.out.VentaRepositoryPort;
import alex.probando.ventas.domain.ProductoMasVendido;
import alex.probando.ventas.domain.Venta;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepositoryPort ventaRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private VentaService ventaService;

    private UUID clienteId;
    private UUID productoId;
    private Usuario clienteActivo;
    private Producto productoEnStock;

    @BeforeEach
    void setUp() {
        ventaService = new VentaService(
                ventaRepositoryPort,
                productoRepositoryPort,
                usuarioRepositoryPort
        );

        clienteId = UUID.randomUUID();
        productoId = UUID.randomUUID();

        clienteActivo = new Usuario(
                clienteId,
                "María",
                "mariaperez",
                "Pérez",
                "maria@example.com",
                25,
                true,
                "pass123",
                Set.of(new Role(Role.CLIENTE))
        );

        productoEnStock = new Producto(
                productoId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Baguette",
                "Crujiente",
                new BigDecimal("2.50"),
                10,
                true,
                Instant.now(),
                Instant.now(),
                List.of()
        );
    }

    @Test
    @DisplayName("Debe registrar venta y descontar stock exitosamente")
    void debeRegistrarVentaExitosamente() {
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoEnStock));

        Venta ventaSimulada = new Venta(
                UUID.randomUUID(),
                clienteId,
                productoId,
                3,
                new BigDecimal("2.50"),
                new BigDecimal("7.50"),
                Instant.now()
        );
        when(ventaRepositoryPort.save(any(Venta.class))).thenReturn(ventaSimulada);

        Venta resultado = ventaService.createVenta(clienteId, productoId, 3);

        assertNotNull(resultado);
        assertEquals(3, resultado.cantidad());
        assertEquals(new BigDecimal("7.50"), resultado.total());

        // Verificar que el stock se descontó (10 - 3 = 7)
        verify(productoRepositoryPort).save(org.mockito.ArgumentMatchers.argThat(p -> p.stock() == 7));
        verify(ventaRepositoryPort).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe fallar si el stock disponible es menor al solicitado")
    void debeFallarSiStockEsInsuficiente() {
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoEnStock));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.createVenta(clienteId, productoId, 15)
        );

        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }

    @Test
    @DisplayName("Debe fallar si el cliente no existe")
    void debeFallarSiClienteNoExiste() {
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ventaService.createVenta(clienteId, productoId, 1)
        );
    }

    @Test
    @DisplayName("Debe fallar si el cliente está inactivo")
    void debeFallarSiClienteInactivo() {
        Usuario clienteInactivo = new Usuario(
                clienteId,
                "Inactivo",
                "inactivo123",
                "User",
                "inactivo@example.com",
                30,
                false, // Inactivo
                "pass",
                Set.of(new Role(Role.CLIENTE))
        );
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteInactivo));

        assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.createVenta(clienteId, productoId, 1)
        );
    }

    @Test
    @DisplayName("Debe fallar si el producto está inactivo")
    void debeFallarSiProductoInactivo() {
        Producto productoInactivo = new Producto(
                productoId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Pan Viejo",
                "",
                new BigDecimal("1.00"),
                5,
                false, // Inactivo
                Instant.now(),
                Instant.now(),
                List.of()
        );

        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoInactivo));

        assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.createVenta(clienteId, productoId, 1)
        );
    }

    @Test
    @DisplayName("Debe consultar ranking de productos más vendidos con límite seguro")
    void debeConsultarTopProductosMasVendidos() {
        List<ProductoMasVendido> ranking = List.of(
                new ProductoMasVendido(productoId, "Baguette", 50L, new BigDecimal("125.00"))
        );
        when(ventaRepositoryPort.findTopProductosVendidos(5)).thenReturn(ranking);

        List<ProductoMasVendido> resultado = ventaService.getProductosMasVendidos(5);

        assertEquals(1, resultado.size());
        assertEquals("Baguette", resultado.get(0).nombreProducto());
        verify(ventaRepositoryPort).findTopProductosVendidos(5);
    }
}
