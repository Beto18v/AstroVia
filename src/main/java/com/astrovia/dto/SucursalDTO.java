package com.astrovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/** DTOs relacionados con Sucursal. */
public final class SucursalDTO {
    private SucursalDTO() {}

    /** Datos básicos (para incrustar en Envío). */
    public record Basic(
            Long id,
            String nombre,
            String ciudad
    ) { }

    /** Petición de creación / actualización. */
    public record Request(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
            String nombre,

            @NotBlank(message = "La ciudad es obligatoria")
            @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
            String ciudad,

            @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
            String direccion,

            @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
            String telefono
    ) { }

    /** Respuesta completa. */
    public record Response(
            Long id,
            String nombre,
            String ciudad,
            String direccion,
            String telefono,
            LocalDateTime fechaCreacion
    ) { }
}
