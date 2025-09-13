package com.astrovia.impl;

import com.astrovia.config.JwtTokenProvider;
import com.astrovia.dto.AuthDTO;
import com.astrovia.dto.UsuarioDTO;
import com.astrovia.entity.Usuario;
import com.astrovia.exception.BusinessException;
import com.astrovia.exception.ResourceNotFoundException;
import com.astrovia.repository.UsuarioRepository;
import com.astrovia.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de {@link AuthService} gestionando autenticación con JWT.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // En un escenario real se tendría un store (Redis/DB) para refresh tokens.
    private final Map<String, String> refreshTokenStore = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            log.warn("Intento de login fallido para username {}", request.username());
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
    String token = jwtTokenProvider.generateAccessToken(usuario.getUsername(), claims);

        UsuarioDTO.Basic basic = new UsuarioDTO.Basic(
                usuario.getId(), usuario.getUsername(), usuario.getNombres(), usuario.getEmail(), usuario.getRol().name()
        );
        log.info("Usuario {} autenticado correctamente", usuario.getUsername());
        return new AuthDTO.LoginResponse(token, "Bearer", basic);
    }

    /** {@inheritDoc} */
    @Override
    public void logout(String token) {
        // Estrategia simple: almacenar token revocado en memoria temporal (no persistente)
        if (token != null && !token.isBlank()) {
            refreshTokenStore.put(token, "revoked");
            log.info("Token invalidado manualmente");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;
        if (refreshTokenStore.containsKey(token)) return false; // revocado
        try {
        String username = jwtTokenProvider.getUsername(token);
            return usuarioRepository.findByUsername(username)
            .filter(u -> jwtTokenProvider.isValid(token, u.getUsername()))
                    .isPresent();
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public AuthDTO.LoginResponse refreshToken(String refreshToken) {
        // Implementación simplificada: reutiliza el token si es válido; en real emitir nuevo basado en refresh token.
        if (!validateToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido");
        }
    String username = jwtTokenProvider.getUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
    String nuevo = jwtTokenProvider.generateAccessToken(usuario.getUsername(), claims);
        UsuarioDTO.Basic basic = new UsuarioDTO.Basic(
                usuario.getId(), usuario.getUsername(), usuario.getNombres(), usuario.getEmail(), usuario.getRol().name()
        );
        log.info("Token refrescado para usuario {}", username);
        return new AuthDTO.LoginResponse(nuevo, "Bearer", basic);
    }
}
