# Service Implementations

Este documento describe la lógica principal implementada en las clases `*ServiceImpl` de la capa de negocio.

## Principios Aplicados

- **Inyección por constructor** con Lombok `@RequiredArgsConstructor`.
- **Anotación `@Service`** para detección por Spring.
- **Transaccionalidad**: Métodos de lectura usan `@Transactional(readOnly = true)`, mutaciones usan `@Transactional` por defecto.
- **Mapeo DTO ↔ Entidad**: Se implementó manualmente para control fino (aunque se dispone de `ModelMapper`).
- **Validaciones** y reglas de negocio: Excepciones personalizadas `NotFoundException` y `BusinessException`.
- **Logs**: Operaciones críticas registradas con `@Slf4j`.
- **JWT**: Generación y validación básica en `AuthServiceImpl` usando `JwtUtil`.

## AuthServiceImpl

Responsable de autenticación básica y manejo simplificado de tokens.

### Funciones Clave

- `login`: Valida credenciales, genera JWT con claim `rol`.
- `logout`: Revoca token (en memoria). Extensible a Redis / DB.
- `validateToken`: Verifica firma, expiración y revocación.
- `refreshToken`: Renueva token (implementación simplificada).

### Reglas / Consideraciones

- Contraseña verificada con `PasswordEncoder` (BCrypt).
- Usuario debe estar activo.
- Se evita exponer el password en respuestas.

## UsuarioServiceImpl

Gestión de usuarios (incluye clientes y operadores).

### Funciones Clave

- CRUD completo.
- `findByUsername`, `findByDoc`, `findByRol`.
- `searchByName` para búsqueda parcial.
- Cifrado de contraseña solo en creación o cuando se pasa un nuevo password en actualización.

### Reglas

- Username único validado antes de creación.
- Excepciones si recurso no existe.
- Se expone conteo de envíos (`cantidadEnvios`).

## SucursalServiceImpl

Gestión de sucursales.

### Funciones Clave

- CRUD y búsqueda por ciudad.
- Mapeo directo de atributos.

### Reglas

- Excepción si la sucursal no existe al actualizar/eliminar.

## EnvioServiceImpl

Gestión integral de envíos.

### Funciones Clave

- Creación con generación de código único (`generateCodigo`).
- Cálculo de precio -> `peso * 10` (regla temporal simplificable).
- Establece fecha estimada de entrega: `now + 3 días`.
- Registra tracking inicial con evento `CREADO`.
- Cambio de estado -> agrega evento de tracking.
- Estadísticas por estado (`getEstadisticasEstados`).

### Reglas

- Valida existencia de cliente y sucursales.
- Genera nuevo código si no existe o al crear.
- Control de número de intentos al generar código (5). Lanza `BusinessException` si falla.

## PaqueteServiceImpl

Gestión de paquetes pertenecientes a un envío.

### Funciones Clave

- Agregar paquete a envío (`save`).
- Listado por envío en orden de inserción.
- Eliminación con validación de existencia.

### Reglas

- Debe existir el envío antes de asociar.

## TrackingServiceImpl

Registro y consulta de eventos de tracking.

### Funciones Clave

- Listado ordenado ascendente para construir timeline.
- Registro de nuevo evento (ubicación opcional, evento obligatorio).
- Recuperar último evento (`getUltimoTracking`).

### Reglas

- Excepción si el envío no existe al registrar evento.
- (Futuro) Asociar usuario autenticado desde contexto de seguridad.

## Excepciones

- `NotFoundException`: Recurso no encontrado.
- `BusinessException`: Violación de regla de negocio o estado inválido.

## Mejoras Futuras

- Integrar caché para búsquedas frecuentes.
- Añadir auditoría (who/when) con Spring Data JPA Auditing.
- Externalizar estrategia de cálculo de precio.
- Implementar tokens de refresh persistentes.
- Uso consistente de `ModelMapper` configurable con perfiles.
- Incorporar validaciones en cascada en DTOs compuestos.
