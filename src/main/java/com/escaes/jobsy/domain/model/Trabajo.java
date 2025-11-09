package com.escaes.jobsy.domain.model;

import com.escaes.jobsy.application.dto.trabajo.CrearTrabajoRequest;

import java.util.Date;
import java.util.UUID;

public record Trabajo(UUID id, String titulo, String descripcion, Date fechaPublicacion,
                      Double pago, String ubicacion, Usuario solicitante,
                      Usuario trabajador, Categoria categoria, Estado estado, Pago tipoPago) {
    public static Trabajo crear(CrearTrabajoRequest request, Usuario solicitante, Usuario trabajador,
                                Categoria categoria, Estado estado, Pago tipoPago) {
        return new Trabajo(
                UUID.randomUUID(),
                request.titulo(),
                request.descripcion(),
                new Date(),
                request.pago(),
                request.ubicacion(),
                solicitante,
                trabajador,
                categoria,
                estado,
                tipoPago);
    }
}
