package com.escaes.jobsy.application.usecase.admin;

import com.escaes.jobsy.application.dto.admin.MetricasResponse;
import com.escaes.jobsy.domain.model.Categoria;
import com.escaes.jobsy.domain.model.Estado;
import com.escaes.jobsy.domain.repository.CategoriaRepository;
import com.escaes.jobsy.domain.repository.EstadoRepository;
import com.escaes.jobsy.domain.repository.SolicitudRepository;
import com.escaes.jobsy.domain.repository.TrabajoRepository;
import com.escaes.jobsy.domain.repository.UsuarioRepository;
import com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Métricas para el dashboard del panel de administración.
 */
@Service
@RequiredArgsConstructor
public class ObtenerMetricasUseCase {

    private final UsuarioRepository usuarioRepository;

    private final TrabajoRepository trabajoRepository;

    private final SolicitudRepository solicitudRepository;

    private final EstadoRepository estadoRepository;

    private final CategoriaRepository categoriaRepository;

    public MetricasResponse obtenerMetricas() {
        long totalUsuarios = usuarioRepository.count();
        long usuariosBloqueados = usuarioRepository.countByBloqueado(true);

        // Se parte de todos los valores del catálogo en 0 para que el dashboard muestre cada serie
        Map<String, Long> trabajosPorEstado = new LinkedHashMap<>();
        estadoRepository.findAll().stream().map(Estado::nombre).forEach(nombre -> trabajosPorEstado.put(nombre, 0L));
        trabajosPorEstado.putAll(trabajoRepository.contarPorEstado());

        Map<String, Long> trabajosPorCategoria = new LinkedHashMap<>();
        categoriaRepository.findAll().stream().map(Categoria::nombre)
                .forEach(nombre -> trabajosPorCategoria.put(nombre, 0L));
        trabajosPorCategoria.putAll(trabajoRepository.contarPorCategoria());

        Map<String, Long> solicitudesPorEstado = new LinkedHashMap<>();
        Map<EstadoSolicitud, Long> conteoSolicitudes = solicitudRepository.contarPorEstado();
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            solicitudesPorEstado.put(estado.name(), conteoSolicitudes.getOrDefault(estado, 0L));
        }

        return new MetricasResponse(
                totalUsuarios,
                totalUsuarios - usuariosBloqueados,
                usuariosBloqueados,
                trabajoRepository.count(),
                trabajosPorEstado,
                trabajosPorCategoria,
                solicitudRepository.count(),
                solicitudesPorEstado);
    }
}
