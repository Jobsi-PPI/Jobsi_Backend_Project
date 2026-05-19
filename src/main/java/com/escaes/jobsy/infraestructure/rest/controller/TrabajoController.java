package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.trabajo.CrearTrabajoRequest;
import com.escaes.jobsy.application.dto.trabajo.FinalizarTrabajoRequest;
import com.escaes.jobsy.application.dto.trabajo.TrabajoResponse;
import com.escaes.jobsy.application.usecase.trabajo.GestionTrabajosUseCase;
import com.escaes.jobsy.application.usecase.trabajo.ListarTrabajosUseCase;
import com.escaes.jobsy.domain.model.Trabajo;
import com.escaes.jobsy.infraestructure.mapper.TrabajoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@Tag(name = "Trabajos", description = "Gestión completa del ciclo de vida de trabajos: publicación, búsqueda, asignación, finalización y abandono")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TrabajoController {

    private final GestionTrabajosUseCase gestionTrabajosUseCase;

    private final ListarTrabajosUseCase listarTrabajosUseCase;

    private static final Logger LOG = Logger.getLogger(TrabajoController.class.getName());

    @Operation(summary = "Publicar trabajo", description = "Crea y publica un nuevo trabajo. El solicitante se toma del token JWT del usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Trabajo creado y publicado exitosamente",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (campos requeridos faltantes, categoría o estado inexistente)", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping("/jobs/create")
    public ResponseEntity<TrabajoResponse> crearTrabajo(
            @RequestBody CrearTrabajoRequest request,
            Authentication authentication) {
        String solicitanteCorreo = authentication.getName();
        Trabajo trabajoCreado = gestionTrabajosUseCase.crearTrabajo(request, solicitanteCorreo);
        LOG.info("Trabajo creado correctamente para usuario: " + solicitanteCorreo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TrabajoMapper.entityToResponse(trabajoCreado));
    }

    @Operation(summary = "Listar todos los trabajos", description = "Devuelve todos los trabajos publicados en la plataforma. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de todos los trabajos",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class)))
    })
    @GetMapping("/public/all-jobs")
    public ResponseEntity<List<TrabajoResponse>> obtenerTrabajos() {
        List<Trabajo> trabajos = listarTrabajosUseCase.listar();
        List<TrabajoResponse> responses = trabajos.stream()
                .map(TrabajoMapper::entityToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar trabajos con filtros", description = "Búsqueda dinámica de trabajos con múltiples filtros opcionales. Soporta paginación. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de trabajos que coinciden con los filtros",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class)))
    })
    @GetMapping("/public/jobs/search")
    public ResponseEntity<List<TrabajoResponse>> buscarTrabajos(
            @Parameter(description = "Filtrar por título (búsqueda parcial)", example = "Diseño") @RequestParam(required = false) String titulo,
            @Parameter(description = "Filtrar por nombre de categoría", example = "Tecnología") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por estado del trabajo", example = "DISPONIBLE") @RequestParam(required = false) String estado,
            @Parameter(description = "Filtrar por nombre de ubicación", example = "Campus Norte") @RequestParam(required = false) String ubicacion,
            @Parameter(description = "Filtrar por tipo de pago", example = "EFECTIVO") @RequestParam(required = false) String tipoPago,
            @Parameter(description = "Pago mínimo", example = "10000") @RequestParam(required = false) Double pagoMin,
            @Parameter(description = "Pago máximo", example = "100000") @RequestParam(required = false) Double pagoMax,
            @Parameter(description = "Filtrar por correo del solicitante", example = "usuario@email.com") @RequestParam(required = false) String solicitanteCorreo,
            @Parameter(description = "Cantidad de resultados por página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") int page) {

        List<TrabajoResponse> responses = listarTrabajosUseCase.buscarTrabajos(
                titulo, categoria, estado, ubicacion, tipoPago, pagoMin, pagoMax,
                solicitanteCorreo, size, page)
                .stream()
                .map(TrabajoMapper::entityToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Mis trabajos publicados", description = "Devuelve los trabajos publicados por el usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de trabajos publicados por el usuario",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/jobs/posted-by-me")
    public ResponseEntity<List<TrabajoResponse>> obtenerTrabajosMyJobs(Authentication auth) {
        String solicitanteCorreo = auth.getName();
        List<Trabajo> trabajos = listarTrabajosUseCase.listarPorUsuarioSolicitante(solicitanteCorreo);
        List<TrabajoResponse> responses = trabajos.stream()
                .map(TrabajoMapper::entityToResponse)
                .toList();
        LOG.info("Trabajos obtenidos para usuario: " + solicitanteCorreo);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Trabajos en los que participo", description = "Devuelve los trabajos en los que el usuario autenticado está registrado como trabajador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de trabajos donde el usuario es trabajador",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/jobs/worked-by-me")
    public ResponseEntity<List<TrabajoResponse>> obtenerTrabajosWorkedByMe(Authentication auth) {
        String trabajadorCorreo = auth.getName();
        List<Trabajo> trabajos = listarTrabajosUseCase.listarPorUsuarioTrabajador(trabajadorCorreo);
        List<TrabajoResponse> responses = trabajos.stream()
                .map(TrabajoMapper::entityToResponse)
                .toList();
        LOG.info("Trabajos obtenidos para usuario: " + trabajadorCorreo);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Asignar trabajador a un trabajo", description = "Asigna un trabajador (identificado por su solicitud) al trabajo indicado. Solo el creador del trabajo puede realizar esta acción.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trabajador asignado exitosamente",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class))),
        @ApiResponse(responseCode = "400", description = "El trabajo ya tiene trabajador asignado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo o solicitud no encontrada", content = @Content)
    })
    @PatchMapping("/jobs/apply-job/{jobId}")
    public ResponseEntity<TrabajoResponse> aplicarTrabajo(
            @Parameter(description = "ID UUID del trabajo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId, UUID solicitudId, Authentication auth){
        String trabajadorCorreo = auth.getName();
        Trabajo appliedJob = gestionTrabajosUseCase.asignarTrabajo(jobId, solicitudId);
        LOG.info("Trabajo aplicado correo: " + trabajadorCorreo);
        return ResponseEntity.ok(TrabajoMapper.entityToResponse(appliedJob));
    }

    @Operation(summary = "Retirar asignación de un trabajo", description = "Elimina la asignación del trabajador autenticado en el trabajo indicado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asignación eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado o el usuario no está asignado", content = @Content)
    })
    @PatchMapping("/jobs/unassigned-job/{jobId}")
    public ResponseEntity<Void> eliminarTrabajoAsignado(
            @Parameter(description = "ID UUID del trabajo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId, Authentication auth){
        String trabajadorCorreo = auth.getName();
        gestionTrabajosUseCase.eliminarAplicacionATrabajoCorreoTrabajador(jobId, trabajadorCorreo);
        LOG.info("Trabajo quita aplicación correo: " + trabajadorCorreo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar trabajo publicado", description = "Elimina permanentemente un trabajo publicado por el usuario autenticado. Solo el creador puede eliminarlo.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Trabajo eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no es el creador del trabajo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado", content = @Content)
    })
    @DeleteMapping("/jobs/published/delete/{jobId}")
    public ResponseEntity<Void> eliminarTrabajoPublicado(
            @Parameter(description = "ID UUID del trabajo a eliminar", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId, Authentication auth) {
        String solicitanteCorreo = auth.getName();
        gestionTrabajosUseCase.eliminarTrabajoPorIdYUsuarioCorreoSolicitante(jobId, solicitanteCorreo);
        LOG.info("Trabajo eliminado id: " + jobId + " solicitante correo: " + solicitanteCorreo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar trabajo", description = "Marca el trabajo como finalizado. Solo el creador puede finalizarlo. Puede incluir valoración del trabajador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trabajo finalizado exitosamente",
                content = @Content(schema = @Schema(implementation = TrabajoResponse.class))),
        @ApiResponse(responseCode = "400", description = "El trabajo no puede finalizarse en su estado actual", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no es el creador del trabajo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado", content = @Content)
    })
    @PatchMapping("/jobs/finalize/{jobId}")
    public ResponseEntity<TrabajoResponse> finalizarTrabajo(
            @Parameter(description = "ID UUID del trabajo a finalizar", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId,
            @RequestBody FinalizarTrabajoRequest request,
            Authentication auth) {
        String solicitanteCorreo = auth.getName();
        Trabajo trabajoFinalizado = gestionTrabajosUseCase.finalizarTrabajo(jobId, request, solicitanteCorreo);
        LOG.info("Trabajo finalizado id: " + jobId + ", solicitante: " + solicitanteCorreo);
        return ResponseEntity.ok(TrabajoMapper.entityToResponse(trabajoFinalizado));
    }

    @Operation(summary = "Abandonar trabajo", description = "El trabajador autenticado abandona el trabajo al que estaba asignado, liberándolo para que otro pueda tomarlo.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Trabajo abandonado exitosamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "El trabajo no puede abandonarse en su estado actual", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado o el usuario no es el trabajador asignado", content = @Content)
    })
    @PatchMapping("/jobs/abandon/{jobId}")
    public ResponseEntity<Void> abandonarTrabajo(
            @Parameter(description = "ID UUID del trabajo a abandonar", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId, Authentication auth) {
        String trabajadorCorreo = auth.getName();
        gestionTrabajosUseCase.abandonarTrabajoPorIdYUsuarioCorreoTrabajador(jobId, trabajadorCorreo);
        LOG.info("Trabajo abandonado. JobId: " + jobId + ", trabajador: " + trabajadorCorreo);
        return ResponseEntity.noContent().build();
    }

}



