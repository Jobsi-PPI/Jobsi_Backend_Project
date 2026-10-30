package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.repository.CorreoService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Implementación SMTP del puerto CorreoService. El HTML del correo se renderiza con Thymeleaf
 * (templates/mail). El envío es asíncrono para que el tiempo de respuesta no revele si el
 * correo está registrado; un fallo de SMTP se registra en el log y no se propaga.
 */
@Component
public class SmtpCorreoAdapter implements CorreoService {

    private static final Logger logger = Logger.getLogger(SmtpCorreoAdapter.class.getName());

    private final JavaMailSender mailSender;

    private final ITemplateEngine templateEngine;

    private final String remitente;

    public SmtpCorreoAdapter(JavaMailSender mailSender, ITemplateEngine templateEngine,
                             @Value("${app.mail.from}") String remitente) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.remitente = remitente;
    }

    @Async
    @Override
    public void enviarRecuperacionClave(String correo, String nombre, String enlace, long minutosExpiracion) {
        Context context = new Context();
        context.setVariable("nombre", nombre);
        context.setVariable("enlace", enlace);
        context.setVariable("minutos", minutosExpiracion);
        String html = templateEngine.process("mail/recuperacion-clave", context);

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, StandardCharsets.UTF_8.name());
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Jobsi - Recupera tu contraseña");
            helper.setText(html, true);
            mailSender.send(mensaje);
            logger.info("Correo de recuperación enviado a " + correo);
        } catch (MessagingException | MailException ex) {
            logger.log(Level.SEVERE, "No se pudo enviar el correo de recuperación a " + correo, ex);
        }
    }
}
