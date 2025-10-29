package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "TBL_INSTITUCIONES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstitucionEntity {
    @Id
    @Column(name = "Institucion_ID", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "Nombre_institucion", nullable = false)
    private String nombre;

    @Column(name = "Departamento", nullable = false)
    private String departamento;

    @Column(name = "Municipio", nullable = true)
    private String municipio;
}
