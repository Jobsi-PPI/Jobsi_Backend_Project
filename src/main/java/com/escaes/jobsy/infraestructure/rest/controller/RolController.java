package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.rol.RolResponse;
import com.escaes.jobsy.application.usecase.rol.ListarRolesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Roles", description = "Consulta de roles del sistema — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RolController {

    private final ListarRolesUseCase listarRolesUseCase;

    @Operation(summary = "Listar roles", description = "Devuelve los roles disponibles (USER, ADMIN) para asignarlos desde el panel de administración.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de roles",
                content = @Content(schema = @Schema(implementation = RolResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/roles")
    public ResponseEntity<List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(listarRolesUseCase.listarRoles().stream()
                .map(rol -> new RolResponse(rol.nombre(), rol.descripcion())).toList());
    }
}
