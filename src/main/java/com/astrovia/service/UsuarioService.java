package com.astrovia.service;

import com.astrovia.dto.UsuarioDTO;
import com.astrovia.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Lógica de negocio para Usuarios (incluye clientes y operadores/admins).
 * Especificación -> DTO:
 *  - UsuarioRequest  -> {@link UsuarioDTO.Request}
 *  - UsuarioResponse -> {@link UsuarioDTO.Response}
 */
public interface UsuarioService {

    Page<UsuarioDTO.Response> findAll(Pageable pageable);

    UsuarioDTO.Response findById(Long id);

    UsuarioDTO.Response findByUsername(String username);

    /** Busca usuario cliente por documento. */
    UsuarioDTO.Response findByDoc(String doc);

    List<UsuarioDTO.Response> findByRol(Rol rol);

    UsuarioDTO.Response save(UsuarioDTO.Request request);

    UsuarioDTO.Response update(Long id, UsuarioDTO.Request request);

    void deleteById(Long id);

    List<UsuarioDTO.Response> searchByName(String nombre);

    boolean existsByUsername(String username);

    boolean existsByDoc(String doc);
}
