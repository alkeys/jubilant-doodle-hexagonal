package alex.probando.usuarios.domain;

import java.util.UUID;

public record Usuario(UUID id, String nombre, String apellido, String email, int edad) {

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
    }

}
