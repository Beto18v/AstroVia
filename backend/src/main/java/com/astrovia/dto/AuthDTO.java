package com.astrovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs relacionados con autenticación / login.
 */
public final class AuthDTO {
    private AuthDTO() {}

    /** Petición de login. */
    public record LoginRequest(
            @NotBlank(message = "El username es obligatorio")
            @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
            String username,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String password
    ) { }

    /** Respuesta de login con token y datos básicos del usuario. */
    public record LoginResponse(
            String token,
            String tipoToken,
            UsuarioDTO.Basic usuario
    ) { }
}
