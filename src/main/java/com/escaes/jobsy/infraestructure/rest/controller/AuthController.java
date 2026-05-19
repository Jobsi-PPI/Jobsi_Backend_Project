package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.auth.JwtResponse;
import com.escaes.jobsy.application.dto.auth.LoginRequest;
import com.escaes.jobsy.config.jwt.JwtProvider;
//import com.escaes.jobsy.domain.repository.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Autenticación", description = "Operaciones de autenticación y autorización")
public class AuthController {

    //private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtUtil;

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT para usar en los endpoints protegidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso — devuelve el token JWT",
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (campos faltantes)", content = @Content)
    })
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

}
