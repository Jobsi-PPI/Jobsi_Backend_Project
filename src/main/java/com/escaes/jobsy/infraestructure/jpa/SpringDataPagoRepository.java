package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPagoRepository extends JpaRepository<PagoEntity, UUID> {
    Optional<PagoEntity> findByNombreIgnoreCase(String nombrePago);
}
