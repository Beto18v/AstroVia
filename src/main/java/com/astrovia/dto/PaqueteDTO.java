package com.astrovia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** DTOs para Paquete. */
public final class PaqueteDTO {
    private PaqueteDTO() {}

    /** Petición de creación / actualización de paquete. */
    public record Request(
            @NotNull(message = "El id del envío es obligatorio")
            Long idEnvio,

            @NotBlank(message = "La descripción es obligatoria")
            @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
            String descripcion,

            @DecimalMin(value = "0.00", message = "El valor declarado no puede ser negativo")
            BigDecimal valorDeclarado,

            @NotNull(message = "El peso es obligatorio")
            @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
            BigDecimal peso,

            @Size(max = 50, message = "Las dimensiones no pueden exceder 50 caracteres")
            String dimensiones
    ) { }

    /** Respuesta de paquete. */
    public record Response(
            Long id,
            String descripcion,
            BigDecimal valorDeclarado,
            BigDecimal peso,
            String dimensiones
    ) { }
}
