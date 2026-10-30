package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.TrabajoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTrabajoRepository extends JpaRepository<TrabajoEntity, UUID> {

    List<TrabajoEntity> findBySolicitanteCorreo(String solicitanteCorreo);

    List<TrabajoEntity> findAllByTituloContainingIgnoreCase(String titulo);

    List<TrabajoEntity> findByTrabajadorCorreo(String trabajadorCorreo);

    Optional<TrabajoEntity> findByDescripcion(String nombre);

    @Query("SELECT t.estado.nombre, COUNT(t) FROM TrabajoEntity t GROUP BY t.estado.nombre")
    List<Object[]> contarPorEstado();

    @Query("SELECT t.categoria.nombre, COUNT(t) FROM TrabajoEntity t GROUP BY t.categoria.nombre")
    List<Object[]> contarPorCategoria();

    @Query("""
            SELECT COUNT(t) FROM TrabajoEntity t
            LEFT JOIN t.trabajador tr
            WHERE t.solicitante.documento = :documento OR tr.documento = :documento
            """)
    long contarPorUsuario(@Param("documento") Long documento);

    List<TrabajoEntity> findByTrabajadorCorreoAndEstadoNombre(
            String correoTrabajador,
            String nombreEstado
    );

}
