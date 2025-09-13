package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.TrackingDTO;
import com.astrovia.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@Tag(name = "Tracking", description = "Eventos de tracking de envíos")
@SecurityRequirement(name = "bearerAuth")
public class TrackingController {

        // Controlador para gestión de eventos de tracking de envíos
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/envio/{envioId}")
    @Operation(summary = "Listar tracking de un envío")
        // Lista todos los eventos de tracking de un envío
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<TrackingDTO.Response>>> findByEnvio(@PathVariable Long envioId) {
        return ResponseEntity.ok(ApiResponse.ok("Tracking del envío", trackingService.findByEnvioId(envioId)));
    }

    @GetMapping("/envio/{envioId}/ultimo")
    @Operation(summary = "Obtener último evento de tracking de un envío")
        // Obtiene el último evento de tracking de un envío
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<TrackingDTO.Response>> ultimo(@PathVariable Long envioId) {
        return ResponseEntity.ok(ApiResponse.ok("Último tracking", trackingService.getUltimoTracking(envioId)));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo evento de tracking")
        // Registra un nuevo evento de tracking
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<TrackingDTO.Response>> create(@Valid @RequestBody TrackingDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Tracking registrado", trackingService.save(request)));
    }
}
