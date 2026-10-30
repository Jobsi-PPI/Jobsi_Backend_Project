package com.escaes.jobsy.infraestructure.rest;

import com.escaes.jobsy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthEndpointTest extends AbstractIntegrationTest {

    @Test
    void healthEsPublicoYNoExponeDetalles() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    void adminVeElDetalleDeLaBaseDeDatos() throws Exception {
        mockMvc.perform(get("/health").header("Authorization", bearer(tokenAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status").value("UP"));
    }

    @Test
    void exponeLivenessYReadiness() throws Exception {
        mockMvc.perform(get("/health/liveness")).andExpect(status().isOk());
        mockMvc.perform(get("/health/readiness")).andExpect(status().isOk());
    }
}
