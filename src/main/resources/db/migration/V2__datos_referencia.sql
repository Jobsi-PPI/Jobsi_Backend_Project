-- =====================================================================
-- V2 - Datos de referencia (catalogos)
-- Reemplaza la siembra que hacia config/DataInitializer.
-- El usuario administrador NO se crea aqui: lo crea AdminInitializer
-- a partir de variables de entorno (la clave debe ir cifrada con BCrypt).
-- =====================================================================

INSERT INTO sexos (nombre_sexo) VALUES
    ('Masculino'),
    ('Femenino'),
    ('Alien'),
    ('Otro');

INSERT INTO roles (rol_id, nombre_rol, descripcion_rol) VALUES
    (gen_random_uuid(), 'ADMIN', 'Administrador del sistema con acceso completo'),
    (gen_random_uuid(), 'USER', 'Usuario regular con acceso limitado');

INSERT INTO estados (nombre_estado) VALUES
    ('PENDIENTE'),
    ('FINALIZADO'),
    ('ASIGNADO'),
    ('CANCELADO'),
    ('BLOQUEADO');

INSERT INTO categorias (nombre_categoria) VALUES
    ('ASESORIAS'),
    ('TAREAS'),
    ('MATERIALES'),
    ('ENTRENAMIENTOS'),
    ('OTRO');

INSERT INTO tipo_pagos (tipo_pago_id, nombre_tipo_pago) VALUES
    (gen_random_uuid(), 'EFECTIVO'),
    (gen_random_uuid(), 'TRANSFERENCIA'),
    (gen_random_uuid(), 'INTERCAMBIO'),
    (gen_random_uuid(), 'OTRO');

-- Las ubicaciones se guardan en minuscula (igual que GestionUbicacionUseCase)
INSERT INTO ubicacion (id, nombre_ubicacion) VALUES
    (gen_random_uuid(), 'palmas'),
    (gen_random_uuid(), 'almendros'),
    (gen_random_uuid(), 'terraza'),
    (gen_random_uuid(), 'p40'),
    (gen_random_uuid(), 'p13');

INSERT INTO valoraciones_categorias (nombre_valoracion_categoria) VALUES
    ('Calidad'),
    ('Puntualidad'),
    ('Comunicacion'),
    ('Profesionalismo'),
    ('Recomendacion');
