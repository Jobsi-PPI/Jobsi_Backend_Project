package com.escaes.jobsy.infraestructure.web.controller;

import com.escaes.jobsy.application.usecase.auth.RecuperarClaveUseCase;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Páginas Thymeleaf del flujo de recuperación de contraseña. Se sirven desde el backend
 * para no depender del frontend: el login solo enlaza a /auth/password/forgot y, al terminar,
 * la página de éxito redirige al /login del frontend.
 */
@Controller
@Hidden
@RequestMapping("/auth/password")
public class RecuperacionClaveController {

    private final RecuperarClaveUseCase recuperarClaveUseCase;

    private final String frontendUrl;

    public RecuperacionClaveController(RecuperarClaveUseCase recuperarClaveUseCase,
                                       @Value("${app.frontend.url}") String frontendUrl) {
        this.recuperarClaveUseCase = recuperarClaveUseCase;
        this.frontendUrl = frontendUrl;
    }

    @ModelAttribute
    public void atributosComunes(Model model) {
        model.addAttribute("loginUrl", frontendUrl + "/login");
        model.addAttribute("longitudMinima", RecuperarClaveUseCase.LONGITUD_MINIMA_CLAVE);
    }

    @GetMapping("/forgot")
    public String formularioCorreo() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot")
    public String solicitarRecuperacion(@RequestParam(name = "correo", required = false) String correo) {
        recuperarClaveUseCase.solicitarRecuperacion(correo);
        // Misma respuesta exista o no el correo
        return "auth/forgot-password-enviado";
    }

    @GetMapping("/reset")
    public String formularioNuevaClave(@RequestParam(name = "token", required = false) String token, Model model) {
        if (!recuperarClaveUseCase.validarToken(token)) {
            return "auth/reset-invalido";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset")
    public String restablecerClave(@RequestParam(name = "token", required = false) String token,
                                   @RequestParam(name = "clave", required = false) String clave,
                                   @RequestParam(name = "confirmacion", required = false) String confirmacion,
                                   Model model) {
        if (!recuperarClaveUseCase.validarToken(token)) {
            return "auth/reset-invalido";
        }
        try {
            recuperarClaveUseCase.restablecerClave(token, clave, confirmacion);
            return "auth/reset-exito";
        } catch (BusinessExceptions.BadRequestException ex) {
            model.addAttribute("token", token);
            model.addAttribute("error", ex.getMessage());
            return "auth/reset-password";
        }
    }
}
