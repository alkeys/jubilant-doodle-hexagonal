package alex.probando.usuarios.domain;

import java.util.UUID;

public record Usuario(
        UUID id,
        String nombre,
        String NombreDeUsuario,
        String apellido,
        String email,
        int edad,
        boolean activo,
        String password) {

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
        if (activo == false) {
            throw new IllegalArgumentException("El usuario no puede ser inactivo");
        }
    }

    public Usuario(String nombre,String NombreDeUsuario, String apellido, String email, int edad, boolean activo, String password) {
        this(null, nombre, NombreDeUsuario, apellido, email, edad, activo, password);
    }

}
