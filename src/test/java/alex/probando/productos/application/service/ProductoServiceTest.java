package alex.probando.productos.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import alex.probando.categorias.application.port.out.CategoriaRepositoryPort;
import alex.probando.categorias.domain.Categoria;
import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.shared.storage.port.FileStoragePort;
import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import alex.probando.tiendas.domain.Tienda;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private TiendaRepositoryPort tiendaRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @Mock
    private FileStoragePort fileStoragePort;

    private ProductoService productoService;

    private UUID tiendaId;
    private UUID categoriaId;

    @BeforeEach
    void setUp() {
        productoService = new ProductoService(
                productoRepositoryPort,
                tiendaRepositoryPort,
                categoriaRepositoryPort,
                fileStoragePort
        );
        tiendaId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Debe crear producto exitosamente cuando tienda y categoría existen")
    void debeCrearProductoExitosamente() {
        Producto nuevo = new Producto(tiendaId, categoriaId, "Baguette", "Pan francés clásico", new BigDecimal("1.50"), 20);

        when(tiendaRepositoryPort.findById(tiendaId)).thenReturn(Optional.of(new Tienda("Central", "", "")));
        when(categoriaRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(new Categoria("Panes", "")));
        when(productoRepositoryPort.save(nuevo)).thenReturn(nuevo);

        Producto resultado = productoService.createProducto(nuevo);

        assertNotNull(resultado);
        assertEquals("Baguette", resultado.nombre());
        verify(productoRepositoryPort).save(nuevo);
    }

    @Test
    @DisplayName("Debe fallar al crear si la tienda no existe")
    void debeFallarSiTiendaNoExiste() {
        Producto nuevo = new Producto(tiendaId, categoriaId, "Baguette", "Pan francés clásico", new BigDecimal("1.50"), 20);

        when(tiendaRepositoryPort.findById(tiendaId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.createProducto(nuevo));
    }

    @Test
    @DisplayName("Debe subir múltiples imágenes y asignar la primera como principal")
    void debeSubirMultiplesImagenes() {
        UUID prodId = UUID.randomUUID();
        Producto prodExistente = new Producto(prodId, tiendaId, categoriaId, "Baguette", "", new BigDecimal("1.50"), 10, true, null, null, List.of());

        when(productoRepositoryPort.findById(prodId)).thenReturn(Optional.of(prodExistente));
        when(fileStoragePort.storeFile(any(), eq("productos"))).thenReturn("/uploads/productos/test.jpg");

        MockMultipartFile file1 = new MockMultipartFile("imagenes", "foto1.jpg", "image/jpeg", "bytes1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("imagenes", "foto2.png", "image/png", "bytes2".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("imagenes", "foto3.webp", "image/webp", "bytes3".getBytes());

        List<ProductoImagen> subidas = productoService.uploadImagenes(prodId, List.of(file1, file2, file3));

        assertEquals(3, subidas.size());
        assertTrue(subidas.get(0).esPrincipal());
        assertFalse(subidas.get(1).esPrincipal());
        assertFalse(subidas.get(2).esPrincipal());
    }

    @Test
    @DisplayName("Debe rechazar archivos con formato no permitido")
    void debeRechazarFormatoNoPermitido() {
        UUID prodId = UUID.randomUUID();
        Producto prodExistente = new Producto(prodId, tiendaId, categoriaId, "Baguette", "", new BigDecimal("1.50"), 10, true, null, null, List.of());

        when(productoRepositoryPort.findById(prodId)).thenReturn(Optional.of(prodExistente));

        MockMultipartFile pdfFile = new MockMultipartFile("imagenes", "doc.pdf", "application/pdf", "bytes".getBytes());

        assertThrows(IllegalArgumentException.class, () -> productoService.uploadImagenes(prodId, List.of(pdfFile)));
    }

    @Test
    @DisplayName("Debe limitar el rango a máximo 50 para no saturar la base de datos")
    void debeAcotarLimiteMaximoParaNoSaturarBaseDeDatos() {
        Producto prod = new Producto(UUID.randomUUID(), tiendaId, categoriaId, "Baguette", "", new BigDecimal("1.50"), 10, true, null, null, List.of());
        when(productoRepositoryPort.findProductosPrincipales(0, 50)).thenReturn(List.of(prod));

        // Solicitando 100 productos (debe acotarse a 50) y página negativa (debe acotarse a 0)
        List<Producto> resultado = productoService.getProductosPrincipales(-5, 100);

        assertEquals(1, resultado.size());
        verify(productoRepositoryPort).findProductosPrincipales(0, 50);
    }
}
