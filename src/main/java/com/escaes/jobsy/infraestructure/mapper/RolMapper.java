package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.infraestructure.persistence.entity.RolEntity;

public class RolMapper {

     public static Rol toDomain(RolEntity rolEntity) {
         if (rolEntity == null) {
             return null;
         }
         return new Rol(rolEntity.getId(), rolEntity.getNombre(), rolEntity.getDescripcion());
     }

     public static RolEntity toEntity(Rol rol) {
         if (rol == null) {
             return null;
         }
       return new RolEntity(rol.id(), rol.nombre(), rol.descripcion());
     }
}
