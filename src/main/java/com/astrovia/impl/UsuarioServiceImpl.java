package com.astrovia.impl;

import com.astrovia.dto.UsuarioDTO;
import com.astrovia.entity.Usuario;
import com.astrovia.enums.Rol;
import com.astrovia.exception.BusinessException;
import com.astrovia.exception.NotFoundException;
import com.astrovia.repository.UsuarioRepository;
import com.astrovia.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link UsuarioService}.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UsuarioDTO.Response toResponse(Usuario u) {
        Long cantidadEnvios = (u.getEnvios() != null) ? (long) u.getEnvios().size() : 0L;
        return new UsuarioDTO.Response(
                u.getId(), u.getUsername(), u.getNombres(), u.getEmail(), u.getDoc(),
                u.getTelefono(), u.getDireccion(), u.getRol().name(), u.getActivo(),
                u.getFechaCreacion(), cantidadEnvios
        );
    }

    private void applyRequest(UsuarioDTO.Request req, Usuario u, boolean encodePassword) {
        u.setUsername(req.username());
        if (encodePassword) {
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        u.setNombres(req.nombres());
        u.setEmail(req.email());
        u.setDoc(req.doc());
        u.setTelefono(req.telefono());
        u.setDireccion(req.direccion());
        u.setRol(req.rol());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO.Response> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO.Response findById(Long id) {
        return toResponse(usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado")));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO.Response findByUsername(String username) {
        return toResponse(usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado")));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO.Response findByDoc(String doc) {
        return toResponse(usuarioRepository.findByDoc(doc)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado")));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UsuarioDTO.Response save(UsuarioDTO.Request request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username ya existe");
        }
        Usuario u = new Usuario();
        applyRequest(request, u, true);
        usuarioRepository.save(u);
        log.info("Usuario creado con username {}", u.getUsername());
        return toResponse(u);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UsuarioDTO.Response update(Long id, UsuarioDTO.Request request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        boolean cambiarPassword = request.password() != null && !request.password().isBlank();
        applyRequest(request, u, cambiarPassword);
        log.info("Usuario actualizado id {}", id);
        return toResponse(u);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado id {}", id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> searchByName(String nombre) {
        return usuarioRepository.findByNombresContainingIgnoreCase(nombre).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByDoc(String doc) {
        return usuarioRepository.existsByDoc(doc);
    }
}
