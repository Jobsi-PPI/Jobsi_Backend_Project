package com.escaes.jobsy.application.usecase.institucion;

import com.escaes.jobsy.application.dto.institucion.InstitucionRequest;
import com.escaes.jobsy.application.service.UbicacionService;
import com.escaes.jobsy.domain.model.Institucion;
import com.escaes.jobsy.domain.repository.InstitucionRepository;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class GestionInstitucionesUseCase {

    private final InstitucionRepository institucionRepository;
    private final UbicacionService ubicacionService;

    // ------------------- Método helper -------------------
    private String[] validarYObtenerNombres(InstitucionRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la institución no puede estar vacío");
        }

        if (!ubicacionService.existeDepartamento(request.departamentoCodigo())) {
            throw new IllegalArgumentException("El departamento seleccionado no es válido");
        }

        if (!ubicacionService.existeMunicipioEnDepartamento(request.departamentoCodigo(), request.municipioCodigo())) {
            throw new IllegalArgumentException("El municipio no pertenece al departamento seleccionado");
        }

        String nombreDepartamento = ubicacionService.nombreDepartamento(request.departamentoCodigo());
        String nombreMunicipio = ubicacionService.nombreMunicipio(request.departamentoCodigo(), request.municipioCodigo());

        return new String[]{nombreDepartamento, nombreMunicipio};
    }

    public void crearInstitucion(InstitucionRequest request) {
        String[] nombres = validarYObtenerNombres(request);

        Institucion institucion = new Institucion(
                UUID.randomUUID(),
                request.nombre(),
                nombres[0],  // departamento
                nombres[1]   // municipio
        );

        institucionRepository.save(institucion);
    }

    public Institucion encontrarInstitucion(InstitucionRequest request) {
        String[] nombres = validarYObtenerNombres(request);
        return institucionRepository.findByNombreAndDepartamentoAndMuncipio(
                request.nombre(),
                nombres[0],
                nombres[1]
        ).orElseThrow(() -> new IllegalArgumentException(
                "No hay datos que coincidan con la institución proporcionada"
        ));
    }

    public void eliminarInstitucionPorId(UUID id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new BusinessExceptions.NotFoundException("Institución no encontrada"));
        institucionRepository.delete(institucion);
    }

    public void eliminarInstitucion(InstitucionRequest request) {
        Institucion institucion = encontrarInstitucion(request); // valida y obtiene
        institucionRepository.delete(institucion);
    }
}


