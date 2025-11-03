package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {

    Optional<UsuarioEntity> findByDocumento(Integer documento);

    Optional<UsuarioEntity> findByCorreo(String correo);

    Optional<UsuarioEntity> findByTelefono(String telefono);
}
