package com.escaes.jobsy.application.usecase.trabajo;

import com.escaes.jobsy.application.dto.trabajo.CrearTrabajoRequest;
import com.escaes.jobsy.application.dto.trabajo.FinalizarTrabajoRequest;
import com.escaes.jobsy.application.usecase.valoracion.GestionValoracionCategoriasUseCase;
import com.escaes.jobsy.domain.model.*;
import com.escaes.jobsy.domain.repository.*;
import com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class GestionTrabajosUseCase {

    private final TrabajoRepository trabajoRepository;

    private final UsuarioRepository usuarioRepository;

    private final EstadoRepository estadoRepository;

    private final CategoriaRepository categoriaRepository;

    private final PagoRepository pagoRepository;

    private final UbicacionRepository ubicacionRepository;

    private final SolicitudRepository solicitudRepository;

    private final ValoracionRepository valoracionRepository;

    private final GestionValoracionCategoriasUseCase gestionValoracionCategoriasUseCase;

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

        Ubicacion ubicacion=  ubicacionRepository.findByNombre(request.ubicacion());

        Trabajo trabajo = Trabajo.crear(request, userSolcitante,ubicacion, null, categoria, estado, tipoPago);

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
    @Transactional
    public Trabajo asignarTrabajo(UUID trabajoId, UUID solicitudId) {

        Trabajo trabajo = trabajoRepository
                .findById(trabajoId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajo no encontrado"));

        if (!"PENDIENTE".equals(trabajo.estado().nombre())) {
            throw new BusinessExceptions.BadRequestException("Trabajo no disponible para asignarse");
        }

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Solicitud no encontrada"));

        if (!solicitud.trabajo().id().equals(trabajoId)) {
            throw new BusinessExceptions.BadRequestException("La solicitud no pertenece al trabajo");
        }

        Usuario trabajador = solicitud.trabajador();

        if (trabajador.correo().equals(trabajo.solicitante().correo())) {
            throw new BusinessExceptions.BadRequestException("No puedes asignarte tu propio trabajo");
        }

        Estado estadoAsignado = estadoRepository.findByNombre("ASIGNADO")
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Estado 'ASIGNADO' no existe"));

        Trabajo updated = new Trabajo(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(),
                trabajo.solicitante(),
                trabajador,
                trabajo.categoria(),
                estadoAsignado,
                trabajo.tipoPago()
        );

        trabajoRepository.save(updated);

        solicitudRepository.actualizarEstado(solicitudId, EstadoSolicitud.ACEPTADA);
        solicitudRepository.rechazarOtrasSolicitudes(trabajoId, solicitudId);

        return updated;
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

    public void abandonarTrabajoPorIdYUsuarioCorreoTrabajador(UUID jobId, String correoTrabajador) {
        Trabajo trabajo = trabajoRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));

        if (trabajo.trabajador() == null) {
            throw new RuntimeException("El trabajo no tiene trabajador asignado");
        }

        if (!trabajo.trabajador().correo().equals(correoTrabajador)) {
            throw new RuntimeException("No puedes abandonar un trabajo que no te pertenece");
        }

        // Se cambia el estado a PENDIENTE
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no existe"));

        Trabajo updated = new Trabajo(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(),
                trabajo.solicitante(),
                null,        // quitar trabajador
                trabajo.categoria(),
                estadoPendiente,      // volver a pendiente
                trabajo.tipoPago()
        );

        trabajoRepository.save(updated);
    }

    /**
     * Finaliza un trabajo ASIGNADO: solo el solicitante (quien contrató) puede hacerlo.
     * En la misma operación registra una valoración por cada categoría sembrada y
     * recalcula la valoración del trabajador. Un trabajo finalizado cuenta como
     * +1 en valoracionConteo (no +1 por categoría); aporta una sola nota al promedio.
     */
    @Transactional
    public Trabajo finalizarTrabajo(UUID jobId, FinalizarTrabajoRequest request, String solicitanteCorreo) {

        Trabajo trabajo = trabajoRepository.findById(jobId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajo no encontrado"));

        if (!"ASIGNADO".equals(trabajo.estado().nombre())) {
            throw new BusinessExceptions.BadRequestException(
                    "El trabajo no está asignado, no se puede finalizar");
        }
        if (!trabajo.solicitante().correo().equals(solicitanteCorreo)) {
            throw new BusinessExceptions.ForbiddenException(
                    "Solo el creador del trabajo puede finalizarlo");
        }
        if (trabajo.trabajador() == null) {
            throw new BusinessExceptions.BadRequestException("El trabajo no tiene trabajador asignado");
        }

        List<ValoracionCategoria> categorias = gestionValoracionCategoriasUseCase.listar();
        if (request == null || request.puntuaciones() == null
                || request.puntuaciones().size() != categorias.size()) {
            throw new BusinessExceptions.BadRequestException(
                    "Debes enviar una puntuación para cada una de las " + categorias.size()
                            + " categorías de valoración");
        }

        // Pasar el trabajo a FINALIZADO
        Estado estadoFinalizado = estadoRepository.findByNombre("FINALIZADO")
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Estado 'FINALIZADO' no existe"));

        Trabajo trabajoFinalizado = new Trabajo(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(),
                trabajo.solicitante(),
                trabajo.trabajador(),
                trabajo.categoria(),
                estadoFinalizado,
                trabajo.tipoPago());
        trabajoRepository.save(trabajoFinalizado);

        // Registrar una valoración por categoría y acumular la nota del trabajo
        Set<String> categoriasVistas = new HashSet<>();
        double suma = 0;
        for (FinalizarTrabajoRequest.PuntuacionCategoria p : request.puntuaciones()) {
            if (p.categoria() == null || p.puntuacion() == null) {
                throw new BusinessExceptions.BadRequestException(
                        "Cada valoración requiere categoría y puntuación");
            }
            if (p.puntuacion() < 1 || p.puntuacion() > 5) {
                throw new BusinessExceptions.BadRequestException(
                        "La puntuación debe estar entre 1 y 5");
            }
            ValoracionCategoria categoria = gestionValoracionCategoriasUseCase.obtenerPorNombre(p.categoria());
            if (!categoriasVistas.add(categoria.nombre().toLowerCase())) {
                throw new BusinessExceptions.BadRequestException(
                        "Categoría de valoración repetida: " + p.categoria());
            }
            valoracionRepository.save(new Valoracion(
                    null, trabajoFinalizado, categoria, p.puntuacion(), request.comentario()));
            suma += p.puntuacion();
        }

        // Nota del trabajo = promedio de sus categorías -> aporta UN valor al promedio del trabajador
        double notaTrabajo = suma / request.puntuaciones().size();

        Usuario trabajador = usuarioRepository.findByCorreo(trabajo.trabajador().correo())
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajador no encontrado"));

        int conteoActual = trabajador.valoracionConteo() != null ? trabajador.valoracionConteo() : 0;
        double promedioActual = trabajador.valoracionPromedio() != null ? trabajador.valoracionPromedio() : 0.0;
        int nuevoConteo = conteoActual + 1;
        double nuevoPromedio = (promedioActual * conteoActual + notaTrabajo) / nuevoConteo;
        nuevoPromedio = Math.round(nuevoPromedio * 10.0) / 10.0;

        Usuario trabajadorActualizado = new Usuario(
                trabajador.documento(),
                trabajador.nombre(),
                trabajador.primerApellido(),
                trabajador.segundoApellido(),
                trabajador.correo(),
                trabajador.clave(),
                trabajador.telefono(),
                trabajador.bloqueado(),
                trabajador.fechaNacimiento(),
                nuevoConteo,
                nuevoPromedio,
                trabajador.genero(),
                trabajador.rol(),
                trabajador.trabajos(),
                trabajador.trabajosRealizados());
        usuarioRepository.save(trabajadorActualizado);

        return trabajoFinalizado;
    }

    /*
     * Moderación (ADMIN): cancela un trabajo activo y cierra sus postulaciones pendientes.
     */
    @Transactional
    public Trabajo cancelarTrabajoAdmin(UUID jobId) {
        Trabajo trabajo = trabajoRepository.findById(jobId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajo no encontrado"));
        String estadoActual = trabajo.estado().nombre();
        if ("FINALIZADO".equals(estadoActual) || "CANCELADO".equals(estadoActual)) {
            throw new BusinessExceptions.ConflictException("El trabajo ya está " + estadoActual.toLowerCase());
        }
        Estado estadoCancelado = estadoRepository.findByNombre("CANCELADO")
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Estado 'CANCELADO' no existe"));
        Trabajo cancelado = new Trabajo(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(),
                trabajo.solicitante(),
                trabajo.trabajador(),
                trabajo.categoria(),
                estadoCancelado,
                trabajo.tipoPago());
        trabajoRepository.save(cancelado);
        solicitudRepository.cerrarSolicitudesPendientes(jobId);
        return cancelado;
    }

    /*
     * Moderación (ADMIN): elimina cualquier trabajo junto con sus postulaciones.
     * Las valoraciones se eliminan en cascada desde la entidad del trabajo.
     */
    @Transactional
    public void eliminarTrabajoAdmin(UUID jobId) {
        trabajoRepository.findById(jobId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajo no encontrado"));
        solicitudRepository.deleteByTrabajoId(jobId);
        trabajoRepository.deleteById(jobId);
    }

}
