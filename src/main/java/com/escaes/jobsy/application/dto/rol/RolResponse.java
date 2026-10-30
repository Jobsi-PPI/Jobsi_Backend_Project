package com.escaes.jobsy.application.dto.rol;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RolResponse(String nombre, String descripcion) {
}
