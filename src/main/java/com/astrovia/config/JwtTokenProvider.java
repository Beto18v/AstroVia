package com.astrovia.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Proveedor central de generación y validación de JWT para el sistema.
 * Lee propiedades desde application.properties / yml:
 *  - app.jwt.secret (Base64)
 *  - app.jwt.expiration-seconds
 *  - app.jwt.refresh-expiration-seconds
 *
 * Expone métodos para generar tokens de acceso y (opcional) refresh tokens.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret:ZmFrZVNlY3JldEtleUZvckpXVDEyMzQ1Njc4OTA=}")
    private String secretBase64;

    @Value("${app.jwt.expiration-seconds:3600}")
    private long accessExpirationSeconds;

    @Value("${app.jwt.refresh-expiration-seconds:86400}")
    private long refreshExpirationSeconds;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token de acceso con claims adicionales.
     * @param subject identificador (username)
     * @param extraClaims mapa de claims extra (rol, etc.)
     */
    public String generateAccessToken(String subject, Map<String, Object> extraClaims) {
        return buildToken(subject, extraClaims, accessExpirationSeconds);
    }

    /** Genera un refresh token simple (misma firma, distinta expiración). */
    public String generateRefreshToken(String subject, Map<String, Object> extraClaims) {
        return buildToken(subject, extraClaims, refreshExpirationSeconds);
    }

    private String buildToken(String subject, Map<String, Object> extraClaims, long validitySeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(extraClaims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(validitySeconds)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extrae el username (subject) del token. */
    public String getUsername(String token) { return getClaim(token, Claims::getSubject); }

    /** Valida firma y expiración. */
    public boolean isValid(String token, String expectedUsername) {
        try {
            String username = getUsername(token);
            return username.equals(expectedUsername) && !isExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /** Indica si el token expiró. */
    public boolean isExpired(String token) { return getExpiration(token).before(new Date()); }

    public Date getExpiration(String token) { return getClaim(token, Claims::getExpiration); }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }
}
