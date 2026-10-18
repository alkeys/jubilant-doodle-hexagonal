package alex.probando.usuarios.infra.adapter.out.persistence.nosql;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.nosql.mapper.UsuarioNoSqlMapper;
import alex.probando.usuarios.infra.adapter.out.persistence.nosql.repository.SpringDataMongoUsuarioRepository;

@Component 
@Profile("nosql")
public class NosqlUsuarioAdapter implements UsuarioRepositoryPort {

    private final SpringDataMongoUsuarioRepository mongoRepository;
    private final UsuarioNoSqlMapper mapper;

    public NosqlUsuarioAdapter(SpringDataMongoUsuarioRepository mongoRepository, UsuarioNoSqlMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }



    @Override
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
     
        var UsuarioDocument = mapper.toDocument(usuario);
      
        var savedDocument = mongoRepository.save(UsuarioDocument);
        return mapper.toDomain(savedDocument);
    }

    

    
    
}
