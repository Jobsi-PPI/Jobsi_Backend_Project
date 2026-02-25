package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.pago.PagoResponse;
import com.escaes.jobsy.domain.model.Pago;
import com.escaes.jobsy.infraestructure.persistence.entity.PagoEntity;

public class PagoMapper {

    public static Pago toDomain(PagoEntity pagoEntity) {
        if (pagoEntity == null) {
            return null;
        }
        return new Pago(
                pagoEntity.getId(),
                pagoEntity.getNombre()
        );
    }
    public static PagoEntity toEntity(Pago pago) {
        if (pago == null) {
            return null;
        }
        return new PagoEntity(
                pago.id(),
                pago.nombrePago()
        );
    }
    public static PagoResponse toResponse(Pago pago) {
        if (pago == null) {
            return null;
        }
        return new PagoResponse(
                pago.nombrePago()
        );
    }
}
