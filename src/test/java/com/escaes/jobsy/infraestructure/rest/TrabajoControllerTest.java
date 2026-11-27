package com.escaes.jobsy.infraestructure.rest;

import com.escaes.jobsy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrabajoControllerTest extends AbstractIntegrationTest {

    private static final String CLAVE = "Clave.12345";

    @Test
    void elCreadorEditaTodoMenosElPago() throws Exception {
        registrarUsuario(400000001, "edita@jobsi.test", CLAVE, "3400000001");
        String token = login("edita@jobsi.test", CLAVE);
        String id = crearTrabajo(token);

        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json(edicion(null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Clase de cálculo integral"))
                .andExpect(jsonPath("$.descripcion").value("Necesito repasar integrales por partes"))
                .andExpect(jsonPath("$.categoria").value("TAREAS"))
                .andExpect(jsonPath("$.tipoPago").value("TRANSFERENCIA"))
                .andExpect(jsonPath("$.ubicacion").value("ALMENDROS"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pago").value(20000.0));

        // Reenviar el mismo pago está permitido
        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json(edicion(20000.0))))
                .andExpect(status().isOk());
    }

    @Test
    void noSePuedeCambiarElPago() throws Exception {
        registrarUsuario(400000002, "pagofijo@jobsi.test", CLAVE, "3400000002");
        String token = login("pagofijo@jobsi.test", CLAVE);
        String id = crearTrabajo(token);

        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json(edicion(50000.0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El pago no se puede modificar"));
    }

    @Test
    void soloElCreadorPuedeEditar() throws Exception {
        registrarUsuario(400000003, "duenio@jobsi.test", CLAVE, "3400000003");
        registrarUsuario(400000004, "ajeno@jobsi.test", CLAVE, "3400000004");
        String id = crearTrabajo(login("duenio@jobsi.test", CLAVE));

        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(login("ajeno@jobsi.test", CLAVE)))
                        .contentType(MediaType.APPLICATION_JSON).content(json(edicion(null))))
                .andExpect(status().isForbidden());
    }

    @Test
    void soloSeEditaEnEstadoPendiente() throws Exception {
        registrarUsuario(400000005, "cancelado@jobsi.test", CLAVE, "3400000005");
        String token = login("cancelado@jobsi.test", CLAVE);
        String id = crearTrabajo(token);
        mockMvc.perform(patch("/v1/admin/jobs/" + id + "/cancel").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk());

        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json(edicion(null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("PENDIENTE")));
    }

    @Test
    void validaLosCamposAlEditar() throws Exception {
        registrarUsuario(400000006, "invalido@jobsi.test", CLAVE, "3400000006");
        String token = login("invalido@jobsi.test", CLAVE);
        String id = crearTrabajo(token);
        Map<String, Object> cuerpo = edicion(null);
        cuerpo.put("titulo", "a");

        mockMvc.perform(put("/v1/jobs/edit/" + id).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json(cuerpo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("El título debe tener entre 5 y 150")));
    }

    private static Map<String, Object> edicion(Double pago) {
        Map<String, Object> cuerpo = new HashMap<>(Map.of(
                "titulo", "Clase de cálculo integral",
                "descripcion", "Necesito repasar integrales por partes",
                "tipoPago", "TRANSFERENCIA",
                "ubicacion", "Almendros",
                "categoria", "TAREAS"));
        if (pago != null) {
            cuerpo.put("pago", pago);
        }
        return cuerpo;
    }

    private String crearTrabajo(String token) throws Exception {
        String respuesta = mockMvc.perform(post("/v1/jobs/create").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "titulo", "Asesoría de cálculo",
                                "descripcion", "Ayuda con derivadas para el parcial",
                                "pago", 20000,
                                "tipoPago", "EFECTIVO",
                                "ubicacion", "palmas",
                                "categoria", "ASESORIAS"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asText();
    }
}
