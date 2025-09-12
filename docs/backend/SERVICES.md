# SERVICES.md - Capa de Servicios (Backend)

Este documento describe los contratos (interfaces) de la capa de servicios del backend del Sistema de Gestión Logística (AstroVia). Define las responsabilidades, métodos principales, DTOs utilizados y consideraciones de negocio para cada agregado.

> Nota: Los sufijos `Request` y `Response` corresponden a records contenidos dentro de cada `*DTO` agrupador. Ej: `SucursalDTO.Request` / `SucursalDTO.Response`.

## Principios de Diseño

- **Segregación de Responsabilidades**: Cada servicio se enfoca en un agregado / subdominio (Auth, Usuario, Envío, etc.).
- **DTO Records Inmutables**: Los datos de entrada/salida viajan encapsulados en records para claridad y seguridad.
- **Idempotencia**: Las operaciones de lectura son side‑effect free. Las de escritura deben validar duplicidades (ej. username, código de envío) antes de persistir.
- **Validación**: Las constraints de los inputs se definen a nivel de DTO (Bean Validation). La implementación puede reforzar reglas de negocio adicionales.
- **Manejabilidad**: Métodos devuelven respuestas específicas, evitando uso excesivo de genéricos salvo en contenedores como `Page<>`.

---

## 1. AuthService

`package com.astrovia.service`

Responsable de autenticación, emisión, validación y renovación de tokens.

| Método                              | Propósito                              | Consideraciones                                                                                |
| ----------------------------------- | -------------------------------------- | ---------------------------------------------------------------------------------------------- |
| `login(LoginRequest)`               | Autenticar credenciales y emitir token | Debe registrar intentos fallidos, hashing seguro de password, bloqueo tras N intentos (futuro) |
| `logout(String token)`              | Invalidar token activo                 | Estrategia: blacklist en cache / invalidación de refresh token                                 |
| `validateToken(String token)`       | Validar firma, expiración y estado     | Puede incluir verificación de revocación / roles                                               |
| `refreshToken(String refreshToken)` | Emitir nuevo access token              | Debe validar integridad y fecha de expiración del refresh                                      |

DTOs: `AuthDTO.LoginRequest`, `AuthDTO.LoginResponse`.

Errores típicos: credenciales inválidas, token expirado, refresh inválido.

---

## 2. SucursalService

Gestiona ciclo de vida de sucursales.

| Método                  | Propósito            | Reglas de negocio                                                        |
| ----------------------- | -------------------- | ------------------------------------------------------------------------ |
| `findAll()`             | Listar todas         | Puede cachearse                                                          |
| `findById(Long)`        | Obtener una sucursal | 404 si no existe                                                         |
| `save(Request)`         | Crear sucursal       | Validar unicidad (nombre + ciudad)                                       |
| `update(Long, Request)` | Actualizar datos     | Validar existencia previa                                                |
| `deleteById(Long)`      | Eliminar             | Evitar borrar si tiene envíos asociados (soft delete recomendado futuro) |
| `findByCiudad(String)`  | Filtrar por ciudad   | Case-insensitive sugerido                                                |

DTOs: `SucursalDTO.Request`, `SucursalDTO.Response`.

---

## 3. UsuarioService

Unifica usuarios del sistema (clientes, operadores, admins).

| Método                     | Propósito                        | Reglas de negocio                                     |
| -------------------------- | -------------------------------- | ----------------------------------------------------- |
| `findAll(Pageable)`        | Paginación general               | Filtros futuros (rol/activo)                          |
| `findById(Long)`           | Detalle usuario                  | 404 si no existe                                      |
| `findByUsername(String)`   | Búsqueda exacta                  | Index único en DB                                     |
| `findByDoc(String)`        | Búsqueda por documento (cliente) | Solo clientes; doc puede ser null para otros roles    |
| `findByRol(Rol)`           | Filtrar por rol                  | Resultado no paginado                                 |
| `save(Request)`            | Crear usuario                    | Hash de password; validar unicidad username/doc/email |
| `update(Long, Request)`    | Modificar usuario                | Password: re-hash si cambia                           |
| `deleteById(Long)`         | Baja lógica recomendada          | Evitar cascadas indeseadas                            |
| `searchByName(String)`     | Búsqueda parcial por nombres     | Usar LIKE/ILIKE                                       |
| `existsByUsername(String)` | Verificación existencia          | Para validaciones frontend                            |
| `existsByDoc(String)`      | Verificación existencia doc      | Solo clientes                                         |

DTOs: `UsuarioDTO.Request`, `UsuarioDTO.Response`, plus enum `Rol`.

Seguridad: En respuesta nunca exponer password hash.

---

## 4. EnvioService

Gestión integral de envíos, su estado y estadísticas.

| Método                              | Propósito            | Reglas / Notas                                                    |
| ----------------------------------- | -------------------- | ----------------------------------------------------------------- |
| `findAll(Pageable)`                 | Listar envíos        | Paginado; filtros futuros por estado/fecha                        |
| `findById(Long)`                    | Obtener detalle      | Incluye paquetes y último tracking                                |
| `findByCodigo(String)`              | Búsqueda pública     | Código único obligatorio                                          |
| `save(Request)`                     | Crear envío          | Generar código; estado inicial CREADO; registrar tracking inicial |
| `update(Long, Request)`             | Actualización básica | Restricciones si ya no está en CREADO (negocio)                   |
| `deleteById(Long)`                  | Eliminar             | Evitar si ya avanzado; preferible cancelación                     |
| `findByClienteId(Long)`             | Envíos de cliente    | Seguridad: cliente solo ve los suyos                              |
| `findByEstado(EstadoEnvio)`         | Filtrar por estado   | Index recomendado en DB                                           |
| `updateEstado(Long, EstadoRequest)` | Cambiar estado       | Validar transición válida + tracking automático                   |
| `generateCodigo()`                  | Generar código único | Formato sugerido: PREFIJO + epoch + random base36                 |
| `getEstadisticasEstados()`          | Conteo por estado    | Para dashboard                                                    |

DTOs: `EnvioDTO.Request`, `EnvioDTO.Response`, `EnvioDTO.EstadoRequest`. Enum: `EstadoEnvio`.

Transiciones sugeridas: CREADO -> RECOLECTADO -> EN_TRANSITO -> EN_DESTINO -> ENTREGADO (o DEVUELTO/CANCELADO). Validar orden.

---

## 5. PaqueteService

Maneja paquetes asociados a un envío.

| Método                | Propósito                   | Reglas                                            |
| --------------------- | --------------------------- | ------------------------------------------------- |
| `findByEnvioId(Long)` | Listar paquetes de un envío | Validar pertenencia                               |
| `save(Request)`       | Agregar paquete             | No permitir si envío ENTREGADO/CANCELADO/DEVUELTO |
| `deleteById(Long)`    | Borrar paquete              | Recalcular peso/valor total (futuro)              |

DTOs: `PaqueteDTO.Request`, `PaqueteDTO.Response`.

---

## 6. TrackingService

Registra y consulta eventos del tracking de envíos.

| Método                    | Propósito          | Reglas                                        |
| ------------------------- | ------------------ | --------------------------------------------- |
| `findByEnvioId(Long)`     | Historial completo | Orden cronológico ascendente                  |
| `save(Request)`           | Registrar evento   | Asociar usuario (operador) y timestamp actual |
| `getUltimoTracking(Long)` | Último evento      | Cache corto (futuro)                          |

DTOs: `TrackingDTO.Request`, `TrackingDTO.Response`.

---

## Consideraciones Comunes de Implementación

### Manejo de Excepciones

- Lanzar excepciones específicas (e.g. `EntityNotFoundException`, `BusinessRuleException`).
- Traducir a respuestas HTTP coherentes en capa REST (Controller Advice).

### Transaccionalidad

- Métodos de escritura anotados con `@Transactional`.
- Lecturas intensivas pueden usar `@Transactional(readOnly = true)`.

### Mapeo Entity <-> DTO

- Usar Mapper dedicado (MapStruct recomendado) o servicios conversores.
- Evitar exponer entidades JPA directamente.

### Seguridad

- Verificar roles antes de operaciones sensibles.
- Endpoints públicos: tracking por código.

### Performance

- Paginación obligatoria en listados grandes (`Envio`, `Usuario`).
- Uso de `fetch join` o proyecciones para evitar N+1.

### Próximas Extensiones

- Eventos de dominio para cambios de estado de envío (publicar notificaciones).
- Capa de caché (estadísticas, catálogos de sucursales).
- Auditoría (quién creó / modificó registros).

---

## Resumen

Estas interfaces proveen un contrato claro para la lógica de negocio, separando responsabilidades por agregado y facilitando test unitario y evolución. Las implementaciones deberán reforzar reglas de transición, validación cruzada y seguridad.
