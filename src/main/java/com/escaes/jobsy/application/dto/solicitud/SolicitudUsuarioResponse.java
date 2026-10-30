package com.escaes.jobsy.application.dto.solicitud;

import com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitudUsuarioResponse(UUID solicitudId,
                                       String nombreUsuario,
                                       String correoUsuario,
                                       String telefonoUsuario,
                                       EstadoSolicitud estado,
                                       LocalDateTime fechaSolicitud,
                                       Double valoracionPromedio) {
}
