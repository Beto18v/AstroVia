package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.SucursalDTO;
import com.astrovia.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@Tag(name = "Sucursales", description = "Gestión de sucursales")
@SecurityRequirement(name = "bearerAuth")
public class SucursalController {

        // Controlador para gestión de sucursales (listar, crear, actualizar, eliminar)
    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las sucursales")
        // Lista todas las sucursales
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<SucursalDTO.Response>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Listado de sucursales", sucursalService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal por id")
        // Busca una sucursal por su ID
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<SucursalDTO.Response>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Sucursal encontrada", sucursalService.findById(id)));
    }

    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Buscar sucursales por ciudad")
        // Busca sucursales por ciudad
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<SucursalDTO.Response>>> findByCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(ApiResponse.ok("Sucursales por ciudad", sucursalService.findByCiudad(ciudad)));
    }

    @PostMapping
    @Operation(summary = "Crear sucursal")
        // Crea una nueva sucursal
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SucursalDTO.Response>> create(@Valid @RequestBody SucursalDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Sucursal creada", sucursalService.save(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sucursal")
        // Actualiza los datos de una sucursal
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SucursalDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody SucursalDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Sucursal actualizada", sucursalService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sucursal")
        // Elimina una sucursal por su ID
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sucursalService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Sucursal eliminada"));
    }
}
