package com.astrovia.impl;

import com.astrovia.dto.EnvioDTO;
import com.astrovia.dto.PaqueteDTO;
import com.astrovia.dto.SucursalDTO;
import com.astrovia.dto.UsuarioDTO;
import com.astrovia.entity.Envio;
import com.astrovia.entity.Sucursal;
import com.astrovia.entity.Tracking;
import com.astrovia.entity.Usuario;
import com.astrovia.enums.EstadoEnvio;
import com.astrovia.exception.BusinessException;
import com.astrovia.exception.NotFoundException;
import com.astrovia.repository.EnvioRepository;
import com.astrovia.repository.SucursalRepository;
import com.astrovia.repository.TrackingRepository;
import com.astrovia.repository.UsuarioRepository;
import com.astrovia.service.EnvioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación de {@link EnvioService} incluyendo generación de código y tracking inicial.
 */
@Service
public class EnvioServiceImpl implements EnvioService {

    private static final Logger log = LoggerFactory.getLogger(EnvioServiceImpl.class);

    private final EnvioRepository envioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final TrackingRepository trackingRepository;

    public EnvioServiceImpl(EnvioRepository envioRepository, UsuarioRepository usuarioRepository, SucursalRepository sucursalRepository, TrackingRepository trackingRepository) {
        this.envioRepository = envioRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.trackingRepository = trackingRepository;
    }

    private EnvioDTO.Response toResponse(Envio e) {
        Usuario c = e.getCliente();
        UsuarioDTO.Basic clienteBasic = new UsuarioDTO.Basic(c.getId(), c.getUsername(), c.getNombres(), c.getEmail(), c.getRol().name());
        Sucursal o = e.getSucursalOrigen();
        Sucursal d = e.getSucursalDestino();
        SucursalDTO.Basic origenBasic = new SucursalDTO.Basic(o.getId(), o.getNombre(), o.getCiudad());
        SucursalDTO.Basic destinoBasic = new SucursalDTO.Basic(d.getId(), d.getNombre(), d.getCiudad());
        Tracking ultimo = e.getTrackings().stream().max(Comparator.comparing(Tracking::getFechaHora)).orElse(null);
        com.astrovia.dto.TrackingDTO.Response ultimoDto = null;
        if (ultimo != null) {
            Usuario tu = ultimo.getUsuario();
            com.astrovia.dto.UsuarioDTO.Basic traUser = tu != null ? new com.astrovia.dto.UsuarioDTO.Basic(tu.getId(), tu.getUsername(), tu.getNombres(), tu.getEmail(), tu.getRol().name()) : null;
            ultimoDto = new com.astrovia.dto.TrackingDTO.Response(ultimo.getId(), ultimo.getFechaHora(), ultimo.getUbicacion(), ultimo.getEvento(), ultimo.getObservaciones(), traUser);
        }
        List<PaqueteDTO.Response> paquetes = e.getPaquetes().stream()
                .map(p -> new PaqueteDTO.Response(p.getId(), p.getDescripcion(), p.getValorDeclarado(), p.getPeso(), p.getDimensiones()))
                .collect(Collectors.toList());
        return new EnvioDTO.Response(e.getId(), e.getCodigo(), clienteBasic, origenBasic, destinoBasic, e.getPeso(), e.getEstado().name(), e.getPrecio(), e.getFechaCreacion(), e.getFechaEstimadaEntrega(), e.getObservaciones(), paquetes, ultimoDto);
    }

    private void applyRequest(EnvioDTO.Request r, Envio e) {
        Usuario cliente = usuarioRepository.findById(r.idCliente()).orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        Sucursal origen = sucursalRepository.findById(r.idSucursalOrigen()).orElseThrow(() -> new NotFoundException("Sucursal origen no encontrada"));
        Sucursal destino = sucursalRepository.findById(r.idSucursalDestino()).orElseThrow(() -> new NotFoundException("Sucursal destino no encontrada"));
        e.setCliente(cliente);
        e.setSucursalOrigen(origen);
        e.setSucursalDestino(destino);
        e.setPeso(r.peso());
        e.setObservaciones(r.observaciones());
        if (e.getEstado() == null) e.setEstado(EstadoEnvio.CREADO);
        if (e.getCodigo() == null) e.setCodigo(generateCodigo());
        // Cálculo de precio simple: peso * factor (ejemplo base 10)
        e.setPrecio(r.peso().multiply(BigDecimal.valueOf(10)));
        e.setFechaEstimadaEntrega(LocalDateTime.now().plusDays(3));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvioDTO.Response> findAll(Pageable pageable) {
        return envioRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioDTO.Response findById(Long id) {
        return toResponse(envioRepository.findById(id).orElseThrow(() -> new NotFoundException("Envío no encontrado")));
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioDTO.Response findByCodigo(String codigo) {
        return toResponse(envioRepository.findByCodigo(codigo).orElseThrow(() -> new NotFoundException("Envío no encontrado")));
    }

    @Override
    @Transactional
    public EnvioDTO.Response save(EnvioDTO.Request request) {
        Envio e = new Envio();
        applyRequest(request, e);
        envioRepository.save(e);
        // Tracking inicial
        Tracking t = new Tracking(e, "CREADO", (String) null);
        trackingRepository.save(t);
        e.addTracking(t);
        log.info("Envío creado código {}", e.getCodigo());
        return toResponse(e);
    }

    @Override
    @Transactional
    public EnvioDTO.Response update(Long id, EnvioDTO.Request request) {
        Envio e = envioRepository.findById(id).orElseThrow(() -> new NotFoundException("Envío no encontrado"));
        applyRequest(request, e);
        log.info("Envío actualizado id {}", id);
        return toResponse(e);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!envioRepository.existsById(id)) throw new NotFoundException("Envío no encontrado");
        envioRepository.deleteById(id);
        log.info("Envío eliminado id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioDTO.Response> findByClienteId(Long clienteId) {
        return envioRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioDTO.Response> findByEstado(EstadoEnvio estado) {
        return envioRepository.findByEstado(estado).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnvioDTO.Response updateEstado(Long id, EnvioDTO.EstadoRequest request) {
        Envio e = envioRepository.findById(id).orElseThrow(() -> new NotFoundException("Envío no encontrado"));
        if (request.estado() == null) throw new BusinessException("Estado requerido");
        e.setEstado(request.estado());
        Tracking t = new Tracking(e, request.estado().name(), request.observaciones());
        trackingRepository.save(t);
        e.addTracking(t);
        log.info("Estado de envío {} cambiado a {}", e.getCodigo(), request.estado());
        return toResponse(e);
    }

    @Override
    public String generateCodigo() {
        String codigo;
        int intentos = 0;
        do {
            codigo = "ENV" + System.currentTimeMillis() + new Random().nextInt(1000);
            intentos++;
            if (intentos > 5) throw new BusinessException("No se pudo generar código único");
        } while (envioRepository.existsByCodigo(codigo));
        return codigo;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<EstadoEnvio, Long> getEstadisticasEstados() {
        Map<EstadoEnvio, Long> map = new EnumMap<>(EstadoEnvio.class);
        for (EstadoEnvio est : EstadoEnvio.values()) {
            map.put(est, envioRepository.countByEstado(est));
        }
        return map;
    }
}
