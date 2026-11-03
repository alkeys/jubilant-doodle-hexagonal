package alex.probando.productos.infra.adapter.out.persistence.jpa.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.domain.TiendaProducto;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoImagenEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.TiendaProductoEntity;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.entity.TiendaEntity;

@Component
public class ProductoJpaMapper {

    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) {
            return null;
        }

        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.id());
        entity.setCategoriaId(domain.categoriaId());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        entity.setTalla(domain.talla());
        entity.setActivo(domain.activo());
        if (domain.creadoEn() != null) {
            entity.setCreadoEn(domain.creadoEn());
        }
        if (domain.actualizadoEn() != null) {
            entity.setActualizadoEn(domain.actualizadoEn());
        }

        if (domain.imagenes() != null && !domain.imagenes().isEmpty()) {
            List<ProductoImagenEntity> imagenEntities = new ArrayList<>();
            for (ProductoImagen img : domain.imagenes()) {
                ProductoImagenEntity imgEntity = toEntity(img);
                imgEntity.setProducto(entity);
                imagenEntities.add(imgEntity);
            }
            entity.setImagenes(imagenEntities);
        }

        if (domain.inventarios() != null && !domain.inventarios().isEmpty()) {
            List<TiendaProductoEntity> inventarioEntities = new ArrayList<>();
            for (TiendaProducto inv : domain.inventarios()) {
                TiendaEntity tiendaRef = new TiendaEntity();
                tiendaRef.setId(inv.tiendaId());
                if (inv.tiendaNombre() != null) {
                    tiendaRef.setNombre(inv.tiendaNombre());
                }

                TiendaProductoEntity invEntity = toEntity(inv, entity, tiendaRef);
                inventarioEntities.add(invEntity);
            }
            entity.setInventarios(inventarioEntities);
        }

        return entity;
    }

    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }

        List<ProductoImagen> imagenes = entity.getImagenes() != null
                ? entity.getImagenes().stream().map(this::toDomain).collect(Collectors.toList())
                : List.of();

        List<TiendaProducto> inventarios = entity.getInventarios() != null
                ? entity.getInventarios().stream().map(this::toDomain).collect(Collectors.toList())
                : List.of();

        return new Producto(
                entity.getId(),
                entity.getCategoriaId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getTalla(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn(),
                imagenes,
                inventarios);
    }

    public ProductoImagenEntity toEntity(ProductoImagen domain) {
        if (domain == null) {
            return null;
        }
        ProductoImagenEntity entity = new ProductoImagenEntity();
        entity.setId(domain.id());
        entity.setUrl(domain.url());
        entity.setEsPrincipal(domain.esPrincipal());
        entity.setOrden(domain.orden());
        if (domain.creadoEn() != null) {
            entity.setCreadoEn(domain.creadoEn());
        }
        return entity;
    }

    public ProductoImagen toDomain(ProductoImagenEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProductoImagen(
                entity.getId(),
                entity.getUrl(),
                entity.isEsPrincipal(),
                entity.getOrden(),
                entity.getCreadoEn());
    }

    public TiendaProductoEntity toEntity(TiendaProducto domain, ProductoEntity producto, TiendaEntity tienda) {
        if (domain == null) {
            return null;
        }
        TiendaProductoEntity entity = new TiendaProductoEntity();
        entity.setId(domain.id());
        entity.setProducto(producto);
        entity.setTienda(tienda);
        entity.setPrecio(domain.precio());
        entity.setStock(domain.stock());
        entity.setActivo(domain.activo());
        if (domain.creadoEn() != null) {
            entity.setCreadoEn(domain.creadoEn());
        }
        if (domain.actualizadoEn() != null) {
            entity.setActualizadoEn(domain.actualizadoEn());
        }
        return entity;
    }

    public TiendaProducto toDomain(TiendaProductoEntity entity) {
        if (entity == null) {
            return null;
        }
        String tiendaNombre = entity.getTienda() != null ? entity.getTienda().getNombre() : null;
        java.util.UUID tiendaId = entity.getTienda() != null ? entity.getTienda().getId() : null;

        return new TiendaProducto(
                entity.getId(),
                tiendaId,
                tiendaNombre,
                entity.getPrecio(),
                entity.getStock(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn());
    }
}
