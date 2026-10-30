package alex.probando.tiendas.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alex.probando.shared.exception.ResourceNotFoundException;
import alex.probando.tiendas.application.port.in.CreateTiendaUseCase;
import alex.probando.tiendas.application.port.in.DeleteTiendaUseCase;
import alex.probando.tiendas.application.port.in.GetTiendaByIdUseCase;
import alex.probando.tiendas.application.port.in.ListTiendasUseCase;
import alex.probando.tiendas.application.port.in.UpdateTiendaUseCase;
import alex.probando.tiendas.application.port.out.TiendaRepositoryPort;
import alex.probando.tiendas.domain.Tienda;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TiendaService implements
        CreateTiendaUseCase,
        GetTiendaByIdUseCase,
        ListTiendasUseCase,
        UpdateTiendaUseCase,
        DeleteTiendaUseCase {

    private final TiendaRepositoryPort tiendaRepositoryPort;

    @Override
    public Tienda createTienda(Tienda tienda) {
        if (tienda == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        return tiendaRepositoryPort.save(tienda);
    }

    @Override
    @Transactional(readOnly = true)
    public Tienda getTiendaById(UUID id) {
        return tiendaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tienda> listTiendasActivas() {
        return tiendaRepositoryPort.findAllActivas();
    }

    @Override
    public Tienda updateTienda(UUID id, Tienda tiendaActualizada) {
        Tienda existente = getTiendaById(id);

        Tienda nueva = new Tienda(
                existente.id(),
                tiendaActualizada.nombre(),
                tiendaActualizada.descripcion(),
                tiendaActualizada.direccion(),
                tiendaActualizada.activo(),
                existente.creadoEn()
        );

        return tiendaRepositoryPort.save(nueva);
    }

    @Override
    public void deleteTienda(UUID id) {
        if (!tiendaRepositoryPort.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        }
        tiendaRepositoryPort.deleteById(id);
    }
}
