package com.escaes.jobsy.application.dto.usuario;

import java.util.Date;

public record UsuarioRequest(
        Long documento,
        String nombre,
        String primerApellido,
        String segundoApellido,
        String email,
        String password,
        String telefono,
        Date fechaNacimiento,
        String genero,
        String rol
) {}

