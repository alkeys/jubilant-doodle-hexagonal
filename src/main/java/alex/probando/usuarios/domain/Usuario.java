package alex.probando.usuarios.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record Usuario(
        UUID id,
        String nombre,
        String NombreDeUsuario,
        String apellido,
        String email,
        int edad,
        boolean activo,
        String password,
        Set<Role> roles) {

    public Usuario {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        if (NombreDeUsuario == null || NombreDeUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (roles == null) {
            roles = Collections.emptySet();
        } else {
            roles = Collections.unmodifiableSet(new HashSet<>(roles));
        }
    }

    public Usuario(UUID id, String nombre, String NombreDeUsuario, String apellido, String email, int edad, boolean activo, String password) {
        this(id, nombre, NombreDeUsuario, apellido, email, edad, activo, password, Set.of(new Role(Role.CLIENTE)));
    }

    public Usuario(String nombre, String NombreDeUsuario, String apellido, String email, int edad, boolean activo, String password) {
        this(null, nombre, NombreDeUsuario, apellido, email, edad, activo, password, Set.of(new Role(Role.CLIENTE)));
    }

    public Usuario(String nombre, String NombreDeUsuario, String apellido, String email, int edad, boolean activo, String password, Set<Role> roles) {
        this(null, nombre, NombreDeUsuario, apellido, email, edad, activo, password, roles);
    }
}
