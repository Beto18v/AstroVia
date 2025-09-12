# Controllers API AstroVia

Base Path global: `/api`
Todas las respuestas (excepto errores automáticos de Spring) se envuelven en `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Texto descriptivo",
  "data": { ... },
  "timestamp": "2025-01-01T12:00:00"
}
```

## Seguridad y Roles

Roles definidos en `Rol`: `ADMIN`, `OPERADOR`, `CLIENTE`.

- `ADMIN`: Acceso total a todos los endpoints (CRUD completo).
- `OPERADOR`: Gestión de sucursales (solo lectura), usuarios (lectura), envíos (CRUD excepto delete), paquetes (CRUD limitado), tracking (crear y listar).
- `CLIENTE`: Solo acceso a sus propios envíos (filtro a nivel de servicio o expresión en @PreAuthorize donde aplique).

Autenticación vía JWT Bearer. Esquema documentado en Swagger como `bearerAuth`.

## AuthController `/api/auth`

| Método | Path      | Descripción                | Roles       | Request DTO            | Response DTO            |
| ------ | --------- | -------------------------- | ----------- | ---------------------- | ----------------------- |
| POST   | /login    | Autenticar y obtener token | Público     | `AuthDTO.LoginRequest` | `AuthDTO.LoginResponse` |
| POST   | /logout   | Invalidar token            | Autenticado | Header Auth            | `Void`                  |
| GET    | /validate | Validar token              | Autenticado | param `token`          | `Boolean`               |
| POST   | /refresh  | Refrescar token            | Autenticado | param `refreshToken`   | `AuthDTO.LoginResponse` |

## SucursalController `/api/sucursales`

| Método | Path             | Descripción         | Roles           | Request DTO           | Response DTO                 |
| ------ | ---------------- | ------------------- | --------------- | --------------------- | ---------------------------- |
| GET    | /                | Listar sucursales   | ADMIN, OPERADOR | -                     | `List<SucursalDTO.Response>` |
| GET    | /{id}            | Obtener sucursal    | ADMIN, OPERADOR | -                     | `SucursalDTO.Response`       |
| GET    | /ciudad/{ciudad} | Buscar por ciudad   | ADMIN, OPERADOR | -                     | `List<SucursalDTO.Response>` |
| POST   | /                | Crear sucursal      | ADMIN           | `SucursalDTO.Request` | `SucursalDTO.Response`       |
| PUT    | /{id}            | Actualizar sucursal | ADMIN           | `SucursalDTO.Request` | `SucursalDTO.Response`       |
| DELETE | /{id}            | Eliminar sucursal   | ADMIN           | -                     | Void                         |

## UsuarioController `/api/usuarios`

| Método | Path                 | Descripción               | Roles           | Request DTO          | Response DTO                |
| ------ | -------------------- | ------------------------- | --------------- | -------------------- | --------------------------- |
| GET    | /                    | Listar usuarios paginados | ADMIN, OPERADOR | params page,size     | `Page<UsuarioDTO.Response>` |
| GET    | /{id}                | Obtener usuario por id    | ADMIN, OPERADOR | -                    | `UsuarioDTO.Response`       |
| GET    | /username/{username} | Buscar por username       | ADMIN, OPERADOR | -                    | `UsuarioDTO.Response`       |
| GET    | /doc/{doc}           | Buscar por documento      | ADMIN, OPERADOR | -                    | `UsuarioDTO.Response`       |
| GET    | /rol/{rol}           | Usuarios por rol          | ADMIN, OPERADOR | -                    | `List<UsuarioDTO.Response>` |
| GET    | /search?nombre=      | Búsqueda por nombre       | ADMIN, OPERADOR | param nombre         | `List<UsuarioDTO.Response>` |
| POST   | /                    | Crear usuario             | ADMIN           | `UsuarioDTO.Request` | `UsuarioDTO.Response`       |
| PUT    | /{id}                | Actualizar usuario        | ADMIN           | `UsuarioDTO.Request` | `UsuarioDTO.Response`       |
| DELETE | /{id}                | Eliminar usuario          | ADMIN           | -                    | Void                        |

## EnvioController `/api/envios`

| Método | Path                  | Descripción             | Roles                           | Request DTO              | Response DTO              |
| ------ | --------------------- | ----------------------- | ------------------------------- | ------------------------ | ------------------------- |
| GET    | /                     | Listar envíos paginados | ADMIN, OPERADOR                 | params page,size         | `Page<EnvioDTO.Response>` |
| GET    | /{id}                 | Obtener envío por id    | ADMIN, OPERADOR                 | -                        | `EnvioDTO.Response`       |
| GET    | /codigo/{codigo}      | Obtener por código      | ADMIN, OPERADOR                 | -                        | `EnvioDTO.Response`       |
| GET    | /cliente/{clienteId}  | Envíos por cliente      | ADMIN, OPERADOR o CLIENTE dueño | -                        | `List<EnvioDTO.Response>` |
| GET    | /estado/{estado}      | Envíos por estado       | ADMIN, OPERADOR                 | -                        | `List<EnvioDTO.Response>` |
| POST   | /                     | Crear envío             | ADMIN, OPERADOR                 | `EnvioDTO.Request`       | `EnvioDTO.Response`       |
| PUT    | /{id}                 | Actualizar envío        | ADMIN, OPERADOR                 | `EnvioDTO.Request`       | `EnvioDTO.Response`       |
| PATCH  | /{id}/estado          | Actualizar estado       | ADMIN, OPERADOR                 | `EnvioDTO.EstadoRequest` | `EnvioDTO.Response`       |
| GET    | /estadisticas/estados | Estadísticas por estado | ADMIN, OPERADOR                 | -                        | `Map<EstadoEnvio,Long>`   |
| DELETE | /{id}                 | Eliminar envío          | ADMIN                           | -                        | Void                      |

## PaqueteController `/api/paquetes`

| Método | Path             | Descripción                 | Roles           | Request DTO          | Response DTO                |
| ------ | ---------------- | --------------------------- | --------------- | -------------------- | --------------------------- |
| GET    | /envio/{envioId} | Listar paquetes de un envío | ADMIN, OPERADOR | -                    | `List<PaqueteDTO.Response>` |
| POST   | /                | Crear paquete               | ADMIN, OPERADOR | `PaqueteDTO.Request` | `PaqueteDTO.Response`       |
| DELETE | /{id}            | Eliminar paquete            | ADMIN, OPERADOR | -                    | Void                        |

## TrackingController `/api/tracking`

| Método | Path                    | Descripción                 | Roles           | Request DTO           | Response DTO                 |
| ------ | ----------------------- | --------------------------- | --------------- | --------------------- | ---------------------------- |
| GET    | /envio/{envioId}        | Listar tracking de un envío | ADMIN, OPERADOR | -                     | `List<TrackingDTO.Response>` |
| GET    | /envio/{envioId}/ultimo | Último evento               | ADMIN, OPERADOR | -                     | `TrackingDTO.Response`       |
| POST   | /                       | Registrar tracking          | ADMIN, OPERADOR | `TrackingDTO.Request` | `TrackingDTO.Response`       |

## Notas de Implementación

- Paginación: se usa `PageRequest.of(page,size)` cuando aplica.
- Validación: DTOs anotados con `jakarta.validation`.
- Seguridad fina para clientes en `/envios/cliente/{clienteId}` requiere que el principal exponga `id` (UserDetails custom) o se aplique filtrado adicional en el servicio.
- Respuestas: siempre `ResponseEntity<ApiResponse<...>>` para consistencia.
- Swagger UI disponible típicamente en `/swagger-ui/index.html`.
