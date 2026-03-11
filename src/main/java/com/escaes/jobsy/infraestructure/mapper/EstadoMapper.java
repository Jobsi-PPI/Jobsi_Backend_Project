package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.estado.EstadoRequest;
import com.escaes.jobsy.application.dto.estado.EstadoResponse;
import com.escaes.jobsy.domain.model.Estado;
import com.escaes.jobsy.infraestructure.persistence.entity.EstadoEntity;

public class EstadoMapper {

    public static Estado toDomain(EstadoEntity estadoEntity) {
        if  (estadoEntity == null) {
            return null;
        }
        return new Estado(
                estadoEntity.getId(), estadoEntity.getNombre()
        );
    }
    public static EstadoEntity toEntity(Estado estado) {
        if  (estado == null) {
            return null;
        }
        return new EstadoEntity(
                estado.id(),
                estado.nombre()
        );
    }
    public static EstadoResponse entityToResponse(Estado estado) {
        if  (estado == null) {
            return null;
        }
        return new EstadoResponse(
                estado.nombre() != null? estado.nombre() : null
        );
    }
    public static EstadoResponse requestToResponse(EstadoRequest estadoRequest) {
        if  (estadoRequest == null) {
            return null;
        }
        return new EstadoResponse(
                estadoRequest.nombre() != null? estadoRequest.nombre() : null
        );
    }
}
