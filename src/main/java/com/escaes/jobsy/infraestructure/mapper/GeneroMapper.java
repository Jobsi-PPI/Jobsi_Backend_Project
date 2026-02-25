package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.genero.GeneroRequest;
import com.escaes.jobsy.application.dto.genero.GeneroResponse;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.infraestructure.persistence.entity.GeneroEntity;

public class GeneroMapper {

    public static Genero toDomain(GeneroEntity generoEntity){

        if(generoEntity == null){
            return null;
        }
        return new Genero(generoEntity.getId(), generoEntity.getNombreGenero());
    }

    public static  GeneroEntity toEntity(Genero domain){
        if(domain==null){
            return null;
        }

        return GeneroEntity.builder()
                .id(domain.id())
                .nombreGenero(domain.nombreGenero())
                .build();
    }
    public static GeneroResponse entityToResponse(Genero genero){
        if(genero==null){
            return null;
        }
        return new GeneroResponse(
                genero.nombreGenero()!= null? genero.nombreGenero() : null
        );
    }
    public static GeneroResponse requestToResponse(GeneroRequest generoRequest){
        if(generoRequest==null){
            return null;
        }
        return new GeneroResponse(
                generoRequest.nombre()!= null? generoRequest.nombre() : null
        );
    }
}
