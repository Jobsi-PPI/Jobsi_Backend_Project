package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SpringDataEstadoRepository extends JpaRepository<EstadoEntity, Long> {
    Optional<EstadoEntity> findByNombreIgnoreCase(String nombre);
    @Transactional
    void deleteByNombreIgnoreCase(String nombre);
}
