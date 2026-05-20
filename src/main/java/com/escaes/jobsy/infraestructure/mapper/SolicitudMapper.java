package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.solicitud.SolicitudResponse;
import com.escaes.jobsy.application.dto.solicitud.SolicitudUsuarioResponse;
import com.escaes.jobsy.domain.model.Solicitud;
import com.escaes.jobsy.infraestructure.persistence.entity.SolicitudEntity;

public class SolicitudMapper {


    public static SolicitudEntity toEntity(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        return SolicitudEntity.builder()
                .id(solicitud.id())
                .usuario(UsuarioMapper.toEntity(solicitud.trabajador()))
                .trabajo(TrabajoMapper.toEntity(solicitud.trabajo()))
                .estado(solicitud.estado())
                .build();
    }

    public static Solicitud toDomain(SolicitudEntity solicitud) {
        if (solicitud == null) {
            return null;
        }
        return new Solicitud(
                solicitud.getId(),
                UsuarioMapper.toDomainBasic(solicitud.getUsuario()),
                TrabajoMapper.toDomain(solicitud.getTrabajo()),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud(),
                solicitud.getFechaModificacion()
        );
    }

    public static SolicitudResponse  toResponse(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        return new SolicitudResponse(
                solicitud.id(),
                solicitud.trabajador(),
                solicitud.trabajo(),
                solicitud.estado(),
                solicitud.fechaCreacion()
        );
    }

    public static SolicitudUsuarioResponse toUserResponse(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        return new SolicitudUsuarioResponse(
                solicitud.id(),
                solicitud.trabajador().nombre(),
                solicitud.trabajador().correo(),
                solicitud.trabajador().telefono(),
                solicitud.estado(),
                solicitud.fechaCreacion(),
                solicitud.trabajador().valoracionPromedio()
        );
    }
}
