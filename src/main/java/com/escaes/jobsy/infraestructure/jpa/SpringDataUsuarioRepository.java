package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.GeneroEntity;
import com.escaes.jobsy.infraestructure.persistence.entity.RolEntity;
import com.escaes.jobsy.infraestructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataUsuarioRepository
        extends JpaRepository<UsuarioEntity, Long>, JpaSpecificationExecutor<UsuarioEntity> {

    Optional<UsuarioEntity> findByDocumentoOrCorreoOrTelefono(Long documento, String correo, String telefono);

    Optional<UsuarioEntity> findByCorreo(String correo);

    Optional<UsuarioEntity> findByTelefono(String telefono);

    boolean existsByDocumento(Long documento);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefono(String telefono);

    List<UsuarioEntity>findAllByBloqueado(Boolean bloqueado);

    long countByBloqueado(Boolean bloqueado);

    boolean existsByRolNombre(String nombre);

    List<UsuarioEntity>findAllByGenero(GeneroEntity genero);

    List<UsuarioEntity> findAllByRol(RolEntity rol);

    List<UsuarioEntity> findAllByFechaNacimientoBetween(
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

}
