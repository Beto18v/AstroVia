package com.astrovia.service;

import com.astrovia.dto.PaqueteDTO;
import java.util.List;

/**
 * Lógica de negocio para Paquetes ligados a un Envío.
 * Especificación -> DTO:
 *  - PaqueteRequest  -> {@link PaqueteDTO.Request}
 *  - PaqueteResponse -> {@link PaqueteDTO.Response}
 */
public interface PaqueteService {

    List<PaqueteDTO.Response> findByEnvioId(Long envioId);

    PaqueteDTO.Response save(PaqueteDTO.Request request);

    void deleteById(Long id);
}
