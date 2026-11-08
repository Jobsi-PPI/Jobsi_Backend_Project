package com.escaes.jobsy.application.service;


import com.escaes.jobsy.application.dto.institucion.ubicacion.Departamento;
import com.escaes.jobsy.application.dto.institucion.ubicacion.Municipio;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class UbicacionService {

    private final List<Departamento> departamentos;

    public UbicacionService(ObjectMapper objectMapper) throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/ubicaciones.json")) {
            this.departamentos = objectMapper.readValue(is, new TypeReference<>() {});
        }
    }

    public boolean existeDepartamento(String codigoDepto) {
        return departamentos.stream()
                .anyMatch(dep -> dep.codigo().equalsIgnoreCase(codigoDepto));
    }

    public boolean existeMunicipioEnDepartamento(String codigoDepto, String codigoMpio) {
        return departamentos.stream()
                .filter(dep -> dep.codigo().equalsIgnoreCase(codigoDepto))
                .flatMap(dep -> dep.municipios().stream())
                .anyMatch(mpio -> mpio.codigo().equalsIgnoreCase(codigoMpio));
    }
    public String nombreDepartamento(String codigoDepto) {
        return departamentos.stream()
                .filter(dep -> dep.codigo().equalsIgnoreCase(codigoDepto))
                .map(Departamento::nombre)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));
    }

    public String nombreMunicipio(String codigoDepto, String codigoMpio) {
        return departamentos.stream()
                .filter(dep -> dep.codigo().equalsIgnoreCase(codigoDepto))
                .flatMap(dep -> dep.municipios().stream())
                .filter(mpio -> mpio.codigo().equalsIgnoreCase(codigoMpio))
                .map(Municipio::nombre)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Municipio no encontrado"));
    }

    public List<Departamento> listarDepartamentos() {
        return departamentos;
    }

    public List<Municipio> municipiosPorDepartamento(String codigoDepto) {
         Departamento depto=listarDepartamentos().stream()
                .filter(d -> d.codigo().equalsIgnoreCase(codigoDepto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));
         return depto.municipios();
    }
}