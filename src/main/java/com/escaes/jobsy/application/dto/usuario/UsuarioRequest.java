package com.escaes.jobsy.application.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;

import static com.escaes.jobsy.application.validacion.ReglasValidacion.*;

/*
 * La política de contraseña (mayúscula, número, especial, sin datos personales...) se valida en
 * GestionUsuariosUseCase con PoliticaClave, porque necesita comparar contra el nombre y el correo.
 */
public record UsuarioRequest(
        @NotNull(message = "El documento es obligatorio")
        @Positive(message = "El documento debe ser un número positivo")
        @Max(value = DOCUMENTO_MAX, message = "El documento debe tener máximo 15 dígitos")
        Long documento,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = NOMBRE_MIN, max = NOMBRE_MAX, message = "El nombre debe tener entre {min} y {max} caracteres")
        @Pattern(regexp = PATRON_NOMBRE, message = "El nombre " + MENSAJE_NOMBRE)
        String nombre,

        @NotBlank(message = "El primer apellido es obligatorio")
        @Size(min = NOMBRE_MIN, max = NOMBRE_MAX, message = "El primer apellido debe tener entre {min} y {max} caracteres")
        @Pattern(regexp = PATRON_NOMBRE, message = "El primer apellido " + MENSAJE_NOMBRE)
        String primerApellido,

        // Opcional (null o ""): si se envía con contenido, aplica la misma regla que el primer apellido
        @Size(max = NOMBRE_MAX, message = "El segundo apellido debe tener máximo {max} caracteres")
        @Pattern(regexp = PATRON_NOMBRE_OPCIONAL,
                message = "El segundo apellido debe tener al menos 2 caracteres y " + MENSAJE_NOMBRE)
        String segundoApellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = CORREO_MAX, message = "El correo debe tener máximo {max} caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = PATRON_TELEFONO, message = "El teléfono debe ser un celular de 10 dígitos que empiece por 3")
        String telefono,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        Date fechaNacimiento,

        @NotBlank(message = "El género es obligatorio")
        String genero,

        String rol
) {}
