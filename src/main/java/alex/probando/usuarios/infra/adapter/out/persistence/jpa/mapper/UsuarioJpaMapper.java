package alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.UsuarioEntity;

@Component 
public class UsuarioJpaMapper {

    public  UsuarioEntity toEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.id());
        entity.setNombre(usuario.nombre());
        entity.setApellido(usuario.apellido());
        entity.setEmail(usuario.email());
        entity.setEdad(usuario.edad());
        entity.setPassword(usuario.password());
        entity.setNombreDeUsuario(usuario.NombreDeUsuario());
        entity.setActivo(usuario.activo());
        return entity;
    }



    public  Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Usuario(entity.getId(), entity.getNombre(),
        entity.getNombreDeUsuario(), entity.getApellido(), entity.getEmail()
        ,entity.getEdad(), entity.getActivo(), entity.getPassword());
    }
    
}
