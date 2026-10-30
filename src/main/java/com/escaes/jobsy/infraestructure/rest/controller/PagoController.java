package com.escaes.jobsy.infraestructure.rest.controller;

import com.escaes.jobsy.application.dto.pago.PagoRequest;
import com.escaes.jobsy.application.dto.pago.PagoResponse;
import com.escaes.jobsy.application.usecase.pago.GestionPagosUseCase;
import com.escaes.jobsy.application.usecase.pago.ListarPagosUseCase;
import com.escaes.jobsy.infraestructure.mapper.PagoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Tipos de pago", description = "Gestión de tipos de pago — requiere rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PagoController {

    private final GestionPagosUseCase gestionPagosUseCase;

    private final ListarPagosUseCase listarPagosUseCase;

    @Operation(summary = "Listar tipos de pago", description = "Devuelve todos los tipos de pago registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de tipos de pago",
                content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/admin/payment-all")
    public ResponseEntity<List<PagoResponse>> listarPagos() {
        return ResponseEntity.ok(listarPagosUseCase.listar().stream().map(PagoMapper::toResponse).toList());
    }

    @Operation(summary = "Crear tipo de pago", description = "Registra un nuevo tipo de pago. El nombre debe ser único.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tipo de pago creado",
                content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo de pago ya existente", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/admin/payment/create")
    public ResponseEntity<PagoResponse> crearPago(@RequestBody PagoRequest request) {
        gestionPagosUseCase.crearTipoPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PagoResponse(request.nombre()));
    }

    @Operation(summary = "Renombrar tipo de pago", description = "Cambia el nombre de un tipo de pago. El nuevo nombre debe ser único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo de pago actualizado",
                content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Tipo de pago no encontrado o nombre vacío", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ya existe un tipo de pago con ese nombre", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PutMapping("/admin/payment/{name}")
    public ResponseEntity<PagoResponse> actualizarPago(
            @Parameter(description = "Nombre actual del tipo de pago", example = "INTERCAMBIO") @PathVariable String name,
            @RequestBody PagoRequest request) {
        return ResponseEntity.ok(PagoMapper.toResponse(gestionPagosUseCase.actualizarPagoPorNombre(name, request.nombre())));
    }

    @Operation(summary = "Eliminar tipo de pago", description = "Elimina un tipo de pago. Si hay trabajos que lo usan responde 409.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tipo de pago eliminado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Tipo de pago no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El tipo de pago está en uso", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @DeleteMapping("/admin/payment/{name}")
    public ResponseEntity<Void> eliminarPago(
            @Parameter(description = "Nombre del tipo de pago", example = "INTERCAMBIO") @PathVariable String name) {
        gestionPagosUseCase.eliminarPagoPorNombre(name);
        return ResponseEntity.noContent().build();
    }
}
