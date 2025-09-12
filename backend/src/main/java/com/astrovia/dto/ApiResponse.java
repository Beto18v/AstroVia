package com.astrovia.dto;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

/**
 * Wrapper genérico estándar para respuestas de la API.
 * Inmutable gracias a record (Java 17+).
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        int status,
        LocalDateTime timestamp
) {
    // --- Factory methods de éxito ---
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null, HttpStatus.OK.value(), LocalDateTime.now());
    }
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, HttpStatus.OK.value(), LocalDateTime.now());
    }

    // --- Factory methods de error genérico (500) ---
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    }
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    }

    // --- Factory methods parametrizados ---
    public static <T> ApiResponse<T> of(HttpStatus status, boolean success, String message) {
        return new ApiResponse<>(success, message, null, status.value(), LocalDateTime.now());
    }
    public static <T> ApiResponse<T> of(HttpStatus status, boolean success, String message, T data) {
        return new ApiResponse<>(success, message, data, status.value(), LocalDateTime.now());
    }
}
