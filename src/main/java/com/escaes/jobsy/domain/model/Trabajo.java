package com.escaes.jobsy.domain.model;

import java.util.Date;
import java.util.UUID;

public record Trabajo(UUID id, String titulo, String descripcion, Date fechaPublicacion,
        Double pago, String ubicacion, Usuario solicitante,
        Usuario trabajador, Categoria categoria, Estado estado, Pago tipoPago) {
}
