package com.escaes.jobsy.application.usecase.auth;

import com.escaes.jobsy.domain.model.TokenRecuperacion;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.CorreoService;
import com.escaes.jobsy.domain.repository.TokenRecuperacionRepository;
import com.escaes.jobsy.domain.repository.UsuarioRepository;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/*
 * Recuperación de contraseña por correo:
 * 1. solicitarRecuperacion: genera un token de un solo uso y envía el enlace por correo.
 * 2. validarToken: indica si el enlace sigue vigente.
 * 3. restablecerClave: cambia la clave y consume el token.
 */
@Service
public class RecuperarClaveUseCase {

    public static final int LONGITUD_MINIMA_CLAVE = 8;

    private static final String MENSAJE_ENLACE_INVALIDO = "El enlace no es válido o ha expirado";

    private static final int BYTES_TOKEN = 32;

    private final UsuarioRepository usuarioRepository;

    private final TokenRecuperacionRepository tokenRecuperacionRepository;

    private final CorreoService correoService;

    private final PasswordEncoder passwordEncoder;

    private final long minutosExpiracion;

    private final String urlPublica;

    private final SecureRandom secureRandom = new SecureRandom();

    public RecuperarClaveUseCase(UsuarioRepository usuarioRepository,
                                 TokenRecuperacionRepository tokenRecuperacionRepository,
                                 CorreoService correoService,
                                 PasswordEncoder passwordEncoder,
                                 @Value("${app.password-reset.expiration-minutes}") long minutosExpiracion,
                                 @Value("${app.public-url}") String urlPublica) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRecuperacionRepository = tokenRecuperacionRepository;
        this.correoService = correoService;
        this.passwordEncoder = passwordEncoder;
        this.minutosExpiracion = minutosExpiracion;
        this.urlPublica = urlPublica;
    }

    /*
     * No informa si el correo existe: siempre termina sin error para no revelar qué cuentas
     * están registradas. Los usuarios bloqueados no reciben enlace.
     */
    @Transactional
    public void solicitarRecuperacion(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        usuarioRepository.findByCorreo(correo.trim())
                .filter(usuario -> !Boolean.TRUE.equals(usuario.bloqueado()))
                .ifPresent(this::generarYEnviarToken);
    }

    private void generarYEnviarToken(Usuario usuario) {
        tokenRecuperacionRepository.invalidarTokensDeUsuario(usuario.documento());

        String token = generarToken();
        Instant expiraEn = Instant.now().plus(Duration.ofMinutes(minutosExpiracion));
        tokenRecuperacionRepository.save(TokenRecuperacion.crear(hash(token), usuario.documento(), expiraEn));

        String enlace = urlPublica + "/auth/password/reset?token=" + token;
        correoService.enviarRecuperacionClave(usuario.correo(), usuario.nombre(), enlace, minutosExpiracion);
    }

    public boolean validarToken(String token) {
        return buscarTokenVigente(token).isPresent();
    }

    @Transactional
    public void restablecerClave(String token, String nuevaClave, String confirmacion) {
        TokenRecuperacion tokenRecuperacion = buscarTokenVigente(token)
                .orElseThrow(() -> new BusinessExceptions.BadRequestException(MENSAJE_ENLACE_INVALIDO));

        if (nuevaClave == null || nuevaClave.length() < LONGITUD_MINIMA_CLAVE) {
            throw new BusinessExceptions.BadRequestException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres");
        }
        if (!nuevaClave.equals(confirmacion)) {
            throw new BusinessExceptions.BadRequestException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository.findById(tokenRecuperacion.usuarioDocumento())
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Usuario no encontrado"));

        Usuario actualizado = new Usuario(usuario.documento(), usuario.nombre(), usuario.primerApellido(),
                usuario.segundoApellido(), usuario.correo(), passwordEncoder.encode(nuevaClave),
                usuario.telefono(), usuario.bloqueado(), usuario.fechaNacimiento(), usuario.valoracionConteo(),
                usuario.valoracionPromedio(), usuario.genero(), usuario.rol(),
                usuario.trabajos(), usuario.trabajosRealizados());
        usuarioRepository.save(actualizado);
        tokenRecuperacionRepository.save(tokenRecuperacion.marcarUsado());
    }

    private Optional<TokenRecuperacion> buscarTokenVigente(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRecuperacionRepository.findByTokenHash(hash(token))
                .filter(t -> t.vigente(Instant.now()));
    }

    private String generarToken() {
        byte[] bytes = new byte[BYTES_TOKEN];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
