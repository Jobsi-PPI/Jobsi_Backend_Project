package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.admin.MetricasResponse;
import com.escaes.jobsy.application.usecase.admin.ObtenerMetricasUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Tag(name = "Administración", description = "Métricas para el dashboard del panel de administración — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminMetricasController {

    private final ObtenerMetricasUseCase obtenerMetricasUseCase;

    @Operation(summary = "Métricas del dashboard", description = "Totales de usuarios (activos y bloqueados), trabajos por estado y categoría, y postulaciones por estado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métricas actuales",
                content = @Content(schema = @Schema(implementation = MetricasResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/metrics")
    public ResponseEntity<MetricasResponse> obtenerMetricas() {
        return ResponseEntity.ok(obtenerMetricasUseCase.obtenerMetricas());
    }
}
