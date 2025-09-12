package com.astrovia.service;

import com.astrovia.dto.TrackingDTO;
import java.util.List;

/**
 * Lógica de negocio para eventos de Tracking de un Envío.
 * Especificación -> DTO:
 *  - TrackingRequest  -> {@link TrackingDTO.Request}
 *  - TrackingResponse -> {@link TrackingDTO.Response}
 */
public interface TrackingService {

    List<TrackingDTO.Response> findByEnvioId(Long envioId);

    TrackingDTO.Response save(TrackingDTO.Request request);

    /** Obtiene el último evento registrado para un envío. */
    TrackingDTO.Response getUltimoTracking(Long envioId);
}
