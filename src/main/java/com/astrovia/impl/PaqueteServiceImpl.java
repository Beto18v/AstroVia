package com.astrovia.impl;

import com.astrovia.dto.PaqueteDTO;
import com.astrovia.entity.Envio;
import com.astrovia.entity.Paquete;
import com.astrovia.exception.ResourceNotFoundException;
import com.astrovia.repository.EnvioRepository;
import com.astrovia.repository.PaqueteRepository;
import com.astrovia.service.PaqueteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link PaqueteService}.
 */
@Service
public class PaqueteServiceImpl implements PaqueteService {

    private static final Logger log = LoggerFactory.getLogger(PaqueteServiceImpl.class);

    private final PaqueteRepository paqueteRepository;
    private final EnvioRepository envioRepository;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository, EnvioRepository envioRepository) {
        this.paqueteRepository = paqueteRepository;
        this.envioRepository = envioRepository;
    }

    private PaqueteDTO.Response toResponse(Paquete p) {
        return new PaqueteDTO.Response(p.getId(), p.getDescripcion(), p.getValorDeclarado(), p.getPeso(), p.getDimensiones());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteDTO.Response> findByEnvioId(Long envioId) {
        return paqueteRepository.findByEnvioIdOrderByIdAsc(envioId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaqueteDTO.Response save(PaqueteDTO.Request request) {
    Envio envio = envioRepository.findById(request.idEnvio()).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        Paquete p = new Paquete(envio, request.descripcion(), request.valorDeclarado(), request.peso(), request.dimensiones());
        paqueteRepository.save(p);
        log.info("Paquete agregado al envío {}", envio.getCodigo());
        return toResponse(p);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!paqueteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paquete no encontrado");
        }
        paqueteRepository.deleteById(id);
        log.info("Paquete eliminado id {}", id);
    }
}
