package com.escaes.jobsy.infraestructure.persistence.specification;

import com.escaes.jobsy.infraestructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UsuarioEntity> hasDocument(String document) {
        return (root, query, cb) -> {
            if (document == null || document.isEmpty()) {
                return cb.conjunction();
            } else {
                return cb.like(root.get("documento"), "%"+document+"%");
            }
        };
    }
    public static Specification<UsuarioEntity> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            } else {
                return cb.like(root.get("correo"), "%"+email+"%");
            }
        };
    }
    public static Specification<UsuarioEntity> hasGender(String genero) {
        return (root, query, cb) -> {
            if (genero == null || genero.isEmpty()) {
                return cb.conjunction();
            } else {
                return cb.like(root.join("genero").get("nombreGenero"), "%"+genero+"%");
            }
        };
    }
    public static Specification<UsuarioEntity> hasRol(String rol) {
      return (root, query, cb) -> {
          if (rol == null || rol.isEmpty()) {
              return cb.conjunction();
          }else  {
              return cb.like(root.join("rol").get("nombre"), "%" + rol + "%");
          }
      };
    }
    public static Specification<UsuarioEntity>valoracionConteo(Integer valoracionConteo) {
        return (root, query, cb) ->{
            if (valoracionConteo == null) {
                return cb.conjunction();
            }else{
                return cb.equal(root.get("valoracionConteo"), valoracionConteo);
            }
        };
    }
    public static Specification<UsuarioEntity>valoracionPromedio(Integer valoracionPromedio) {
        return (root, query, cb) ->  {
            if (valoracionPromedio == null) {
               return cb.conjunction();
            }else {
                return cb.equal(root.get("valoracionPromedio"), valoracionPromedio);
            }
        };
    }

}
