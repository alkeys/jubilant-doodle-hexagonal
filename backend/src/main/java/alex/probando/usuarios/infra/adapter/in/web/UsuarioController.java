package alex.probando.usuarios.infra.adapter.in.web;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import alex.probando.shared.exception.ErrorResponse;
import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.usuarios.application.port.in.AssignRolesUseCase;
import alex.probando.usuarios.application.port.in.CreateUserCase;
import alex.probando.usuarios.application.port.in.DeleteUserUseCase;
import alex.probando.usuarios.application.port.in.GetUserByIdUseCase;
import alex.probando.usuarios.application.port.in.GetUsuariosUseCase;
import alex.probando.usuarios.application.port.in.UpdateUserUseCase;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.in.web.dto.AssignRolesRequest;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioMapper;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioRequest;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioResponse;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "CRUD completo de usuarios y gestión de roles administrado centralizadamente por seguridad")
public class UsuarioController {

    private final CreateUserCase createUserCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUsuariosUseCase getUsuariosUseCase;
    private final AssignRolesUseCase assignRolesUseCase;

    @Operation(summary = "Registrar nuevo usuario", description = "Endpoint público para el registro de usuarios (rol inicial asignado: CLIENTE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Email o username ya en uso", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse createUser(@RequestBody @Valid UsuarioRequest request) {
        Usuario usuario = UsuarioMapper.toDomain(request);
        Usuario created = createUserCase.createUser(usuario);
        return UsuarioMapper.toResponse(created);
    }

    @Operation(
        summary = "Listar usuarios con filtro por rol (Solo ADMIN y RH)",
        description = "Permite a los administradores y personal de RH listar todos los usuarios o filtrar por rol específico (ej: ?rol=CLIENTE, ?rol=ADMIN, ?rol=RH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN y RH tienen permiso")
    })
    @GetMapping
    public List<UsuarioResponse> getUsuariosFiltradosPorRol(
            @RequestParam(required = false) String rol
    ) {
        List<Usuario> usuarios = getUsuariosUseCase.getUsuariosFiltradosPorRol(rol);
        return usuarios.stream()
                .map(UsuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener mi perfil", description = "Permite a cualquier usuario autenticado (incluido CLIENTE) consultar su propio perfil")
    @GetMapping("/perfil")
    public UsuarioResponse getMiPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No se encontró usuario autenticado en la sesión");
        }
        Usuario usuario = getUsuariosUseCase.getPerfil(authentication.getName());
        return UsuarioMapper.toResponse(usuario);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Permite consultar los datos de un usuario por su UUID (Solo ADMIN y RH)")
    @GetMapping("/{id}")
    public UsuarioResponse getUserById(@PathVariable UUID id) {
        Usuario usuario = getUserByIdUseCase.getUserById(id);
        return UsuarioMapper.toResponse(usuario);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario por su ID (Solo ADMIN)")
    @PutMapping("/{id}")
    public UsuarioResponse updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UsuarioUpdateRequest request
    ) {
        Usuario actualizado = UsuarioMapper.toDomain(request);
        Usuario resultado = updateUserUseCase.updateUser(id, actualizado);
        return UsuarioMapper.toResponse(resultado);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID (Solo ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        deleteUserUseCase.deleteUser(id);
    }

    @Operation(summary = "Asignar roles a usuario", description = "Permite modificar los roles de un usuario (Solo ADMIN)")
    @PatchMapping("/{id}/roles")
    public UsuarioResponse assignRoles(
            @PathVariable UUID id,
            @RequestBody @Valid AssignRolesRequest request
    ) {
        Usuario resultado = assignRolesUseCase.assignRoles(id, request.getRoles());
        return UsuarioMapper.toResponse(resultado);
    }
}
