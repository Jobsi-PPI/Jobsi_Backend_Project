package com.escaes.jobsy.application.usecase.usuario;

import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ListarUsuariosUseCase {
    
    private final UsuarioRepository usuarioRepository;

    /*
    * Obtiene todos los usuarios registrados en el sistema
    * de acuerdo a la condición.
    * @return conteo de usuarios.
    */
    public int contarUsuarios() {
        return usuarioRepository.findAll().size();
    }
    public int contarUsuariosDesbloqueado() {
        return usuarioRepository.findAllByBloqueado(false).size();
    }
    public int contarUsuariosBloqueado() {
        return usuarioRepository.findAllByBloqueado(true).size();
    }
    /*
     * Obtiene todos los usuarios registrados en el sistema
     * de acuerdo a la condición.
     * @return lista de usuarios.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    public List<Usuario> listarUsuariosDesbloqueado() {
        return usuarioRepository.findAllByBloqueado(false);
    }
    public List<Usuario> listarUsuariosBloqueado() {
        return usuarioRepository.findAllByBloqueado(true);
    }
    public List<Usuario> listarUsuariosPorGenero(String genero) {
        return usuarioRepository.findAllByGenero(genero);
    }
    public List<Usuario> listarUsuariosPorRol(String rol) {
        return usuarioRepository.findAllByRol(rol);
    }
    public List<Usuario> listarUsuariosPorFechaNacimiento(LocalDate fechaInicio, LocalDate fechaFin) {
        return usuarioRepository.findAllByFechaNacimientoBetween(fechaInicio, fechaFin);
    }

}
