package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "SEXOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Sexo_ID")
    private Long id;

    @Column(name = "Nombre_Sexo", nullable = false, unique = true, length = 50)
    private String nombreGenero;

    @OneToMany(mappedBy = "genero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioEntity> usuarios;

}
