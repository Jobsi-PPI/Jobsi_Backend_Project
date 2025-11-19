package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CATEGORIAS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Categoria_ID",length = 10)
    private Long id;

    @Column(name = "Nombre_Categoria", nullable = false, length = 100,unique = true)
    private String nombre;
}
