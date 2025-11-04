package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TBL_TRABAJOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrabajoEntity {

    @Id
    @Column(name="Trabajo_Id",columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "Titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "Descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "Fecha_Publicacion", nullable = false)
    private Date fechaPublicacion;

    @Column(name = "Pago", nullable = false)
    private Double pago;

    @Column(name = "Ubicacion", nullable = false, length = 200)
    private String ubicacion;

    @ManyToOne()
    @JoinColumn(name = "Solicitante_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TRABAJO_SOLICITANTE"))
    private UsuarioEntity solicitante;

    @ManyToOne()
    @JoinColumn(name = "Trabajador_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRABAJO_TRABAJADOR"))
    private UsuarioEntity trabajador;

    @ManyToOne
    @JoinColumn(name = "Estados_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TRABAJO_ESTADOS"))
    private EstadoEntity estado;

    @ManyToOne
    @JoinColumn(name = "Categorias_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TRABAJO_CATEGORIAS"))
    private CategoriaEntity categoria;

    @ManyToOne
    @JoinColumn(name = "Tipos_De_Pago_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRABAJO_TIPO_PAGO"))
    private PagoEntity tipoPago;

    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ValoracionEntity> valoraciones;
}
