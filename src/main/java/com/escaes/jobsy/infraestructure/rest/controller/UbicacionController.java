package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.ubicacion.UbicacionRequest;
import com.escaes.jobsy.application.dto.ubicacion.UbicacionResponse;
import com.escaes.jobsy.application.usecase.ubicacion.GestionUbicacionUseCase;
import com.escaes.jobsy.application.usecase.ubicacion.ListarUbicacionUseCase;
import com.escaes.jobsy.infraestructure.mapper.UbicacionMapper;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@Tag(name = "Ubicaciones", description = "Gestión de ubicaciones de trabajos — los endpoints de escritura/eliminación requieren autenticación")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UbicacionController {

    private final GestionUbicacionUseCase  gestionUbicacionUseCase;

    private final ListarUbicacionUseCase listarUbicacionUseCase;

    private static final Logger LOG = Logger.getLogger(UbicacionController.class.getName());


    @Operation(summary = "Crear ubicación", description = "Registra una nueva ubicación para ser asociada a trabajos.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ubicación creada exitosamente",
                content = @Content(schema = @Schema(implementation = UbicacionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ubicación ya existente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping("/ubication")
    public ResponseEntity<UbicacionResponse> createUbicacion(@RequestBody UbicacionRequest ubicacionRequest){
        LOG.info("Solicitud de creación de ubicación con nombre: "+ ubicacionRequest.nombre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UbicacionMapper
                        .domainToResponse(gestionUbicacionUseCase
                                .crear(ubicacionRequest)));
    }

    @Operation(summary = "Listar todas las ubicaciones", description = "Devuelve todas las ubicaciones registradas.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de ubicaciones",
                content = @Content(schema = @Schema(implementation = UbicacionResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/ubication")
    public ResponseEntity<List<UbicacionResponse>> listarUbicacion() {
        LOG.info("Solicitud obtener todas las ubicaciones");
        List<UbicacionResponse> responses = listarUbicacionUseCase
                .buscarTodos()
                .stream()
                .map(UbicacionMapper::domainToResponse).toList();
        return ResponseEntity.ok((responses));
    }

    @Operation(summary = "Listar ubicaciones (metadata)", description = "Endpoint público para poblar selectores de ubicación en formularios del frontend.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de ubicaciones",
                content = @Content(schema = @Schema(implementation = UbicacionResponse.class)))
    })
    @GetMapping("/lookups/ubication")
    public ResponseEntity<List<UbicacionResponse>> listarUbicacionMetaData(){
        LOG.info("Solicitud obtener todas las ubicaciones");
        List<UbicacionResponse> responses = listarUbicacionUseCase
                .buscarTodos()
                .stream()
                .map(UbicacionMapper::domainToResponse).toList();
        return ResponseEntity.ok((responses));
    }

    @Operation(summary = "Buscar ubicación por nombre", description = "Retorna la ubicación cuyo nombre coincida exactamente.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ubicación encontrada",
                content = @Content(schema = @Schema(implementation = UbicacionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ubicación no encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/ubication/{name}")
    public ResponseEntity<UbicacionResponse> obtenerUbicacion(
            @Parameter(description = "Nombre exacto de la ubicación", example = "Campus Norte")
            @PathVariable String name){
        LOG.info("Solicitud obtener ubicación con nombre: "+ name);
        UbicacionRequest ubicacionRequest = new UbicacionRequest(name);
        return ResponseEntity.ok(UbicacionMapper
                .domainToResponse(gestionUbicacionUseCase
                        .buscarPorNombre(ubicacionRequest)));
    }

    @Operation(summary = "Eliminar ubicación por nombre", description = "Elimina permanentemente la ubicación con el nombre indicado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ubicación eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ubicación no encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/ubication/{name}")
    public ResponseEntity<UbicacionResponse> deleteUbicacion(
            @Parameter(description = "Nombre exacto de la ubicación a eliminar", example = "Campus Norte")
            @PathVariable String name){
        LOG.info("Solicitud eliminar ubicación con nombre: "+ name);
        UbicacionRequest ubicacionRequest = new UbicacionRequest(name);
        gestionUbicacionUseCase.eliminarPorNombre(ubicacionRequest);
        return ResponseEntity.noContent().build();
    }
}
