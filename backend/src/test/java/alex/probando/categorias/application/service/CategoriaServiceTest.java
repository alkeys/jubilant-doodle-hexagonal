package alex.probando.categorias.application.service;

import java.util.List;
import java.util.Optional;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import alex.probando.categorias.application.port.out.CategoriaRepositoryPort;
import alex.probando.categorias.domain.Categoria;
import alex.probando.shared.exception.DuplicateResourceException;
import alex.probando.shared.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepositoryPort repositoryPort;

    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        categoriaService = new CategoriaService(repositoryPort);
    }

    @Test
    @DisplayName("Debe crear una categoría correctamente si el nombre no está duplicado")
    void debeCrearCategoriaExitosamente() {
        Categoria nueva = new Categoria("Panadería", "Pan dulce y francés");
        UUID id = UUID.randomUUID();
        Categoria guardada = new Categoria(id, "Panadería", "Pan dulce y francés", true);

        when(repositoryPort.existsByNombre("Panadería")).thenReturn(false);
        when(repositoryPort.save(any(Categoria.class))).thenReturn(guardada);

        Categoria resultado = categoriaService.createCategoria(nueva);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
        assertEquals("Panadería", resultado.nombre());
        verify(repositoryPort).save(nueva);
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException si el nombre ya existe")
    void debeLanzarExcepcionSiNombreDuplicado() {
        Categoria nueva = new Categoria("Panadería", "Pan dulce y francés");
        when(repositoryPort.existsByNombre("Panadería")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoriaService.createCategoria(nueva));
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe listar únicamente categorías activas")
    void debeListarCategoriasActivas() {
        Categoria cat1 = new Categoria(UUID.randomUUID(), "Bebidas", "Cafés y jugos", true);
        when(repositoryPort.findAllActivas()).thenReturn(List.of(cat1));

        List<Categoria> resultado = categoriaService.listCategoriasActivas();

        assertEquals(1, resultado.size());
        assertEquals("Bebidas", resultado.get(0).nombre());
        verify(repositoryPort).findAllActivas();
    }

    @Test
    @DisplayName("Debe obtener categoría por ID o lanzar ResourceNotFoundException")
    void debeObtenerCategoriaPorId() {
        UUID id = UUID.randomUUID();
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoriaService.getCategoriaById(id));
    }
}
