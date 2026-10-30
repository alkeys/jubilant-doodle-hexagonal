package alex.probando.usuarios.domain;

import java.util.UUID;

public record Role(
        UUID id,
        String nombre) {

    public static final String ADMIN = "ADMIN";
    public static final String CLIENTE = "CLIENTE";
    public static final String RH = "RH";

    public Role {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío");
        }
    }

    public Role(String nombre) {
        this(null, nombre);
    }
}
