package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.estado.EstadoRequest;
import com.escaes.jobsy.application.dto.estado.EstadoResponse;
import com.escaes.jobsy.application.usecase.estado.GestionEstadosUseCase;
import com.escaes.jobsy.application.usecase.estado.ListarEstadosUseCase;
import com.escaes.jobsy.domain.model.Estado;
import com.escaes.jobsy.infraestructure.mapper.EstadoMapper;
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

@RestController
@Tag(name = "Estados", description = "Gestión de estados de trabajos — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class EstadoController {

    private final GestionEstadosUseCase gestionEstadosUseCase;

    private final ListarEstadosUseCase listarEstadosUseCase;


    @Operation(summary = "Crear nuevo estado", description = "Registra un nuevo estado de trabajo (ej. DISPONIBLE, EN_PROCESO, FINALIZADO).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estado creado exitosamente",
                content = @Content(schema = @Schema(implementation = EstadoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o estado ya existente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/create/state")
    public ResponseEntity<EstadoResponse> createEstado(@RequestBody EstadoRequest request){
        gestionEstadosUseCase.crearEstado(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EstadoMapper.requestToResponse(request));
    }

    @Operation(summary = "Buscar estado por nombre", description = "Retorna el estado cuyo nombre coincida exactamente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado encontrado",
                content = @Content(schema = @Schema(implementation = EstadoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/state/{name}")
    public ResponseEntity<EstadoResponse> getEstado(
            @Parameter(description = "Nombre exacto del estado", example = "DISPONIBLE")
            @PathVariable String name){
        Estado estado = gestionEstadosUseCase.obtenerEstadoPorNombre(name);
        return ResponseEntity.ok(EstadoMapper.entityToResponse(estado));
    }

    @Operation(summary = "Listar todos los estados", description = "Devuelve todos los estados de trabajo disponibles en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de estados",
                content = @Content(schema = @Schema(implementation = EstadoResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/state-all")
    public ResponseEntity<List<EstadoResponse>> getAllEstado(){
        return ResponseEntity.ok(listarEstadosUseCase.listarEstados().stream().map(
                EstadoMapper::entityToResponse).toList());
    }

    @Operation(summary = "Renombrar estado", description = "Cambia el nombre de un estado. Los estados usados por el sistema (PENDIENTE, ASIGNADO, FINALIZADO, CANCELADO) no se pueden modificar.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
                content = @Content(schema = @Schema(implementation = EstadoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Estado del sistema o nombre duplicado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PutMapping("/admin/state/{name}")
    public ResponseEntity<EstadoResponse> actualizarEstado(
            @Parameter(description = "Nombre actual del estado", example = "BLOQUEADO") @PathVariable String name,
            @RequestBody EstadoRequest request) {
        gestionEstadosUseCase.actualizarEstado(name, request.nombre());
        return ResponseEntity.ok(EstadoMapper.requestToResponse(request));
    }

    @Operation(summary = "Eliminar estado", description = "Elimina un estado que no sea del sistema ni esté en uso por algún trabajo.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estado eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Estado del sistema o en uso", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @DeleteMapping("/admin/state/{name}")
    public ResponseEntity<Void> eliminarEstado(
            @Parameter(description = "Nombre del estado", example = "BLOQUEADO") @PathVariable String name) {
        gestionEstadosUseCase.eliminarEstadoPorNombre(name);
        return ResponseEntity.noContent().build();
    }
}
