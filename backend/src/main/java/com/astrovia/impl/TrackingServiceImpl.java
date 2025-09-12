package com.astrovia.impl;

import com.astrovia.dto.TrackingDTO;
import com.astrovia.entity.Envio;
import com.astrovia.entity.Tracking;
import com.astrovia.entity.Usuario;
import com.astrovia.exception.ResourceNotFoundException;
import com.astrovia.repository.EnvioRepository;
import com.astrovia.repository.TrackingRepository;
import com.astrovia.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link TrackingService}.
 */
@Service
public class TrackingServiceImpl implements TrackingService {

    private static final Logger log = LoggerFactory.getLogger(TrackingServiceImpl.class);

    private final TrackingRepository trackingRepository;
    private final EnvioRepository envioRepository;

    public TrackingServiceImpl(TrackingRepository trackingRepository, EnvioRepository envioRepository) {
        this.trackingRepository = trackingRepository;
        this.envioRepository = envioRepository;
    }

    private TrackingDTO.Response toResponse(Tracking t) {
        Usuario u = t.getUsuario();
        TrackingDTO.Response resp = new TrackingDTO.Response(
                t.getId(), t.getFechaHora(), t.getUbicacion(), t.getEvento(), t.getObservaciones(),
                u != null ? new com.astrovia.dto.UsuarioDTO.Basic(u.getId(), u.getUsername(), u.getNombres(), u.getEmail(), u.getRol().name()) : null
        );
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingDTO.Response> findByEnvioId(Long envioId) {
        return trackingRepository.findByEnvioIdOrderByFechaHoraAsc(envioId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrackingDTO.Response save(TrackingDTO.Request request) {
    Envio envio = envioRepository.findById(request.idEnvio()).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        Usuario usuario = null; // En un escenario real vendría del SecurityContext
        Tracking t = new Tracking(envio, request.ubicacion(), request.evento(), usuario, request.observaciones());
        trackingRepository.save(t);
        log.info("Tracking registrado envio {} evento {}", envio.getCodigo(), t.getEvento());
        return toResponse(t);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackingDTO.Response getUltimoTracking(Long envioId) {
        return trackingRepository.findFirstByEnvioIdOrderByFechaHoraDesc(envioId)
                .map(this::toResponse)
                .orElse(null);
    }
}
