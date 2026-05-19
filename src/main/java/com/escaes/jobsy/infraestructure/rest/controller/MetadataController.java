package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.categoria.CatergoriaResponse;
import com.escaes.jobsy.application.dto.estado.EstadoResponse;
import com.escaes.jobsy.application.dto.genero.GeneroResponse;
import com.escaes.jobsy.application.dto.pago.PagoResponse;
import com.escaes.jobsy.application.usecase.categoria.ListarCategoriasUseCase;
import com.escaes.jobsy.application.usecase.estado.ListarEstadosUseCase;
import com.escaes.jobsy.application.usecase.genero.ListarGenerosUseCase;
import com.escaes.jobsy.application.usecase.pago.ListarPagosUseCase;
import com.escaes.jobsy.infraestructure.mapper.CategoriaMapper;
import com.escaes.jobsy.infraestructure.mapper.EstadoMapper;
import com.escaes.jobsy.infraestructure.mapper.GeneroMapper;
import com.escaes.jobsy.infraestructure.mapper.PagoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Metadata", description = "Endpoints públicos de consulta para poblar formularios y filtros en el frontend")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MetadataController {

    private final ListarCategoriasUseCase listarCategoriasUseCase;

    private final ListarEstadosUseCase listarEstadosUseCase;

    private final ListarGenerosUseCase listarGenerosUseCase;

    private final ListarPagosUseCase listarPagosUseCase;


    @Operation(summary = "Listar categorías (metadata)", description = "Devuelve todas las categorías disponibles. Usado para poblar selectores en formularios de creación de trabajos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías",
                content = @Content(schema = @Schema(implementation = CatergoriaResponse.class)))
    })
    @GetMapping("/lookups/category-all")
    public ResponseEntity<List<CatergoriaResponse>> listarCategoriasMetaData(){
        return ResponseEntity.ok(listarCategoriasUseCase.listarCategorias().stream()
                .map(CategoriaMapper::entityToResponse).toList());
    }

    @Operation(summary = "Listar estados (metadata)", description = "Devuelve todos los estados de trabajo disponibles. Usado para filtros y selectores en el frontend.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de estados",
                content = @Content(schema = @Schema(implementation = EstadoResponse.class)))
    })
    @GetMapping("/lookups/state-all")
    public ResponseEntity<List<EstadoResponse>> getAllEstadoMetaData(){
        return ResponseEntity.ok(listarEstadosUseCase.listarEstados().stream()
                .map(EstadoMapper::entityToResponse).toList());
    }

    @Operation(summary = "Listar géneros (metadata)", description = "Devuelve todos los géneros disponibles para el perfil de usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de géneros",
                content = @Content(schema = @Schema(implementation = GeneroResponse.class)))
    })
    @GetMapping("/lookups/gender-all")
    public ResponseEntity<List<GeneroResponse>> getAllGenerosMetaData(){
        return ResponseEntity.ok(listarGenerosUseCase.listarGeneros()
                .stream().map(GeneroMapper::entityToResponse).toList());
    }

    @Operation(summary = "Listar tipos de pago (metadata)", description = "Devuelve todos los tipos de pago disponibles (ej. EFECTIVO, TRANSFERENCIA). Usado para el formulario de creación de trabajo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de tipos de pago",
                content = @Content(schema = @Schema(implementation = PagoResponse.class)))
    })
    @GetMapping("/lookups/payment-all")
    public ResponseEntity<List<PagoResponse>> getAllPagosMetaData(){
        return ResponseEntity.ok(listarPagosUseCase.listar()
                .stream().map(PagoMapper::toResponse).toList());
    }
}
