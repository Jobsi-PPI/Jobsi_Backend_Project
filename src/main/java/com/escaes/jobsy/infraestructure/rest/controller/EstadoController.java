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
}
