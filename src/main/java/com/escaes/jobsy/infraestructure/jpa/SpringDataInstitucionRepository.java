package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.InstitucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataInstitucionRepository extends JpaRepository<InstitucionEntity, UUID> {
    List<InstitucionEntity>findByNombre(String nombre);
    List<InstitucionEntity> findByDepartamento(String departamento);
    List<InstitucionEntity> findByMunicipio(String municipio);
    List<InstitucionEntity>findByDepartamentoAndMunicipio(String departamento,String municipio);
    List<InstitucionEntity>findByNombreAndDepartamento(String nombre, String departamento);
    Optional <InstitucionEntity> findByNombreAndDepartamentoAndMunicipioIgnoreCase(String nombre, String departamento, String municipio);
}
