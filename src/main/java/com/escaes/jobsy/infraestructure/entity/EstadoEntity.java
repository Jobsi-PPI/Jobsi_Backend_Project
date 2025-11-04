package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_ESTADOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Estado_ID")
    private Long id;

    @Column(name = "Nombre_Estado",length = 50, nullable = false)
    private String nombre;

}
