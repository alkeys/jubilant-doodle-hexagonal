package alex.probando.productos.infra.adapter.out.persistence.jpa.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.productos.domain.Producto;
import alex.probando.productos.domain.ProductoImagen;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoImagenEntity;

@Component
public class ProductoJpaMapper {

    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) {
            return null;
        }

        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.id());
        entity.setTiendaId(domain.tiendaId());
        entity.setCategoriaId(domain.categoriaId());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        entity.setPrecio(domain.precio());
        entity.setStock(domain.stock());
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

            // Mantener imagenUrl de V1 sincronizada con la principal o primera
            domain.imagenes().stream()
                    .filter(ProductoImagen::esPrincipal)
                    .findFirst()
                    .or(() -> domain.imagenes().stream().findFirst())
                    .ifPresent(p -> entity.setImagenUrl(p.url()));
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

        return new Producto(
                entity.getId(),
                entity.getTiendaId(),
                entity.getCategoriaId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecio(),
                entity.getStock(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn(),
                imagenes
        );
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
                entity.getCreadoEn()
        );
    }
}
