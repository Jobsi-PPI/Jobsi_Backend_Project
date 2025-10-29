package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TBL_USUARIOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @Column(name = "Usuario_ID", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @Column(name = "Documento", unique = true, nullable = false)
    private Integer documento;

    @Column(name = "Correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "Contraseña", nullable = false)
    private String clave;

    @Column(name = "Bloqueado", nullable = false)
    private Boolean bloqueado = false;

    @Column(name = "Fecha_Nacimiento", nullable = true)
    private Date fechaNacimiento;

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
