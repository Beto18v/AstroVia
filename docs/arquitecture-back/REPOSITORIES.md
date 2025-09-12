# Repositorios JPA - AstroVia

Este documento describe los repositorios JPA implementados para el sistema de gestión logística, sus métodos principales, consultas personalizadas y ejemplos de uso.

## Índice
- [UsuarioRepository](#usuariorepository)
- [SucursalRepository](#sucursalrepository)
- [EnvioRepository](#enviorepository)
- [PaqueteRepository](#paqueterepository)
- [TrackingRepository](#trackingrepository)
- [Consultas Personalizadas](#consultas-personalizadas)
- [Ejemplo de Servicio](#ejemplo-de-servicio)

---

## UsuarioRepository
**Paquete:** `com.astrovia.repository`

Gestión de usuarios (clientes, operadores, administradores).

### Métodos principales
- `findByUsername(String username)`
- `existsByUsername(String username)`
- `existsByEmail(String email)`
- `findByDoc(String doc)`
- `existsByDoc(String doc)`
- `findByRol(Rol rol)`
- `findByActivoTrue()`
- `findByRolAndActivoTrue(Rol rol)`
- `findByNombresContainingIgnoreCase(String nombres)`
- Versiones paginadas de los anteriores
- `searchActivosByNombres(String texto, Pageable pageable)` (query personalizada)

### Ejemplo de uso
```java
usuarioRepository.findByUsername("admin");
usuarioRepository.findByActivoTrue(PageRequest.of(0, 20));
usuarioRepository.searchActivosByNombres("juan", PageRequest.of(0, 10));
```

---

## SucursalRepository
**Paquete:** `com.astrovia.repository`

Gestión de sucursales.

### Métodos principales
- `findByCiudad(String ciudad)`
- `findByNombreContainingIgnoreCase(String nombre)`
- `findAllByOrderByNombreAsc()`
- Versiones paginadas de los anteriores

### Ejemplo de uso
```java
sucursalRepository.findAllByOrderByNombreAsc(PageRequest.of(0, 50));
```

---

## EnvioRepository
**Paquete:** `com.astrovia.repository`

Gestión de envíos.

### Métodos principales
- `findByCodigo(String codigo)`
- `existsByCodigo(String codigo)`
- `findByClienteIdOrderByFechaCreacionDesc(Long clienteId)`
- `findBySucursalOrigenIdOrSucursalDestinoId(Long origenId, Long destinoId)`
- `findByEstado(EstadoEnvio estado)`
- `findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin)`
- `countByEstado(EstadoEnvio estado)`
- `findTop10ByOrderByFechaCreacionDesc()`
- Versiones paginadas de los anteriores

### Consultas personalizadas
- `findEnviosSinTracking24h()` y versión paginada
- `obtenerEstadisticasEnviosPorSucursal()` → proyección `SucursalEnvioStatsProjection`
- `topUsuariosUltimoMes()` y versión paginada → proyección `UsuarioTopEnviosProjection`

### Ejemplo de uso
```java
List<Envio> sinTracking = envioRepository.findEnviosSinTracking24h();
List<EnvioRepository.SucursalEnvioStatsProjection> stats = envioRepository.obtenerEstadisticasEnviosPorSucursal();
Page<EnvioRepository.UsuarioTopEnviosProjection> top = envioRepository.topUsuariosUltimoMes(PageRequest.of(0, 5));
```

---

## PaqueteRepository
**Paquete:** `com.astrovia.repository`

Gestión de paquetes.

### Métodos principales
- `findByEnvioId(Long envioId)`
- `findByEnvioIdOrderByIdAsc(Long envioId)`
- `countByEnvioId(Long envioId)`
- `sumValorDeclaradoByEnvioId(Long envioId)` (query personalizada)
- Versión paginada de `findByEnvioId`

### Ejemplo de uso
```java
BigDecimal totalDeclarado = paqueteRepository.sumValorDeclaradoByEnvioId(envioId);
```

---

## TrackingRepository
**Paquete:** `com.astrovia.repository`

Gestión de eventos de tracking.

### Métodos principales
- `findByEnvioIdOrderByFechaHoraDesc(Long envioId)`
- `findByEnvioIdOrderByFechaHoraAsc(Long envioId)`
- `findFirstByEnvioIdOrderByFechaHoraDesc(Long envioId)`
- `findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin)`
- `findByUbicacionContainingIgnoreCase(String ubicacion)`
- Versiones paginadas de los anteriores
- `findUltimoEventoByEnvioId(Long envioId)` (query nativa optimizada)

### Ejemplo de uso
```java
Tracking ultimo = trackingRepository.findUltimoEventoByEnvioId(envioId);
```

---

## Consultas Personalizadas
1. **Suma de valores declarados por envío:**
   - `PaqueteRepository.sumValorDeclaradoByEnvioId(Long envioId)`
2. **Estadísticas de envíos por sucursal:**
   - `EnvioRepository.obtenerEstadisticasEnviosPorSucursal()`
3. **Envíos sin tracking en las últimas 24 horas:**
   - `EnvioRepository.findEnviosSinTracking24h()`
4. **Usuarios con más envíos en el último mes:**
   - `EnvioRepository.topUsuariosUltimoMes(Pageable pageable)`

---

## Ejemplo de Servicio
```java
@Service
public class ReporteEnvioService {
    private final EnvioRepository envioRepository;
    private final PaqueteRepository paqueteRepository;

    public ReporteEnvioService(EnvioRepository envioRepository, PaqueteRepository paqueteRepository) {
        this.envioRepository = envioRepository;
        this.paqueteRepository = paqueteRepository;
    }

    public List<EnvioRepository.SucursalEnvioStatsProjection> estadisticasSucursales() {
        return envioRepository.obtenerEstadisticasEnviosPorSucursal();
    }

    public Page<EnvioRepository.UsuarioTopEnviosProjection> rankingUsuarios(Pageable pageable) {
        return envioRepository.topUsuariosUltimoMes(pageable);
    }

    public List<Envio> enviosSinTracking24h() {
        return envioRepository.findEnviosSinTracking24h();
    }
}
```

---

## Recomendaciones
- Usar paginación en consultas de alto volumen.
- Aprovechar proyecciones para reportes y estadísticas.
- Validar los métodos en tests unitarios e integración.
- Optimizar índices en base de datos para los campos más consultados.

---

¿Dudas o necesitas ejemplos de tests para estos repositorios? Solicítalo y se agregan.
