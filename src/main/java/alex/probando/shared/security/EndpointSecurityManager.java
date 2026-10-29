package alex.probando.shared.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.domain.Role;

@Component
public class EndpointSecurityManager {

    /**
     * Administra centralizadamente todos los permisos y reglas de acceso (RBAC).
     * IMPORTANTE: Las rutas específicas (como /perfil) deben registrarse ANTES
     * que las rutas parametrizadas (como /{id}) para evitar que el parámetro capture la ruta.
     */
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // ==========================================
            // 1. ENDPOINTS PÚBLICOS
            // ==========================================
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            
            // Catálogo público de sucursales de la cadena
            .requestMatchers(HttpMethod.GET, "/api/v1/tiendas").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/tiendas/{id:[a-fA-F0-9\\-]+}").permitAll()

            // Catálogo público de categorías de productos
            .requestMatchers(HttpMethod.GET, "/api/v1/categorias").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categorias/{id:[a-fA-F0-9\\-]+}").permitAll()

            // ==========================================
            // 2. ENDPOINTS PARA CUALQUIER USUARIO AUTENTICADO (CLIENTE, RH, ADMIN)
            // Registrado antes de /{id} para que no sea absorbido como variable de ruta
            // ==========================================
            .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/perfil").authenticated()

            // ==========================================
            // 3. ENDPOINTS EXCLUSIVOS DE ADMIN
            // ==========================================
            // Gestión de sucursales
            .requestMatchers(HttpMethod.POST, "/api/v1/tiendas").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.PUT, "/api/v1/tiendas/**").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.DELETE, "/api/v1/tiendas/**").hasRole(Role.ADMIN)

            // Gestión de categorías de productos
            .requestMatchers(HttpMethod.POST, "/api/v1/categorias").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**").hasRole(Role.ADMIN)
            
            // Administración de usuarios
            .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/**").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/**").hasRole(Role.ADMIN)
            .requestMatchers(HttpMethod.PATCH, "/api/v1/usuarios/*/roles").hasRole(Role.ADMIN)

            // ==========================================
            // 4. ENDPOINTS PARA ADMIN Y RH (Listado y Detalle de Usuarios)
            // ==========================================
            .requestMatchers(HttpMethod.GET, "/api/v1/usuarios").hasAnyRole(Role.ADMIN, Role.RH)
            .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/{id:[a-fA-F0-9\\-]+}").hasAnyRole(Role.ADMIN, Role.RH)

            // ==========================================
            // 5. CUALQUIER OTRA RUTA
            // ==========================================
            .anyRequest().authenticated();
    }
}
