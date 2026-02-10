package com.escaes.jobsy.infraestructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "TIPO_PAGOS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoEntity {
    @Id
    @Column(name = "Tipo_Pago_ID", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "Nombre_Tipo_Pago", nullable = false, length = 50,unique = true)
    private String nombre;
}
