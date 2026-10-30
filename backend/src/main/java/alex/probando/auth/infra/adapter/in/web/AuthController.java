package alex.probando.auth.infra.adapter.in.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alex.probando.auth.application.port.in.LoginUseCase;
import alex.probando.auth.infra.adapter.in.web.dto.AuthResponse;
import alex.probando.auth.infra.adapter.in.web.dto.LoginRequest;
import alex.probando.shared.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión, cierre de sesión y obtención del perfil autenticado")
public class AuthController {

        private final LoginUseCase loginUseCase;
        private final JwtService jwtService;

        @Operation(summary = "Iniciar sesión", description = "Autentica credenciales y emite una cookie HttpOnly 'access_token' con el JWT")
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
                UserDetails userDetails = loginUseCase.authenticate(request.getUsername(), request.getPassword());
                String jwtToken = jwtService.generateToken(userDetails);

                ResponseCookie cookie = ResponseCookie.from(jwtService.getCookieName(), jwtToken)
                                .httpOnly(true)
                                .secure(false) // Permite pruebas en HTTP local (en prod con SSL puede ser true)
                                .path("/")
                                .maxAge(24 * 60 * 60) // 1 día
                                .sameSite("Lax")
                                .build();

                List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                AuthResponse response = AuthResponse.builder()
                                .mensaje("Inicio de sesión exitoso. Cookie HttpOnly establecida y token generado.")
                                .token(jwtToken)
                                .tipoToken("Bearer")
                                .username(userDetails.getUsername())
                                .roles(roles)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(response);
        }

        @Operation(summary = "Cerrar sesión", description = "Invalida la cookie HttpOnly 'access_token' eliminándola del navegador")
        @PostMapping("/logout")
        public ResponseEntity<AuthResponse> logout() {
                ResponseCookie cookie = ResponseCookie.from(jwtService.getCookieName(), "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0) // Expira inmediatamente
                                .sameSite("Lax")
                                .build();

                AuthResponse response = AuthResponse.builder()
                                .mensaje("Sesión cerrada correctamente. Cookie eliminada.")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(response);
        }

        @Operation(summary = "Consultar sesión actual", description = "Retorna los datos del usuario autenticado y sus roles")
        @GetMapping("/me")
        public ResponseEntity<AuthResponse> me(Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(401).build();
                }

                List<String> roles = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                AuthResponse response = AuthResponse.builder()
                                .mensaje("Usuario autenticado")
                                .username(authentication.getName())
                                .roles(roles)
                                .build();

                return ResponseEntity.ok(response);
        }
}
