package com.astrovia.repository;

import com.astrovia.entity.Usuario;
import com.astrovia.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de usuarios (incluye clientes, operadores y administradores).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /**
     * Busca un usuario por documento (aplica principalmente a clientes).
     * @param doc Documento de identificación.
     * @return Optional con el usuario si existe.
     */
    Optional<Usuario> findByDoc(String doc);

    boolean existsByDoc(String doc);

    /**
     * Lista usuarios por rol específico.
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * Obtiene todos los usuarios activos.
     */
    List<Usuario> findByActivoTrue();

    /**
     * Lista usuarios activos filtrando por rol.
     */
    List<Usuario> findByRolAndActivoTrue(Rol rol);

    /**
     * Búsqueda por coincidencia parcial de nombres ignorando mayúsculas/minúsculas.
     */
    List<Usuario> findByNombresContainingIgnoreCase(String nombres);

    // Versiones paginadas para grandes volúmenes
    // Versiones paginadas para grandes volúmenes
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
    Page<Usuario> findByActivoTrue(Pageable pageable);
    Page<Usuario> findByRolAndActivoTrue(Rol rol, Pageable pageable);
    Page<Usuario> findByNombresContainingIgnoreCase(String nombres, Pageable pageable);

    /**
     * Recupera usuarios activos cuyo nombre contiene el texto indicado (case-insensitive).
     * Combina filtro de nombre y estado activo para búsquedas de clientes u operadores.
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND LOWER(u.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Usuario> searchActivosByNombres(@Param("texto") String texto, Pageable pageable);
}
