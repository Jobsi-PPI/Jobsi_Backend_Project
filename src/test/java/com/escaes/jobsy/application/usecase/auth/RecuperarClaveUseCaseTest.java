package com.escaes.jobsy.application.usecase.auth;

import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.TokenRecuperacion;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.CorreoService;
import com.escaes.jobsy.domain.repository.TokenRecuperacionRepository;
import com.escaes.jobsy.domain.repository.UsuarioRepository;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecuperarClaveUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacionRepository tokenRecuperacionRepository;

    @Mock
    private CorreoService correoService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private RecuperarClaveUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RecuperarClaveUseCase(usuarioRepository, tokenRecuperacionRepository, correoService,
                passwordEncoder, 30, "http://localhost:8080");
    }

    @Test
    void enviaUnEnlaceConElTokenYSoloGuardaSuHash() {
        when(usuarioRepository.findByCorreo("ana@jobsi.test")).thenReturn(Optional.of(usuario(false)));

        useCase.solicitarRecuperacion("ana@jobsi.test");

        ArgumentCaptor<String> enlace = ArgumentCaptor.forClass(String.class);
        verify(correoService).enviarRecuperacionClave(eq("ana@jobsi.test"), eq("Ana"), enlace.capture(), eq(30L));
        ArgumentCaptor<TokenRecuperacion> guardado = ArgumentCaptor.forClass(TokenRecuperacion.class);
        verify(tokenRecuperacionRepository).invalidarTokensDeUsuario(1L);
        verify(tokenRecuperacionRepository).save(guardado.capture());

        String token = enlace.getValue().substring(enlace.getValue().indexOf("token=") + 6);
        assertThat(enlace.getValue()).startsWith("http://localhost:8080/auth/password/reset?token=");
        assertThat(guardado.getValue().tokenHash()).hasSize(64).doesNotContain(token);
        assertThat(guardado.getValue().usado()).isFalse();
    }

    @Test
    void correoInexistenteOBloqueadoNoRecibeEnlace() {
        when(usuarioRepository.findByCorreo("nadie@jobsi.test")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCorreo("ana@jobsi.test")).thenReturn(Optional.of(usuario(true)));

        useCase.solicitarRecuperacion("nadie@jobsi.test");
        useCase.solicitarRecuperacion("ana@jobsi.test");
        useCase.solicitarRecuperacion("  ");

        verify(correoService, never()).enviarRecuperacionClave(anyString(), anyString(), anyString(), anyLong());
        verify(tokenRecuperacionRepository, never()).save(any());
    }

    @Test
    void tokenExpiradoOUsadoNoEsValido() {
        TokenRecuperacion expirado = new TokenRecuperacion(UUID.randomUUID(), "hash", 1L,
                Instant.now().minus(Duration.ofMinutes(1)), false);
        TokenRecuperacion usado = new TokenRecuperacion(UUID.randomUUID(), "hash", 1L,
                Instant.now().plus(Duration.ofMinutes(10)), true);

        when(tokenRecuperacionRepository.findByTokenHash(anyString())).thenReturn(Optional.of(expirado));
        assertThat(useCase.validarToken("abc")).isFalse();

        when(tokenRecuperacionRepository.findByTokenHash(anyString())).thenReturn(Optional.of(usado));
        assertThat(useCase.validarToken("abc")).isFalse();

        assertThatThrownBy(() -> useCase.restablecerClave("abc", "ClaveNueva1", "ClaveNueva1"))
                .isInstanceOf(BusinessExceptions.BadRequestException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void rechazaClaveCortaOConfirmacionDistinta() {
        when(tokenRecuperacionRepository.findByTokenHash(anyString())).thenReturn(Optional.of(tokenVigente()));

        assertThatThrownBy(() -> useCase.restablecerClave("abc", "corta", "corta"))
                .isInstanceOf(BusinessExceptions.BadRequestException.class)
                .hasMessageContaining("al menos 8");
        assertThatThrownBy(() -> useCase.restablecerClave("abc", "ClaveNueva1", "ClaveNueva2"))
                .isInstanceOf(BusinessExceptions.BadRequestException.class)
                .hasMessageContaining("no coinciden");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void restablecerCifraLaClaveYConsumeElToken() {
        when(tokenRecuperacionRepository.findByTokenHash(anyString())).thenReturn(Optional.of(tokenVigente()));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(false)));

        useCase.restablecerClave("abc", "ClaveNueva1", "ClaveNueva1");

        ArgumentCaptor<Usuario> usuario = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuario.capture());
        assertThat(passwordEncoder.matches("ClaveNueva1", usuario.getValue().clave())).isTrue();

        ArgumentCaptor<TokenRecuperacion> token = ArgumentCaptor.forClass(TokenRecuperacion.class);
        verify(tokenRecuperacionRepository).save(token.capture());
        assertThat(token.getValue().usado()).isTrue();
    }

    private static TokenRecuperacion tokenVigente() {
        return new TokenRecuperacion(UUID.randomUUID(), "hash", 1L, Instant.now().plus(Duration.ofMinutes(10)), false);
    }

    private static Usuario usuario(boolean bloqueado) {
        return new Usuario(1L, "Ana", "Prueba", null, "ana@jobsi.test", "hash-anterior", "3000000000",
                bloqueado, new Date(), 0, 0.0, new Genero(1L, "Otro"), new Rol(UUID.randomUUID(), "USER", null),
                List.of(), List.of());
    }
}
