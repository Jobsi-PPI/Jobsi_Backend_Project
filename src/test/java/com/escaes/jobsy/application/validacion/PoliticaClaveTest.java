package com.escaes.jobsy.application.validacion;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PoliticaClaveTest {

    private static final String NOMBRE = "María";
    private static final String CORREO = "mjose@elpoli.edu.co";

    @Test
    void aceptaClaveQueCumpleTodo() {
        assertThat(PoliticaClave.incumplimientos("Clave.12345", NOMBRE, CORREO)).isEmpty();
    }

    @Test
    void exigeLongitudMinimaYMaxima() {
        assertThat(PoliticaClave.incumplimientos("Ab1.", NOMBRE, CORREO))
                .anyMatch(m -> m.contains("al menos 8"));
        assertThat(PoliticaClave.incumplimientos("Ab1." + "x".repeat(61), NOMBRE, CORREO))
                .anyMatch(m -> m.contains("máximo 64"));
    }

    @Test
    void exigeMayusculaMinusculaNumeroYEspecial() {
        assertThat(PoliticaClave.incumplimientos("clave.12345", NOMBRE, CORREO))
                .containsExactly("La contraseña debe tener al menos una letra mayúscula");
        assertThat(PoliticaClave.incumplimientos("CLAVE.12345", NOMBRE, CORREO))
                .containsExactly("La contraseña debe tener al menos una letra minúscula");
        assertThat(PoliticaClave.incumplimientos("Clave.abcde", NOMBRE, CORREO))
                .containsExactly("La contraseña debe tener al menos un número");
        assertThat(PoliticaClave.incumplimientos("Clave12345", NOMBRE, CORREO))
                .anyMatch(m -> m.contains("carácter especial"));
    }

    @Test
    void rechazaEspacios() {
        assertThat(PoliticaClave.incumplimientos("Clave 12345.", NOMBRE, CORREO))
                .anyMatch(m -> m.contains("espacios"));
    }

    @Test
    void rechazaNombreOCorreoSinImportarTildesNiMayusculas() {
        assertThat(PoliticaClave.incumplimientos("MARIA.2026", NOMBRE, CORREO))
                .anyMatch(m -> m.contains("nombre"));
        assertThat(PoliticaClave.incumplimientos("Mjose.2026", NOMBRE, CORREO))
                .anyMatch(m -> m.contains("correo"));
    }

    @Test
    void ignoraFragmentosPersonalesMuyCortos() {
        // "Al" tiene 2 letras: no se considera dato personal
        assertThat(PoliticaClave.incumplimientos("Alto.12345", "Al", "al@x.co")).isEmpty();
    }

    @Test
    void validarLanzaBadRequestConTodosLosMensajes() {
        assertThatThrownBy(() -> PoliticaClave.validar("abc", null, null))
                .isInstanceOf(BusinessExceptions.BadRequestException.class)
                .hasMessageContaining("al menos 8")
                .hasMessageContaining("mayúscula")
                .hasMessageContaining("número");
        assertThatThrownBy(() -> PoliticaClave.validar(null, null, null))
                .hasMessageContaining("obligatoria");
    }
}
