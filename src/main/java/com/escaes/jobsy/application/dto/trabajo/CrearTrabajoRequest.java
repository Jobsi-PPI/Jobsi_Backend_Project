package com.escaes.jobsy.application.dto.trabajo;

public record CrearTrabajoRequest(
                String titulo,
                String descripcion,
                Double pago,
                String tipoPago,
                String ubicacion,
                String categoria) {
}