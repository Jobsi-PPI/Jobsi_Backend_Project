package com.escaes.jobsy.domain.model;

import java.util.Date;
import java.util.List;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;

public record Usuario(Integer documento,String nombre,String primerApellido,String segundoApellido, String correo,
        String clave, String telefono, Boolean bloqueado,
        Date fechaNacimiento,
        Integer valoracionConteo,
        Double valoracionPromedio,
        Genero genero, Rol rol, List<Trabajo> trabajos, List<Trabajo> trabajosRealizados) {

    public static Usuario crear(UsuarioRequest request, String encodedPassword, Genero genero, Rol rol) {
        return new Usuario(
                request.documento(),
                request.nombre(),
                request.primerApellido(),
                request.segundoApellido(),
                request.email(),
                encodedPassword,
                request.telefono(),
                false,
                request.fechaNacimiento(),
                0,
                0.0,
                genero,
                rol,
                List.of(),
                List.of());
    }
}