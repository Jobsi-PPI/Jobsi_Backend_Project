package com.escaes.jobsy.domain.repository;

import com.escaes.jobsy.domain.model.TokenRecuperacion;

import java.util.Optional;

public interface TokenRecuperacionRepository {

    void save(TokenRecuperacion token);

    Optional<TokenRecuperacion> findByTokenHash(String tokenHash);

    void invalidarTokensDeUsuario(Long usuarioDocumento);
}
