package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.EnvioDTO;
import com.astrovia.enums.EstadoEnvio;
import com.astrovia.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios", description = "Gestión de envíos")
@SecurityRequirement(name = "bearerAuth")
public class EnvioController {

        // Controlador para gestión de envíos (listar, crear, actualizar, eliminar, estadísticas)
    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    @Operation(summary = "Listar envíos paginados")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Lista todos los envíos de forma paginada
    public ResponseEntity<ApiResponse<Page<EnvioDTO.Response>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EnvioDTO.Response> result = envioService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Listado de envíos", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por id")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Busca un envío por su ID
    public ResponseEntity<ApiResponse<EnvioDTO.Response>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Envío encontrado", envioService.findById(id)));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener envío por código")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Busca un envío por su código
    public ResponseEntity<ApiResponse<EnvioDTO.Response>> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ApiResponse.ok("Envío encontrado", envioService.findByCodigo(codigo)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar envíos por cliente")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR') or (hasRole('CLIENTE') and #clienteId == principal.id)")
        // Lista los envíos de un cliente específico
    public ResponseEntity<ApiResponse<List<EnvioDTO.Response>>> findByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.ok("Envíos del cliente", envioService.findByClienteId(clienteId)));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar envíos por estado")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Lista los envíos según su estado
    public ResponseEntity<ApiResponse<List<EnvioDTO.Response>>> findByEstado(@PathVariable EstadoEnvio estado) {
        return ResponseEntity.ok(ApiResponse.ok("Envíos por estado", envioService.findByEstado(estado)));
    }

    @PostMapping
    @Operation(summary = "Crear envío")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Crea un nuevo envío
    public ResponseEntity<ApiResponse<EnvioDTO.Response>> create(@Valid @RequestBody EnvioDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Envío creado", envioService.save(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar envío")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Actualiza los datos de un envío existente
    public ResponseEntity<ApiResponse<EnvioDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody EnvioDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Envío actualizado", envioService.update(id, request)));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de envío")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Actualiza el estado de un envío
    public ResponseEntity<ApiResponse<EnvioDTO.Response>> updateEstado(@PathVariable Long id, @Valid @RequestBody EnvioDTO.EstadoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", envioService.updateEstado(id, request)));
    }

    @GetMapping("/estadisticas/estados")
    @Operation(summary = "Estadísticas de envíos por estado")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
        // Obtiene estadísticas de envíos agrupados por estado
    public ResponseEntity<ApiResponse<Map<EstadoEnvio, Long>>> estadisticasEstados() {
        return ResponseEntity.ok(ApiResponse.ok("Estadísticas por estado", envioService.getEstadisticasEstados()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar envío")
    @PreAuthorize("hasRole('ADMIN')")
        // Elimina un envío por su ID
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        envioService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Envío eliminado"));
    }
}
