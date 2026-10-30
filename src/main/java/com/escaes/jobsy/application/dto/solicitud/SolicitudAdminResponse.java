package com.escaes.jobsy.application.dto.solicitud;

import com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Vista de una solicitud para el panel de administración. Solo expone datos públicos del
 * postulante (nunca la entidad Usuario completa, que incluye el hash de la clave).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitudAdminResponse(
        UUID id,
        UUID trabajoId,
        String trabajoTitulo,
        String trabajadorNombre,
        String trabajadorCorreo,
        EstadoSolicitud estado,
        LocalDateTime fechaCreacion
) {
}
