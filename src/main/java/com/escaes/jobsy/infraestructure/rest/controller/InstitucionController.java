package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.institucion.InstitucionRequest;
import com.escaes.jobsy.application.dto.institucion.InstitucionResponse;
import com.escaes.jobsy.application.dto.institucion.ubicacion.Departamento;
import com.escaes.jobsy.application.dto.institucion.ubicacion.DepartamentoSimpleResponse;
import com.escaes.jobsy.application.dto.institucion.ubicacion.Municipio;
import com.escaes.jobsy.application.service.UbicacionService;
import com.escaes.jobsy.application.usecase.institucion.GestionInstitucionesUseCase;
import com.escaes.jobsy.application.usecase.institucion.ListarInstitucionesUseCase;
import com.escaes.jobsy.domain.model.Institucion;
import com.escaes.jobsy.infraestructure.mapper.InstitucionMapper;
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
@RequestMapping("/v1")
@Tag(name = "Instituciones", description = "Gestión de instituciones educativas y consultas de ubicación geográfica (departamentos y municipios)")
@RequiredArgsConstructor
public class InstitucionController {

    private final ListarInstitucionesUseCase listarInstitucionesUseCase;

    private final GestionInstitucionesUseCase gestionInstitucionesUseCase;

    private final UbicacionService ubicacionService;

    @Operation(summary = "Crear institución", description = "Registra una nueva institución educativa en el sistema. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Institución creada exitosamente",
                content = @Content(schema = @Schema(implementation = InstitucionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/create-institution")
    public ResponseEntity<InstitucionResponse> crearInstitucion(@RequestBody InstitucionRequest request) {
        gestionInstitucionesUseCase.crearInstitucion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InstitucionMapper.requestToResponse(request));
    }

    @Operation(summary = "Listar departamentos y municipios", description = "Devuelve la estructura completa de departamentos con sus municipios anidados. Útil para construir selectores en cascada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estructura de departamentos y municipios",
                content = @Content(schema = @Schema(implementation = Departamento.class)))
    })
    @GetMapping("/public/departments-and-municipalities")
    public ResponseEntity<List<Departamento>> listarDepartamentosMunicipios() {
        return ResponseEntity.ok(ubicacionService.listarDepartamentos());
    }

    @Operation(summary = "Listar departamentos", description = "Devuelve únicamente los departamentos (código y nombre) sin municipios anidados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de departamentos",
                content = @Content(schema = @Schema(implementation = DepartamentoSimpleResponse.class)))
    })
    @GetMapping("/public/departments")
    public ResponseEntity<List<DepartamentoSimpleResponse>> listarDepartamentos() {
        List<Departamento> departamentos = ubicacionService.listarDepartamentos();
        return ResponseEntity.ok(departamentos.stream()
                .map(dep -> new DepartamentoSimpleResponse(dep.codigo(), dep.nombre()))
                .toList());
    }

    @Operation(summary = "Listar municipios por departamento", description = "Devuelve todos los municipios pertenecientes al departamento identificado por su código.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de municipios del departamento",
                content = @Content(schema = @Schema(implementation = Municipio.class))),
        @ApiResponse(responseCode = "404", description = "Departamento no encontrado", content = @Content)
    })
    @GetMapping("/public/departments/{code}/municipalities")
    public ResponseEntity<List<Municipio>> listarMunicipios(
            @Parameter(description = "Código del departamento", example = "05")
            @PathVariable String code) {
        return ResponseEntity.ok(ubicacionService.municipiosPorDepartamento(code));
    }

    @Operation(summary = "Listar instituciones por ubicación", description = "Devuelve las instituciones registradas en un departamento y municipio específico, identificados por sus códigos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de instituciones en la ubicación",
                content = @Content(schema = @Schema(implementation = InstitucionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Departamento o municipio no encontrado", content = @Content)
    })
    @GetMapping("/public/institutions/{departamentoCodigo}/{municipioCodigo}")
    public ResponseEntity<List<InstitucionResponse>> getInstitucionesPorUbicacion(
            @Parameter(description = "Código del departamento", example = "05")
            @PathVariable String departamentoCodigo,
            @Parameter(description = "Código del municipio", example = "001")
            @PathVariable String municipioCodigo) {

        String nombreDepartamento = ubicacionService.nombreDepartamento(departamentoCodigo);
        String nombreMunicipio = ubicacionService.nombreMunicipio(departamentoCodigo, municipioCodigo);

        List<Institucion> instituciones =
                listarInstitucionesUseCase.institucionesPorDepartamentoMunicipio(nombreDepartamento, nombreMunicipio);

        return ResponseEntity.ok(
                instituciones.stream()
                        .map(InstitucionMapper::entityToResponse)
                        .toList()
        );
    }

    @Operation(summary = "Listar todas las instituciones", description = "Devuelve todas las instituciones registradas en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado completo de instituciones",
                content = @Content(schema = @Schema(implementation = InstitucionResponse.class)))
    })
    @GetMapping("/public/institutions-all")
    public ResponseEntity<List<InstitucionResponse>> getInstituciones() {
        return ResponseEntity.ok(listarInstitucionesUseCase.listarInstituciones().stream()
                .map(InstitucionMapper::entityToResponse).toList());
    }

}
