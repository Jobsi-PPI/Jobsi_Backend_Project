package com.escaes.jobsy.domain.repository;

/*
 * Puerto de salida para el envío de correos. El dominio no conoce SMTP ni plantillas;
 * la implementación vive en infraestructure/adapter.
 */
public interface CorreoService {

    void enviarRecuperacionClave(String correo, String nombre, String enlace, long minutosExpiracion);
}
