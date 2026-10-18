package alex.probando.usuarios.infra.adapter.out.persistence.jpa;

import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper.UsuarioJpaMapper;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.repository.SpringDataUsuarioRepository;

@Component 
public class JpaUsuarioAdapter  implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;

    public JpaUsuarioAdapter(SpringDataUsuarioRepository repository) {
        this.repository = repository;
    }

    @Override 
    public Usuario save(Usuario usuario) {
        return UsuarioJpaMapper.toDomain(repository.save(UsuarioJpaMapper.toEntity(usuario)));
    }


    
}
