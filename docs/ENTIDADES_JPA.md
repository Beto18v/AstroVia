# Entity JPA - Sistema AstroVía

## Resumen de Entidades Creadas

Se han implementado 5 entidades JPA para el sistema de gestión logística AstroVía, siguiendo las especificaciones técnicas del proyecto.

### 📋 Enums Creados

#### `Rol` (com.astrovia.enums)

- **ADMIN**: Administrador del sistema
- **OPERADOR**: Operador logístico
- **CLIENTE**: Cliente final

#### `EstadoEnvio` (com.astrovia.enums)

- **CREADO**: Envío registrado en el sistema
- **RECOLECTADO**: Paquete recogido en sucursal origen
- **EN_TRANSITO**: En camino hacia sucursal destino
- **EN_DESTINO**: Llegó a sucursal destino
- **ENTREGADO**: Entregado al destinatario final
- **DEVUELTO**: Devuelto al remitente
- **CANCELADO**: Envío cancelado

### 🗃️ Entidades Principales

#### 1. `Usuario` (com.astrovia.entity)

**Tabla:** `usuario`

**Campos:**

- `id` (Long, PK, auto-generated)
- `username` (String, unique, 3-50 chars)
- `password` (String, min 6 chars)
- `nombres` (String, max 100 chars)
- `email` (String, valid email, max 100 chars)
- `doc` (String, max 20 chars) - Para usuarios con rol CLIENTE
- `telefono` (String, max 20 chars)
- `direccion` (String, max 500 chars)
- `rol` (Enum: Rol)
- `activo` (Boolean, default true)
- `fechaCreacion` (LocalDateTime, auto-set)

**Relaciones:**

- OneToMany con `Tracking` (trackings)
- OneToMany con `Envio` como cliente (envios) - Solo para rol CLIENTE

**Validaciones:**

- Username único y obligatorio
- Email válido y obligatorio
- Contraseña mínimo 6 caracteres
- Nombres obligatorios
- Documento opcional (usado para clientes)

**Nota:** Esta entidad maneja tanto usuarios del sistema (ADMIN, OPERADOR) como clientes (CLIENTE). Los campos `doc`, `telefono` y `direccion` son principalmente utilizados para usuarios con rol CLIENTE.

---

#### 2. `Sucursal` (com.astrovia.entity)

**Tabla:** `sucursal`

**Campos:**

- `id` (Long, PK, auto-generated)
- `nombre` (String, max 100 chars)
- `ciudad` (String, max 50 chars)
- `direccion` (String, max 200 chars)
- `telefono` (String, max 20 chars)
- `fechaCreacion` (LocalDateTime, auto-set)

**Relaciones:**

- OneToMany con `Envio` como origen (enviosOrigen)
- OneToMany con `Envio` como destino (enviosDestino)

**Validaciones:**

- Nombre y ciudad obligatorios
- Longitudes máximas definidas

---

#### 2. `Envio` (com.astrovia.entity)

**Tabla:** `envio`

**Campos:**

- `id` (Long, PK, auto-generated)
- `codigo` (String, unique, auto-generated)
- `peso` (BigDecimal, min 0.01)
- `estado` (Enum: EstadoEnvio, default CREADO)
- `precio` (BigDecimal, min 0.00)
- `fechaCreacion` (LocalDateTime, auto-set)
- `fechaEstimadaEntrega` (LocalDateTime)
- `observaciones` (String, max 1000 chars)

**Relaciones:**

- ManyToOne con `Usuario` (cliente) - Usuario con rol CLIENTE
- ManyToOne con `Sucursal` (sucursalOrigen)
- ManyToOne con `Sucursal` (sucursalDestino)
- OneToMany con `Paquete` (paquetes, cascada ALL, orphanRemoval)
- OneToMany con `Tracking` (trackings, cascada ALL, orphanRemoval)

**Características Especiales:**

- Código único auto-generado con formato "ENV" + UUID
- Estado inicial automático: CREADO
- Validaciones de peso y relaciones obligatorias

---

#### 3. `Paquete` (com.astrovia.entity)

**Tabla:** `paquete`

**Campos:**

- `id` (Long, PK, auto-generated)
- `descripcion` (String, max 200 chars)
- `valorDeclarado` (BigDecimal, min 0.00)
- `peso` (BigDecimal, min 0.01)
- `dimensiones` (String, max 50 chars)

**Relaciones:**

- ManyToOne con `Envio` (envio, obligatorio)

**Validaciones:**

- Descripción obligatoria
- Peso mínimo 0.01
- Valor declarado no negativo

---

#### 4. `Tracking` (com.astrovia.entity)

**Tabla:** `tracking`

**Campos:**

- `id` (Long, PK, auto-generated)
- `fechaHora` (LocalDateTime, auto-set)
- `ubicacion` (String, max 100 chars)
- `evento` (String, max 100 chars, obligatorio)
- `observaciones` (String, max 1000 chars)

**Relaciones:**

- ManyToOne con `Envio` (envio, obligatorio)
- ManyToOne con `Usuario` (usuario, opcional)

**Características:**

- Fecha/hora automática al crear
- Evento obligatorio
- Usuario opcional (para tracking automático)

## 🔗 Diagrama de Relaciones

```
USUARIO (1) ────── (N) ENVIO (N) ────── (1) SUCURSAL (origen)
   │                  │                 │
   │                  │                 └─── (1) SUCURSAL (destino)
   │                  │
   │               (1)│(N)              (1)│(N)
   │                  │                    │
   │               PAQUETE             TRACKING ────── (N) USUARIO (1)
   │                                      │
   └────────────────────────────────────────
```

**Nota:** La relación Usuario-Envio solo aplica para usuarios con rol CLIENTE. Los usuarios con rol ADMIN u OPERADOR solo se relacionan con Tracking.

## ✅ Características Implementadas

### Anotaciones JPA

- ✅ `@Entity` y `@Table` en todas las entidades
- ✅ `@Id` y `@GeneratedValue` para claves primarias
- ✅ `@Column` con configuraciones específicas
- ✅ `@ManyToOne` y `@OneToMany` para relaciones
- ✅ `@JoinColumn` para claves foráneas
- ✅ `@Enumerated` para enums

### Validaciones Bean Validation

- ✅ `@NotNull` para campos obligatorios
- ✅ `@NotBlank` para strings obligatorios
- ✅ `@Size` para longitudes máximas/mínimas
- ✅ `@Email` para validación de emails
- ✅ `@DecimalMin` para valores mínimos

### Configuraciones Avanzadas

- ✅ `@PrePersist` para fechas automáticas
- ✅ `cascade = CascadeType.ALL` donde corresponde
- ✅ `orphanRemoval = true` para entidades dependientes
- ✅ `fetch = FetchType.LAZY` para optimización
- ✅ Constraints de unicidad (`unique = true`)

### Relaciones Bidireccionales

- ✅ Métodos helper para mantener sincronía
- ✅ `toString()` sin referencias circulares
- ✅ Constructores múltiples para flexibilidad

### Características Especiales

- ✅ Generación automática de códigos únicos para envíos
- ✅ Estados predefinidos para envíos
- ✅ Fechas de creación automáticas
- ✅ Validaciones de negocio (peso mínimo, etc.)

## 🚀 Próximos Pasos

1. **Configurar application.yml** con la configuración de base de datos
2. **Crear Repositories** para acceso a datos
3. **Implementar DTOs** para transferencia de datos
4. **Desarrollar Services** para lógica de negocio
5. **Crear Controllers** para API REST
6. **Configurar Seguridad** con JWT
7. **Implementar Tests** unitarios e integración

## 📝 Notas Importantes

- Todas las entidades están listas para ser usadas con PostgreSQL
- Las relaciones bidireccionales están correctamente configuradas
- Los nombres de tablas y columnas siguen la convención snake_case de PostgreSQL
- Las validaciones cubren tanto aspectos técnicos como de negocio
- El sistema está preparado para manejar cascadas y eliminación de huérfanos
- **CAMBIO IMPORTANTE**: Se unificó Cliente y Usuario en una sola entidad Usuario con diferentes roles
- Los usuarios con rol CLIENTE utilizan campos adicionales: doc, telefono, direccion
- Los usuarios con rol ADMIN u OPERADOR no requieren estos campos adicionales
