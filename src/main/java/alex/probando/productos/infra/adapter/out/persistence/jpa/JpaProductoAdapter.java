package alex.probando.productos.infra.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoImagenEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.mapper.ProductoJpaMapper;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataProductoImagenRepository;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataProductoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaProductoAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository productoRepository;
    private final SpringDataProductoImagenRepository imagenRepository;
    private final ProductoJpaMapper mapper;

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = mapper.toEntity(producto);
        return mapper.toDomain(productoRepository.save(entity));
    }

    @Override
    public Optional<Producto> findById(UUID id) {
        return productoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Producto> findAllActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findByTiendaId(UUID tiendaId) {
        return productoRepository.findByTiendaId(tiendaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findByCategoriaId(UUID categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> buscarPorNombre(String query) {
        return productoRepository.searchByNombre(query).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findProductosPrincipales(int pagina, int limite) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pagina, limite);
        return productoRepository.findByActivoTrueOrderByCreadoEnDesc(pageable).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        productoRepository.deleteById(id);
    }

    @Override
    public void saveImagen(UUID productoId, ProductoImagen imagen) {
        ProductoEntity productoEntity = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ProductoImagenEntity imagenEntity = mapper.toEntity(imagen);
        imagenEntity.setProducto(productoEntity);
        imagenRepository.save(imagenEntity);
    }

    @Override
    public void deleteImagen(UUID productoId, UUID imagenId) {
        imagenRepository.deleteById(imagenId);
    }

    @Override
    public Optional<ProductoImagen> findImagenById(UUID imagenId) {
        return imagenRepository.findById(imagenId).map(mapper::toDomain);
    }
}
