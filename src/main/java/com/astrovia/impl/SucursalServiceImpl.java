package com.astrovia.impl;

import com.astrovia.dto.SucursalDTO;
import com.astrovia.entity.Sucursal;
import com.astrovia.exception.NotFoundException;
import com.astrovia.repository.SucursalRepository;
import com.astrovia.service.SucursalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link SucursalService}.
 */
@Service
public class SucursalServiceImpl implements SucursalService {

    private static final Logger log = LoggerFactory.getLogger(SucursalServiceImpl.class);

    private final SucursalRepository sucursalRepository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    private SucursalDTO.Response toResponse(Sucursal s) {
        return new SucursalDTO.Response(s.getId(), s.getNombre(), s.getCiudad(), s.getDireccion(), s.getTelefono(), s.getFechaCreacion());
    }

    private void applyRequest(SucursalDTO.Request r, Sucursal s) {
        s.setNombre(r.nombre());
        s.setCiudad(r.ciudad());
        s.setDireccion(r.direccion());
        s.setTelefono(r.telefono());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO.Response> findAll() {
        return sucursalRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalDTO.Response findById(Long id) {
        return toResponse(sucursalRepository.findById(id).orElseThrow(() -> new NotFoundException("Sucursal no encontrada")));
    }

    @Override
    @Transactional
    public SucursalDTO.Response save(SucursalDTO.Request request) {
        Sucursal s = new Sucursal();
        applyRequest(request, s);
        sucursalRepository.save(s);
        log.info("Sucursal creada id {}", s.getId());
        return toResponse(s);
    }

    @Override
    @Transactional
    public SucursalDTO.Response update(Long id, SucursalDTO.Request request) {
        Sucursal s = sucursalRepository.findById(id).orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
        applyRequest(request, s);
        log.info("Sucursal actualizada id {}", id);
        return toResponse(s);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!sucursalRepository.existsById(id)) {
            throw new NotFoundException("Sucursal no encontrada");
        }
        sucursalRepository.deleteById(id);
        log.info("Sucursal eliminada id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO.Response> findByCiudad(String ciudad) {
        return sucursalRepository.findByCiudad(ciudad).stream().map(this::toResponse).collect(Collectors.toList());
    }
}
