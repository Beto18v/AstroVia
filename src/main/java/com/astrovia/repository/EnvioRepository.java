package com.astrovia.repository;

import com.astrovia.entity.Envio;
import com.astrovia.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de envíos.
 */
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Envio> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    List<Envio> findBySucursalOrigenIdOrSucursalDestinoId(Long origenId, Long destinoId);

    List<Envio> findByEstado(EstadoEnvio estado);

    List<Envio> findByEstadoOrderByFechaCreacionDesc(EstadoEnvio estado);

    List<Envio> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEstado(EstadoEnvio estado);

    List<Envio> findTop10ByOrderByFechaCreacionDesc();

    // Versiones paginadas
    Page<Envio> findByClienteIdOrderByFechaCreacionDesc(Long clienteId, Pageable pageable);
    Page<Envio> findBySucursalOrigenIdOrSucursalDestinoId(Long origenId, Long destinoId, Pageable pageable);
    Page<Envio> findByEstado(EstadoEnvio estado, Pageable pageable);
    Page<Envio> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    /**
     * Obtiene envíos que no tienen eventos de tracking registrados en las últimas 24 horas.
     * Se utiliza LEFT JOIN para incluir envíos sin ningún tracking.
     * Native Query optimizada con subconsulta para último tracking.
     */
    @Query(value = """
            SELECT e.* FROM envio e
            LEFT JOIN (
                SELECT t.id_envio, MAX(t.fecha_hora) AS last_fecha
                FROM tracking t
                GROUP BY t.id_envio
            ) lt ON lt.id_envio = e.id
            WHERE (lt.last_fecha IS NULL OR lt.last_fecha < DATEADD('HOUR', -24, CURRENT_TIMESTAMP))
            """,
            nativeQuery = true)
    List<Envio> findEnviosSinTracking24h();

    /**
     * Versión paginada de envíos sin tracking 24h.
     */
    @Query(value = """
            SELECT e.* FROM envio e
            LEFT JOIN (
                SELECT t.id_envio, MAX(t.fecha_hora) AS last_fecha
                FROM tracking t
                GROUP BY t.id_envio
            ) lt ON lt.id_envio = e.id
            WHERE (lt.last_fecha IS NULL OR lt.last_fecha < DATEADD('HOUR', -24, CURRENT_TIMESTAMP))
            """,
            countQuery = """
            SELECT COUNT(*) FROM envio e
            LEFT JOIN (
                SELECT t.id_envio, MAX(t.fecha_hora) AS last_fecha
                FROM tracking t
                GROUP BY t.id_envio
            ) lt ON lt.id_envio = e.id
            WHERE (lt.last_fecha IS NULL OR lt.last_fecha < DATEADD('HOUR', -24, CURRENT_TIMESTAMP))
            """,
            nativeQuery = true)
    Page<Envio> findEnviosSinTracking24h(Pageable pageable);

    /**
     * Estadísticas de envíos por sucursal (origen y destino).
     * Devuelve el ID de la sucursal y la cantidad de envíos asociados como origen o destino.
     */
    @Query(value = """
            SELECT s.id AS sucursalId,
                   s.nombre AS nombre,
                   s.ciudad AS ciudad,
                   COALESCE(COUNT(DISTINCT e.id),0) AS totalEnvios
            FROM sucursal s
            LEFT JOIN envio e ON (e.id_origen = s.id OR e.id_destino = s.id)
            GROUP BY s.id, s.nombre, s.ciudad
            ORDER BY totalEnvios DESC
            """, nativeQuery = true)
    List<SucursalEnvioStatsProjection> obtenerEstadisticasEnviosPorSucursal();

    /**
     * Usuarios con más envíos en el último mes.
     * Se considera NOW() - INTERVAL '30 DAYS'. Retorna ranking descendente.
     */
    @Query(value = """
            SELECT u.id AS usuarioId,
                   u.username AS username,
                   u.nombres AS nombres,
                   COUNT(e.id) AS totalEnvios
            FROM usuario u
            JOIN envio e ON e.id_cliente = u.id
            WHERE e.fecha_creacion >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
            GROUP BY u.id, u.username, u.nombres
            ORDER BY totalEnvios DESC
            """,
            nativeQuery = true)
    List<UsuarioTopEnviosProjection> topUsuariosUltimoMes();

    /** Versión paginada de ranking de usuarios con más envíos último mes */
    @Query(value = """
            SELECT u.id AS usuarioId,
                   u.username AS username,
                   u.nombres AS nombres,
                   COUNT(e.id) AS totalEnvios
            FROM usuario u
            JOIN envio e ON e.id_cliente = u.id
            WHERE e.fecha_creacion >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
            GROUP BY u.id, u.username, u.nombres
            ORDER BY totalEnvios DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT u.id
                FROM usuario u
                JOIN envio e ON e.id_cliente = u.id
                WHERE e.fecha_creacion >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                GROUP BY u.id
            ) sub
            """,
            nativeQuery = true)
    Page<UsuarioTopEnviosProjection> topUsuariosUltimoMes(Pageable pageable);

    /**
     * Obtiene estadísticas agrupadas por estado de envío.
     */
    @Query("SELECT e.estado as estado, COUNT(e) as cantidad FROM Envio e GROUP BY e.estado")
    List<Object[]> countByEstadoGrouped();

    /** Proyección para estadísticas de sucursales */
    /**
     * Proyección para estadísticas de envíos por sucursal.
     */
    interface SucursalEnvioStatsProjection {
        Long getSucursalId();
        String getNombre();
        String getCiudad();
        Long getTotalEnvios();
    }

    /**
     * Proyección para ranking de usuarios con más envíos en un período.
     */
    interface UsuarioTopEnviosProjection {
        Long getUsuarioId();
        String getUsername();
        String getNombres();
        Long getTotalEnvios();
    }
}
