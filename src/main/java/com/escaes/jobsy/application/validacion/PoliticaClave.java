package com.escaes.jobsy.application.validacion;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Política de seguridad para contraseñas nuevas (registro y restablecimiento).
 * No se aplica al login: las cuentas existentes siguen entrando con su clave actual.
 */
public final class PoliticaClave {

    public static final int LONGITUD_MINIMA = 8;
    public static final int LONGITUD_MAXIMA = 64;

    /** Fragmentos personales más cortos que esto no se revisan (evita falsos positivos como "al"). */
    private static final int LONGITUD_MINIMA_FRAGMENTO = 3;

    private PoliticaClave() {
    }

    /**
     * Devuelve los requisitos que la clave no cumple (lista vacía si es válida).
     *
     * @param nombre nombre del usuario (puede ser null)
     * @param correo correo del usuario (puede ser null); se revisa la parte antes de la @
     */
    public static List<String> incumplimientos(String clave, String nombre, String correo) {
        List<String> errores = new ArrayList<>();
        if (clave == null || clave.isEmpty()) {
            errores.add("La contraseña es obligatoria");
            return errores;
        }
        if (clave.length() < LONGITUD_MINIMA) {
            errores.add("La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres");
        }
        if (clave.length() > LONGITUD_MAXIMA) {
            errores.add("La contraseña debe tener máximo " + LONGITUD_MAXIMA + " caracteres");
        }
        if (clave.chars().noneMatch(Character::isUpperCase)) {
            errores.add("La contraseña debe tener al menos una letra mayúscula");
        }
        if (clave.chars().noneMatch(Character::isLowerCase)) {
            errores.add("La contraseña debe tener al menos una letra minúscula");
        }
        if (clave.chars().noneMatch(Character::isDigit)) {
            errores.add("La contraseña debe tener al menos un número");
        }
        if (clave.chars().allMatch(Character::isLetterOrDigit)) {
            errores.add("La contraseña debe tener al menos un carácter especial (ej: . * # $ ! @)");
        }
        if (clave.chars().anyMatch(Character::isWhitespace)) {
            errores.add("La contraseña no puede contener espacios");
        }
        if (contieneDatoPersonal(clave, nombre, correo)) {
            errores.add("La contraseña no puede contener tu nombre ni tu correo");
        }
        return errores;
    }

    /** Lanza BadRequest con todos los requisitos incumplidos separados por ". ". */
    public static void validar(String clave, String nombre, String correo) {
        List<String> errores = incumplimientos(clave, nombre, correo);
        if (!errores.isEmpty()) {
            throw new BusinessExceptions.BadRequestException(String.join(". ", errores));
        }
    }

    private static boolean contieneDatoPersonal(String clave, String nombre, String correo) {
        String claveNormalizada = normalizar(clave);
        List<String> fragmentos = new ArrayList<>();
        if (nombre != null) {
            fragmentos.addAll(List.of(normalizar(nombre).split("[^a-z0-9]+")));
        }
        if (correo != null) {
            String local = correo.contains("@") ? correo.substring(0, correo.indexOf('@')) : correo;
            fragmentos.addAll(List.of(normalizar(local).split("[^a-z0-9]+")));
        }
        return fragmentos.stream()
                .filter(f -> f.length() >= LONGITUD_MINIMA_FRAGMENTO)
                .anyMatch(claveNormalizada::contains);
    }

    /** Minúsculas y sin tildes, para que "María" y "maria" se consideren iguales. */
    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
