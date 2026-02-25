package com.escaes.jobsy.infraestructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "VALORACIONES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValoracionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Valoracion_Id", length = 10)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Trabajo_Id", nullable = false, foreignKey = @ForeignKey(name = "FK_Valoracion_Trabajo"))
    private TrabajoEntity trabajo;
    
    @ManyToOne
    @JoinColumn(name = "Categoria_Id", nullable = false,foreignKey = @ForeignKey(name = "FK_Valoracion_Categoria"))
    private ValoracionCategoriaEntity categoria;

    @Column(name="Puntuacion_Valoracion", nullable = false)
    @Builder.Default
    private Integer puntuacion=0;

    @Column(name="Comentario_Valoracion", length = 500)
    private String comentario;

}
