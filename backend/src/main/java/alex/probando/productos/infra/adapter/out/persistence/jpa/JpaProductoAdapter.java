package alex.probando.productos.infra.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.productos.application.port.out.ProductoRepositoryPort;
import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoImagenEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.TiendaProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.mapper.ProductoJpaMapper;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataProductoImagenRepository;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataProductoRepository;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataTiendaProductoRepository;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.entity.TiendaEntity;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.repository.SpringDataTiendaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaProductoAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository productoRepository;
    private final SpringDataProductoImagenRepository imagenRepository;
    private final SpringDataTiendaProductoRepository tiendaProductoRepository;
    private final SpringDataTiendaRepository tiendaRepository;
    private final ProductoJpaMapper mapper;

    @Override
    public Producto save(Producto producto) {
        if (producto.id() != null) {
            Optional<ProductoEntity> existenteOpt = productoRepository.findById(producto.id());
            if (existenteOpt.isPresent()) {
                ProductoEntity existente = existenteOpt.get();
                existente.setCategoriaId(producto.categoriaId());
                existente.setNombre(producto.nombre());
                existente.setDescripcion(producto.descripcion());
                existente.setTalla(producto.talla());
                existente.setActivo(producto.activo());
                existente.setActualizadoEn(
                        producto.actualizadoEn() != null ? producto.actualizadoEn() : java.time.Instant.now());
                return mapper.toDomain(productoRepository.saveAndFlush(existente));
            }
        }
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
    public ProductoImagen saveImagen(UUID productoId, ProductoImagen imagen) {
        ProductoEntity productoEntity = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Si la imagen ya existe (ej. actualizar esPrincipal u orden), modificar la entidad gestionada
        if (imagen.id() != null) {
            Optional<ProductoImagenEntity> existente = imagenRepository.findById(imagen.id());
            if (existente.isPresent()) {
                ProductoImagenEntity entity = existente.get();
                entity.setUrl(imagen.url());
                entity.setEsPrincipal(imagen.esPrincipal());
                entity.setOrden(imagen.orden());
                ProductoImagenEntity actualizada = imagenRepository.saveAndFlush(entity);
                return mapper.toDomain(actualizada);
            }
        }

        ProductoImagenEntity imagenEntity = mapper.toEntity(imagen);
        imagenEntity.setProducto(productoEntity);
        ProductoImagenEntity guardada = imagenRepository.saveAndFlush(imagenEntity);
        if (productoEntity.getImagenes() != null) {
            productoEntity.getImagenes().add(guardada);
        }
        return mapper.toDomain(guardada);
    }

    @Override
    public void deleteImagen(UUID productoId, UUID imagenId) {
        productoRepository.findById(productoId).ifPresent(p -> {
            if (p.getImagenes() != null) {
                p.getImagenes().removeIf(img -> imagenId.equals(img.getId()));
                productoRepository.saveAndFlush(p);
            }
        });
        if (imagenRepository.existsById(imagenId)) {
            imagenRepository.deleteById(imagenId);
            imagenRepository.flush();
        }
    }

    @Override
    public Optional<ProductoImagen> findImagenById(UUID imagenId) {
        return imagenRepository.findById(imagenId).map(mapper::toDomain);
    }

    @Override
    public TiendaProducto saveInventario(UUID productoId, TiendaProducto inventario) {
        ProductoEntity productoEntity = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        TiendaEntity tiendaEntity = tiendaRepository.findById(inventario.tiendaId())
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada con ID: " + inventario.tiendaId()));

        Optional<TiendaProductoEntity> existente = tiendaProductoRepository
                .findByProductoIdAndTiendaId(productoId, inventario.tiendaId());

        TiendaProductoEntity entity;
        if (existente.isPresent()) {
            entity = existente.get();
            entity.setPrecio(inventario.precio());
            entity.setStock(inventario.stock());
            entity.setActivo(inventario.activo());
            entity.setActualizadoEn(java.time.Instant.now());
        } else {
            entity = mapper.toEntity(inventario, productoEntity, tiendaEntity);
            if (productoEntity.getInventarios() != null) {
                productoEntity.getInventarios().add(entity);
            }
        }

        TiendaProductoEntity guardado = tiendaProductoRepository.save(entity);
        return mapper.toDomain(guardado);
    }

    @Override
    public void deleteInventario(UUID productoId, UUID tiendaId) {
        tiendaProductoRepository.deleteByProductoIdAndTiendaId(productoId, tiendaId);
    }

    @Override
    public Optional<TiendaProducto> findInventario(UUID productoId, UUID tiendaId) {
        return tiendaProductoRepository.findByProductoIdAndTiendaId(productoId, tiendaId)
                .map(mapper::toDomain);
    }
}
