package com.escaes.jobsy.infraestructure.rest.controller;


import com.escaes.jobsy.application.dto.comun.PaginaResponse;
import com.escaes.jobsy.application.dto.usuario.CambiarRolRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioResponse;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.rol.GestionRolesUseCase;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
import com.escaes.jobsy.application.usecase.usuario.ListarUsuariosUseCase;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.infraestructure.mapper.PaginaMapper;
import com.escaes.jobsy.infraestructure.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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


    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario. Endpoint público — no requiere autenticación. El rol siempre es USER; el campo rol de la petición se ignora.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (correo duplicado, campos faltantes, etc.)", content = @Content)
    })
    @PostMapping("/public/users/create")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest request) {

        Genero genero = gestionGenerosUseCase.obtenerGeneroPorNombre(request.genero());

        // El registro público nunca puede asignar un rol distinto a USER
        Rol rol = gestionRolesUseCase.obtenerRolPorNombre("USER");

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
            @PathVariable Long documento) {

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
            @Parameter(description = "Número de documento", example = "1234567890") @RequestParam(required = false) Long documento,
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
                .contenido().stream()
                .map(UsuarioMapper::entityToResponse)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Listar usuarios (paginado)", description = "Listado paginado para el panel de administración, con los mismos filtros opcionales que la búsqueda e información de paginación. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de usuarios"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/users")
    public ResponseEntity<PaginaResponse<UsuarioResponse>> listarUsuarios(
            @Parameter(description = "Número de documento", example = "1234567890") @RequestParam(required = false) Long documento,
            @Parameter(description = "Correo electrónico (búsqueda parcial)", example = "usuario@email.com") @RequestParam(required = false) String correo,
            @Parameter(description = "Rol del usuario (USER o ADMIN)", example = "USER") @RequestParam(required = false) String rol,
            @Parameter(description = "Filtrar por estado bloqueado", example = "false") @RequestParam(required = false) Boolean bloqueado,
            @Parameter(description = "Cantidad de resultados por página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") int page) {

        return ResponseEntity.ok(PaginaMapper.toResponse(
                listarUsuariosUseCase.buscarUsuarios(documento, correo, null, rol, bloqueado, null, null, size, page),
                UsuarioMapper::entityToResponse));
    }

    @Operation(summary = "Bloquear usuario", description = "Bloquea la cuenta: el usuario ya no podrá iniciar sesión. Un administrador no puede bloquearse a sí mismo. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario bloqueado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Intento de bloquear la propia cuenta", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario ya estaba bloqueado", content = @Content)
    })
    @PatchMapping("/admin/users/{documento}/block")
    public ResponseEntity<UsuarioResponse> bloquearUsuario(
            @Parameter(description = "Documento del usuario", example = "1234567890") @PathVariable Long documento,
            Authentication authentication) {
        return ResponseEntity.ok(UsuarioMapper.entityToResponse(
                gestionUsuariosUseCase.bloquearUsuario(documento, authentication.getName())));
    }

    @Operation(summary = "Desbloquear usuario", description = "Reactiva una cuenta bloqueada. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario desbloqueado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario no estaba bloqueado", content = @Content)
    })
    @PatchMapping("/admin/users/{documento}/unblock")
    public ResponseEntity<UsuarioResponse> desbloquearUsuario(
            @Parameter(description = "Documento del usuario", example = "1234567890") @PathVariable Long documento) {
        return ResponseEntity.ok(UsuarioMapper.entityToResponse(gestionUsuariosUseCase.desbloquearUsuario(documento)));
    }

    @Operation(summary = "Cambiar rol de usuario", description = "Asigna el rol USER o ADMIN. Un administrador no puede cambiar su propio rol. El cambio aplica en el siguiente inicio de sesión del usuario. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualizado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Rol vacío o intento de cambiar el propio rol", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content)
    })
    @PatchMapping("/admin/users/{documento}/role")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @Parameter(description = "Documento del usuario", example = "1234567890") @PathVariable Long documento,
            @Valid @RequestBody CambiarRolRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(UsuarioMapper.entityToResponse(
                gestionUsuariosUseCase.cambiarRol(documento, request.rol(), authentication.getName())));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario sin trabajos ni postulaciones. Si tiene historial responde 409: en ese caso se debe bloquear. Requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Intento de eliminar la propia cuenta", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario tiene historial de trabajos", content = @Content)
    })
    @DeleteMapping("/admin/users/{documento}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "Documento del usuario", example = "1234567890") @PathVariable Long documento,
            Authentication authentication) {
        gestionUsuariosUseCase.eliminarUsuarioAdmin(documento, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
