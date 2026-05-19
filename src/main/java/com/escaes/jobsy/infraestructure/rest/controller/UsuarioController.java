package com.escaes.jobsy.infraestructure.rest.controller;


import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioResponse;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.rol.GestionRolesUseCase;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
import com.escaes.jobsy.application.usecase.usuario.ListarUsuariosUseCase;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.infraestructure.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/v1")
@Tag(name = "Usuarios", description = "Gestión de usuarios — registro público y administración con rol ADMIN")
@RequiredArgsConstructor
public class UsuarioController {

    private final GestionUsuariosUseCase gestionUsuariosUseCase;

    private final GestionGenerosUseCase gestionGenerosUseCase;

    private final ListarUsuariosUseCase listarUsuariosUseCase;

    private final GestionRolesUseCase gestionRolesUseCase;


    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario. Endpoint público — no requiere autenticación. El rol por defecto es USER si no se especifica.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (correo duplicado, campos faltantes, etc.)", content = @Content)
    })
    @PostMapping("/public/users/create")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest request) {

        Genero genero = gestionGenerosUseCase.obtenerGeneroPorNombre(request.genero());

        Rol rol = gestionRolesUseCase.obtenerRolPorNombre(
                request.rol() != null ? request.rol() : "USER"
        );

        gestionUsuariosUseCase.crearUsuario(request, genero, rol);

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.requestToResponse(request));
    }

    @Operation(summary = "Obtener usuario por documento", description = "Devuelve los datos de un usuario a partir de su número de documento. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/users/{documento}")
    public ResponseEntity<UsuarioResponse> obtenerPorDocumento(
            @Parameter(description = "Número de documento de identidad del usuario", example = "1234567890")
            @PathVariable Integer documento) {

        Usuario usuario = gestionUsuariosUseCase.obtenerUsuarioPorId(documento);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(UsuarioMapper.entityToResponse(usuario));
    }

    @Operation(summary = "Buscar usuarios con filtros", description = "Permite buscar usuarios aplicando múltiples filtros opcionales. Soporta paginación. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios que coinciden con los filtros",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/users/search")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(
            @Parameter(description = "Número de documento", example = "1234567890") @RequestParam(required = false) Integer documento,
            @Parameter(description = "Correo electrónico del usuario", example = "usuario@email.com") @RequestParam(required = false) String correo,
            @Parameter(description = "Género del usuario (ej. MASCULINO)", example = "MASCULINO") @RequestParam(required = false) String genero,
            @Parameter(description = "Rol del usuario (USER o ADMIN)", example = "USER") @RequestParam(required = false) String rol,
            @Parameter(description = "Filtrar por estado bloqueado", example = "false") @RequestParam(required = false) Boolean bloqueado,
            @Parameter(description = "Mínimo de valoraciones recibidas", example = "5") @RequestParam(required = false) Integer valoracionConteo,
            @Parameter(description = "Promedio mínimo de valoración (0.0 - 5.0)", example = "4.0") @RequestParam(required = false) Double valoracionPromedio,
            @Parameter(description = "Cantidad de resultados por página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") int page) {

        List<UsuarioResponse> usuarios = listarUsuariosUseCase.buscarUsuarios(
                documento, correo, genero, rol, bloqueado, valoracionConteo, valoracionPromedio, size, page)
                .stream()
                .map(UsuarioMapper::entityToResponse)
                .toList();
        return ResponseEntity.ok(usuarios);
    }
}
