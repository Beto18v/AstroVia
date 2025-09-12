package com.astrovia.exception;

/**
 * Excepción para reglas de negocio violadas.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
