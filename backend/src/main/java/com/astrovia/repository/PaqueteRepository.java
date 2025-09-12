package com.astrovia.repository;

import com.astrovia.entity.Paquete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para gestión de paquetes.
 */
@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    List<Paquete> findByEnvioId(Long envioId);

    List<Paquete> findByEnvioIdOrderByIdAsc(Long envioId);

    long countByEnvioId(Long envioId);

    /**
     * Suma el valor declarado total de los paquetes asociados a un envío.
     * Usa COALESCE para devolver 0 en caso de que no existan registros.
     */
    @Query("SELECT COALESCE(SUM(p.valorDeclarado), 0) FROM Paquete p WHERE p.envio.id = :envioId")
    BigDecimal sumValorDeclaradoByEnvioId(@Param("envioId") Long envioId);

    // Paginación
    Page<Paquete> findByEnvioId(Long envioId, Pageable pageable);
}
