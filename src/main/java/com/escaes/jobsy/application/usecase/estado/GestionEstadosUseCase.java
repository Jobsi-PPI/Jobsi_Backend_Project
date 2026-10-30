package com.escaes.jobsy.application.usecase.estado;

import com.escaes.jobsy.application.dto.estado.EstadoRequest;
import com.escaes.jobsy.domain.model.Estado;
import com.escaes.jobsy.domain.repository.EstadoRepository;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GestionEstadosUseCase {

    private final EstadoRepository estadoRepository;

    /*
     * Estados usados por nombre en GestionTrabajosUseCase: renombrarlos o borrarlos rompería el flujo
     */
    private static final Set<String> ESTADOS_DEL_SISTEMA =
            Set.of("PENDIENTE", "ASIGNADO", "FINALIZADO", "CANCELADO");

    private void validarNoEsEstadoDelSistema(String nombreEstado) {
        if (ESTADOS_DEL_SISTEMA.contains(nombreEstado.trim().toUpperCase())) {
            throw new BusinessExceptions.ConflictException(
                    "El estado " + nombreEstado + " es usado por el sistema y no se puede modificar");
        }
    }

    public void crearEstado(EstadoRequest request){
        if (request.nombre() == null){
            throw new IllegalArgumentException("El nombre de estado es null");
        }
        if(estadoRepository.findByNombre(request.nombre()).isPresent()){
            throw new IllegalArgumentException("El nombre de estado ya existe");
        }
        Estado estado = new Estado(null, request.nombre());

        estadoRepository.save(estado);
    }
    public Estado obtenerEstadoPorNombre(String nombreEstado){
        if (nombreEstado == null){
            throw new IllegalArgumentException("El nombre de estado es null");
        }
        return estadoRepository.findByNombre(nombreEstado)
                .orElseThrow(() -> new IllegalArgumentException("El nombre de estado no existe"));
    }

    public Estado obtenerEstadoPorId(Long idEstado){
        if (idEstado == null){
            throw new IllegalArgumentException("El identificador de estado es null");
        }
        return estadoRepository
                .findById(idEstado).orElseThrow(()-> new IllegalArgumentException("El identificador de estado no existe"));
    }

    public void actualizarEstado(String nombreEstado, String nuevoEstado){
        if (nombreEstado == null){
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        Estado estado= estadoRepository.findByNombre(nombreEstado)
                .orElseThrow(()-> new BusinessExceptions.NotFoundException("El estado no existe"));
        validarNoEsEstadoDelSistema(nombreEstado);
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new BusinessExceptions.BadRequestException("El nuevo nombre no puede ser vacío");
        }
        estadoRepository.findByNombre(nuevoEstado)
                .filter(existente -> !existente.id().equals(estado.id()))
                .ifPresent(existente -> {
                    throw new BusinessExceptions.ConflictException("Ya existe un estado con nombre: " + nuevoEstado);
                });

        Estado actualizado= new Estado(
          estado.id(),nuevoEstado
        );
        estadoRepository.save(actualizado);
    }
    public void eliminarEstadoPorId(Long idEstado){
        if (idEstado == null){
            throw new IllegalArgumentException("El identificador de estado es null");
        }
        estadoRepository.deleteById(idEstado);
    }
    public void eliminarEstadoPorNombre(String nombreEstado){
        if (nombreEstado == null){
            throw new IllegalArgumentException("El nombre de estado es null");
        }
        estadoRepository.findByNombre(nombreEstado)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("El estado no existe"));
        validarNoEsEstadoDelSistema(nombreEstado);
        estadoRepository.deleteByNombre(nombreEstado);
    }
}
