package com.escaes.jobsy.application.dto.trabajo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.escaes.jobsy.application.validacion.ReglasValidacion.*;

public record CrearTrabajoRequest(
                @NotBlank(message = "El título es obligatorio")
                @Size(min = TITULO_MIN, max = TITULO_MAX, message = "El título debe tener entre {min} y {max} caracteres")
                @Pattern(regexp = PATRON_TITULO, message = "El título debe contener letras, no solo números o símbolos")
                String titulo,

                @NotBlank(message = "La descripción es obligatoria")
                @Size(min = DESCRIPCION_MIN, max = DESCRIPCION_MAX, message = "La descripción debe tener entre {min} y {max} caracteres")
                String descripcion,

                @NotNull(message = "El pago es obligatorio")
                @DecimalMin(value = PAGO_MIN, message = "El pago mínimo es $1.000")
                @DecimalMax(value = PAGO_MAX, message = "El pago máximo es $10.000.000")
                Double pago,

                @NotBlank(message = "El tipo de pago es obligatorio")
                String tipoPago,

                @NotBlank(message = "La ubicación es obligatoria")
                String ubicacion,

                @NotBlank(message = "La categoría es obligatoria")
                String categoria) {
}
