package com.escaes.jobsy.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TOKENS_RECUPERACION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRecuperacionEntity {

    @Id
    @Column(name = "Token_ID", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "Token_Hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "Usuario_Documento", nullable = false)
    private Long usuarioDocumento;

    @Column(name = "Expira_En", nullable = false)
    private Instant expiraEn;

    @Column(name = "Usado", nullable = false)
    private Boolean usado;

    @CreationTimestamp
    @Column(name = "Fecha_Creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;
}
