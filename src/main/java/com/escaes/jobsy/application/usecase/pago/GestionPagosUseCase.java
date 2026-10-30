package com.escaes.jobsy.application.usecase.pago;

import com.escaes.jobsy.application.dto.pago.PagoRequest;
import com.escaes.jobsy.domain.model.Pago;
import com.escaes.jobsy.domain.repository.PagoRepository;

import com.escaes.jobsy.infraestructure.rest.exception.BusinessExceptions;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class GestionPagosUseCase {

    private final PagoRepository pagoRepository;


    public void crearTipoPago(PagoRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de pago no puede ser nulo o vacío");
        }
        if(pagoRepository.findByNombre(request.nombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un tipo de pago con el nombre proporcionado");
        }
        Pago nuevoPago = new Pago(UUID.randomUUID(), request.nombre());
        pagoRepository.save(nuevoPago);
    }

    public void updatePago(Pago pago) {
        if (pago.nombrePago() == null || pago.nombrePago().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de pago no puede ser nulo o vacío");
        }
        if (pagoRepository.findById(pago.id()).isEmpty()) {
            throw new IllegalArgumentException("Tipo de pago no encontrado con el ID proporcionado");
        }
        pagoRepository.save(pago);
    }
    public Pago obtenerPagoPorId(UUID id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el tipo de pago con el ID proporcionado: "+ id));
    }
    public Pago  obtenerPagoPorNombre(String nombre) {
        return pagoRepository.findByNombre(nombre)
                .orElseThrow(()->new IllegalArgumentException("No exite tipo de pago con el nombre proporcionado: "+ nombre));
    }

    public void eliminarPago(Pago pago) {
        if (pago.nombrePago() == null || pago.nombrePago().isBlank()) {
            throw new IllegalArgumentException("El nombre de tipo de pago no puede ser nulo");
        }
        if(pagoRepository.findById(pago.id()).isEmpty()) {
            throw new IllegalArgumentException("No existe el tipo de pago con el ID proporcionado");
        }
        pagoRepository.delete(pago);
    }

    public Pago actualizarPagoPorNombre(String nombre, String nuevoNombre) {
        Pago pago = obtenerPagoPorNombre(nombre);
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new BusinessExceptions.BadRequestException("El nuevo nombre no puede ser vacío");
        }
        pagoRepository.findByNombre(nuevoNombre)
                .filter(existente -> !existente.id().equals(pago.id()))
                .ifPresent(existente -> {
                    throw new BusinessExceptions.ConflictException("Ya existe un tipo de pago con nombre: " + nuevoNombre);
                });
        Pago actualizado = new Pago(pago.id(), nuevoNombre);
        pagoRepository.save(actualizado);
        return actualizado;
    }

    public void eliminarPagoPorNombre(String nombre) {
        Pago pago= obtenerPagoPorNombre(nombre);
        pagoRepository.delete(pago);
    }
}
