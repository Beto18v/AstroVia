package com.astrovia.exception;

/**
 * Excepción para indicar falta de autenticación o credenciales inválidas (HTTP 401).
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}
