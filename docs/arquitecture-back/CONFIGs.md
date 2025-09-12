# CONFIGs.md - Configuración Backend AstroVia

Este documento describe las clases de configuración clave del backend: seguridad, JWT, CORS, OpenAPI y beans generales.

## Índice

1. SecurityConfig
2. JwtTokenProvider
3. JwtAuthenticationFilter
4. CorsConfig
5. OpenApiConfig
6. AppBeansConfig (PasswordEncoder / ModelMapper)
7. Propiedades en `application.properties`
8. Flujo de Autenticación

---

## 1. SecurityConfig

Ubicación: `com.astrovia.config.SecurityConfig`

Responsabilidades:

- Define `SecurityFilterChain` con sesión stateless
- Registra el `JwtAuthenticationFilter`
- Expone `UserDetailsService` basado en entidad `Usuario`
- Expone `AuthenticationProvider` personalizado
- Define endpoints públicos y protegidos

Endpoints públicos:

- `/api/auth/**`
- `/api/tracking/public/**` (reservado para tracking anónimo futuro)
- Swagger: `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
- Recursos estáticos básicos

Cualquier otro endpoint requiere JWT válido.

## 2. JwtTokenProvider

Ubicación: `com.astrovia.config.JwtTokenProvider`

Responsabilidades:

- Generar tokens de acceso y refresh (mismo algoritmo HS256)
- Validar firma y expiración
- Extraer claims y subject (username)

Propiedades utilizadas:

```
app.jwt.secret= <Base64Key>
app.jwt.expiration-seconds=3600
app.jwt.refresh-expiration-seconds=86400
```

Consideraciones de seguridad:

- La clave debe ser de al menos 256 bits antes de Base64 para HS256
- Rotación de claves recomendada en producción
- Refresh tokens deberían almacenarse y revocarse (futuro)

## 3. JwtAuthenticationFilter

Ubicación: `com.astrovia.config.JwtAuthenticationFilter`

Responsabilidades:

- Interceptar cada request
- Extraer header `Authorization: Bearer <token>`
- Validar token vía `JwtTokenProvider`
- Construir `UsernamePasswordAuthenticationToken` con autoridad `ROLE_<ROL>`

Si el token es inválido, la request continúa sin autenticación y caerá en 401 cuando el endpoint lo requiera.

## 4. CorsConfig

Ubicación: `com.astrovia.config.CorsConfig`

Propiedad dinámica:

```
app.cors.allowed-origins=http://localhost:4200,https://app.astrovia.com
```

Características:

- Métodos permitidos: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Headers permitidos: Authorization, Content-Type, Accept
- Credenciales permitidas
- Tiempo de cache de preflight: 3600s

## 5. OpenApiConfig

Ubicación: `com.astrovia.config.OpenApiConfig`

Responsabilidades:

- Agrupa documentación para paths `/api/**`
- Define esquema de seguridad `bearerAuth` para UI (vía anotaciones)

Acceso a Swagger UI:

```
/swagger-ui.html
/swagger-ui/index.html
/v3/api-docs
```

Autorización en Swagger:

1. Click botón Authorize
2. Ingresar: `Bearer <token>`

## 6. AppBeansConfig

Ubicación: `com.astrovia.config.AppBeansConfig`

Provee:

- `ModelMapper` con estrategia STANDARD y `skipNullEnabled`
- `PasswordEncoder` -> `BCryptPasswordEncoder`

BCrypt selecciona automáticamente un salt y factor de costo.

## 7. Propiedades Clave (`application.properties`)

```
# JWT
app.jwt.secret=ZmFrZVNlY3JldEtleUZvckFzdHJvVmlhMTIzNDU2Nzg5MDEyMzQ=
app.jwt.expiration-seconds=3600
app.jwt.refresh-expiration-seconds=86400

# CORS
a pp.cors.allowed-origins=http://localhost:4200
```

Recomendado: mover secretos a variables de entorno en producción.

## 8. Flujo de Autenticación

```
Login (POST /api/auth/login)
  -> Valida credenciales
  -> Genera JWT con claim rol
  -> Devuelve { token, tokenType, usuarioBasic }

Request protegida
  -> Cliente envía Authorization: Bearer <token>
  -> Filtro valida y setea SecurityContext
  -> @PreAuthorize evalúa rol
```

Roles soportados: `ADMIN`, `OPERADOR`, `CLIENTE`

## 9. Próximas Mejores Prácticas (Futuro)

- Lista negra persistente para logout real
- Refresh tokens dedicados y rotación
- Auditoría de accesos
- Rate limiting por IP/usuario
- Configuración CSP y headers de seguridad avanzados

---

Si necesitas extender o adaptar alguna configuración (multi-tenant, OAuth2, etc.), documenta cambios aquí para mantener consistencia.
