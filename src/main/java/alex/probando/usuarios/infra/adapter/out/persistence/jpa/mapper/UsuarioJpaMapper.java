package alex.probando.usuarios.infra.adapter.out.persistence.jpa.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.RoleEntity;
import alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity.UsuarioEntity;

@Component
public class UsuarioJpaMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
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
        
        if (usuario.roles() != null) {
            Set<RoleEntity> roleEntities = usuario.roles().stream()
                    .map(r -> new RoleEntity(r.id(), r.nombre()))
                    .collect(Collectors.toSet());
            entity.setRoles(roleEntities);
        } else {
            entity.setRoles(new HashSet<>());
        }
        return entity;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        Set<Role> roles = Collections.emptySet();
        if (entity.getRoles() != null) {
            roles = entity.getRoles().stream()
                    .map(re -> new Role(re.getId(), re.getNombre()))
                    .collect(Collectors.toSet());
        }

        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getNombreDeUsuario(),
                entity.getApellido(),
                entity.getEmail(),
                entity.getEdad(),
                entity.getActivo(),
                entity.getPassword(),
                roles
        );
    }
}
