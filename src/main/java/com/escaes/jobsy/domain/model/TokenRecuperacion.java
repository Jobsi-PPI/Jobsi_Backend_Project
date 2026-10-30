package com.escaes.jobsy.domain.model;

import java.time.Instant;
import java.util.UUID;

/*
 * Token de un solo uso para restablecer la contraseña.
 * Solo se persiste el hash SHA-256 del token; el valor en claro únicamente viaja en el correo.
 */
public record TokenRecuperacion(UUID id, String tokenHash, Long usuarioDocumento, Instant expiraEn,
                                boolean usado) {

    public static TokenRecuperacion crear(String tokenHash, Long usuarioDocumento, Instant expiraEn) {
        return new TokenRecuperacion(UUID.randomUUID(), tokenHash, usuarioDocumento, expiraEn, false);
    }

    public boolean vigente(Instant ahora) {
        return !usado && ahora.isBefore(expiraEn);
    }

    public TokenRecuperacion marcarUsado() {
        return new TokenRecuperacion(id, tokenHash, usuarioDocumento, expiraEn, true);
    }
}
