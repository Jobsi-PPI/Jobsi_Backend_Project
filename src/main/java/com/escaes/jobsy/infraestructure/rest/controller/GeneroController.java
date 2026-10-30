package com.escaes.jobsy.infraestructure.rest.controller;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import com.escaes.jobsy.application.dto.genero.GeneroRequest;
import com.escaes.jobsy.application.dto.genero.GeneroResponse;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.genero.ListarGenerosUseCase;
import com.escaes.jobsy.infraestructure.mapper.GeneroMapper;
import io.swagger.v3.oas.annotations.Operation;
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



@RestController
@RequestMapping("/v1")
@Tag(name = "Generos", description = "Gestión de géneros de usuario — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class GeneroController {

    private final GestionGenerosUseCase gestionGenerosUseCase;

    private final ListarGenerosUseCase listarGenerosUseCase;

    @Operation(summary = "Crear nuevo género", description = "Registra un nuevo género disponible para el perfil de usuario (ej. MASCULINO, FEMENINO, NO_BINARIO).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Género creado exitosamente",
                content = @Content(schema = @Schema(implementation = GeneroResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o género ya existente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/gender/create")
    public ResponseEntity<GeneroResponse> crearGenero(@RequestBody GeneroRequest request) {
        gestionGenerosUseCase.crearGenero(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneroMapper.requestToResponse(request));
    }

    @Operation(summary = "Listar géneros", description = "Devuelve todos los géneros registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de géneros",
                content = @Content(schema = @Schema(implementation = GeneroResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/gender-all")
    public ResponseEntity<List<GeneroResponse>> listarGeneros() {
        return ResponseEntity.ok(listarGenerosUseCase.listarGeneros().stream()
                .map(GeneroMapper::entityToResponse).toList());
    }

    @Operation(summary = "Renombrar género", description = "Cambia el nombre de un género. El nuevo nombre debe ser único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Género actualizado",
                content = @Content(schema = @Schema(implementation = GeneroResponse.class))),
        @ApiResponse(responseCode = "404", description = "Género no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ya existe un género con ese nombre", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PutMapping("/admin/gender/{name}")
    public ResponseEntity<GeneroResponse> actualizarGenero(
            @Parameter(description = "Nombre actual del género", example = "Alien") @PathVariable String name,
            @RequestBody GeneroRequest request) {
        return ResponseEntity.ok(GeneroMapper.entityToResponse(
                gestionGenerosUseCase.actualizarGenero(name, request.nombre())));
    }

    @Operation(summary = "Eliminar género", description = "Elimina un género. Si hay usuarios con ese género responde 409.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Género eliminado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Género no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El género está en uso", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @DeleteMapping("/admin/gender/{name}")
    public ResponseEntity<Void> eliminarGenero(
            @Parameter(description = "Nombre del género", example = "Alien") @PathVariable String name) {
        gestionGenerosUseCase.eliminarGeneroPorNombre(name);
        return ResponseEntity.noContent().build();
    }
}
