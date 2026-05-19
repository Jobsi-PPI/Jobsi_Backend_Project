package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.solicitud.SolicitudRequest;
import com.escaes.jobsy.application.dto.solicitud.SolicitudResponse;
import com.escaes.jobsy.application.dto.solicitud.SolicitudUsuarioResponse;
import com.escaes.jobsy.application.usecase.solicitud.GestionSolicitudesUseCase;
import com.escaes.jobsy.application.usecase.solicitud.ListarSolicitudesUseCase;
import com.escaes.jobsy.infraestructure.mapper.SolicitudMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@Tag(name = "Solicitudes", description = "Gestión de postulaciones a trabajos — todos los endpoints requieren autenticación JWT")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SolicitudesController {

    private final GestionSolicitudesUseCase gestionSolicitudesUseCase;

    private final ListarSolicitudesUseCase listarSolicitudesUseCase;

    private static final Logger LOG = Logger.getLogger(SolicitudesController.class.getName());

    @Operation(summary = "Postularse a un trabajo", description = "Crea una solicitud del usuario autenticado para el trabajo indicado en el body. El correo del solicitante se extrae del token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitud creada exitosamente",
                content = @Content(schema = @Schema(implementation = SolicitudResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o el usuario ya se postuló", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado", content = @Content)
    })
    @PostMapping("/requests")
    public ResponseEntity<SolicitudResponse> aplicarTrabajo(@RequestBody @Valid SolicitudRequest request,
            Authentication authentication) {

        String trabajadorCorreo = authentication.getName();

        return ResponseEntity
                .ok(SolicitudMapper.toResponse(gestionSolicitudesUseCase.crearSolicitud(request, trabajadorCorreo)));
    }

    @Operation(summary = "Listar postulaciones de un trabajo", description = "Devuelve todas las solicitudes recibidas para un trabajo específico. Solo el creador del trabajo puede consultarlas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de postulaciones al trabajo",
                content = @Content(schema = @Schema(implementation = SolicitudUsuarioResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no es el creador del trabajo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Trabajo no encontrado", content = @Content)
    })
    @GetMapping("/requests/job/{jobId}")
    public ResponseEntity<List<SolicitudUsuarioResponse>> aplicacionesTrabajoId(
            @Parameter(description = "ID UUID del trabajo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID jobId, Authentication authentication) {
        LOG.info("Listando solicitudes para trabajo con Id: " + jobId);

        return ResponseEntity.ok(
                listarSolicitudesUseCase.listarSolicitudes(jobId, authentication.getName()));
    }

    @Operation(summary = "Mis postulaciones", description = "Devuelve todas las solicitudes enviadas por el usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de postulaciones del usuario",
                content = @Content(schema = @Schema(implementation = SolicitudResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/requests-applied")
    public ResponseEntity<List<SolicitudResponse>> aplicacionesTrabajoCorreoUser(Authentication authentication) {
        LOG.info("Listando aplicaciones para usuario con correo: " + authentication.getName());

        String trabajadorCorreo = authentication.getName();

        return ResponseEntity.ok(listarSolicitudesUseCase.listarSolicitudesPorUsuario(trabajadorCorreo));
    }

}
