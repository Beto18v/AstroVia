package com.astrovia.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * Usar para entidades buscadas por id, código u otro identificador que no se encuentran.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
