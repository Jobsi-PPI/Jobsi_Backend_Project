package com.escaes.jobsy.application.dto.valoracion;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValoracionCategoriaResponse(String nombre) {
}
