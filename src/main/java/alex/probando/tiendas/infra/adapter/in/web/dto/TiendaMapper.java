package alex.probando.tiendas.infra.adapter.in.web.dto;

import java.time.Instant;

import alex.probando.tiendas.domain.Tienda;

public class TiendaMapper {

    public static TiendaResponse toResponse(Tienda tienda) {
        if (tienda == null) {
            return null;
        }
        return new TiendaResponse(
                tienda.id() != null ? tienda.id().toString() : null,
                tienda.nombre(),
                tienda.descripcion(),
                tienda.direccion(),
                tienda.activo(),
                tienda.creadoEn() != null ? tienda.creadoEn().toString() : null
        );
    }

    public static Tienda toDomain(TiendaRequest request) {
        return new Tienda(
                null,
                request.getNombre(),
                request.getDescripcion(),
                request.getDireccion(),
                true,
                Instant.now()
        );
    }
}
