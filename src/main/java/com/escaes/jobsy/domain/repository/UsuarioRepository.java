package com.escaes.jobsy.domain.repository;

import com.escaes.jobsy.domain.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    void save (Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByDocumento(Integer documento);
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario>findByTelefono(String telefono);
    Usuario findByCorreoAndClave(String correo, String clave);
    Usuario findByCorreoAndClaveAndBloqueado(String correo, String clave, Boolean bloqueado);
    Usuario findByCorreoAndBloqueado(String correo, Boolean bloqueado);
    List<Usuario>findAll();
    List<Usuario>findAllByBloqueado(Boolean bloqueado);
    List<Usuario>findAllByGenero(String genero);
    List<Usuario>findAllByRol(String rol);
    List<Usuario>findAllByFechaNacimientoBetween(String fechaInicio, String fechaFin);
    void deleteById(UUID id);
    void deleteByDocumento(Integer documento);
    void deleteByCorreo(String correo);
    void delete(Usuario usuario);
}
