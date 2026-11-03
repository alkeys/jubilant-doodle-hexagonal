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
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import alex.probando.tiendas.domain.Tienda;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.ventas.application.port.out.VentaRepositoryPort;
import alex.probando.ventas.domain.Venta;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepositoryPort ventaRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private TiendaRepositoryPort tiendaRepositoryPort;

    private VentaService ventaService;

    private UUID clienteId;
    private UUID tiendaId;
    private UUID productoId;
    private Usuario clienteActivo;
    private Producto productoEnStock;
    private Tienda tiendaActiva;

    @BeforeEach
    void setUp() {
        ventaService = new VentaService(
                ventaRepositoryPort,
                productoRepositoryPort,
                usuarioRepositoryPort,
                tiendaRepositoryPort
        );

        clienteId = UUID.randomUUID();
        tiendaId = UUID.randomUUID();
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

        tiendaActiva = new Tienda(
                tiendaId,
                "Sucursal Central",
                "Panadería",
                "Av. 1",
                true,
                Instant.now()
        );

        TiendaProducto inventario = new TiendaProducto(
                UUID.randomUUID(),
                tiendaId,
                "Sucursal Central",
                new BigDecimal("2.50"),
                10,
                true,
                Instant.now(),
                Instant.now()
        );

        productoEnStock = new Producto(
                productoId,
                UUID.randomUUID(),
                "Baguette",
                "Crujiente",
                true,
                Instant.now(),
                Instant.now(),
                List.of(),
                List.of(inventario)
        );
    }

    @Test
    @DisplayName("Debe registrar venta y descontar stock exitosamente de la sucursal")
    void debeRegistrarVentaExitosamente() {
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(tiendaRepositoryPort.findById(tiendaId)).thenReturn(Optional.of(tiendaActiva));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoEnStock));

        Venta ventaSimulada = new Venta(
                UUID.randomUUID(),
                clienteId,
                tiendaId,
                productoId,
                3,
                new BigDecimal("2.50"),
                new BigDecimal("7.50"),
                Instant.now()
        );
        when(ventaRepositoryPort.save(any(Venta.class))).thenReturn(ventaSimulada);

        Venta resultado = ventaService.createVenta(clienteId, tiendaId, productoId, 3);

        assertNotNull(resultado);
        assertEquals(3, resultado.cantidad());
        assertEquals(new BigDecimal("7.50"), resultado.total());

        verify(productoRepositoryPort).saveInventario(any(), org.mockito.ArgumentMatchers.argThat(inv -> inv.stock() == 7));
        verify(ventaRepositoryPort).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe fallar si el stock disponible en la sucursal es menor al solicitado")
    void debeFallarSiStockEsInsuficiente() {
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(tiendaRepositoryPort.findById(tiendaId)).thenReturn(Optional.of(tiendaActiva));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoEnStock));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.createVenta(clienteId, tiendaId, productoId, 15)
        );

        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }

    @Test
    @DisplayName("Debe fallar si el producto no está disponible en la sucursal indicada")
    void debeFallarSiProductoNoEstaEnTienda() {
        UUID otraTienda = UUID.randomUUID();
        when(usuarioRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteActivo));
        when(tiendaRepositoryPort.findById(otraTienda)).thenReturn(Optional.of(tiendaActiva));
        when(productoRepositoryPort.findById(productoId)).thenReturn(Optional.of(productoEnStock));

        assertThrows(
                alex.probando.shared.exception.ResourceNotFoundException.class,
                () -> ventaService.createVenta(clienteId, otraTienda, productoId, 1)
        );
    }
}
