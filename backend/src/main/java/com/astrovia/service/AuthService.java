package com.astrovia.service;

import com.astrovia.dto.AuthDTO;

/**
 * Contrato de lógica de autenticación y gestión de tokens.
 *
 * Nombres mapeados desde especificación:
 *  - LoginRequest  -> {@link AuthDTO.LoginRequest}
 *  - LoginResponse -> {@link AuthDTO.LoginResponse}
 */
public interface AuthService {

    /**
     * Autentica un usuario y devuelve token + datos básicos.
     * @param request credenciales de login
     * @return respuesta con token JWT u otro mecanismo
     */
    AuthDTO.LoginResponse login(AuthDTO.LoginRequest request);

    /**
     * Invalida / revoca un token activo (lista negra, expiración forzada, etc.).
     * @param token token a invalidar
     */
    void logout(String token);

    /**
     * Valida un token (firma, expiración, estado revocado, etc.).
     * @param token token a validar
     * @return true si es válido
     */
    boolean validateToken(String token);

    /**
     * Genera un nuevo token a partir de un refresh token válido.
     * @param refreshToken token de refresco
     * @return nueva respuesta de login con token renovado
     */
    AuthDTO.LoginResponse refreshToken(String refreshToken);
}
