package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataRolRepository extends JpaRepository<RolEntity, UUID> {
    Optional<RolEntity> findByNombre(String nombre);
}
