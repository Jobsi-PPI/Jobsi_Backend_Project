package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.entity.TrabajoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTrabajoRepository extends JpaRepository<TrabajoEntity, UUID> {

    List<TrabajoEntity> findBySolicitanteCorreo(String solicitanteCorreo);

    List<TrabajoEntity> findAllByTituloContainingIgnoreCase(String titulo);

    List<TrabajoEntity> findByTrabajadorCorreo(String trabajadorCorreo);

    Optional<TrabajoEntity> findByDescripcion(String nombre);
}
