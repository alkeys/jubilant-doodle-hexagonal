package alex.probando.tiendas.domain;

import java.time.Instant;
import java.util.UUID;

public record Tienda(
        UUID id,
        String nombre,
        String descripcion,
        String direccion,
        boolean activo,
        Instant creadoEn) {

    public Tienda {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede ser nulo o vacío");
        }
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
    }

    public Tienda(String nombre, String descripcion, String direccion) {
        this(null, nombre, descripcion, direccion, true, Instant.now());
    }
}
