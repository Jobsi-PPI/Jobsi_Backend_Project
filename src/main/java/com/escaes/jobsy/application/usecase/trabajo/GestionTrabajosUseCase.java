package com.escaes.jobsy.application.usecase.trabajo;

import com.escaes.jobsy.application.dto.trabajo.CrearTrabajoRequest;
import com.escaes.jobsy.domain.model.*;
import com.escaes.jobsy.domain.repository.*;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class GestionTrabajosUseCase {

    private final TrabajoRepository trabajoRepository;

    private final UsuarioRepository usuarioRepository;

    private final EstadoRepository estadoRepository;

    private final CategoriaRepository categoriaRepository;

    private final PagoRepository pagoRepository;

    /*
     * Eventualmente, delimitar cuantos trabajos activos puede crear un usuario
     * */
    private void validateTrabajo(CrearTrabajoRequest request, String solicitanteCorreo) {
        if (request == null) {
            throw new IllegalArgumentException("El trabajo no puede ser nulo");
        }
        if (solicitanteCorreo == null || solicitanteCorreo.isEmpty()) {
            throw new BusinessExceptions.BadRequestException("El solicitante no puede ser nulo");
        }
        if (request.pago() <= 0) {
            throw new BusinessExceptions.BadRequestException("El pago debe ser un valor positivo");
        }
        if (request.titulo().isEmpty() || request.titulo().isBlank()) {
            throw new BusinessExceptions.BadRequestException("El titulo no puede ser nulo o vacio");
        }
        if (request.ubicacion().isEmpty()) {
            throw new BusinessExceptions.BadRequestException("La ubicacion no puede ser nulo o vacio");
        }
    }

    public Trabajo crearTrabajo(CrearTrabajoRequest request, String solicitanteCorreo) {

        validateTrabajo(request, solicitanteCorreo);
        Usuario userSolcitante = usuarioRepository.findByCorreo(solicitanteCorreo)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findByNombre(request.categoria())
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Categoria no encontrado"));

        Estado estado = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Estado no encontrado"));

        Pago tipoPago = pagoRepository.findByNombre(request.tipoPago())
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Tipo de pago no encontrado"));

        Trabajo trabajo = Trabajo.crear(request, userSolcitante, null, categoria, estado, tipoPago);

        trabajoRepository.save(trabajo);

        return trabajo;
    }

    public Trabajo obtenerTrabajoPorId(UUID id) {
        return trabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));
    }

    /*
     *Eventualmente, limitar que un usuario no pueda asignarse mas de X trabajos al mismo tiempo
     * */
    public Trabajo asignarTrabajo(UUID trabajoId, String trabajadorCorreo) {

        Trabajo trabajo = trabajoRepository
                .findById(trabajoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        if (!"PENDIENTE".equals(trabajo.estado().nombre())) {
            throw new IllegalArgumentException("Trabajo no disponible para asignarse");
        }

        Usuario trabajador = usuarioRepository.findByCorreo(trabajadorCorreo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (trabajadorCorreo.equals(trabajo.solicitante().correo())) {
            throw new IllegalArgumentException("No puedes asginarte tu propio trabajo :O ");
        }


        Estado estadoAsignado = estadoRepository.findByNombre("ASIGNADO")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'ASIGNADO' no existe"));

        Trabajo updated = new Trabajo(
                trabajo.id(), trabajo.titulo(), trabajo.descripcion(), trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(), trabajo.solicitante(), trabajador, trabajo.categoria(),
                estadoAsignado,
                trabajo.tipoPago());

        trabajoRepository.save(updated);

        return (updated);
    }

    public void eliminarTrabajoPorIdYUsuarioCorreoSolicitante(UUID id, String solicitanteCorreo) {
        Trabajo trabajo = obtenerTrabajoPorId(id);
        if (!trabajo.solicitante().correo().equals(solicitanteCorreo)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este trabajo");
        }
        trabajoRepository.deleteById(id);
    }
    public void eliminarAplicacionATrabajoCorreoTrabajador(UUID id, String trabajadorCorreo){
        Trabajo trabajo = obtenerTrabajoPorId(id);

        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no existe"));

        Optional<Trabajo> job = trabajoRepository
                .findByTrabajadorCorreoAndEstado(trabajadorCorreo, "ASIGNADO")
                .stream()
                .filter(t -> t.id().equals(trabajo.id()))
                .findFirst();

        if (job.isEmpty()) {
            throw new BusinessExceptions.NotFoundException(
                    "No se puede quitar aplicación a trabajo ya que no está en tus trabajos con estado asignado"
            );
        }
        Trabajo trabajoActualizado = new Trabajo(
                job.get().id(),
                job.get().titulo(),
                job.get().descripcion(),
                job.get().fechaPublicacion(),
                job.get().pago(),
                job.get().ubicacion(),
                job.get().solicitante(),
                null,
                job.get().categoria(),
                estadoPendiente,
                job.get().tipoPago()
        );

        trabajoRepository.save(trabajoActualizado);
    }

}
