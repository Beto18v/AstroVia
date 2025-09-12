package com.astrovia.controller;

import com.astrovia.dto.ApiResponse;
import com.astrovia.dto.UsuarioDTO;
import com.astrovia.enums.Rol;
import com.astrovia.service.UsuarioService;
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

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios paginados")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<UsuarioDTO.Response>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UsuarioDTO.Response> result = usuarioService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Listado de usuarios", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<UsuarioDTO.Response>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", usuarioService.findById(id)));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Buscar usuario por username")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<UsuarioDTO.Response>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", usuarioService.findByUsername(username)));
    }

    @GetMapping("/doc/{doc}")
    @Operation(summary = "Buscar usuario por documento")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<UsuarioDTO.Response>> findByDoc(@PathVariable String doc) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", usuarioService.findByDoc(doc)));
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioDTO.Response>>> findByRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios por rol", usuarioService.findByRol(rol)));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre parcial")
    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioDTO.Response>>> search(@RequestParam String nombre) {
        return ResponseEntity.ok(ApiResponse.ok("Búsqueda por nombre", usuarioService.searchByName(nombre)));
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioDTO.Response>> create(@Valid @RequestBody UsuarioDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado", usuarioService.save(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", usuarioService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado"));
    }
}
