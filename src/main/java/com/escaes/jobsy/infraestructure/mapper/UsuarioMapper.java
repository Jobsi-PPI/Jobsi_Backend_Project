package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.usuario.UsuarioRequest;
import com.escaes.jobsy.application.dto.usuario.UsuarioResponse;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.infraestructure.entity.UsuarioEntity;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

public class UsuarioMapper {


    public static Usuario toDomain(UsuarioEntity usuarioEntity) {
        if (usuarioEntity == null) {
            return null;
        }
        return new Usuario(
                usuarioEntity.getId(),
                usuarioEntity.getNombre(),
                usuarioEntity.getDocumento(),
                usuarioEntity.getCorreo(),
                usuarioEntity.getClave(),
                usuarioEntity.getTelefono(),
                usuarioEntity.getBloqueado(),
                usuarioEntity.getFechaNacimiento(),
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
                usuarioEntity.getId(),
                usuarioEntity.getNombre(),
                usuarioEntity.getDocumento(),
                usuarioEntity.getCorreo(),
                usuarioEntity.getTelefono(),
                usuarioEntity.getClave(),
                usuarioEntity.getBloqueado(),
                usuarioEntity.getFechaNacimiento(),
                usuarioEntity.getGenero() != null ? GeneroMapper.toDomain(usuarioEntity.getGenero()) : null,
                usuarioEntity.getRol() != null ? RolMapper.toDomain(usuarioEntity.getRol()) : null,
                List.of(),
                List.of()
        );
    }

    public static UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .id(usuario.id() != null ? usuario.id() : UUID.randomUUID())
                .nombre(usuario.nombre())
                .documento(usuario.documento())
                .correo(usuario.correo())
                .clave(usuario.clave())
                .telefono(usuario.telefono())
                .bloqueado(usuario.bloqueado())
                .fechaNacimiento(usuario.fechaNacimiento())
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
                usuario.id() != null ? usuario.id().toString() : null,
                usuario.nombre(),
                usuario.correo(),
                usuario.rol() != null ? usuario.rol().nombre() : null
        );
    }

    public static UsuarioResponse requestToResponse(UsuarioRequest request) {
        if (request == null) {
            return null;
        }

        return new UsuarioResponse(
                null,
                request.nombre(),
                request.email(),
                request.rol() != null ? request.rol() : "USER"
        );
    }
}
