package com.escaes.jobsy.infraestructure;

import com.escaes.jobsy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigracionesTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void aplicaTodasLasMigraciones() {
        List<String> versiones = jdbcTemplate.queryForList(
                // version IS NULL corresponde al registro de creación del esquema (create-schemas=true)
                "SELECT version FROM jobsi.flyway_schema_history WHERE success AND version IS NOT NULL ORDER BY installed_rank",
                String.class);
        assertThat(versiones).containsExactly("1", "2", "3", "4");
    }

    @Test
    void siembraLosCatalogosDeReferencia() {
        assertThat(contar("sexos")).isEqualTo(4);
        assertThat(contar("roles")).isEqualTo(2);
        assertThat(contar("estados")).isEqualTo(5);
        assertThat(contar("tipo_pagos")).isEqualTo(4);
        assertThat(contar("ubicacion")).isEqualTo(5);
        assertThat(contar("valoraciones_categorias")).isEqualTo(5);
        assertThat(contar("categorias")).isGreaterThanOrEqualTo(5);
    }

    @Test
    void creaElAdministradorDesdeLasPropiedades() {
        String rol = jdbcTemplate.queryForObject("""
                SELECT r.nombre_rol FROM jobsi.usuarios u
                JOIN jobsi.roles r ON r.rol_id = u.rol_id
                WHERE u.correo_usuario = ?
                """, String.class, ADMIN_CORREO);
        String clave = jdbcTemplate.queryForObject(
                "SELECT clave_usuario FROM jobsi.usuarios WHERE correo_usuario = ?", String.class, ADMIN_CORREO);

        assertThat(rol).isEqualTo("ADMIN");
        // La clave se guarda cifrada con BCrypt, nunca en claro
        assertThat(clave).startsWith("$2").isNotEqualTo(ADMIN_CLAVE);
    }

    private int contar(String tabla) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jobsi." + tabla, Integer.class);
    }
}
