package com.escaes.jobsy.infraestructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "INSTITUCIONES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstitucionEntity {
    @Id
    @Column(name = "Institucion_ID", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "Nombre_institucion", nullable = false, length = 100)
    private String nombre;

    @Column(name = "Departamento_Institucion", nullable = false, length = 100)
    private String departamento;

    @Column(name = "Municipio_Institucion", nullable = true, length = 100)
    private String municipio;
}
