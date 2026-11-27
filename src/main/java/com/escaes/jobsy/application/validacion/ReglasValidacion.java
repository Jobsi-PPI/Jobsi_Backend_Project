package com.escaes.jobsy.application.validacion;

/*
 * Reglas de formato compartidas por los DTOs (anotaciones de Bean Validation) y los casos de uso.
 * Son constantes para poder usarse dentro de las anotaciones. Si cambian, actualizar también
 * el frontend (Jobsi_Frontend_Project/docs/VALIDACIONES_PENDIENTES.md).
 */
public final class ReglasValidacion {

    private ReglasValidacion() {
    }

    // Usuario
    public static final int NOMBRE_MIN = 2;
    public static final int NOMBRE_MAX = 50;
    /** Letras (con tildes y ñ) separadas por un espacio, guion o apóstrofo. Ej: "María José", "O'Neil". */
    public static final String PATRON_NOMBRE = "^\\p{L}+(?:[ '\\-]\\p{L}+)*$";
    public static final String MENSAJE_NOMBRE = "solo puede contener letras, espacios, guion o apóstrofo";
    /** Igual que PATRON_NOMBRE con mínimo 2 caracteres, pero acepta cadena vacía (campo opcional). */
    public static final String PATRON_NOMBRE_OPCIONAL = "^$|^(?=.{2,})\\p{L}+(?:[ '\\-]\\p{L}+)*$";

    public static final int CORREO_MAX = 100;
    /** Celular colombiano: 10 dígitos y empieza por 3. */
    public static final String PATRON_TELEFONO = "^3\\d{9}$";
    public static final long DOCUMENTO_MAX = 999_999_999_999_999L;
    public static final int EDAD_MINIMA = 14;

    // Trabajo
    public static final int TITULO_MIN = 5;
    public static final int TITULO_MAX = 150;
    /** El título debe contener al menos una letra (no solo números o símbolos). */
    public static final String PATRON_TITULO = "^(?s).*\\p{L}.*$";
    public static final int DESCRIPCION_MIN = 20;
    public static final int DESCRIPCION_MAX = 500;
    public static final String PAGO_MIN = "1000";
    public static final String PAGO_MAX = "10000000";
}
