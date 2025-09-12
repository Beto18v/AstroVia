package com.astrovia.repository;

import com.astrovia.entity.Sucursal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestión de sucursales.
 */
@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    List<Sucursal> findByCiudad(String ciudad);

    List<Sucursal> findByNombreContainingIgnoreCase(String nombre);

    List<Sucursal> findAllByOrderByNombreAsc();

    // Versiones paginadas
    Page<Sucursal> findByCiudad(String ciudad, Pageable pageable);
    Page<Sucursal> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Sucursal> findAllByOrderByNombreAsc(Pageable pageable);
}
