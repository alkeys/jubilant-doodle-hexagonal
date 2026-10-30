package alex.probando.ventas.infra.adapter.out.persistence.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import alex.probando.productos.infra.adapter.out.persistence.jpa.entity.ProductoEntity;
import alex.probando.productos.infra.adapter.out.persistence.jpa.repository.SpringDataProductoRepository;
import alex.probando.ventas.application.port.out.VentaRepositoryPort;
import alex.probando.ventas.domain.ProductoMasVendido;
import alex.probando.ventas.domain.Venta;
import alex.probando.ventas.infra.adapter.out.persistence.jpa.entity.VentaEntity;
import alex.probando.ventas.infra.adapter.out.persistence.jpa.mapper.VentaJpaMapper;
import alex.probando.ventas.infra.adapter.out.persistence.jpa.repository.SpringDataVentaRepository;
import lombok.RequiredArgsConstructor;

@Component
@Profile("sql")
@RequiredArgsConstructor
public class JpaVentaAdapter implements VentaRepositoryPort {

    private final SpringDataVentaRepository ventaRepository;
    private final SpringDataProductoRepository productoRepository;
    private final VentaJpaMapper mapper;

    @Override
    public Venta save(Venta venta) {
        VentaEntity entity = mapper.toEntity(venta);
        VentaEntity guardada = ventaRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Venta> findById(UUID id) {
        return ventaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Venta> findByClienteId(UUID clienteId) {
        return ventaRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Venta> findByProductoId(UUID productoId) {
        return ventaRepository.findByProductoIdOrderByCreatedAtDesc(productoId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoMasVendido> findTopProductosVendidos(int limite) {
        return ventaRepository.findTopVendidos(PageRequest.of(0, limite)).stream()
                .map(proj -> {
                    String nombreProducto = productoRepository.findById(proj.getProductoId())
                            .map(ProductoEntity::getNombre)
                            .orElse("Producto no encontrado");

                    return new ProductoMasVendido(
                            proj.getProductoId(),
                            nombreProducto,
                            proj.getTotalCantidad() != null ? proj.getTotalCantidad() : 0L,
                            proj.getTotalRecaudado() != null ? proj.getTotalRecaudado() : BigDecimal.ZERO
                    );
                })
                .collect(Collectors.toList());
    }
}
