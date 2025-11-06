package com.escaes.jobsy.application.usecase.usuario;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;

@Service
@RequiredArgsConstructor
public class GestionUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private void validateUser(UsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        usuarioRepository.findByDocumentoOrCorreoOrTelefono(
                request.documento(), request.email(), request.telefono()).ifPresent(existing -> {
                    // puedes chequear el campo exacto para mensaje más claro
                    if (existing.documento().equals(request.documento())) {
                        throw new BusinessExceptions.ConflictException(
                                "Ya existe un usuario con el documento proporcionado");
                    }
                    if (existing.correo().equalsIgnoreCase(request.email())) {
                        throw new BusinessExceptions.ConflictException(
                                "Ya existe un usuario con el correo proporcionado");
                    }
                    if (existing.telefono().equals(request.telefono())) {
                        throw new BusinessExceptions.ConflictException(
                                "Ya existe un usuario con el teléfono proporcionado");
                    }
                });

    }

    public void crearUsuario(UsuarioRequest request, Genero genero, Rol rol) {

        validateUser(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        Usuario usuario = Usuario.crear(request, encodedPassword, genero, rol);
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException(
                        "Usuario no encontrado con el documento proporcionado"));
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException(
                        "Usuario no encontrado con el correo proporcionado"));
    }

    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new BusinessExceptions.BadRequestException("El usuario no puede ser nulo");
        }
        if (usuarioRepository.findById(usuario.documento()).isEmpty()) {
            throw new BusinessExceptions.NotFoundException("Usuario no encontrado con el documento proporcionado");
        }
        usuarioRepository.save(usuario);
    }

    public void eliminarUsuarioPorId(Integer id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el documento proporcionado");
        }
        usuarioRepository.deleteById(id);
    }

    public void eliminarUsuarioPorDocumento(Integer documento) {
        usuarioRepository.findById(documento).orElseThrow(
                () -> new BusinessExceptions.NotFoundException("Usuario no encontrado con el documento proporcionado"));
        usuarioRepository.deleteById(documento);
    }

    public void eliminarUsuarioPorCorreo(String correo) {
        usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el correo proporcionado"));
        usuarioRepository.deleteByCorreo(correo);
    }

    public void eliminarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuarioRepository.findById(usuario.documento()).isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el documento proporcionado");
        }
        usuarioRepository.delete(usuario);
    }

    public void bloquearUsuarioPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el correo proporcionado"));
        if (usuario.bloqueado()) {
            throw new IllegalArgumentException("El usuario ya está bloqueado");
        }
        Usuario usuarioBloqueado = new Usuario(usuario.documento(), usuario.nombre(),
                usuario.correo(),
                usuario.clave(), usuario.telefono(), true, usuario.fechaNacimiento(), usuario.valoracionConteo(),
                usuario.valoracionPromedio(), usuario.genero(), usuario.rol(),
                usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(usuarioBloqueado);
    }

    public void desbloquearUsuarioPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el correo proporcionado"));
        if (!usuario.bloqueado()) {
            throw new IllegalArgumentException("El usuario ya está desbloqueado");
        }
        Usuario usuarioDesbloqueado = new Usuario(usuario.documento(), usuario.nombre(), usuario.correo(),
                usuario.clave(), usuario.telefono(), false, usuario.fechaNacimiento(), usuario.valoracionConteo(),
                usuario.valoracionPromedio(), usuario.genero(), usuario.rol(),
                usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(usuarioDesbloqueado);
    }
}
