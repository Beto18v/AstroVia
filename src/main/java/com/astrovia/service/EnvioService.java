package com.astrovia.service;

import com.astrovia.dto.EnvioDTO;
import com.astrovia.enums.EstadoEnvio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio para Envíos.
 * Especificación -> DTO:
 *  - EnvioRequest         -> {@link EnvioDTO.Request}
 *  - EnvioResponse        -> {@link EnvioDTO.Response}
 *  - EstadoEnvioRequest   -> {@link EnvioDTO.EstadoRequest}
 */
public interface EnvioService {

    Page<EnvioDTO.Response> findAll(Pageable pageable);

    EnvioDTO.Response findById(Long id);

    EnvioDTO.Response findByCodigo(String codigo);

    EnvioDTO.Response save(EnvioDTO.Request request);

    EnvioDTO.Response update(Long id, EnvioDTO.Request request);

    void deleteById(Long id);

    List<EnvioDTO.Response> findByClienteId(Long clienteId);

    List<EnvioDTO.Response> findByEstado(EstadoEnvio estado);

    EnvioDTO.Response updateEstado(Long id, EnvioDTO.EstadoRequest request);

    /** Genera un código único para un envío (ej: estrategia basada en prefijo + timestamp + random). */
    String generateCodigo();

    /** Devuelve estadísticas de cantidad por estado actual. */
    Map<EstadoEnvio, Long> getEstadisticasEstados();
}
