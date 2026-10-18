package alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper;

import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.UsuarioEntity;

public class UsuarioJpaMapper {

    public static UsuarioEntity toEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.id());
        entity.setNombre(usuario.nombre());
        entity.setApellido(usuario.apellido());
        entity.setEmail(usuario.email());
        entity.setEdad(usuario.edad());
        return entity;
    }


    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Usuario(entity.getId(), entity.getNombre(), entity.getApellido(), entity.getEmail(),entity.getEdad());
    }
    
}
