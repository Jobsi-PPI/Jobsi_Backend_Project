package com.escaes.jobsy.domain.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;

public record Usuario(UUID id, String nombre, Integer documento, String correo,
        String clave, String telefono, Boolean bloqueado,
        Date fechaNacimiento,
        Genero genero, Rol rol, List<Trabajo> trabajos, List<Trabajo> trabajosRealizados) {

    public static Usuario crear(UsuarioRequest request, String encodedPassword, Genero genero, Rol rol) {
        return new Usuario(
                UUID.randomUUID(),
                request.nombre(),
                request.documento(),
                request.email(),
                encodedPassword,
                request.telefono(),
                false,
                request.fechaNacimiento(),
                genero,
                rol,
                List.of(),
                List.of());
    }
}