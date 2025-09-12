package com.astrovia.exception;

/**
 * Excepción para representar errores de validación o datos de entrada inválidos
 * suministrados por el cliente (HTTP 400).
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
