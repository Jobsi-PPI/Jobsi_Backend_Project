package com.escaes.jobsy.application.dto.usuario;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsuarioResponse(
        Long documento,
        String nombre,
        String primerApellido,
        String segundoApellido,
        String email,
        String rol,
        Integer valoracionConteo,
        Double valoracionPromedio,
        String telefono,
        String genero,
        Boolean bloqueado
) {}
