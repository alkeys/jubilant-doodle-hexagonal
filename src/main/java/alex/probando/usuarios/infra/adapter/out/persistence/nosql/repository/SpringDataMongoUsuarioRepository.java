package alex.probando.usuarios.infra.adapter.out.persistence.nosql.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import alex.probando.usuarios.infra.adapter.out.persistence.nosql.document.UsuarioDocument;

@Repository 
public interface SpringDataMongoUsuarioRepository extends MongoRepository<UsuarioDocument, UUID> {
    
}
