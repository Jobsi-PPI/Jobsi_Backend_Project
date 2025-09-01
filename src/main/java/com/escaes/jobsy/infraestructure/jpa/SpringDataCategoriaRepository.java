package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
    void deleteByNombreIgnoreCase(String nombre);
}
