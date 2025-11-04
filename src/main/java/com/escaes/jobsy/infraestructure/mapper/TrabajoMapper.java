package com.escaes.jobsy.infraestructure.mapper;

import java.util.List;

import com.escaes.jobsy.application.dto.trabajo.CrearTrabajoRequest;
import com.escaes.jobsy.application.dto.trabajo.TrabajoResponse;
import com.escaes.jobsy.domain.model.Trabajo;
import com.escaes.jobsy.infraestructure.entity.TrabajoEntity;

public class TrabajoMapper {

    public static Trabajo toDomain(TrabajoEntity trabajoEntity) {

        if (trabajoEntity == null) {
            return null;
        }
        return new Trabajo(
                trabajoEntity.getId(),
                trabajoEntity.getTitulo(),
                trabajoEntity.getDescripcion(),
                trabajoEntity.getFechaPublicacion(),
                trabajoEntity.getPago(),
                trabajoEntity.getUbicacion(),
                trabajoEntity.getSolicitante() != null ? UsuarioMapper.toDomainBasic(trabajoEntity.getSolicitante())
                        : null,
                trabajoEntity.getTrabajador() != null ? UsuarioMapper.toDomainBasic(trabajoEntity.getTrabajador())
                        : null,
                CategoriaMapper.toDomain(trabajoEntity.getCategoria()),
                EstadoMapper.toDomain(trabajoEntity.getEstado()),
                PagoMapper.toDomain(trabajoEntity.getTipoPago()));
    }
    /*
     * FALTA LOGICA PARA VALORACIONES DE TRABAJO
     */
    public static TrabajoEntity toEntity(Trabajo trabajo) {
        if (trabajo == null) {
            return null;
        }
        return new TrabajoEntity(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.fechaPublicacion(),
                trabajo.pago(),
                trabajo.ubicacion(),
                trabajo.solicitante() != null ? UsuarioMapper.toEntity(trabajo.solicitante()) : null,
                trabajo.trabajador() != null ? UsuarioMapper.toEntity(trabajo.trabajador()) : null,
                EstadoMapper.toEntity(trabajo.estado()),
                CategoriaMapper.toEntity(trabajo.categoria()),
                PagoMapper.toEntity(trabajo.tipoPago()),List.of()

        );
    }

    public static TrabajoResponse entityToResponse(Trabajo trabajo) {
        if (trabajo == null)
            return null;

        return new TrabajoResponse(
                trabajo.id(),
                trabajo.titulo(),
                trabajo.descripcion(),
                trabajo.pago(),
                trabajo.tipoPago() != null ? trabajo.tipoPago().nombrePago() : null,
                trabajo.ubicacion(),
                trabajo.estado() != null ? trabajo.estado().nombre() : null,
                trabajo.categoria() != null ? trabajo.categoria().nombre() : null,
                trabajo.solicitante() != null ? trabajo.solicitante().correo() : null,
                trabajo.trabajador() != null ? trabajo.trabajador().correo() : null);
    }

    public static TrabajoResponse requestToResponse(CrearTrabajoRequest request, String solicitanteCorreo) {
        if (request == null)
            return null;

        return new TrabajoResponse(
                null,
                request.titulo(),
                request.descripcion(),
                request.pago(),
                request.tipoPago(),
                request.ubicacion(),
                "PENDIENTE",
                request.categoria(),
                solicitanteCorreo,
                null);
    }
}
