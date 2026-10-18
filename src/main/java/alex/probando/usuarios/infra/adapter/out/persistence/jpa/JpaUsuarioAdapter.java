package alex.probando.usuarios.infra.adapter.out.persistence.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper.UsuarioJpaMapper;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository.SpringDataUsuarioRepository;

@Component 
@Profile("sql")  // para indicar que este adaptador se utilizará solo cuando el perfil activo sea "sql"
public class JpaUsuarioAdapter  implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;
    private final UsuarioJpaMapper mapper;

    public JpaUsuarioAdapter(SpringDataUsuarioRepository repository, UsuarioJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override 
    public Usuario save(Usuario usuario) {
        return mapper.toDomain(repository.save(mapper.toEntity(usuario)));
    }

    
}
