package com.escaes.jobsy.config;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.rol.GestionRolesUseCase;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
import com.escaes.jobsy.config.properties.AdminProperties;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

/*
 * Crea el usuario administrador inicial a partir de las propiedades app.admin.*
 * (variables de entorno ADMIN_*). Los catálogos los siembra Flyway (V2__datos_referencia.sql).
 * Es idempotente: si ya existe un usuario con ese correo no hace nada.
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private static final String ROL_ADMIN = "ADMIN";

    private static final String GENERO_POR_DEFECTO = "Otro";

    private final AdminProperties adminProperties;

    private final GestionUsuariosUseCase gestionUsuariosUseCase;

    private final GestionGenerosUseCase gestionGenerosUseCase;

    private final GestionRolesUseCase gestionRolesUseCase;

    private static final Logger logger = Logger.getLogger(AdminInitializer.class.getName());

    @Override
    public void run(ApplicationArguments args) {
        if (!adminProperties.configurado()) {
            logger.warning("ADMIN_EMAIL / ADMIN_PASSWORD no configurados: no se crea el administrador inicial");
            return;
        }
        if (gestionUsuariosUseCase.existeUsuarioConCorreo(adminProperties.correo())) {
            logger.info("El administrador inicial ya existe: " + adminProperties.correo());
            return;
        }

        Genero genero = gestionGenerosUseCase.obtenerGeneroPorNombre(GENERO_POR_DEFECTO);
        Rol rol = gestionRolesUseCase.obtenerRolPorNombre(ROL_ADMIN);
        Date fechaNacimiento = Date.from(LocalDate.parse(adminProperties.fechaNacimiento())
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        UsuarioRequest admin = new UsuarioRequest(
                adminProperties.documento(),
                adminProperties.nombre(),
                adminProperties.primerApellido(),
                null,
                adminProperties.correo(),
                adminProperties.clave(),
                adminProperties.telefono(),
                fechaNacimiento,
                GENERO_POR_DEFECTO,
                ROL_ADMIN);
        try {
            gestionUsuariosUseCase.crearUsuario(admin, genero, rol);
            logger.info("Administrador inicial creado: " + adminProperties.correo());
        } catch (RuntimeException ex) {
            // Ej. documento o teléfono ya usados por otro usuario: se informa sin detener la aplicación
            logger.severe("No se pudo crear el administrador inicial: " + ex.getMessage());
        }
    }
}
