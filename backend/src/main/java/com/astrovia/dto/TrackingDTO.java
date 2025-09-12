package com.astrovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** DTOs para Tracking de envíos. */
public final class TrackingDTO {
    private TrackingDTO() {}

    /** Petición para agregar evento de tracking. */
    public record Request(
            @NotNull(message = "El id del envío es obligatorio")
            Long idEnvio,

            @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
            String ubicacion,

            @NotBlank(message = "El evento es obligatorio")
            @Size(max = 100, message = "El evento no puede exceder 100 caracteres")
            String evento,

            @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
            String observaciones
    ) { }

    /** Respuesta de tracking. */
    public record Response(
            Long id,
            LocalDateTime fechaHora,
            String ubicacion,
            String evento,
            String observaciones,
            UsuarioDTO.Basic usuario
    ) { }
}
