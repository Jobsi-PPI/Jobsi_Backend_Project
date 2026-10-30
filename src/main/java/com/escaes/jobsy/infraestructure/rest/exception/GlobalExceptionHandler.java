package com.escaes.jobsy.infraestructure.rest.exception;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Credenciales inválidas, verifica usuario y contraseña");
        response.put("status", 401);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(DisabledException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "Usuario bloqueado, contacta al administrador");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        String campo = campoConError(ex.getCause());
        if (campo != null) {
            return respuesta(HttpStatus.BAD_REQUEST,
                    "El valor del campo '" + campo + "' no es válido o está fuera de rango");
        }
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido");
    }

    /*
     * Nombre del campo JSON que no se pudo convertir: tipo incorrecto (JsonMappingException)
     * o número fuera de rango (StreamReadException, p. ej. una cédula que no cabe en el tipo).
     */
    private static String campoConError(Throwable causa) {
        try {
            if (causa instanceof JsonMappingException mapping && !mapping.getPath().isEmpty()) {
                return mapping.getPath().get(mapping.getPath().size() - 1).getFieldName();
            }
            if (causa instanceof StreamReadException lectura && lectura.getProcessor() instanceof JsonParser parser) {
                return parser.getParsingContext().getCurrentName();
            }
        } catch (RuntimeException ignorada) {
            // Sin contexto disponible: se responde con el mensaje genérico
        }
        return null;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return respuesta(HttpStatus.CONFLICT,
                "No se puede completar la operación porque el registro está en uso o duplicado");
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus status, String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", mensaje);
        response.put("status", status.value());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ha ocurrido un error inesperado");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(BusinessExceptions.ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflictException(BusinessExceptions.ConflictException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(BusinessExceptions.NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(BusinessExceptions.NotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(BusinessExceptions.BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BusinessExceptions.BadRequestException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(BusinessExceptions.ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(BusinessExceptions.ForbiddenException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
