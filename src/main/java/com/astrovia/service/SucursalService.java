package com.astrovia.service;

import com.astrovia.dto.SucursalDTO;
import java.util.List;

/**
 * Lógica de negocio para gestión de Sucursales.
 * Especificación -> DTO:
 *  - SucursalRequest  -> {@link SucursalDTO.Request}
 *  - SucursalResponse -> {@link SucursalDTO.Response}
 */
public interface SucursalService {

    List<SucursalDTO.Response> findAll();

    SucursalDTO.Response findById(Long id);

    SucursalDTO.Response save(SucursalDTO.Request request);

    SucursalDTO.Response update(Long id, SucursalDTO.Request request);

    void deleteById(Long id);

    List<SucursalDTO.Response> findByCiudad(String ciudad);
}
