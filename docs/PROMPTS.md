# PROMPTS.md - Prompts para Desarrollo

## Flujo de Desarrollo Backend (Spring Boot + PostgreSQL)

### 1. PROMPT - ENTITIES

```
Necesito crear las entidades JPA para un sistema de logística con Spring Boot y PostgreSQL.

CONTEXTO DEL PROYECTO:
- Sistema de gestión logística para envíos
- Base de datos PostgreSQL
- Spring Boot 3.x con JPA

ESPECIFICACIONES:
Crear las siguientes entidades JPA con sus relaciones:

1. **Usuario**
   - id (Long, PK, auto-generated)
   - username (String, unique, 3-50 chars)
   - password (String, min 6 chars)
   - nombres (String, max 100 chars)
   - email (String, valid email)
   - doc (String, max 20 chars) - Para usuarios con rol CLIENTE
   - telefono (String, max 20 chars)
   - direccion (String, max 500 chars)
   - rol (Enum: ADMIN, OPERADOR, CLIENTE)
   - activo (Boolean, default true)
   - fechaCreacion (LocalDateTime)

2. **Sucursal**
   - id (Long, PK, auto-generated)
   - nombre (String, max 100 chars)
   - ciudad (String, max 50 chars)
   - direccion (String, max 200 chars)
   - telefono (String, max 20 chars)
   - fechaCreacion (LocalDateTime)

3. **Envio**
   - id (Long, PK, auto-generated)
   - codigo (String, unique, auto-generated)
   - cliente (ManyToOne con Usuario - rol CLIENTE)
   - sucursalOrigen (ManyToOne con Sucursal)
   - sucursalDestino (ManyToOne con Sucursal)
   - peso (BigDecimal, min 0.01)
   - estado (Enum: CREADO, RECOLECTADO, EN_TRANSITO, EN_DESTINO, ENTREGADO, DEVUELTO, CANCELADO)
   - precio (BigDecimal, min 0.00)
   - fechaCreacion (LocalDateTime)
   - fechaEstimadaEntrega (LocalDateTime)
   - observaciones (String, max 1000 chars)

4. **Paquete**
   - id (Long, PK, auto-generated)
   - envio (ManyToOne con Envio)
   - descripcion (String, max 200 chars)
   - valorDeclarado (BigDecimal, min 0.00)
   - peso (BigDecimal, min 0.01)
   - dimensiones (String, max 50 chars)

5. **Tracking**
   - id (Long, PK, auto-generated)
   - envio (ManyToOne con Envio)
   - fechaHora (LocalDateTime, auto-set)
   - ubicacion (String, max 100 chars)
   - evento (String, max 100 chars)
   - usuario (ManyToOne con Usuario) - Operador que registra
   - observaciones (String, max 1000 chars)

REQUERIMIENTOS:
- Usar anotaciones JPA apropiadas
- Configurar relaciones bidireccionales donde sea necesario
- Incluir validaciones básicas con Bean Validation
- Usar @PrePersist para fechas de creación automáticas
- Configurar cascadas apropiadas
- Incluir constructors, getters, setters
- Implementar toString() excluyendo relaciones circulares

ADJUNTO: [Aquí adjuntarías los archivos del proyecto base si ya los tienes]
```

### 2. PROMPT - DTOS

```
Basándome en las entidades JPA creadas anteriormente, necesito generar los DTOs (Data Transfer Objects) para el sistema de logística.

CONTEXTO:
He creado las entidades: Usuario (unificando cliente y operadores), Sucursal, Envio, Paquete, Tracking.
Ahora necesito DTOs para separar la capa de presentación de las entidades.

ESPECIFICACIONES:
Crear DTOs organizados en paquetes request/ y response/:

**REQUEST DTOs (para recibir datos):**
1. **LoginRequest**
   - username, password

2. **SucursalRequest**
   - nombre, ciudad, direccion, telefono

3. **UsuarioRequest**
   - username, password, nombres, email, rol
   - doc, telefono, direccion (opcionales para clientes)

4. **EnvioRequest**
   - idCliente (Usuario con rol CLIENTE), idSucursalOrigen, idSucursalDestino, peso, observaciones

5. **PaqueteRequest**
   - idEnvio, descripcion, valorDeclarado, peso, dimensiones

6. **TrackingRequest**
   - idEnvio, ubicacion, evento, observaciones

7. **EstadoEnvioRequest**
   - estado, observaciones

**RESPONSE DTOs (para enviar datos):**
1. **LoginResponse**
   - token, tipoToken, usuario (sin password)

2. **SucursalResponse**
   - Todos los campos de Sucursal

3. **UsuarioResponse**
   - Todos los campos de Usuario (sin password)
   - Para clientes: cantidad de envíos

4. **EnvioResponse**
   - Todos los campos + sucursalOrigen, sucursalDestino, cliente (datos básicos del usuario)
   - Lista de paquetes
   - Último tracking

5. **PaqueteResponse**
   - Todos los campos de Paquete

6. **TrackingResponse**
   - Todos los campos + usuario (sin password)

7. **ApiResponse<T>**
   - success (boolean)
   - message (String)
   - data (T)
   - timestamp (LocalDateTime)

REQUERIMIENTOS:
- Usar Bean Validation (@NotNull, @NotBlank, @Email, etc.)
- Incluir constructores, getters, setters
- DTOs de response deben ser immutables cuando sea posible
- Documentar con JavaDoc los campos importantes
- Usar BigDecimal para valores monetarios
- Validar formato de email y teléfono donde aplique

ADJUNTO: [Archivos de entidades creadas en el paso anterior]
```

### 3. PROMPT - REPOSITORIES

```
Necesito crear los repositorios JPA para el sistema de logística basándome en las entidades ya creadas.

CONTEXTO:
Tengo las entidades: Usuario (unificado cliente/operador), Sucursal, Envio, Paquete, Tracking.
Necesito repositorios con consultas específicas para el negocio.

ESPECIFICACIONES:
Crear interfaces Repository que extiendan JpaRepository:

1. **UsuarioRepository**
   - findByUsername(String username)
   - existsByUsername(String username)
   - existsByEmail(String email)
   - findByDoc(String doc) // Para clientes
   - existsByDoc(String doc) // Para clientes
   - findByRol(Rol rol)
   - findByActivoTrue()
   - findByRolAndActivoTrue(Rol rol)
   - findByNombresContainingIgnoreCase(String nombres)

2. **SucursalRepository**
   - findByCiudad(String ciudad)
   - findByNombreContainingIgnoreCase(String nombre)
   - findAllByOrderByNombreAsc()

3. **EnvioRepository**
   - findByCodigo(String codigo)
   - existsByCodigo(String codigo)
   - findByClienteIdOrderByFechaCreacionDesc(Long clienteId) // Cliente es Usuario
   - findBySucursalOrigenIdOrSucursalDestinoId(Long origenId, Long destinoId)
   - findByEstado(EstadoEnvio estado)
   - findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin)
   - countByEstado(EstadoEnvio estado)
   - findTop10ByOrderByFechaCreacionDesc()

4. **PaqueteRepository**
   - findByEnvioId(Long envioId)
   - findByEnvioIdOrderByIdAsc(Long envioId)
   - countByEnvioId(Long envioId)
   - sumValorDeclaradoByEnvioId(Long envioId) // Query personalizada

5. **TrackingRepository**
   - findByEnvioIdOrderByFechaHoraDesc(Long envioId)
   - findByEnvioIdOrderByFechaHoraAsc(Long envioId)
   - findFirstByEnvioIdOrderByFechaHoraDesc(Long envioId) // Último tracking
   - findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin)
   - findByUbicacionContainingIgnoreCase(String ubicacion)

REQUERIMIENTOS:
- Usar @Repository annotation
- Implementar consultas personalizadas con @Query cuando sea necesario
- Incluir consultas nativas SQL donde sea más eficiente
- Documentar métodos complejos con JavaDoc
- Optimizar consultas para rendimiento
- Incluir paginación donde sea apropiado (Pageable parameter)

QUERIES PERSONALIZADAS NECESARIAS:
- Suma de valores declarados por envío
- Estadísticas de envíos por sucursal
- Envíos sin tracking en las últimas 24 horas
- Usuarios con más envíos en el último mes

Crea un .md en /arquitecture-back con la informacion de los repositories
ADJUNTO: [Entidades y DTOs creados en pasos anteriores]
```

### 4. PROMPT - SERVICES

```
Necesito crear las interfaces de servicio para el sistema de logística, definiendo los contratos de la lógica de negocio.

CONTEXTO:
Tengo creadas las entidades, DTOs y repositorios.
Ahora necesito las interfaces de servicio que definan la lógica de negocio.

ESPECIFICACIONES:
Crear interfaces de servicio para cada entidad principal:

1. **AuthService**
   - LoginResponse login(LoginRequest request)
   - void logout(String token)
   - boolean validateToken(String token)
   - LoginResponse refreshToken(String refreshToken)

2. **SucursalService**
   - List<SucursalResponse> findAll()
   - SucursalResponse findById(Long id)
   - SucursalResponse save(SucursalRequest request)
   - SucursalResponse update(Long id, SucursalRequest request)
   - void deleteById(Long id)
   - List<SucursalResponse> findByCiudad(String ciudad)

3. **UsuarioService**
   - Page<UsuarioResponse> findAll(Pageable pageable)
   - UsuarioResponse findById(Long id)
   - UsuarioResponse findByUsername(String username)
   - UsuarioResponse findByDoc(String doc) // Para clientes
   - List<UsuarioResponse> findByRol(Rol rol)
   - UsuarioResponse save(UsuarioRequest request)
   - UsuarioResponse update(Long id, UsuarioRequest request)
   - void deleteById(Long id)
   - List<UsuarioResponse> searchByName(String nombre)
   - boolean existsByUsername(String username)
   - boolean existsByDoc(String doc)

4. **EnvioService**
   - Page<EnvioResponse> findAll(Pageable pageable)
   - EnvioResponse findById(Long id)
   - EnvioResponse findByCodigo(String codigo)
   - EnvioResponse save(EnvioRequest request)
   - EnvioResponse update(Long id, EnvioRequest request)
   - void deleteById(Long id)
   - List<EnvioResponse> findByClienteId(Long clienteId)
   - List<EnvioResponse> findByEstado(EstadoEnvio estado)
   - EnvioResponse updateEstado(Long id, EstadoEnvioRequest request)
   - String generateCodigo()
   - Map<EstadoEnvio, Long> getEstadisticasEstados()

5. **PaqueteService**
   - List<PaqueteResponse> findByEnvioId(Long envioId)
   - PaqueteResponse save(PaqueteRequest request)
   - void deleteById(Long id)

6. **TrackingService**
   - List<TrackingResponse> findByEnvioId(Long envioId)
   - TrackingResponse save(TrackingRequest request)
   - TrackingResponse getUltimoTracking(Long envioId)
```

Crea un .md en /arquitecture-back con la informacion de los services

### 5. PROMPT - IMPLEMENTACIONES

```
Necesito implementar las interfaces de servicio definidas en los service.

CONTEXTO:
Tengo interfaces de servicio para Auth, Sucursal, Usuario, Envio, Paquete y Tracking.
Debo crear las clases `Impl` con la lógica de negocio.

ESPECIFICACIONES:
- Cada implementación debe anotarse con `@Service`
- Usar inyección de dependencias vía constructor
- Manejar validaciones (ej: si no existe un usuario → lanzar excepción personalizada)
- Mapear entidades ↔ DTOs usando ModelMapper
- Manejar transacciones con `@Transactional` donde aplique
- Usar repositorios creados previamente
- Generar códigos de envío únicos en `EnvioServiceImpl`
- Registrar automáticamente un evento de tracking inicial al crear un envío
- Manejar seguridad en AuthServiceImpl (JWT, passwords con BCrypt)
- Retornar DTOs en responses, no entidades
- Registrar logs de operaciones importantes con `@Slf4j`

REQUERIMIENTOS:
- Crear clases `AuthServiceImpl`, `SucursalServiceImpl`, `UsuarioServiceImpl`, `EnvioServiceImpl`, `PaqueteServiceImpl`, `TrackingServiceImpl`
- Documentar métodos con JavaDoc

Crea por ultimo, un .md en /docs/arquitecture-back con la informacion de los Impl
```

### 6. PROMPT - CONTROLLERS

```
Necesito crear los controladores REST del sistema de logística.

CONTEXTO:
Tengo services implementados. Ahora necesito controladores para exponer endpoints.

ESPECIFICACIONES:
- Anotar controladores con `@RestController`
- Base path: `/api`
- Usar `@RequestMapping` para agrupar endpoints por entidad
- Usar DTOs de request/response
- Manejar respuestas uniformes con `ApiResponse<T>`
- Proteger endpoints con roles según especificaciones:
  - ADMIN: acceso total
  - OPERADOR: sucursales, usuarios, envíos, tracking
  - CLIENTE: solo acceso a sus envíos
- Implementar paginación en listados
- Usar `ResponseEntity` para respuestas HTTP correctas
- Documentar endpoints con Swagger/OpenAPI

CONTROLADORES A CREAR:
1. **AuthController** → /api/auth
2. **SucursalController** → /api/sucursales
3. **UsuarioController** → /api/usuarios
4. **EnvioController** → /api/envios
5. **PaqueteController** → /api/paquetes
6. **TrackingController** → /api/tracking

Crea por ultimo, un .md en /docs/arquitecture-back con la informacion de los CONTROLLERS.md
```

### 7. PROMPT - CONFIGURACION

```
Necesito configurar seguridad y utilidades del sistema.

CONTEXTO:
El sistema usa Spring Security con JWT, CORS y Swagger.

ESPECIFICACIONES:
1. **SecurityConfig**
   - Autenticación vía JWT
   - Configurar `UsernamePasswordAuthenticationFilter`
   - Roles: ADMIN, OPERADOR, CLIENTE
   - Proteger rutas según rol
   - Permitir acceso público a `/api/auth/**` y `/api/tracking/public/**`

2. **JwtTokenProvider**
   - Generar y validar tokens JWT
   - Manejar expiración y refresh
   - Usar secret de application.yml

3. **PasswordEncoderConfig**
   - Usar `BCryptPasswordEncoder`

4. **SwaggerConfig**
   - Documentar API
   - Configurar seguridad para endpoints protegidos

5. **CorsConfig**
   - Configurar CORS dinámico (origins en properties)

REQUERIMIENTOS:
- Clases en paquete `config`
- Anotar con `@Configuration`
- Usar `@Bean` donde aplique
- Documentar métodos principales

Crea por ultimo, un .md en /docs/arquitecture-back con la informacion de los CONFIGs.MD
```

### 8. PROMPT - EXCEPCIONES

```
Necesito crear un manejo global de excepciones para el sistema.

CONTEXTO:
El sistema debe devolver errores consistentes en formato JSON.

ESPECIFICACIONES:
1. **Excepciones personalizadas**
   - ResourceNotFoundException
   - BadRequestException
   - UnauthorizedException
   - BusinessException

2. **GlobalExceptionHandler**
   - Anotar con `@ControllerAdvice`
   - Manejar excepciones personalizadas y genéricas
   - Retornar ApiResponse con:
     - success = false
     - message = descripción del error
     - timestamp = fecha/hora
     - status = código HTTP
   - Loggear errores

REQUERIMIENTOS:
- Paquete `exception`
- Uso de `@ExceptionHandler`
- Documentar cada excepción

Crea por ultimo, un .md en /docs/arquitecture-back con la informacion de los EXCEPTIONS.md
```

### 9. PROMPT - TESTING

```
Necesito crear pruebas unitarias e integrales para el sistema.

CONTEXTO:
El backend usa Spring Boot, JUnit 5, Mockito y Testcontainers.

ESPECIFICACIONES:
1. **Unit Tests**
   - Probar servicios con Mockito
   - Validar lógica de negocio (ej: generación de código único en EnvioService)
   - Mockear repositorios

2. **Integration Tests**
   - Usar Testcontainers para PostgreSQL
   - Probar endpoints con `@SpringBootTest` y `MockMvc`
   - Validar seguridad (JWT requerido)

3. **Repository Tests**
   - Probar consultas personalizadas
   - Usar H2 en memoria
   - Validar integridad de datos

REQUERIMIENTOS:
- Paquete `test`
- Nombrar clases de test con sufijo `Test`
- Cobertura mínima recomendada: 80%
- Generar datos de prueba con `@BeforeEach`
- Validar casos de error en servicios y controladores

```

### 10. PROMPT - FRONTEND

```
Necesito crear el frontend del sistema de logística usando React 18+, Vite y TypeScript.

CONTEXTO:
El backend ya está listo con endpoints REST y seguridad JWT.
El frontend debe ser moderno, elegante y relacionado al mundo de envíos/logística.

DISEÑO:
- Tema visual inspirado en logística moderna:
  - Colores elegantes: Azul oscuro (#1E3A8A), gris neutro (#374151), acentos en verde (#10B981) y naranja (#F97316).
  - Tipografía: "Inter" para legibilidad.
  - Estilo: Minimalista, con tarjetas, tablas limpias y gráficos.
- Componentes clave:
  - Dashboard con métricas de envíos (cards y gráficos).
  - Gestión de sucursales, usuarios, envíos y tracking.
  - Página pública de tracking con código de envío.
- Layout:
  - Sidebar con navegación
  - Header con logo y usuario
  - Footer discreto
- UI:
  - Uso de Tailwind CSS + shadcn/ui
  - Iconos con lucide-react
  - Feedback visual con react-hot-toast

ESPECIFICACIONES:
- Autenticación:
  - Página de Login
  - Contexto global de Auth
  - Guardado de tokens JWT en localStorage
- Rutas protegidas según rol:
  - ADMIN: acceso completo
  - OPERADOR: sucursales, usuarios, envíos, tracking
  - CLIENTE: solo sus envíos
- Funcionalidades:
  - CRUD sucursales, usuarios, envíos, paquetes
  - Tracking con timeline visual
  - Dashboard con estadísticas (gráficos con recharts)
- Arquitectura:
  - src/
    - components/ (UI reusables: forms, tables, modals)
    - pages/ (Dashboard, Sucursales, Usuarios, Envios, Tracking, Login)
    - hooks/ (useAuth, useApi)
    - context/ (AuthContext)
    - services/ (axios client)
    - router/ (configuración de rutas)
  - Configurar proxy hacia backend en vite.config.ts

REQUERIMIENTOS:
- Manejar formularios con react-hook-form + yup
- Llamadas HTTP con axios
- Manejo de cache con react-query
- Validaciones en frontend y backend
- Testing con Vitest y React Testing Library
- Documentar componentes principales

```

### 11. PROMPT - DEPLOYMENT Y CI/CD

```
Necesito preparar el despliegue del sistema en Railway usando Docker y configurar CI/CD.

CONTEXTO:
El proyecto tiene backend (Spring Boot) y frontend (Vite + React).
Se debe usar Railway para hosting.

ESPECIFICACIONES:
1. **Docker Backend**
   - Crear Dockerfile basado en OpenJDK 17
   - Empaquetar aplicación Spring Boot en un jar
   - Exponer puerto 8080
   - Usar variables de entorno Railway para DB y JWT

2. **Docker Frontend**
   - Multi-stage build con Node 18-alpine
   - Compilar proyecto con `npm run build`
   - Servir con Nginx
   - Exponer puerto 80

3. **Docker Compose (opcional local)**
   - Incluir postgres, backend, frontend
   - Variables en `.env`

4. **Railway Config**
   - Crear servicio PostgreSQL en Railway
   - Desplegar backend y frontend como servicios independientes
   - Configurar variables:
     - DATABASE_URL
     - DB_USERNAME
     - DB_PASSWORD
     - JWT_SECRET
     - SPRING_PROFILES_ACTIVE=prod
     - VITE_API_URL

5. **CI/CD**
   - Usar GitHub Actions
   - Workflow:
     - Ejecutar tests backend (mvn test)
     - Ejecutar tests frontend (npm test)
     - Build imágenes Docker
     - Push a Railway

REQUERIMIENTOS:
- Crear archivos: `Dockerfile.backend`, `Dockerfile.frontend`, `docker-compose.yml`
- Crear `.github/workflows/deploy.yml`
- Documentar pasos de deployment en README

```
