package com.escaes.jobsy.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * Datos del administrador inicial, leídos de app.admin.* (variables de entorno ADMIN_*).
 * La fecha de nacimiento se recibe en formato ISO (yyyy-MM-dd).
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        Long documento,
        String nombre,
        String primerApellido,
        String correo,
        String clave,
        String telefono,
        String fechaNacimiento
) {

    public boolean configurado() {
        return correo != null && !correo.isBlank() && clave != null && !clave.isBlank();
    }
}
