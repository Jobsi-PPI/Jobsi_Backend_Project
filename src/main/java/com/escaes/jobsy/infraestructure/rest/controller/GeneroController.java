package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.genero.GeneroRequest;
import com.escaes.jobsy.application.dto.genero.GeneroResponse;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
//import com.escaes.jobsy.application.usecase.genero.ListarGenerosUseCase;
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

    //private final ListarGenerosUseCase listarGenerosUseCase;

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
}
