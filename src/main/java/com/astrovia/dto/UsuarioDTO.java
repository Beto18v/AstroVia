package com.astrovia.dto;

import com.astrovia.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTOs relacionados con Usuario (unifica cliente / operador / admin).
 */
public final class UsuarioDTO {
    private UsuarioDTO() {}

    /** Datos básicos embebibles (sin password). */
    public record Basic(
            Long id,
            String username,
            String nombres,
            String email,
            String rol
    ) { }

    /** Petición de creación / actualización de usuario. */
    public record Request(
            @NotBlank(message = "El username es obligatorio")
            @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
            String username,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String password,

            @NotBlank(message = "Los nombres son obligatorios")
            @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
            String nombres,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Debe ser un email válido")
            @Size(max = 100, message = "El email no puede exceder 100 caracteres")
            String email,

            @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
            String doc,

            @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
            @Pattern(regexp = "^$|^[+]?[-0-9()\\s]{6,20}$", message = "Formato de teléfono inválido")
            String telefono,

            @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
            String direccion,

            @NotNull(message = "El rol es obligatorio")
            Rol rol
    ) { }

    /** Respuesta completa de usuario (sin password). */
    public record Response(
            Long id,
            String username,
            String nombres,
            String email,
            String doc,
            String telefono,
            String direccion,
            String rol,
            Boolean activo,
            LocalDateTime fechaCreacion,
            Long cantidadEnvios
    ) { }
}
