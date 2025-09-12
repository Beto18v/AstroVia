package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.PaqueteDTO;
import com.astrovia.service.PaqueteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@Tag(name = "Paquetes", description = "Gestión de paquetes dentro de un envío")
@SecurityRequirement(name = "bearerAuth")
public class PaqueteController {

    private final PaqueteService paqueteService;

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @GetMapping("/envio/{envioId}")
    @Operation(summary = "Listar paquetes de un envío")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<PaqueteDTO.Response>>> findByEnvio(@PathVariable Long envioId) {
        return ResponseEntity.ok(ApiResponse.ok("Paquetes del envío", paqueteService.findByEnvioId(envioId)));
    }

    @PostMapping
    @Operation(summary = "Crear paquete")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<PaqueteDTO.Response>> create(@Valid @RequestBody PaqueteDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Paquete creado", paqueteService.save(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paquete")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        paqueteService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Paquete eliminado"));
    }
}
