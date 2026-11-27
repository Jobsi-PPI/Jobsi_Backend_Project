package com.escaes.jobsy.infraestructure.rest;

import com.escaes.jobsy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    void loginValidoDevuelveToken() throws Exception {
        assertThat(tokenAdmin()).isNotBlank();
    }

    @Test
    void loginConClaveIncorrectaDevuelve401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", ADMIN_CORREO, "password", "incorrecta"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioBloqueadoNoPuedeIniciarSesion() throws Exception {
        registrarUsuario(100000001, "bloqueado@jobsi.test", "Clave.12345", "3100000001");
        login("bloqueado@jobsi.test", "Clave.12345");

        mockMvc.perform(patch("/v1/admin/users/100000001/block")
                        .header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "bloqueado@jobsi.test", "password", "Clave.12345"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void registroPublicoIgnoraElRolEnviado() throws Exception {
        mockMvc.perform(post("/v1/public/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "documento", 100000002,
                                "nombre", "Intruso",
                                "primerApellido", "Prueba",
                                "email", "intruso@jobsi.test",
                                "password", "Clave.12345",
                                "telefono", "3100000002",
                                "fechaNacimiento", "2000-01-01",
                                "genero", "Otro",
                                "rol", "ADMIN"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("USER"));

        mockMvc.perform(get("/v1/admin/metrics")
                        .header("Authorization", bearer(login("intruso@jobsi.test", "Clave.12345"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void registraCedulaDeOnceDigitos() throws Exception {
        registrarUsuario(11078384392L, "cedulalarga@jobsi.test", "Clave.12345", "3100000003");

        mockMvc.perform(get("/v1/admin/users/11078384392").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value(11078384392L));
    }

    @Test
    void documentoFueraDeRangoIndicaElCampo() throws Exception {
        String cuerpo = """
                {"documento": 99999999999999999999999, "nombre": "Ximena", "primerApellido": "Yepes",
                 "email": "rango@jobsi.test", "password": "Clave.12345", "telefono": "3100000004",
                 "fechaNacimiento": "2000-01-01", "genero": "Otro"}
                """;
        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("documento")));

        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo.replace("99999999999999999999999", "1234567890123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("15 dígitos")));
    }

    @Test
    void registroRechazaNombreDeUnaLetraYClaveDebil() throws Exception {
        String valido = """
                {"documento": 100000010, "nombre": "Camila", "primerApellido": "Rojas",
                 "email": "camila@jobsi.test", "password": "Clave.12345", "telefono": "3100000010",
                 "fechaNacimiento": "2000-01-01", "genero": "Otro"}
                """;

        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(valido.replace("\"Camila\"", "\"C\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("El nombre debe tener entre 2 y 50")));

        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(valido.replace("\"Rojas\"", "\"R0jas\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("solo puede contener letras")));

        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(valido.replace("Clave.12345", "12345678")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("mayúscula")));

        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(valido.replace("3100000010", "12345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("celular de 10 dígitos")));

        // Segundo apellido vacío es válido (campo opcional)
        mockMvc.perform(post("/v1/public/users/create").contentType(MediaType.APPLICATION_JSON)
                        .content(valido.replace("\"genero\"", "\"segundoApellido\": \"\", \"genero\"")))
                .andExpect(status().isCreated());
    }

    @Test
    void perfilDevuelveExactamenteAlUsuarioDelToken() throws Exception {
        // "juana@" contiene "ana@": una búsqueda con LIKE devolvería el usuario equivocado
        registrarUsuario(100000005, "juana@jobsi.test", "Clave.12345", "3100000005");
        registrarUsuario(100000006, "ana@jobsi.test", "Clave.12345", "3100000006");

        mockMvc.perform(get("/v1/usuarios/perfil").header("Authorization", bearer(login("ana@jobsi.test", "Clave.12345"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@jobsi.test"))
                .andExpect(jsonPath("$.documento").value(100000006))
                .andExpect(jsonPath("$.valoracionPromedio").value(0.0));

        mockMvc.perform(get("/v1/usuarios/perfil")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDevuelve401() throws Exception {
        mockMvc.perform(get("/v1/admin/metrics").header("Authorization", "Bearer token.invalido.x"))
                .andExpect(status().isUnauthorized());
    }
}
