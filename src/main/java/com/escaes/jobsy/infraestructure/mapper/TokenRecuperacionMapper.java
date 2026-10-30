package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.domain.model.TokenRecuperacion;
import com.escaes.jobsy.infraestructure.persistence.entity.TokenRecuperacionEntity;

public class TokenRecuperacionMapper {

    public static TokenRecuperacion toDomain(TokenRecuperacionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TokenRecuperacion(
                entity.getId(),
                entity.getTokenHash(),
                entity.getUsuarioDocumento(),
                entity.getExpiraEn(),
                Boolean.TRUE.equals(entity.getUsado()));
    }

    public static TokenRecuperacionEntity toEntity(TokenRecuperacion token) {
        if (token == null) {
            return null;
        }
        return TokenRecuperacionEntity.builder()
                .id(token.id())
                .tokenHash(token.tokenHash())
                .usuarioDocumento(token.usuarioDocumento())
                .expiraEn(token.expiraEn())
                .usado(token.usado())
                .build();
    }
}
