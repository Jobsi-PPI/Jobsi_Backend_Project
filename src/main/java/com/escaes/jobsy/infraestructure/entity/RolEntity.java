package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ROLES")
public class RolEntity {

    @Id
    @Column(name = "Rol_ID", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "Nombre_Rol", nullable = false, length = 50,unique = true)
    private String nombre;

    @Column(name = "Descripcion_Rol", nullable = true, length = 200)
    private String descripcion;
}
