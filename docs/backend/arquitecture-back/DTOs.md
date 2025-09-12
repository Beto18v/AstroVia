# DTOs - Sistema AstroVía

## Resumen de DTOs Implementados

Se han creado DTOs (Data Transfer Objects) para separar la capa de presentación de las entidades JPA, siguiendo las mejores prácticas de arquitectura y Bean Validation. Cada archivo agrupa los DTOs relacionados con una entidad o funcionalidad, usando records internos para Request, Response y Basic.

---

## 📦 Estructura de Paquete

```
com.astrovia.dto
│
├── ApiResponse.java
├── AuthDTO.java
├── EnvioDTO.java
├── PaqueteDTO.java
├── SucursalDTO.java
├── TrackingDTO.java
└── UsuarioDTO.java
```

---

## 🗃️ DTOs por Archivo

### 1. `ApiResponse` (com.astrovia.dto)

**Propósito:** Wrapper genérico para respuestas estándar de la API.

**Campos:**

- `success` (boolean): Indica si la operación fue exitosa
- `message` (String): Mensaje descriptivo
- `data` (T): Payload genérico
- `timestamp` (LocalDateTime): Momento de generación

**Métodos de fábrica:**

- `ok(message)`
- `ok(message, data)`
- `error(message)`
- `error(message, data)`

---

### 2. `AuthDTO` (com.astrovia.dto)

**Propósito:** DTOs para autenticación/login.

**Records:**

- `LoginRequest`: username, password (Bean Validation)
- `LoginResponse`: token, tipoToken, usuario (UsuarioDTO.Basic)

---

### 3. `UsuarioDTO` (com.astrovia.dto)

**Propósito:** DTOs para usuario (unifica cliente, operador, admin).

**Records:**

- `Basic`: id, username, nombres, email, rol
- `Request`: username, password, nombres, email, doc, telefono, direccion, rol (Bean Validation)
- `Response`: id, username, nombres, email, doc, telefono, direccion, rol, activo, fechaCreacion, cantidadEnvios

**Validaciones:**

- Username único y obligatorio
- Email válido y obligatorio
- Contraseña mínimo 6 caracteres
- Nombres obligatorios
- Documento, teléfono y dirección opcionales (clientes)
- Rol obligatorio
- Formato de teléfono validado con regex

---

### 4. `SucursalDTO` (com.astrovia.dto)

**Propósito:** DTOs para sucursal.

**Records:**

- `Basic`: id, nombre, ciudad
- `Request`: nombre, ciudad, direccion, telefono (Bean Validation)
- `Response`: id, nombre, ciudad, direccion, telefono, fechaCreacion

**Validaciones:**

- Nombre y ciudad obligatorios
- Longitudes máximas definidas

---

### 5. `EnvioDTO` (com.astrovia.dto)

**Propósito:** DTOs para envío.

**Records:**

- `Request`: idCliente, idSucursalOrigen, idSucursalDestino, peso, observaciones (Bean Validation)
- `EstadoRequest`: estado, observaciones (Bean Validation)
- `Response`: id, codigo, cliente (UsuarioDTO.Basic), sucursalOrigen (SucursalDTO.Basic), sucursalDestino (SucursalDTO.Basic), peso, estado, precio, fechaCreacion, fechaEstimadaEntrega, observaciones, paquetes (List<PaqueteDTO.Response>), ultimoTracking (TrackingDTO.Response)

**Validaciones:**

- Peso mínimo 0.01
- Observaciones opcionales (max 1000)
- Estado obligatorio en cambios de estado

---

### 6. `PaqueteDTO` (com.astrovia.dto)

**Propósito:** DTOs para paquete.

**Records:**

- `Request`: idEnvio, descripcion, valorDeclarado, peso, dimensiones (Bean Validation)
- `Response`: id, descripcion, valorDeclarado, peso, dimensiones

**Validaciones:**

- Descripción obligatoria
- Peso mínimo 0.01
- Valor declarado no negativo
- Dimensiones opcionales (max 50)

---

### 7. `TrackingDTO` (com.astrovia.dto)

**Propósito:** DTOs para tracking de envíos.

**Records:**

- `Request`: idEnvio, ubicacion, evento, observaciones (Bean Validation)
- `Response`: id, fechaHora, ubicacion, evento, observaciones, usuario (UsuarioDTO.Basic)

**Validaciones:**

- Evento obligatorio
- Ubicación y observaciones opcionales

---

## 📝 Notas de Implementación

- Todos los DTOs usan Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@DecimalMin`, `@Pattern`).
- Los records son inmutables y facilitan el mapeo y la serialización.
- Los DTOs `Basic` permiten incrustar datos ligeros en respuestas compuestas.
- El diseño reduce la cantidad de archivos y mejora la navegación.
- Los nombres y tipos siguen la convención de las entidades JPA.
- Los DTOs están listos para usarse en controladores, servicios y mapeadores.

---

## Ejemplo de Uso en Controlador

```java
@PostMapping("/envios")
public ApiResponse<EnvioDTO.Response> crear(@Valid @RequestBody EnvioDTO.Request req) {
    Envio envio = envioService.crear(req);
    return ApiResponse.ok("Envío creado", toEnvioResponse(envio));
}
```

---

## Guía de Mapeo (Conceptual)

```java
UsuarioDTO.Basic toUsuarioBasic(Usuario u) { ... }
UsuarioDTO.Response toUsuarioResponse(Usuario u) { ... }
SucursalDTO.Basic toSucursalBasic(Sucursal s) { ... }
SucursalDTO.Response toSucursalResponse(Sucursal s) { ... }
PaqueteDTO.Response toPaqueteResponse(Paquete p) { ... }
TrackingDTO.Response toTrackingResponse(Tracking t) { ... }
EnvioDTO.Response toEnvioResponse(Envio e) { ... }
```

---

## Ventajas de la Estructura

- Menos archivos, más claridad.
- Inmutabilidad y menos boilerplate.
- Reutilización de DTOs básicos.
- Validaciones centralizadas.
- Facilita la integración con controladores REST y servicios.

---

## Próximos Pasos

1. Implementar mapeadores entre entidades y DTOs.
2. Integrar DTOs en controladores y servicios.
3. Añadir manejo centralizado de errores usando `ApiResponse.error()`.
4. Tests unitarios para mapeo y validación.
