package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.AuthDTO;
import com.astrovia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticación (login / logout / refresh / validate).
 * No requiere autenticación previa para login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Gestión de autenticación y tokens")
public class AuthController {

        // Controlador para autenticación de usuarios (login, logout, validación y refresh de tokens)
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica usuario y devuelve token")
        // Inicia sesión y retorna el token JWT
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.LoginResponse resp = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", resp));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalida token actual")
        // Cierra sesión e invalida el token JWT
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(name = "Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("Logout exitoso"));
    }

    @GetMapping("/validate")
    @Operation(summary = "Valida un token JWT")
        // Valida si el token JWT es correcto y vigente
    public ResponseEntity<ApiResponse<Boolean>> validate(@RequestParam String token) {
        boolean valid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.ok("Token evaluado", valid));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Genera nuevo token a partir de refresh token")
        // Genera un nuevo token usando el refresh token
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> refresh(@RequestParam String refreshToken) {
        AuthDTO.LoginResponse resp = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Token refrescado", resp));
    }
}
