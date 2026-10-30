package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.valoracion.ValoracionCategoriaRequest;
import com.escaes.jobsy.application.dto.valoracion.ValoracionCategoriaResponse;
import com.escaes.jobsy.application.usecase.valoracion.GestionValoracionCategoriasUseCase;
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

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Categorías de valoración", description = "Criterios con los que se califica un trabajo finalizado — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ValoracionCategoriaController {

    private final GestionValoracionCategoriasUseCase gestionValoracionCategoriasUseCase;

    @Operation(summary = "Listar categorías de valoración", description = "Devuelve los criterios de calificación vigentes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías de valoración",
                content = @Content(schema = @Schema(implementation = ValoracionCategoriaResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/rating-category-all")
    public ResponseEntity<List<ValoracionCategoriaResponse>> listar() {
        return ResponseEntity.ok(gestionValoracionCategoriasUseCase.listar().stream()
                .map(categoria -> new ValoracionCategoriaResponse(categoria.nombre())).toList());
    }

    @Operation(summary = "Crear categoría de valoración", description = "Agrega un criterio de calificación. Al finalizar un trabajo se exige una puntuación por cada categoría, así que el frontend debe incluir la nueva.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría de valoración creada",
                content = @Content(schema = @Schema(implementation = ValoracionCategoriaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Nombre vacío", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ya existe", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/rating-category/create")
    public ResponseEntity<ValoracionCategoriaResponse> crear(@RequestBody ValoracionCategoriaRequest request) {
        gestionValoracionCategoriasUseCase.crear(request.nombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ValoracionCategoriaResponse(request.nombre()));
    }
}
