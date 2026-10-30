package alex.probando.categorias.infra.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.categorias.application.port.out.CategoriaRepositoryPort;
import alex.probando.categorias.domain.Categoria;
import alex.probando.categorias.infra.adapter.out.persistence.jpa.entity.CategoriaEntity;
import alex.probando.categorias.infra.adapter.out.persistence.jpa.mapper.CategoriaJpaMapper;
import alex.probando.categorias.infra.adapter.out.persistence.jpa.repository.SpringDataCategoriaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaCategoriaAdapter implements CategoriaRepositoryPort {

    private final SpringDataCategoriaRepository repository;
    private final CategoriaJpaMapper mapper;

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaEntity entity = mapper.toEntity(categoria);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Categoria> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre).map(mapper::toDomain);
    }

    @Override
    public List<Categoria> findAllActivas() {
        return repository.findByActivoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Categoria> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return repository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existsByNombreAndIdNot(String nombre, UUID id) {
        return repository.existsByNombreIgnoreCaseAndIdNot(nombre, id);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
