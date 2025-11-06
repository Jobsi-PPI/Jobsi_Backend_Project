package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByDocumentoOrCorreoOrTelefono(Integer documento, String correo, String telefono);

    Optional<UsuarioEntity> findByCorreo(String correo);

    Optional<UsuarioEntity> findByTelefono(String telefono);

    boolean existsByDocumento(Integer documento);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefono(String telefono);
}
