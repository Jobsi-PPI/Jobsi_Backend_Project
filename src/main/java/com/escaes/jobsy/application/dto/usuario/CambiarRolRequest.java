package com.escaes.jobsy.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record CambiarRolRequest(@NotBlank(message = "El rol es obligatorio") String rol) {
}
