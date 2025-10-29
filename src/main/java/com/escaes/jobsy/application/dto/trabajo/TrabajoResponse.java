package com.escaes.jobsy.application.dto.trabajo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrabajoResponse(
                UUID id,
                String titulo,
                String descripcion,
                Double pago,
                String tipoPago,
                String ubicacion,
                String estado,
                String categoria,
                String solicitanteCorreo,
                String trabajadorCorreo) {
}