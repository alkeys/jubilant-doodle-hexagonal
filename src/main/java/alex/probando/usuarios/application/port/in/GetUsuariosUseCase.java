package alex.probando.usuarios.application.port.in;

import java.util.List;

import alex.probando.usuarios.domain.Usuario;

public interface GetUsuariosUseCase {
    List<Usuario> getUsuariosPorRol(String usernameAutenticado, List<String> roles);
    List<Usuario> getUsuariosFiltradosPorRol(String rol);
    Usuario getPerfil(String usernameOrEmail);
}
