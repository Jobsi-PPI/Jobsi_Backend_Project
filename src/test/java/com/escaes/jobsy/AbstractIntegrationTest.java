package com.escaes.jobsy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Base de los tests de integración: un único contexto de Spring (y un único contenedor de
 * PostgreSQL) compartido por todas las clases que heredan de aquí.
 * Cada test usa documentos y correos propios para no depender del orden de ejecución.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    protected static final String ADMIN_CORREO = "admin@jobsi.test";

    protected static final String ADMIN_CLAVE = "Admin12345";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String json(Object valor) throws Exception {
        return objectMapper.writeValueAsString(valor);
    }

    protected String login(String correo, String clave) throws Exception {
        String respuesta = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", correo, "password", clave))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("token").asText();
    }

    protected String tokenAdmin() throws Exception {
        return login(ADMIN_CORREO, ADMIN_CLAVE);
    }

    protected void registrarUsuario(long documento, String correo, String clave, String telefono) throws Exception {
        mockMvc.perform(post("/v1/public/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "documento", documento,
                                "nombre", "Usuario",
                                "primerApellido", "Prueba",
                                "email", correo,
                                "password", clave,
                                "telefono", telefono,
                                "fechaNacimiento", "2000-01-01",
                                "genero", "Otro"))))
                .andExpect(status().isCreated());
    }

    protected static String bearer(String token) {
        return "Bearer " + token;
    }
}
