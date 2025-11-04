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
@Table(name = "TBL_ROLES")
public class RolEntity {

    @Id
    @Column(name = "Rol_ID", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "Nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "Descripcion", nullable = true, length = 200)
    private String descripcion;
}
