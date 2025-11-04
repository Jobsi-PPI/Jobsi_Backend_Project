package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_VALORACIONES_CATEGORIAS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValoracionCategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Valoracion_Categoria_ID", nullable = false)
    private Long id;

    @Column(name="Nombre_Categoria", nullable = false, length = 100)
    private String nombre;

}
