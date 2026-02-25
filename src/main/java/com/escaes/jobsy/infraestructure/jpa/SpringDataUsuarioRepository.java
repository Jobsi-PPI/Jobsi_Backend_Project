package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.GeneroEntity;
import com.escaes.jobsy.infraestructure.persistence.entity.RolEntity;
import com.escaes.jobsy.infraestructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByDocumentoOrCorreoOrTelefono(Integer documento, String correo, String telefono);

    Optional<UsuarioEntity> findByCorreo(String correo);

    Optional<UsuarioEntity> findByTelefono(String telefono);

    boolean existsByDocumento(Integer documento);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefono(String telefono);

    List<UsuarioEntity>findAllByBloqueado(Boolean bloqueado);

    List<UsuarioEntity>findAllByGenero(GeneroEntity genero);

    List<UsuarioEntity> findAllByRol(RolEntity rol);

    List<UsuarioEntity> findAllByFechaNacimientoBetween(
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

}
