# PROJECT.md - Documentación del Proyecto

## Sistema de Gestión Logística - Envíos

### Descripción General

Sistema web completo para la gestión de envíos logísticos que permite administrar sucursales, clientes, envíos, paquetes y seguimiento en tiempo real. Diseñado para empresas de mensajería y logística que necesiten un control detallado de sus operaciones.

### Objetivos del Sistema

- **Gestión Centralizada**: Administrar todas las operaciones desde una plataforma única
- **Trazabilidad Completa**: Seguimiento detallado de cada envío desde origen hasta destino
- **Experiencia del Cliente**: Interface intuitiva para consulta pública de envíos
- **Eficiencia Operativa**: Automatización de procesos y generación de reportes
- **Escalabilidad**: Arquitectura preparada para crecimiento empresarial

### Funcionalidades Principales

#### 1. Gestión de Sucursales

- **Registro de Sucursales**: Crear y administrar sucursales por ciudad
- **Información Detallada**: Nombre, ciudad, dirección, teléfono
- **Gestión Centralizada**: CRUD completo desde panel administrativo

#### 2. Gestión de Usuarios y Clientes

- **Registro Unificado**: Los clientes son usuarios con rol CLIENTE
- **Gestión de Usuarios**: Administrar usuarios del sistema (ADMIN, OPERADOR, CLIENTE)
- **Información Detallada**: Username, email, nombres, documento (para clientes), teléfono, dirección
- **Roles Diferenciados**: Diferentes permisos según el rol asignado
- **Historial de Envíos**: Los clientes pueden consultar sus envíos asociados
- **Búsqueda Avanzada**: Por username, documento (para clientes), nombre o email

#### 3. Sistema de Envíos

- **Creación de Envíos**:
  - Selección de cliente (usuario con rol CLIENTE)
  - Selección de sucursal origen y destino
  - Registro de peso y observaciones
  - Generación automática de código único
  - Cálculo automático de precios
- **Estados del Envío**:
  - CREADO: Envío registrado
  - RECOLECTADO: Recogido en origen
  - EN_TRANSITO: En camino
  - EN_DESTINO: Llegó al destino
  - ENTREGADO: Entregado al destinatario
  - DEVUELTO: Devuelto al remitente
  - CANCELADO: Envío cancelado

#### 4. Gestión de Paquetes

- **Múltiples Paquetes por Envío**: Un envío puede contener varios paquetes
- **Información Detallada**: Descripción, valor declarado, peso, dimensiones
- **Valoración del Envío**: Suma automática de valores declarados

#### 5. Sistema de Tracking

- **Seguimiento en Tiempo Real**: Registro de eventos y ubicaciones
- **Historial Completo**: Timeline de todos los movimientos
- **Consulta Pública**: Tracking sin necesidad de autenticación
- **Eventos Automatizados**: Registro automático de cambios de estado

### Flujo de Trabajo del Sistema

#### Flujo Operativo Principal

```
1. REGISTRO INICIAL
   Usuario Cliente → Sucursal de Origen
   ↓
   Registro de envío con datos básicos
   ↓
   Asignación de código único
   ↓
   Estado: CREADO

2. RECOLECCIÓN
   Operador registra recolección
   ↓
   Estado: RECOLECTADO
   ↓
   Tracking: "Paquete recolectado en [sucursal origen]"

3. TRANSPORTE
   Envío sale hacia destino
   ↓
   Estado: EN_TRANSITO
   ↓
   Tracking: "En tránsito hacia [sucursal destino]"

4. LLEGADA A DESTINO
   Envío llega a sucursal destino
   ↓
   Estado: EN_DESTINO
   ↓
   Tracking: "Llegó a [sucursal destino]"

5. ENTREGA FINAL
   Entrega al destinatario
   ↓
   Estado: ENTREGADO
   ↓
   Tracking: "Entregado exitosamente"
```

#### Flujo de Consulta Pública

```
Usuario Cliente ingresa código de envío
   ↓
Consulta pública (sin login)
   ↓
Sistema muestra:
- Estado actual del envío
- Historial completo de tracking
- Información del envío
- Tiempo estimado de entrega
```

### Roles y Permisos

#### ADMIN

- **Acceso Total**: Todas las funcionalidades del sistema
- **Gestión de Usuarios**: Crear y administrar cuentas de operadores
- **Configuración**: Modificar parámetros del sistema
- **Reportes Avanzados**: Estadísticas y métricas completas

#### OPERADOR

- **Gestión de Envíos**: Crear, modificar y gestionar envíos
- **Registro de Tracking**: Actualizar estados y ubicaciones
- **Gestión de Clientes**: CRUD de clientes
- **Consultas**: Acceso a información de envíos y paquetes

#### CLIENTE

- **Consulta de Envíos**: Solo sus propios envíos
- **Historial Personal**: Tracking de sus envíos históricos
- **Gestión de Perfil**: Actualizar datos personales (doc, teléfono, dirección)

### Interfaces del Sistema

#### 1. Panel Administrativo

**Dashboard Principal**

- Resumen de envíos del día
- Estadísticas de estados
- Envíos pendientes de gestión
- Métricas de rendimiento

**Gestión de Sucursales**

- Lista paginada de sucursales
- Formularios de creación/edición
- Búsqueda y filtros
- Validación de datos

**Gestión de Usuarios y Clientes**

- Base de datos unificada de usuarios
- Búsqueda por múltiples criterios
- Historial de envíos por cliente (usuarios con rol CLIENTE)
- Formularios de registro/edición con campos específicos por rol

**Gestión de Envíos**

- Lista completa de envíos
- Filtros por estado, fecha, sucursal
- Creación de nuevos envíos
- Actualización de estados
- Gestión de paquetes asociados

#### 2. Sistema de Tracking

**Panel de Operador**

- Lista de envíos para gestionar
- Registro rápido de eventos
- Actualización masiva de estados
- Búsqueda de envíos

**Interface de Tracking**

- Timeline visual de eventos
- Mapa de ubicaciones (futuro)
- Detalles completos del envío
- Historial chronológico

#### 3. Consulta Pública

**Página de Tracking Público**

- Búsqueda por código de envío
- Información del envío sin datos sensibles
- Estado actual y historial
- Responsive design para móviles

### Beneficios del Sistema

#### Para la Empresa

- **Control Total**: Visibilidad completa de operaciones
- **Eficiencia**: Automatización de procesos manuales
- **Trazabilidad**: Seguimiento detallado para resolución de incidencias
- **Escalabilidad**: Crecimiento sin limitaciones técnicas
- **Reportes**: Datos para toma de decisiones

#### Para los Clientes

- **Transparencia**: Seguimiento en tiempo real
- **Confianza**: Información actualizada constantemente
- **Conveniencia**: Consulta 24/7 desde cualquier dispositivo
- **Historial**: Acceso a envíos anteriores

#### Para Operadores

- **Interface Intuitiva**: Fácil de usar y aprender
- **Eficiencia**: Procesos optimizados
- **Movilidad**: Acceso desde cualquier dispositivo
- **Información Centralizada**: Todo en un solo lugar

### Casos de Uso Principales

#### 1. Crear Envío Nuevo

```
Actor: Operador
Precondición: Usuario autenticado
Flujo:
1. Operador accede a "Nuevo Envío"
2. Busca o registra cliente (usuario con rol CLIENTE)
3. Selecciona sucursal origen y destino
4. Ingresa peso y observaciones
5. Agrega paquetes con descripciones
6. Sistema genera código único
7. Se crea tracking inicial
8. Envío queda en estado CREADO
```

#### 2. Consultar Tracking Público

```
Actor: Usuario Cliente/Público
Precondición: Código de envío válido
Flujo:
1. Usuario accede a página de tracking
2. Ingresa código de envío
3. Sistema valida código
4. Muestra información del envío
5. Presenta timeline de eventos
6. Muestra estado actual
```

#### 3. Actualizar Estado de Envío

```
Actor: Operador
Precondición: Envío existente
Flujo:
1. Operador busca envío
2. Selecciona nuevo estado
3. Agrega ubicación y observaciones
4. Sistema actualiza estado
5. Se registra evento en tracking
6. Se notifica automáticamente (futuro)
```

### Métricas y KPIs

#### Operacionales

- Envíos creados por día/semana/mes
- Tiempo promedio de entrega
- Envíos por estado
- Envíos por sucursal

#### Calidad

- Porcentaje de envíos entregados a tiempo
- Envíos devueltos vs entregados
- Incidencias por cada 1000 envíos

#### Cliente

- Consultas de tracking por envío
- Satisfacción del cliente (futuro)
- Tiempo de respuesta del sistema

### Escalabilidad y Futuras Mejoras

#### Corto Plazo

- Notificaciones automáticas por email/SMS
- Integración con servicios de mapas
- App móvil para operadores
- API pública para clientes

#### Mediano Plazo

- Integración con sistemas de facturación
- Optimización de rutas
- Inteligencia artificial para predicciones
- Dashboard ejecutivo con BI

#### Largo Plazo

- IoT para tracking automático
- Blockchain para trazabilidad
- Machine Learning para optimización
- Marketplace de servicios logísticos
