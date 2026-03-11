package com.escaes.jobsy.domain.repository;

import com.escaes.jobsy.domain.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    void save(Usuario usuario);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByTelefono(String telefono);

    Optional<Usuario> findByDocumentoOrCorreoOrTelefono(Integer documento, String correo, String telefono);

    Usuario findByCorreoAndClave(String correo, String clave);

    Usuario findByCorreoAndClaveAndBloqueado(String correo, String clave, Boolean bloqueado);

    Usuario findByCorreoAndBloqueado(String correo, Boolean bloqueado);

    List<Usuario> findAll();

    List<Usuario> findAllByBloqueado(Boolean bloqueado);

    List<Usuario> findAllByGenero(String genero);

    List<Usuario> findAllByRol(String rol);

    List<Usuario> findAllByFechaNacimientoBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Usuario> findUsersCriteria(Integer documento, String correo, String genero, String rol, Boolean bloqueado, Integer valoracionConteo, Double valoracionPromedio, int size, int page);

    void deleteById(Integer id);

    void deleteByCorreo(String correo);

    void delete(Usuario usuario);
}
