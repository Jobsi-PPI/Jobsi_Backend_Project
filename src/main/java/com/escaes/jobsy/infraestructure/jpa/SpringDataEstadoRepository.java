package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataEstadoRepository extends JpaRepository<EstadoEntity, Long> {
    Optional<EstadoEntity> findByNombreIgnoreCase(String nombre);
    void deleteByNombreIgnoreCase(String nombre);
}
