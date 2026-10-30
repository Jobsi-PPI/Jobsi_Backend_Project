package com.escaes.jobsy.application.usecase.solicitud;

import com.escaes.jobsy.application.dto.solicitud.SolicitudAdminResponse;
import com.escaes.jobsy.application.dto.solicitud.SolicitudResponse;
import com.escaes.jobsy.application.dto.solicitud.SolicitudUsuarioResponse;
import com.escaes.jobsy.application.usecase.usuario.GestionUsuariosUseCase;
import com.escaes.jobsy.domain.model.Trabajo;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.SolicitudRepository;
import com.escaes.jobsy.domain.repository.TrabajoRepository;
import com.escaes.jobsy.infraestructure.mapper.SolicitudMapper;
import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarSolicitudesUseCase {

    private final SolicitudRepository solicitudRepository;

    private final TrabajoRepository trabajoRepository;

    private final GestionUsuariosUseCase  gestionUsuariosUseCase;

    public List<SolicitudResponse> listarSolicitudes(){
        return solicitudRepository.findAll()
                .stream()
                .map(SolicitudMapper::toResponse)
                .collect(Collectors.toList());
    }

    /*
     * Listado para el panel de administración, sin exponer datos sensibles del postulante.
     */
    public List<SolicitudAdminResponse> listarSolicitudesAdmin(){
        return solicitudRepository.findAll()
                .stream()
                .map(SolicitudMapper::toAdminResponse)
                .toList();
    }

    public List<SolicitudUsuarioResponse> listarSolicitudes(UUID trabajoId, String correoSolicitante){
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Trabajo no encontrado"));

        if (trabajo.solicitante() == null || !trabajo.solicitante().correo().equals(correoSolicitante)) {
            throw new BusinessExceptions.ForbiddenException(
                    "Solo el creador del trabajo puede ver sus postulantes");
        }

        return solicitudRepository.findSolicitudesConUsuario(trabajoId)
                .stream()
                .map(SolicitudMapper::toUserResponse)
                .toList();
    }

    public List<SolicitudResponse>listarSolicitudesPorUsuario(String correoUsuario){
        Usuario user= gestionUsuariosUseCase.obtenerUsuarioPorCorreo(correoUsuario);
        return solicitudRepository.findByUsuario(user)
                .stream()
                .map(SolicitudMapper::toResponse)
                .collect(Collectors.toList());
    }
}
