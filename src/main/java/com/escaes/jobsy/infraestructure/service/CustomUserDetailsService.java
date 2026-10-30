package com.escaes.jobsy.infraestructure.service;

import com.escaes.jobsy.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.correo())
                .password(usuario.clave())
                .roles(usuario.rol().nombre())
                .disabled(Boolean.TRUE.equals(usuario.bloqueado()))
                .build();
    }
}

