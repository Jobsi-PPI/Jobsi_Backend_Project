package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.SolicitudEntity;
import com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataSolicitudRepository extends JpaRepository<SolicitudEntity, UUID> {

    @Query("""
            SELECT s FROM SolicitudEntity s
            JOIN FETCH s.usuario
            WHERE s.trabajo.id = :trabajoId
            """)
    List<SolicitudEntity> findSolicitudesConUsuario(UUID trabajoId);

    @Modifying
    @Query("""
    UPDATE SolicitudEntity s
    SET s.estado = :estado
    WHERE s.id = :solicitudId
""")
    void actualizarEstado(@Param("solicitudId") UUID solicitudId,
                          @Param("estado") EstadoSolicitud estado);

    @Modifying
    @Query("""
                UPDATE SolicitudEntity s
                SET s.estado = com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud.RECHAZADA
                WHERE s.trabajo.id = :trabajoId
                AND s.id <> :solicitudId
            """)
    void rechazarOtrasSolicitudes(@Param("trabajoId") UUID trabajoId,
                                  @Param("solicitudId") UUID solicitudId);

    boolean existsByUsuarioCorreoAndTrabajoId(String correo, UUID trabajoId);

    List<SolicitudEntity> findByUsuarioDocumento(Long documento);

    List<SolicitudEntity> findByTrabajoId(UUID trabajoId);

    @Modifying
    @Query("""
                UPDATE SolicitudEntity s
                SET s.estado = com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud.CERRADA
                WHERE s.trabajo.id = :trabajoId
                AND s.estado = com.escaes.jobsy.infraestructure.persistence.enums.EstadoSolicitud.PENDIENTE
            """)
    void cerrarSolicitudesPendientes(@Param("trabajoId") UUID trabajoId);

    @Modifying
    @Query("DELETE FROM SolicitudEntity s WHERE s.trabajo.id = :trabajoId")
    void eliminarPorTrabajo(@Param("trabajoId") UUID trabajoId);

    @Query("SELECT s.estado, COUNT(s) FROM SolicitudEntity s GROUP BY s.estado")
    List<Object[]> contarPorEstado();

    long countByUsuarioDocumento(Long documento);


}
