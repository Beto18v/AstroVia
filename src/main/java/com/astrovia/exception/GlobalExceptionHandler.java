package com.astrovia.exception;

import com.astrovia.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global centralizado de excepciones para la API.
 * Estandariza el formato JSON de los errores devolviendo {@link ApiResponse}.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Excepciones personalizadas ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND, false);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return buildError(ex, HttpStatus.BAD_REQUEST, false);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return buildError(ex, HttpStatus.UNAUTHORIZED, false);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return buildError(ex, HttpStatus.UNPROCESSABLE_ENTITY, false);
    }

    // --- Excepciones comunes de Spring / Java ---
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parámetro '%s' con valor '%s' no es del tipo requerido", ex.getName(), ex.getValue());
        return buildError(message, ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(ex, HttpStatus.BAD_REQUEST, false);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        return buildError(ex, HttpStatus.CONFLICT, false);
    }

    // --- Genéricas ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return buildError("Error interno del servidor", ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Validaciones @Valid ---
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        Map<String, Object> payload = new HashMap<>();
        payload.put("errors", fieldErrors);
        payload.put("count", fieldErrors.size());
        ApiResponse<Map<String, Object>> body = ApiResponse.of(HttpStatus.BAD_REQUEST, false, "Error de validación", payload);
        log.warn("Validation error: {} -> {}", ex.getMessage(), fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // --- Helpers ---
    private ResponseEntity<ApiResponse<Void>> buildError(Throwable ex, HttpStatus status, boolean success) {
        logError(status, ex.getMessage(), ex);
        ApiResponse<Void> body = ApiResponse.of(status, success, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(String message, Throwable ex, HttpStatus status) {
        logError(status, message, ex);
        ApiResponse<Void> body = ApiResponse.of(status, false, message);
        return new ResponseEntity<>(body, status);
    }

    private void logError(HttpStatus status, String message, Throwable ex) {
        if (status.is5xxServerError()) {
            log.error("[{}] {}", status.value(), message, ex);
        } else if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
            log.warn("[{}] {}", status.value(), message);
        } else {
            log.info("[{}] {}", status.value(), message);
        }
    }
}
