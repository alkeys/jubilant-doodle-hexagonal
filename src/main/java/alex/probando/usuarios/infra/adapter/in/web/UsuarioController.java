
package alex.probando.usuarios.infra.adapter.in.web;

import alex.probando.usuarios.application.port.in.CreateUserCase;
import alex.probando.usuarios.domain.Usuario;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioMapper;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioRequest;
import alex.probando.usuarios.infra.adapter.in.web.dto.UsuarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones CRUD para la gestión de usuarios y asignación de roles")
public class UsuarioController {

    private final CreateUserCase createUserCase;

    public UsuarioController(CreateUserCase createUserCase) {
        this.createUserCase = createUserCase;
    }

    @Operation(summary = "Registrar o crear un usuario", description = "Endpoint público para el registro de nuevos usuarios en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos: revisa el campo indicado en el mensaje de error"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse createUser(
            @RequestBody @Valid UsuarioRequest request) {

        Usuario usuario = UsuarioMapper.toDomain(request);
        Usuario createdUsuario = createUserCase.createUser(usuario);

        return UsuarioMapper.toResponse(createdUsuario);
    }
}
