package com.escaes.jobsy.application.usecase.usuario;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.RolRepository;
import com.escaes.jobsy.domain.repository.SolicitudRepository;
import com.escaes.jobsy.domain.repository.TrabajoRepository;
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

    private final RolRepository rolRepository;

    private final TrabajoRepository trabajoRepository;

    private final SolicitudRepository solicitudRepository;

    private static final long MAXIMO_DOCUMENTO = 999_999_999_999_999L;

    private void validateUser(UsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (request.documento() == null || request.documento() <= 0
                || request.documento() > MAXIMO_DOCUMENTO) {
            throw new BusinessExceptions.BadRequestException(
                    "El documento es obligatorio y debe ser un número positivo de máximo 15 dígitos");
        }
        if(request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser vacío ");
        }
        if (request.primerApellido().isBlank()) {
            throw new IllegalArgumentException("El primer apellido no puede ser vacío");
        }

        usuarioRepository.findByDocumentoOrCorreoOrTelefono(
                request.documento(), request.email(), request.telefono()).ifPresent(existing -> {

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

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException(
                        "Usuario no encontrado con el documento proporcionado"));
    }

    public boolean existeUsuarioConCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
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

    public void eliminarUsuarioPorId(Long id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el documento proporcionado");
        }
        usuarioRepository.deleteById(id);
    }

    public void eliminarUsuarioPorDocumento(Long documento) {
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
        Usuario usuarioBloqueado = new Usuario(usuario.documento(), usuario.nombre(), usuario.primerApellido(), usuario.segundoApellido(),
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
        Usuario usuarioDesbloqueado = new Usuario(usuario.documento(), usuario.nombre(),
                usuario.primerApellido(), usuario.segundoApellido(), usuario.correo(),
                usuario.clave(), usuario.telefono(), false, usuario.fechaNacimiento(), usuario.valoracionConteo(),
                usuario.valoracionPromedio(), usuario.genero(), usuario.rol(),
                usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(usuarioDesbloqueado);
    }

    /*
     * Operaciones del panel de administración. El administrador que ejecuta la acción
     * (correoAdmin) no puede bloquearse, eliminarse ni cambiarse el rol a sí mismo.
     */
    public Usuario bloquearUsuario(Long documento, String correoAdmin) {
        Usuario usuario = obtenerUsuarioPorId(documento);
        validarNoEsElMismoAdmin(usuario, correoAdmin, "No puedes bloquear tu propia cuenta");
        if (Boolean.TRUE.equals(usuario.bloqueado())) {
            throw new BusinessExceptions.ConflictException("El usuario ya está bloqueado");
        }
        return guardarConBloqueo(usuario, true);
    }

    public Usuario desbloquearUsuario(Long documento) {
        Usuario usuario = obtenerUsuarioPorId(documento);
        if (!Boolean.TRUE.equals(usuario.bloqueado())) {
            throw new BusinessExceptions.ConflictException("El usuario no está bloqueado");
        }
        return guardarConBloqueo(usuario, false);
    }

    public Usuario cambiarRol(Long documento, String nombreRol, String correoAdmin) {
        Usuario usuario = obtenerUsuarioPorId(documento);
        validarNoEsElMismoAdmin(usuario, correoAdmin, "No puedes cambiar tu propio rol");
        Rol rol = rolRepository.findByNombre(nombreRol.trim().toUpperCase())
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Rol no encontrado: " + nombreRol));
        Usuario actualizado = new Usuario(usuario.documento(), usuario.nombre(), usuario.primerApellido(),
                usuario.segundoApellido(), usuario.correo(), usuario.clave(), usuario.telefono(),
                usuario.bloqueado(), usuario.fechaNacimiento(), usuario.valoracionConteo(),
                usuario.valoracionPromedio(), usuario.genero(), rol,
                usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(actualizado);
        return actualizado;
    }

    /*
     * Solo se elimina un usuario sin historial. Si publicó, realizó o se postuló a trabajos,
     * borrarlo eliminaría ese historial en cascada: en ese caso se debe bloquear.
     */
    public void eliminarUsuarioAdmin(Long documento, String correoAdmin) {
        Usuario usuario = obtenerUsuarioPorId(documento);
        validarNoEsElMismoAdmin(usuario, correoAdmin, "No puedes eliminar tu propia cuenta");
        if (trabajoRepository.contarPorUsuario(documento) > 0 || solicitudRepository.contarPorUsuario(documento) > 0) {
            throw new BusinessExceptions.ConflictException(
                    "El usuario tiene trabajos o postulaciones registradas; bloquéalo en lugar de eliminarlo");
        }
        usuarioRepository.deleteById(documento);
    }

    private void validarNoEsElMismoAdmin(Usuario usuario, String correoAdmin, String mensaje) {
        if (usuario.correo().equalsIgnoreCase(correoAdmin)) {
            throw new BusinessExceptions.BadRequestException(mensaje);
        }
    }

    private Usuario guardarConBloqueo(Usuario usuario, boolean bloqueado) {
        Usuario actualizado = new Usuario(usuario.documento(), usuario.nombre(), usuario.primerApellido(),
                usuario.segundoApellido(), usuario.correo(), usuario.clave(), usuario.telefono(), bloqueado,
                usuario.fechaNacimiento(), usuario.valoracionConteo(), usuario.valoracionPromedio(),
                usuario.genero(), usuario.rol(), usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(actualizado);
        return actualizado;
    }
}
