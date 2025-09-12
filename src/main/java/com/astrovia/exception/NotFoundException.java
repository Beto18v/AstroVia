package com.astrovia.exception;

/**
 * Excepción para recursos no encontrados en la capa de servicio.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
