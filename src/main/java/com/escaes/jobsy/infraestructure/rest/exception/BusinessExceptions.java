package com.escaes.jobsy.infraestructure.rest.exception;

public class BusinessExceptions extends RuntimeException {

    public BusinessExceptions(String message) {
        super(message);
    }

    public static class ConflictException extends BusinessExceptions {
        public ConflictException(String message) {
            super(message);
        }
    }
    public static class NotFoundException extends BusinessExceptions {
        public NotFoundException(String message) {
            super(message);
        }
    }
    public static class BadRequestException extends BusinessExceptions {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
