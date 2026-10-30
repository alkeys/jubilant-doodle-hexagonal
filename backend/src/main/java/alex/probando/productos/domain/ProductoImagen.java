package alex.probando.productos.domain;

import java.time.Instant;
import java.util.UUID;

public record ProductoImagen(
        UUID id,
        String url,
        boolean esPrincipal,
        int orden,
        Instant creadoEn) {

    public ProductoImagen {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL de la imagen no puede ser nula o vacía");
        }
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
    }

    public ProductoImagen(String url, boolean esPrincipal, int orden) {
        this(null, url, esPrincipal, orden, Instant.now());
    }
}
