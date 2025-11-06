package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioResponse;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.infraestructure.entity.UsuarioEntity;

import java.util.List;

import java.util.stream.Collectors;

public class UsuarioMapper {


    public static Usuario toDomain(UsuarioEntity usuarioEntity) {
        if (usuarioEntity == null) {
            return null;
        }
        return new Usuario(
                usuarioEntity.getDocumento(),
                usuarioEntity.getNombre(),
                usuarioEntity.getCorreo(),
                usuarioEntity.getClave(),
                usuarioEntity.getTelefono(),
                usuarioEntity.getBloqueado(),
                usuarioEntity.getFechaNacimiento(),
                usuarioEntity.getValoracionConteo(),
                usuarioEntity.getValoracionPromedio(),
                usuarioEntity.getGenero() != null ? GeneroMapper.toDomain(usuarioEntity.getGenero()) : null,
                usuarioEntity.getRol() != null ? RolMapper.toDomain(usuarioEntity.getRol()) : null,
                usuarioEntity.getTrabajosSolicitados() != null ? usuarioEntity.getTrabajosSolicitados().stream().map(TrabajoMapper::toDomain).collect(Collectors.toList()) : List.of(),
                usuarioEntity.getTrabajosRealizados() != null ? usuarioEntity.getTrabajosRealizados().stream().map(TrabajoMapper::toDomain).collect(Collectors.toList()) : List.of()
        );
    }
    public static Usuario toDomainBasic(UsuarioEntity usuarioEntity) {
        if (usuarioEntity == null) {
            return null;
        }
        return new Usuario(
                usuarioEntity.getDocumento(),
                usuarioEntity.getNombre(),
                usuarioEntity.getCorreo(),
                usuarioEntity.getTelefono(),
                usuarioEntity.getClave(),
                usuarioEntity.getBloqueado(),
                usuarioEntity.getFechaNacimiento(),
                usuarioEntity.getValoracionConteo(),
                usuarioEntity.getValoracionPromedio(),
                usuarioEntity.getGenero() != null ? GeneroMapper.toDomain(usuarioEntity.getGenero()) : null,
                usuarioEntity.getRol() != null ? RolMapper.toDomain(usuarioEntity.getRol()) : null,
                List.of(),
                List.of()
        );
    }

    public static UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .documento(usuario.documento())
                .nombre(usuario.nombre())
                .correo(usuario.correo())
                .clave(usuario.clave())
                .telefono(usuario.telefono())
                .bloqueado(usuario.bloqueado())
                .fechaNacimiento(usuario.fechaNacimiento())
                .valoracionConteo(usuario.valoracionConteo())
                .valoracionPromedio(usuario.valoracionPromedio())
                .genero(usuario.genero() != null ? GeneroMapper.toEntity(usuario.genero()) : null)
                .rol(usuario.rol() != null ? RolMapper.toEntity(usuario.rol()) : null)
                .trabajosSolicitados(usuario.trabajos().stream().map(TrabajoMapper::toEntity).collect(Collectors.toList()))
                .trabajosRealizados(usuario.trabajosRealizados().stream().map(TrabajoMapper::toEntity).collect(Collectors.toList()))
                .build();
    }

    public static UsuarioResponse entityToResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResponse(
                usuario.documento(),
                usuario.nombre(),
                usuario.correo(),
                usuario.rol().nombre(),
                usuario.valoracionConteo(),
                usuario.valoracionPromedio()
        );
    }

    public static UsuarioResponse requestToResponse(UsuarioRequest request) {
        if (request == null) {
            return null;
        }

        return new UsuarioResponse(
                request.documento(),
                request.nombre(),
                request.email(),
                request.rol() != null ? request.rol() : "USER",
                null,
                null
        );
    }
}
