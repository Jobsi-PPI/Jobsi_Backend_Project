package com.escaes.jobsy.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "UBICACION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UbicacionEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;


    @Column(name = "Nombre_Ubicacion",nullable = false, updatable = false,unique = true)
    private String nombreUbicacion;

    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private List<TrabajoEntity> trabajos;
}
