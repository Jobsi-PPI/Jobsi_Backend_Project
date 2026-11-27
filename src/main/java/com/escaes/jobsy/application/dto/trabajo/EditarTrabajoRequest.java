package com.escaes.jobsy.application.dto.trabajo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.escaes.jobsy.application.validacion.ReglasValidacion.*;

/*
 * Edición de un Job: mismos campos y reglas que al crearlo, salvo el pago, que es fijo.
 * `pago` es opcional y solo sirve para detectar un intento de cambio (si llega distinto al
 * actual se responde 400); así el front puede reenviar el Job completo.
 */
public record EditarTrabajoRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(min = TITULO_MIN, max = TITULO_MAX, message = "El título debe tener entre {min} y {max} caracteres")
        @Pattern(regexp = PATRON_TITULO, message = "El título debe contener letras, no solo números o símbolos")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = DESCRIPCION_MIN, max = DESCRIPCION_MAX, message = "La descripción debe tener entre {min} y {max} caracteres")
        String descripcion,

        @NotBlank(message = "El tipo de pago es obligatorio")
        String tipoPago,

        @NotBlank(message = "La ubicación es obligatoria")
        String ubicacion,

        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        Double pago) {
}
