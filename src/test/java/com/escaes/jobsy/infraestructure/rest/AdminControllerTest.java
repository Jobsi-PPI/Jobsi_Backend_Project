package com.escaes.jobsy.infraestructure.rest;

import com.escaes.jobsy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends AbstractIntegrationTest {

    @Test
    void endpointsAdminRequierenRolAdmin() throws Exception {
        registrarUsuario(300000001, "comun@jobsi.test", "Clave12345", "3300000001");
        String tokenUsuario = login("comun@jobsi.test", "Clave12345");

        mockMvc.perform(get("/v1/admin/metrics")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/v1/admin/metrics").header("Authorization", bearer(tokenUsuario)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/v1/admin/metrics").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk());
    }

    @Test
    void metricasIncluyenTodosLosEstadosYCategorias() throws Exception {
        mockMvc.perform(get("/v1/admin/metrics").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").isNumber())
                .andExpect(jsonPath("$.trabajosPorEstado.PENDIENTE").isNumber())
                .andExpect(jsonPath("$.trabajosPorCategoria.TAREAS").isNumber())
                .andExpect(jsonPath("$.solicitudesPorEstado.ACEPTADA").isNumber());
    }

    @Test
    void listadoPaginadoDeUsuarios() throws Exception {
        mockMvc.perform(get("/v1/admin/users").param("size", "2").param("page", "0")
                        .header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray())
                .andExpect(jsonPath("$.tamano").value(2))
                .andExpect(jsonPath("$.totalElementos").isNumber())
                // Nunca se expone la clave
                .andExpect(jsonPath("$.contenido[0].clave").doesNotExist());
    }

    @Test
    void bloquearYDesbloquearUsuario() throws Exception {
        registrarUsuario(300000002, "bloq@jobsi.test", "Clave12345", "3300000002");
        String admin = tokenAdmin();

        mockMvc.perform(patch("/v1/admin/users/300000002/block").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(true));
        mockMvc.perform(patch("/v1/admin/users/300000002/block").header("Authorization", bearer(admin)))
                .andExpect(status().isConflict());
        mockMvc.perform(patch("/v1/admin/users/300000002/unblock").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(false));

        login("bloq@jobsi.test", "Clave12345");
    }

    @Test
    void adminNoPuedeBloquearseNiCambiarseElRol() throws Exception {
        String admin = tokenAdmin();
        mockMvc.perform(patch("/v1/admin/users/999000111/block").header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/v1/admin/users/999000111/role").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("rol", "USER"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarRolAAdmin() throws Exception {
        registrarUsuario(300000003, "promovido@jobsi.test", "Clave12345", "3300000003");

        mockMvc.perform(patch("/v1/admin/users/300000003/role").header("Authorization", bearer(tokenAdmin()))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("rol", "admin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        mockMvc.perform(get("/v1/admin/metrics")
                        .header("Authorization", bearer(login("promovido@jobsi.test", "Clave12345"))))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarUsuarioSinHistorialYRechazarConHistorial() throws Exception {
        registrarUsuario(300000004, "borrable@jobsi.test", "Clave12345", "3300000004");
        registrarUsuario(300000005, "conjobs@jobsi.test", "Clave12345", "3300000005");
        crearTrabajo(login("conjobs@jobsi.test", "Clave12345"), "TAREAS");
        String admin = tokenAdmin();

        mockMvc.perform(delete("/v1/admin/users/300000004").header("Authorization", bearer(admin)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/admin/users/300000004").header("Authorization", bearer(admin)))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/v1/admin/users/300000005").header("Authorization", bearer(admin)))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelarYEliminarTrabajo() throws Exception {
        registrarUsuario(300000006, "publica@jobsi.test", "Clave12345", "3300000006");
        registrarUsuario(300000007, "postula@jobsi.test", "Clave12345", "3300000007");
        String trabajoId = crearTrabajo(login("publica@jobsi.test", "Clave12345"), "ASESORIAS");
        mockMvc.perform(post("/v1/requests")
                        .header("Authorization", bearer(login("postula@jobsi.test", "Clave12345")))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("trabajo", trabajoId))))
                .andExpect(status().is2xxSuccessful());
        String admin = tokenAdmin();

        mockMvc.perform(patch("/v1/admin/jobs/" + trabajoId + "/cancel").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
        mockMvc.perform(patch("/v1/admin/jobs/" + trabajoId + "/cancel").header("Authorization", bearer(admin)))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/v1/admin/requests").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.trabajoId == '" + trabajoId + "')].estado").value("CERRADA"))
                .andExpect(jsonPath("$[0].clave").doesNotExist());

        mockMvc.perform(delete("/v1/admin/jobs/" + trabajoId).header("Authorization", bearer(admin)))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/v1/admin/jobs/" + trabajoId).header("Authorization", bearer(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void crudDeCategoriasConProteccionDeUso() throws Exception {
        String admin = tokenAdmin();
        mockMvc.perform(post("/v1/admin/category/create").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("nombre", "TEMPORAL"))))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/v1/admin/category/TEMPORAL").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("nombre", "TAREAS"))))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/v1/admin/category/TEMPORAL").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("nombre", "RENOMBRADA"))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/v1/admin/category/RENOMBRADA").header("Authorization", bearer(admin)))
                .andExpect(status().isNoContent());

        registrarUsuario(300000008, "usacat@jobsi.test", "Clave12345", "3300000008");
        crearTrabajo(login("usacat@jobsi.test", "Clave12345"), "MATERIALES");
        mockMvc.perform(delete("/v1/admin/category/MATERIALES").header("Authorization", bearer(admin)))
                .andExpect(status().isConflict());
    }

    @Test
    void estadosDelSistemaNoSeModifican() throws Exception {
        mockMvc.perform(delete("/v1/admin/state/PENDIENTE").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isConflict());
    }

    @Test
    void generoEnUsoNoSeEliminaNiBorraUsuarios() throws Exception {
        registrarUsuario(300000009, "genero@jobsi.test", "Clave12345", "3300000009");
        String admin = tokenAdmin();

        mockMvc.perform(delete("/v1/admin/gender/Otro").header("Authorization", bearer(admin)))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/v1/admin/users/300000009").header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void catalogosDePagoYRoles() throws Exception {
        String admin = tokenAdmin();
        mockMvc.perform(get("/v1/admin/roles").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(post("/v1/admin/payment/create").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("nombre", "CRIPTO"))))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/v1/admin/payment/CRIPTO").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("nombre", "BONO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("BONO"));
        mockMvc.perform(delete("/v1/admin/payment/BONO").header("Authorization", bearer(admin)))
                .andExpect(status().isNoContent());
    }

    private String crearTrabajo(String token, String categoria) throws Exception {
        String respuesta = mockMvc.perform(post("/v1/jobs/create").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "titulo", "Trabajo de prueba",
                                "descripcion", "Descripción de prueba",
                                "pago", 10000,
                                "tipoPago", "EFECTIVO",
                                "ubicacion", "palmas",
                                "categoria", categoria))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asText();
    }
}
