package alex.probando.tiendas.infra.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import alex.probando.tiendas.domain.Tienda;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.entity.TiendaEntity;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.mapper.TiendaJpaMapper;
import alex.probando.tiendas.infra.adapter.out.persistence.jpa.repository.SpringDataTiendaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaTiendaAdapter implements TiendaRepositoryPort {

    private final SpringDataTiendaRepository repository;
    private final TiendaJpaMapper mapper;

    @Override
    public Tienda save(Tienda tienda) {
        TiendaEntity entity = mapper.toEntity(tienda);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Tienda> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Tienda> findAllActivas() {
        return repository.findByActivoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tienda> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
