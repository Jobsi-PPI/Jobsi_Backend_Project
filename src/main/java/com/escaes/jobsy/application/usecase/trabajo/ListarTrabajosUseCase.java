package com.escaes.jobsy.application.usecase.trabajo;

import com.escaes.jobsy.domain.model.Trabajo;
import com.escaes.jobsy.domain.repository.TrabajoRepository;
//import com.escaes.jobsy.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarTrabajosUseCase {

    private final TrabajoRepository trabajoRepository;

    //private final UsuarioRepository usuarioRepository;

    public List<Trabajo> listar(){
        return trabajoRepository.findAll();
    }
    /*
     * Este método lista los trabajos asociados a un usuario solicitante específico.
     * Recibe como parámetro el correo del solicitante y utiliza el repositorio de trabajos
     */
    public List<Trabajo> listarPorUsuarioSolicitante(String correoSolicitante){
        return trabajoRepository.findBySolicitanteCorreo(correoSolicitante);
    }
    /*
     * Este método lista los trabajos asociados a un usuario trabajador específico.
     * Recibe como parámetro el correo del trabajador y utiliza el repositorio de trabajos
     */
    public List<Trabajo>listarPorUsuarioTrabajador(String correoTrabajador){
        return trabajoRepository.findByTrabajadorCorreo(correoTrabajador);
    }
}
