package alex.probando.usuarios.infra.adapter.out.persistence.nosql.mapper;

import org.springframework.stereotype.Component;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.nosql.document.UsuarioDocument;

@Component
public class UsuarioNoSqlMapper {

    public UsuarioDocument toDocument(Usuario usuario) {
        if (usuario == null) return null;
        
        UsuarioDocument usuarioDocument = new UsuarioDocument();
        
        // Verificación de seguridad para evitar NullPointerException
        if (usuario.id() != null) {
            usuarioDocument.setId(usuario.id().toString());
        }else {
            usuarioDocument.setId(String.valueOf(java.util.UUID.randomUUID()));
        }
        
        usuarioDocument.setNombre(usuario.nombre());
        usuarioDocument.setEmail(usuario.email());
        usuarioDocument.setApellido(usuario.apellido());
        usuarioDocument.setEdad(usuario.edad());
        return usuarioDocument;
    }

    public Usuario toDomain(UsuarioDocument usuarioDocument) {
        if (usuarioDocument == null) return null;
        
        return new Usuario(
                usuarioDocument.getId() != null ? java.util.UUID.fromString(usuarioDocument.getId()) : null,
                usuarioDocument.getNombre(),
                usuarioDocument.getApellido(),
                usuarioDocument.getEmail(),
                usuarioDocument.getEdad()
        );
    }
}