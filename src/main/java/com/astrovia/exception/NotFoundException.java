package com.astrovia.exception;

/**
 * @deprecated Reemplazada por {@link ResourceNotFoundException}. Mantener sólo por compatibilidad temporal.
 */
@Deprecated
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
