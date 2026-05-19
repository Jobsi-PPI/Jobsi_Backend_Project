package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.categoria.CategoriaRequest;
import com.escaes.jobsy.application.dto.categoria.CatergoriaResponse;
import com.escaes.jobsy.application.usecase.categoria.GestionCategoriasUseCase;
import com.escaes.jobsy.application.usecase.categoria.ListarCategoriasUseCase;
import com.escaes.jobsy.domain.model.Categoria;
import com.escaes.jobsy.infraestructure.mapper.CategoriaMapper;
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
@Tag(name = "Categorias", description = "Operaciones de gestión de categorías — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CategoriaController {

    private final GestionCategoriasUseCase gestionCategoriasUseCase;

    private final ListarCategoriasUseCase  listarCategoriasUseCase;

    @Operation(summary = "Listar todas las categorías", description = "Devuelve todas las categorías registradas en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías",
                content = @Content(schema = @Schema(implementation = CatergoriaResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/category-all")
    public ResponseEntity<List<CatergoriaResponse>> listarCategorias(){
        return ResponseEntity.ok(listarCategoriasUseCase.listarCategorias().stream()
                .map(CategoriaMapper::entityToResponse).toList());
    }

    @Operation(summary = "Buscar categoría por nombre", description = "Retorna la categoría cuyo nombre coincida exactamente con el proporcionado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                content = @Content(schema = @Schema(implementation = CatergoriaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/category/{name}")
    public ResponseEntity<CatergoriaResponse> getCategoria(
            @Parameter(description = "Nombre exacto de la categoría", example = "Tecnología")
            @PathVariable String name){
        Categoria categoria = gestionCategoriasUseCase.buscarCategoriaPorNombre(name);
        return ResponseEntity.ok(new CatergoriaResponse(categoria.nombre()));
    }

    @Operation(summary = "Crear nueva categoría", description = "Crea una categoría nueva. El nombre debe ser único.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                content = @Content(schema = @Schema(implementation = CatergoriaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o categoría ya existente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/category/create")
    public ResponseEntity<CatergoriaResponse> createCategoria(@RequestBody CategoriaRequest request){
        gestionCategoriasUseCase.crearCategoria(request.nombre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaMapper.requestToResponse(request));
    }

}
