package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.institucion.InstitucionRequest;
import com.escaes.jobsy.application.dto.institucion.InstitucionResponse;
import com.escaes.jobsy.domain.model.Institucion;
import com.escaes.jobsy.infraestructure.persistence.entity.InstitucionEntity;

public class InstitucionMapper {

    public static Institucion toDomain(InstitucionEntity institucionEntity) {
        if (institucionEntity == null) {
            return null;
        }
        return new Institucion(
                institucionEntity.getId(), institucionEntity.getNombre(),
                institucionEntity.getDepartamento(),  institucionEntity.getMunicipio()
        );
    }
    public static InstitucionEntity toEntity(Institucion institucion) {
        if (institucion == null) {
            return null;
        }
        return new InstitucionEntity(
                institucion.id(), institucion.nombre(), institucion.departamento(),
                institucion.municipio()
        );
    }
    public static InstitucionResponse entityToResponse(Institucion institucion) {
        if (institucion == null) {
            return null;
        }
        return new InstitucionResponse(
                institucion.id()!= null ? institucion.id():null,
                institucion.nombre(),
                institucion.departamento() !=null? institucion.departamento() : null,
                institucion.municipio() !=null? institucion.municipio() : null
        );
    }
    public static InstitucionResponse requestToResponse(InstitucionRequest request){
        if (request == null) {
            return null;
        }

        return new InstitucionResponse(
                null, request.nombre(),
                request.departamentoCodigo() !=null?request.departamentoCodigo():null,
                request.municipioCodigo() !=null?request.municipioCodigo():null

        );
    }
}
