package com.escaes.jobsy.application.dto.admin;

import java.util.Map;

/*
 * Resumen para el dashboard de administración.
 * Los mapas incluyen todos los valores del catálogo, aunque su conteo sea 0.
 */
public record MetricasResponse(
        long totalUsuarios,
        long usuariosActivos,
        long usuariosBloqueados,
        long totalTrabajos,
        Map<String, Long> trabajosPorEstado,
        Map<String, Long> trabajosPorCategoria,
        long totalSolicitudes,
        Map<String, Long> solicitudesPorEstado
) {
}
