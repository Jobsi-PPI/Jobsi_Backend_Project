package com.escaes.jobsy.config;

import com.escaes.jobsy.application.dto.estado.EstadoRequest;
import com.escaes.jobsy.application.dto.genero.GeneroRequest;
import com.escaes.jobsy.application.dto.pago.PagoRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.usecase.categoria.GestionCategoriasUseCase;
import com.escaes.jobsy.application.usecase.categoria.ListarCategoriasUseCase;
import com.escaes.jobsy.application.usecase.estado.GestionEstadosUseCase;
import com.escaes.jobsy.application.usecase.estado.ListarEstadosUseCase;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.genero.ListarGenerosUseCase;

import com.escaes.jobsy.application.usecase.pago.GestionPagosUseCase;
import com.escaes.jobsy.application.usecase.pago.ListarPagosUseCase;
import com.escaes.jobsy.application.usecase.rol.GestionRolesUseCase;
import com.escaes.jobsy.application.usecase.rol.ListarRolesUseCase;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
import com.escaes.jobsy.application.usecase.usuario.ListarUsuariosUseCase;

import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

import java.util.UUID;
import java.util.logging.Logger;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GestionGenerosUseCase gestionGenerosUseCase;

    private final ListarGenerosUseCase listarGenerosUseCase;

    private final GestionRolesUseCase gestionRolesUseCase;

    private final ListarRolesUseCase listarRolesUseCase;

    private final GestionUsuariosUseCase gestionUsuariosUseCase;

    private final ListarUsuariosUseCase listarUsuariosUseCase;

    private final GestionEstadosUseCase gestionEstadosUseCase;

    private final ListarEstadosUseCase listarEstadosUseCase;

    private final GestionCategoriasUseCase gestionCategoriasUseCase;

    private final ListarCategoriasUseCase listarCategoriasUseCase;

    private final ListarPagosUseCase listarPagosUseCase;

    private final GestionPagosUseCase gestionPagosUseCase;

    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando la inicialización de datos...");
        initializeGeneros();
        initializeRoles();
        initializeAdmin();
        initializeEstados();
        initializeCategorias();
        initializeTipoPagos();
        logger.info("Inicialización de datos completada.");
    }

    private void initializeGeneros() {
        if (listarGenerosUseCase.contarGeneros() == 0) {
            logger.info("Inicializando generos por defecto...");
            gestionGenerosUseCase.crearGenero(new GeneroRequest("Masculino"));
            gestionGenerosUseCase.crearGenero(new GeneroRequest("Femenino"));
            gestionGenerosUseCase.crearGenero(new GeneroRequest("Alien"));
            gestionGenerosUseCase.crearGenero(new GeneroRequest("Otro"));
            logger.info("Generos creados");
        }

    }

    private void initializeRoles() {
        if (listarRolesUseCase.contarRoles() == 0) {
            logger.info("Inicializando roles por defecto...");
            gestionRolesUseCase.crearRol(new Rol(UUID.randomUUID(), "ADMIN",
                    "Administrador del sistema con acceso completo"));
            gestionRolesUseCase.crearRol(new Rol(UUID.randomUUID(), "USER",
                    "Usuario regular con acceso limitado"));
            logger.info("Roles creados");
        }
    }

    private void initializeEstados() {
        if (listarEstadosUseCase.contarEstados() == 0) {
            logger.info("Inicializando estados por defecto...");
            gestionEstadosUseCase.crearEstado(new EstadoRequest("PENDIENTE"));
            gestionEstadosUseCase.crearEstado(new EstadoRequest("FINALIZADO"));
            gestionEstadosUseCase.crearEstado(new EstadoRequest("ASIGNADO"));
            gestionEstadosUseCase.crearEstado(new EstadoRequest("CANCELADO"));
            gestionEstadosUseCase.crearEstado(new EstadoRequest("BLOQUEADO"));
            logger.info("Estados creados");
        }
    }

    private void initializeCategorias() {
        if (listarCategoriasUseCase.listarCategorias().isEmpty()) {
            logger.info("Inicializando categorias por defecto...");
            gestionCategoriasUseCase.crearCategoria("ASESORIAS");
            gestionCategoriasUseCase.crearCategoria("TAREAS");
            gestionCategoriasUseCase.crearCategoria("MATERIALES");
            gestionCategoriasUseCase.crearCategoria("ENTRENAMIENTOS");
            gestionCategoriasUseCase.crearCategoria("OTRO");
            logger.info("Categorias creadas");
        }
    }

    private void initializeAdmin() {
        if (listarUsuariosUseCase.contarUsuarios() == 0) {
            logger.info("Creando usuario admin por defecto...");
            Genero genero = gestionGenerosUseCase.obtenerGeneroPorNombre("Alien");
            Rol rol = gestionRolesUseCase.obtenerRolPorNombre("ADMIN");
            UsuarioRequest admin = new UsuarioRequest(1107834660,
                    "admin","admin","admin", "admin@gmail.com", "123", "300000", new Date(), "", "");
            gestionUsuariosUseCase.crearUsuario(admin, genero, rol);
            logger.info("Admin creado Exitosamente");
        }
    }

    private void initializeTipoPagos() {
        if (listarPagosUseCase.contarTipoDePagos() == 0) {
            logger.info("Inicializando tipos de pago por defecto...");
            gestionPagosUseCase.crearTipoPago(new PagoRequest("EFECTIVO"));
            gestionPagosUseCase.crearTipoPago(new PagoRequest("TRANSFERENCIA"));
            gestionPagosUseCase.crearTipoPago(new PagoRequest("INTERCAMBIO"));
            gestionPagosUseCase.crearTipoPago(new PagoRequest("OTRO"));
            logger.info("Tipos de pago creados");
        }
    }
}
