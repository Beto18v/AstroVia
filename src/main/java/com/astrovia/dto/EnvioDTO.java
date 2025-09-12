package com.astrovia.dto;

import com.astrovia.enums.EstadoEnvio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTOs para Envío. */
public final class EnvioDTO {
    private EnvioDTO() {}

    /** Petición de creación de envío. */
    public record Request(
            @NotNull(message = "El id del cliente es obligatorio")
            Long idCliente,

            @NotNull(message = "La sucursal origen es obligatoria")
            Long idSucursalOrigen,

            @NotNull(message = "La sucursal destino es obligatoria")
            Long idSucursalDestino,

            @NotNull(message = "El peso es obligatorio")
            @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
            BigDecimal peso,

            @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
            String observaciones
    ) { }

    /** Cambio de estado de un envío. */
    public record EstadoRequest(
            @NotNull(message = "El estado es obligatorio")
            EstadoEnvio estado,

            @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
            String observaciones
    ) { }

    /** Respuesta de un envío con paquetes y último tracking. */
    public record Response(
            Long id,
            String codigo,
            UsuarioDTO.Basic cliente,
            SucursalDTO.Basic sucursalOrigen,
            SucursalDTO.Basic sucursalDestino,
            BigDecimal peso,
            String estado,
            BigDecimal precio,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaEstimadaEntrega,
            String observaciones,
            List<PaqueteDTO.Response> paquetes,
            TrackingDTO.Response ultimoTracking
    ) { }
}
