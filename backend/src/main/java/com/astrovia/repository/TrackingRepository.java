package com.astrovia.repository;

import com.astrovia.entity.Tracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de eventos de tracking.
 */
@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    /**
     * Obtiene eventos de tracking de un envío ordenados descendentemente por fecha_hora.
     */
    List<Tracking> findByEnvioIdOrderByFechaHoraDesc(Long envioId);

    /**
     * Obtiene eventos de tracking de un envío ordenados ascendentemente por fecha_hora.
     */
    List<Tracking> findByEnvioIdOrderByFechaHoraAsc(Long envioId);

    /**
     * Recupera el último evento (más reciente) de un envío usando derivación de nombre.
     */
    Optional<Tracking> findFirstByEnvioIdOrderByFechaHoraDesc(Long envioId);

    /**
     * Eventos registrados entre un rango de fechas.
     */
    List<Tracking> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Búsqueda de eventos por coincidencia parcial en ubicación.
     */
    List<Tracking> findByUbicacionContainingIgnoreCase(String ubicacion);

    // Paginación
    Page<Tracking> findByEnvioIdOrderByFechaHoraDesc(Long envioId, Pageable pageable);
    Page<Tracking> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    Page<Tracking> findByUbicacionContainingIgnoreCase(String ubicacion, Pageable pageable);

    /**
     * Obtiene el último evento de tracking por envío usando consulta nativa optimizada.
     */
    @Query(value = """
            SELECT * FROM tracking t
            WHERE t.id_envio = :envioId
            ORDER BY t.fecha_hora DESC
            LIMIT 1
            """, nativeQuery = true)
    Tracking findUltimoEventoByEnvioId(@Param("envioId") Long envioId);
}
