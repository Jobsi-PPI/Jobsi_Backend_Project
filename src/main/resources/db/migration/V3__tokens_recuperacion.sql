-- =====================================================================
-- V3 - Tokens de recuperacion de contrasena
-- Solo se guarda el hash SHA-256 (hex, 64 caracteres) del token.
-- =====================================================================

CREATE TABLE tokens_recuperacion (
    token_id          UUID PRIMARY KEY,
    token_hash        VARCHAR(64)                 NOT NULL,
    usuario_documento INTEGER                     NOT NULL,
    expira_en         TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    usado             BOOLEAN                     NOT NULL DEFAULT FALSE,
    fecha_creacion    TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tokens_recuperacion_hash UNIQUE (token_hash),
    CONSTRAINT fk_token_recuperacion_usuario FOREIGN KEY (usuario_documento)
        REFERENCES usuarios (documento_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_tokens_recuperacion_usuario ON tokens_recuperacion (usuario_documento);
