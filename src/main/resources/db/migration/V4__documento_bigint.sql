-- =====================================================================
-- V4 - El documento del usuario pasa de INTEGER a BIGINT
-- INTEGER solo admite hasta 2.147.483.647: una cedula de 11 digitos no cabia.
-- Se amplian tambien todas las columnas que lo referencian.
-- =====================================================================

ALTER TABLE usuarios ALTER COLUMN documento_usuario TYPE BIGINT;

ALTER TABLE trabajos ALTER COLUMN solicitante_id TYPE BIGINT;
ALTER TABLE trabajos ALTER COLUMN trabajador_id TYPE BIGINT;

ALTER TABLE solicitudes ALTER COLUMN usuario_id TYPE BIGINT;

ALTER TABLE tokens_recuperacion ALTER COLUMN usuario_documento TYPE BIGINT;
