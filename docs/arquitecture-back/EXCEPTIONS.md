# EXCEPTIONS.md - Manejo Global de Excepciones

## Objetivo

Unificar el formato de las respuestas de error de la API y centralizar la captura de excepciones, mejorando trazabilidad, mantenibilidad y experiencia de cliente.

## Formato Estándar de Error

Todas las respuestas de error siguen el record `ApiResponse`:

```json
{
  "success": false,
  "message": "Detalle del error",
  "data": null | object,
  "status": 404,
  "timestamp": "2025-09-12T12:34:56.789"
}
```

- `success`: siempre `false` en errores
- `message`: explicación legible para el cliente
- `data`: opcional (p.e. detalles de validación)
- `status`: HTTP status code numérico
- `timestamp`: fecha/hora del servidor

## Excepciones Personalizadas

| Excepción                   | HTTP | Uso                         | Ejemplos de Mensajes                           |
| --------------------------- | ---- | --------------------------- | ---------------------------------------------- |
| `ResourceNotFoundException` | 404  | Recurso inexistente         | `Usuario no encontrado`, `Envío no encontrado` |
| `BadRequestException`       | 400  | Parámetros/entrada inválida | `Peso inválido`, `Formato incorrecto`          |
| `UnauthorizedException`     | 401  | Falta de autenticación      | `Token inválido`, `Credenciales incorrectas`   |
| `BusinessException`         | 422  | Regla de negocio violada    | `Estado no permitido`, `Username ya existe`    |

`NotFoundException` fue reemplazada y marcada como `@Deprecated` (compatibilidad temporal) → usar `ResourceNotFoundException`.

## Clase Central: `GlobalExceptionHandler`

Ubicación: `com.astrovia.exception.GlobalExceptionHandler`

Responsabilidades:

- Capturar excepciones personalizadas
- Capturar excepciones comunes de Spring (`MethodArgumentNotValidException`, `MethodArgumentTypeMismatchException`)
- Capturar excepciones genéricas (`Exception`)
- Estandarizar respuesta usando `ApiResponse`
- Loggear según severidad:
  - 5xx → `error`
  - 4xx auth → `warn`
  - resto → `info`

## Validaciones (@Valid)

Para errores de validación se retorna:

```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "errors": { "campo": "mensaje" },
    "count": 1
  },
  "status": 400,
  "timestamp": "..."
}
```

## Ejemplos

### 1. Recurso no encontrado

```json
{
  "success": false,
  "message": "Envío no encontrado",
  "data": null,
  "status": 404,
  "timestamp": "2025-09-12T12:34:56.789"
}
```

### 2. Regla de negocio (422)

```json
{
  "success": false,
  "message": "Estado requerido",
  "data": null,
  "status": 422,
  "timestamp": "2025-09-12T12:34:56.789"
}
```

### 3. Error de validación

```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "errors": { "peso": "must be greater than 0" },
    "count": 1
  },
  "status": 400,
  "timestamp": "2025-09-12T12:34:56.789"
}
```

## Cómo Lanzar Excepciones

```java
if (envio == null) throw new ResourceNotFoundException("Envío no encontrado");
if (peso <= 0) throw new BadRequestException("Peso inválido");
if (!usuario.activo()) throw new BusinessException("Usuario inactivo");
```

## Beneficios

- Consistencia de API
- Reducción de duplicación en controladores
- Mejor observabilidad vía logs clasificados
- Facilidad de pruebas automatizadas

## Próximas Mejoras (Opcional)

- Correlación de request-id en respuestas
- Mapeo de códigos de negocio internos
- Internacionalización de mensajes
- Integración con métricas (Micrometer) y alertas
