package com.escaes.jobsy.application.dto.usuario;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsuarioResponse(
        Integer documento,
        String nombre,
        String email,
        String rol,
        Integer valoracionConteo,
        Double valoracionPromedio
) {}