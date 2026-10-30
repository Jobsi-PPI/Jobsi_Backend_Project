package com.escaes.jobsy.infraestructure.web;

import com.escaes.jobsy.AbstractIntegrationTest;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

/*
 * Flujo completo de recuperación de contraseña contra un SMTP en memoria (GreenMail).
 */
class RecuperacionClaveTest extends AbstractIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    private static final Pattern TOKEN_EN_ENLACE = Pattern.compile("/auth/password/reset\\?token=([A-Za-z0-9_-]+)");

    @Test
    void restableceLaClaveConElEnlaceDelCorreo() throws Exception {
        registrarUsuario(200000001, "recupera@jobsi.test", "ClaveVieja1", "3200000001");

        mockMvc.perform(post("/auth/password/forgot").param("correo", "recupera@jobsi.test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Revisa tu correo")));

        String token = extraerTokenDelCorreo("recupera@jobsi.test");

        mockMvc.perform(get("/auth/password/reset").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Crea una nueva contraseña")));

        mockMvc.perform(post("/auth/password/reset")
                        .param("token", token)
                        .param("clave", "ClaveNueva1")
                        .param("confirmacion", "OtraClave1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Las contraseñas no coinciden")));

        mockMvc.perform(post("/auth/password/reset")
                        .param("token", token)
                        .param("clave", "ClaveNueva1")
                        .param("confirmacion", "ClaveNueva1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tu contraseña se actualizó")))
                .andExpect(content().string(containsString("http://localhost:5173/login")));

        assertThat(login("recupera@jobsi.test", "ClaveNueva1")).isNotBlank();
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "recupera@jobsi.test", "password", "ClaveVieja1"))))
                .andExpect(status().isUnauthorized());

        // El enlace es de un solo uso
        mockMvc.perform(get("/auth/password/reset").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enlace no válido o expirado")));
    }

    @Test
    void correoNoRegistradoRecibeLaMismaRespuestaYNoSeEnviaNada() throws Exception {
        mockMvc.perform(post("/auth/password/forgot").param("correo", "noexiste@jobsi.test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Revisa tu correo")));

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isFalse();
    }

    @Test
    void solicitarUnNuevoEnlaceInvalidaElAnterior() throws Exception {
        registrarUsuario(200000002, "doble@jobsi.test", "ClaveVieja1", "3200000002");

        mockMvc.perform(post("/auth/password/forgot").param("correo", "doble@jobsi.test"));
        String primerToken = extraerTokenDelCorreo("doble@jobsi.test");
        greenMail.purgeEmailFromAllMailboxes();

        mockMvc.perform(post("/auth/password/forgot").param("correo", "doble@jobsi.test"));
        String segundoToken = extraerTokenDelCorreo("doble@jobsi.test");

        assertThat(segundoToken).isNotEqualTo(primerToken);
        mockMvc.perform(get("/auth/password/reset").param("token", primerToken))
                .andExpect(content().string(containsString("Enlace no válido o expirado")));
        mockMvc.perform(get("/auth/password/reset").param("token", segundoToken))
                .andExpect(content().string(containsString("Crea una nueva contraseña")));
    }

    @Test
    void elFormularioSeEnviaConSuOrigenReal() throws Exception {
        // "no-referrer" provocaba que el navegador enviara Origin: null y Spring respondiera "Invalid CORS request"
        mockMvc.perform(get("/auth/password/forgot"))
                .andExpect(content().string(containsString("content=\"same-origin\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("content=\"no-referrer\""))));

        mockMvc.perform(post("/auth/password/forgot").header("Origin", "http://localhost")
                        .param("correo", "otro@jobsi.test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Revisa tu correo")));
    }

    @Test
    void tokenInventadoMuestraEnlaceInvalido() throws Exception {
        mockMvc.perform(get("/auth/password/reset").param("token", "inventado"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enlace no válido o expirado")));
    }

    private String extraerTokenDelCorreo(String destinatario) throws Exception {
        // El envío es asíncrono
        assertThat(greenMail.waitForIncomingEmail(10_000, 1)).isTrue();
        MimeMessage[] mensajes = greenMail.getReceivedMessagesForDomain(destinatario);
        assertThat(mensajes).hasSize(1);
        assertThat(mensajes[0].getSubject()).contains("Recupera tu contraseña");

        Matcher matcher = TOKEN_EN_ENLACE.matcher((String) mensajes[0].getContent());
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}
