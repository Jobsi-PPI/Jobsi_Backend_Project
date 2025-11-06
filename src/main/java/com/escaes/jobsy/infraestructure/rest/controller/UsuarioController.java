package com.escaes.jobsy.infraestructure.rest.controller;


import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioResponse;
import com.escaes.jobsy.application.usecase.genero.GestionGenerosUseCase;
import com.escaes.jobsy.application.usecase.rol.GestionRolesUseCase;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
//import com.escaes.jobsy.application.usecase.usuario.ListarUsuariosUseCase;
import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.infraestructure.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/v1")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final GestionUsuariosUseCase gestionUsuariosUseCase;

    private final GestionGenerosUseCase gestionGenerosUseCase;

    //private final ListarUsuariosUseCase listarUsuariosUseCase;

    private final GestionRolesUseCase gestionRolesUseCase;


    @PostMapping("/public/users/create")
    public ResponseEntity<UsuarioResponse>crearUsuario(@RequestBody UsuarioRequest request) {

        Genero genero= gestionGenerosUseCase.obtenerGeneroPorNombre(request.genero());

        Rol rol = gestionRolesUseCase.obtenerRolPorNombre(
                request.rol() != null ? request.rol() : "USER"
        );

        gestionUsuariosUseCase.crearUsuario(request,genero,rol);


        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.requestToResponse(request));
    }

    @GetMapping("/admin/users/{documento}")
    public ResponseEntity<UsuarioResponse> obtenerPorDocumento(@PathVariable Integer documento) {

        Usuario usuario = gestionUsuariosUseCase.obtenerUsuarioPorId(documento);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  
        }
        return ResponseEntity.ok(UsuarioMapper.entityToResponse(usuario));
    }
}
