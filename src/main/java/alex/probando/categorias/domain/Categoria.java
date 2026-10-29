package alex.probando.categorias.domain;

import java.util.UUID;

public record Categoria(
        UUID id,
        String nombre,
        String descripcion,
        boolean activo) {

    public Categoria {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo o vacío");
        }
    }

    public Categoria(String nombre, String descripcion) {
        this(null, nombre, descripcion, true);
    }
}
