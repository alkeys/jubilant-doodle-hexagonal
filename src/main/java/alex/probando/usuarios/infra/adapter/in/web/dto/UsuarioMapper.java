package alex.probando.usuarios.infra.adapter.in.web.dto;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;

public class UsuarioMapper {

    @Operation(summary = "Convertir un usuario a su representación en la API", description = "Mapea un objeto de dominio Usuario a su representación en la API")
    public static UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        Set<String> rolesNames = Collections.emptySet();
        if (usuario.roles() != null) {
            rolesNames = usuario.roles().stream()
                    .map(Role::nombre)
                    .collect(Collectors.toSet());
        }

        return new UsuarioResponse(
                usuario.id() != null ? usuario.id().toString() : null,
                usuario.nombre(),
                usuario.NombreDeUsuario(),
                usuario.apellido(),
                usuario.email(),
                usuario.edad(),
                usuario.activo(),
                rolesNames
        );
    }

    public static Usuario toDomain(UsuarioRequest request) {
        return new Usuario(
                null,
                request.getNombre(),
                request.getNombreDeUsuario(),
                request.getApellido(),
                request.getEmail(),
                request.getEdad(),
                true,
                request.getPassword(),
                Set.of(new Role(Role.CLIENTE))
        );
    }

    public static Usuario toDomain(UsuarioUpdateRequest request) {
        return new Usuario(
                null,
                request.getNombre(),
                request.getNombreDeUsuario(),
                request.getApellido(),
                request.getEmail(),
                request.getEdad(),
                request.getActivo() != null ? request.getActivo() : true,
                request.getPassword(),
                Collections.emptySet()
        );
    }
}
