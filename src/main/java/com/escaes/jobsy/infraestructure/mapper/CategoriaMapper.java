package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.categoria.CategoriaRequest;
import com.escaes.jobsy.application.dto.categoria.CatergoriaResponse;
import com.escaes.jobsy.domain.model.Categoria;
import com.escaes.jobsy.infraestructure.persistence.entity.CategoriaEntity;

public class CategoriaMapper {

    public static Categoria toDomain(CategoriaEntity entity){

        if(entity==null){
            return null;
        }
        return new Categoria(
                entity.getId(), entity.getNombre()
        );
    }
    public static CategoriaEntity toEntity(Categoria categoria){
        if(categoria==null){
            return null;
        }
        return new CategoriaEntity(
                categoria.id(),categoria.nombre()
        );
    }
    public static CatergoriaResponse entityToResponse(Categoria categoria){
        if(categoria==null){
            return null;
        }
        return new CatergoriaResponse(
                categoria.nombre() != null ? categoria.nombre() : null
        );
    }
    public static CatergoriaResponse requestToResponse(CategoriaRequest request){
        if(request==null){
            return null;
        }
        return new CatergoriaResponse(
                request.nombre() !=null ? request.nombre() : null
        );
    }
}
