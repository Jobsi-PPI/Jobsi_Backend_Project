package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.auth.JwtResponse;
import com.escaes.jobsy.application.dto.auth.LoginRequest;
import com.escaes.jobsy.config.jwt.JwtProvider;
//import com.escaes.jobsy.domain.repository.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name="Autenticación", description="Operaciones de autenticación y autorización")
public class AuthController {

    //private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        String token = jwtUtil.generateToken(authentication);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
