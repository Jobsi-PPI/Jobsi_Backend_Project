package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USUARIOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity {

   /*  @Id
    @Column(name = "Usuario_ID", columnDefinition = "RAW(16)")
    private UUID id; */
    @Id
    @Column(name = "Documento_Usuario", unique = true, nullable = false, length = 20)
    private Integer documento;

    @Column(name = "Nombre_Usuario", nullable = false, length = 100)
    private String nombre;

    @Column(name = "Correo_Usuario", unique = true, nullable = false, length = 100)
    private String correo;

    @Column(name = "Contraseña_Usuario", nullable = false, length = 255)
    private String clave;

    @Column(name = "Telefono_Usuario", nullable = false,unique = true, length = 20)
    private String telefono;

    @Column(name = "Bloqueado", nullable = false)
    @Builder.Default
    private final Boolean bloqueado = false;

    @Column(name = "Fecha_Nacimiento_Usuario", nullable = true)
    private Date fechaNacimiento;

    @Column(name="Valoracion_Conteo_Usuario", nullable = false)
    @Builder.Default
    private Integer valoracionConteo=0;

    @Column(name = "Valoracion_Promedio_Usuario", nullable = false)
    @Builder.Default
    private Double valoracionPromedio=0.0;

    @ManyToOne
    @JoinColumn(name = "Sexo_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USUARIO_SEXO"))
    private GeneroEntity genero;

    @ManyToOne
    @JoinColumn(name = "Rol_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USUARIO_ROL"))
    private RolEntity rol;

    @OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrabajoEntity> trabajosSolicitados;

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrabajoEntity> trabajosRealizados;

}
