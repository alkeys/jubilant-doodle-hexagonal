package alex.probando.usuarios.infra.adapter.in.web.dto;

import alex.probando.usuarios.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;

public class UsuarioMapper {

    @Operation(summary = "Convertir un usuario a su representación en la API", description = "Mapea un objeto de dominio Usuario a su representación en la API")
     public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.id().toString(),
            usuario.nombre().toString(),
            usuario.apellido(),
            usuario.email(),
            usuario.edad()
        );
    }
    
    public static Usuario toDomain(UsuarioRequest request) {
        return new Usuario(
            null,
            request.getNombre(),
            request.getApellido(),
            request.getEmail(),
            request.getEdad()
        );
    }
    
}
