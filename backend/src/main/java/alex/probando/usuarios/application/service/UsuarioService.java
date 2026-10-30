package alex.probando.usuarios.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.shared.exception.DuplicateResourceException;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.usuarios.application.port.in.AssignRolesUseCase;
import alex.probando.usuarios.application.port.in.CreateUserCase;
import alex.probando.usuarios.application.port.in.DeleteUserUseCase;
import alex.probando.usuarios.application.port.in.GetUserByIdUseCase;
import alex.probando.usuarios.application.port.in.GetUsuariosUseCase;
import alex.probando.usuarios.application.port.in.UpdateUserUseCase;
import alex.probando.usuarios.application.port.out.PasswordEncoderPort;
import alex.probando.usuarios.application.port.out.UsuarioRepositoryPort;
import alex.probando.usuarios.domain.Role;
import alex.probando.usuarios.domain.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService implements
        CreateUserCase,
        GetUserByIdUseCase,
        UpdateUserUseCase,
        DeleteUserUseCase,
        GetUsuariosUseCase,
        AssignRolesUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public Usuario createUser(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.email() == null || usuario.email().isBlank()) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo o vacío");
        }
        if (usuario.password() == null || usuario.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña del usuario no puede ser nula o vacía");
        }
        if (usuario.password().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña del usuario debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        if (usuario.NombreDeUsuario() == null || usuario.NombreDeUsuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }

        if (usuarioRepositoryPort.existsByEmail(usuario.email())) {
            throw new DuplicateResourceException("El email " + usuario.email() + " ya está registrado");
        }
        if (usuarioRepositoryPort.existsByNombreDeUsuario(usuario.NombreDeUsuario())) {
            throw new DuplicateResourceException("El nombre de usuario " + usuario.NombreDeUsuario() + " ya está en uso");
        }

        String encodedPassword = passwordEncoderPort.codificar(usuario.password());

        Set<Role> roles = usuario.roles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(new Role(Role.CLIENTE));
        }

        Usuario usuarioAGuardar = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.NombreDeUsuario(),
                usuario.apellido(),
                usuario.email(),
                usuario.edad(),
                true,
                encodedPassword,
                roles
        );

        return usuarioRepositoryPort.save(usuarioAGuardar);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUserById(UUID id) {
        return usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public Usuario updateUser(UUID id, Usuario actualizado) {
        Usuario existente = getUserById(id);

        if (!existente.email().equalsIgnoreCase(actualizado.email()) && usuarioRepositoryPort.existsByEmail(actualizado.email())) {
            throw new DuplicateResourceException("El email " + actualizado.email() + " ya está registrado por otro usuario");
        }

        if (!existente.NombreDeUsuario().equalsIgnoreCase(actualizado.NombreDeUsuario()) && usuarioRepositoryPort.existsByNombreDeUsuario(actualizado.NombreDeUsuario())) {
            throw new DuplicateResourceException("El nombre de usuario " + actualizado.NombreDeUsuario() + " ya está en uso");
        }

        String passwordFinal = existente.password();
        if (actualizado.password() != null && !actualizado.password().isBlank()) {
            if (actualizado.password().length() < MIN_PASSWORD_LENGTH) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
            }
            passwordFinal = passwordEncoderPort.codificar(actualizado.password());
        }

        Set<Role> rolesFinal = actualizado.roles() != null && !actualizado.roles().isEmpty()
                ? actualizado.roles()
                : existente.roles();

        Usuario usuarioActualizado = new Usuario(
                existente.id(),
                actualizado.nombre(),
                actualizado.NombreDeUsuario(),
                actualizado.apellido(),
                actualizado.email(),
                actualizado.edad(),
                actualizado.activo(),
                passwordFinal,
                rolesFinal
        );

        return usuarioRepositoryPort.save(usuarioActualizado);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!usuarioRepositoryPort.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepositoryPort.deleteById(id);
    }

    @Override
    public Usuario assignRoles(UUID id, Set<String> rolesNombres) {
        Usuario existente = getUserById(id);

        Set<Role> nuevosRoles = new HashSet<>();
        for (String nombreRol : rolesNombres) {
            String sanitized = nombreRol.toUpperCase().trim();
            if (sanitized.startsWith("ROLE_")) {
                sanitized = sanitized.substring(5);
            }
            nuevosRoles.add(new Role(sanitized));
        }

        Usuario usuarioConRoles = new Usuario(
                existente.id(),
                existente.nombre(),
                existente.NombreDeUsuario(),
                existente.apellido(),
                existente.email(),
                existente.edad(),
                existente.activo(),
                existente.password(),
                nuevosRoles
        );

        return usuarioRepositoryPort.save(usuarioConRoles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorRol(String usernameAutenticado, List<String> roles) {
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_RH") || roles.contains("ADMIN") || roles.contains("RH")) {
            return usuarioRepositoryPort.findAll();
        }

        Usuario usuario = usuarioRepositoryPort.findByNombreDeUsuario(usernameAutenticado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + usernameAutenticado));

        return List.of(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosFiltradosPorRol(String rol) {
        List<Usuario> todos = usuarioRepositoryPort.findAll();
        if (rol == null || rol.isBlank()) {
            return todos;
        }

        String rawRol = rol.toUpperCase().trim();
        final String rolBuscado = rawRol.startsWith("ROLE_") ? rawRol.substring(5) : rawRol;

        return todos.stream()
                .filter(u -> u.roles() != null && u.roles().stream()
                        .anyMatch(r -> r.nombre().equalsIgnoreCase(rolBuscado)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getPerfil(String usernameOrEmail) {
        return usuarioRepositoryPort.findByNombreDeUsuario(usernameOrEmail)
                .or(() -> usuarioRepositoryPort.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con credencial: " + usernameOrEmail));
    }
}
