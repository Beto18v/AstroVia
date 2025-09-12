# TECHNICAL.md - Documentación Técnica

## Sistema de Gestión Logística - Envíos

### Arquitectura del Sistema

#### Stack Tecnológico

**Backend:**

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (JWT)
- PostgreSQL
- Maven

**Frontend:**

- Vite
- React 18+
- TypeScript
- Tailwind CSS
- Axios
- React Router DOM

### Modelo de Base de Datos

#### Diagrama ERD

```
USUARIO (1) ──── (N) ENVIO (N) ──── (1) SUCURSAL (origen)
   │               │                │
   │               │                └── (1) SUCURSAL (destino)
   │               │
   │            (1)│(N)
   │               │
   │            PAQUETE
   │
   │ ENVIO (1) ──── (N) TRACKING (N) ──── (1) USUARIO
   │
   └──────────────────────────────────────────────────────

Nota: La relación Usuario-Envio como cliente solo aplica para rol CLIENTE
```

#### Tablas Principales

**sucursal**

```sql
CREATE TABLE sucursal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**usuario**

```sql
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    doc VARCHAR(20), -- Para clientes
    telefono VARCHAR(20),
    direccion TEXT,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**envio**

```sql
CREATE TABLE envio (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    id_cliente INTEGER REFERENCES usuario(id), -- Usuario con rol CLIENTE
    id_origen INTEGER REFERENCES sucursal(id),
    id_destino INTEGER REFERENCES sucursal(id),
    peso DECIMAL(8,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'CREADO',
    precio DECIMAL(10,2),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_estimada_entrega TIMESTAMP,
    observaciones TEXT
);
```

**paquete**

```sql
CREATE TABLE paquete (
    id SERIAL PRIMARY KEY,
    id_envio INTEGER REFERENCES envio(id) ON DELETE CASCADE,
    descripcion VARCHAR(200) NOT NULL,
    valor_declarado DECIMAL(10,2),
    peso DECIMAL(8,2),
    dimensiones VARCHAR(50)
);
```

**tracking**

```sql
CREATE TABLE tracking (
    id SERIAL PRIMARY KEY,
    id_envio INTEGER REFERENCES envio(id) ON DELETE CASCADE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ubicacion VARCHAR(100),
    evento VARCHAR(100) NOT NULL,
    id_usuario INTEGER REFERENCES usuario(id),
    observaciones TEXT
);
```

### Arquitectura Backend

#### Estructura de Capas

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Entity Layer (JPA Entities)
```

#### Patrones Implementados

1. **Repository Pattern**: Acceso a datos abstraído
2. **DTO Pattern**: Transferencia de datos entre capas
3. **Service Pattern**: Lógica de negocio encapsulada
4. **MVC Pattern**: Separación de responsabilidades

#### Seguridad

**JWT Authentication:**

- Token-based authentication
- Roles: ADMIN, OPERADOR, CLIENTE
- Endpoints protegidos según rol

**CORS Configuration:**

- Configurado para desarrollo y producción
- Permite origins específicos

### API REST Endpoints

#### Autenticación

```
POST /api/auth/login          # Login de usuario
POST /api/auth/refresh        # Refresh token
POST /api/auth/logout         # Logout
```

#### Sucursales

```
GET    /api/sucursales        # Listar todas las sucursales
GET    /api/sucursales/{id}   # Obtener sucursal por ID
POST   /api/sucursales        # Crear nueva sucursal
PUT    /api/sucursales/{id}   # Actualizar sucursal
DELETE /api/sucursales/{id}   # Eliminar sucursal
```

#### Usuarios

```
GET    /api/usuarios         # Listar usuarios
GET    /api/usuarios/{id}    # Obtener usuario por ID
GET    /api/usuarios/clientes # Listar solo usuarios con rol CLIENTE
GET    /api/usuarios/doc/{doc} # Buscar cliente por documento
POST   /api/usuarios         # Crear usuario/cliente
PUT    /api/usuarios/{id}    # Actualizar usuario/cliente
DELETE /api/usuarios/{id}    # Eliminar usuario/cliente
```

#### Envíos

```
GET    /api/envios            # Listar envíos
GET    /api/envios/{id}       # Obtener envío por ID
GET    /api/envios/codigo/{codigo} # Buscar por código
GET    /api/envios/cliente/{clienteId} # Envíos de un usuario cliente
POST   /api/envios            # Crear envío
PUT    /api/envios/{id}       # Actualizar envío
PUT    /api/envios/{id}/estado # Cambiar estado
DELETE /api/envios/{id}       # Eliminar envío
```

#### Paquetes

```
GET    /api/paquetes/envio/{envioId} # Paquetes de un envío
POST   /api/paquetes                # Crear paquete
PUT    /api/paquetes/{id}           # Actualizar paquete
DELETE /api/paquetes/{id}           # Eliminar paquete
```

#### Tracking

```
GET    /api/tracking/envio/{envioId} # Tracking de un envío
GET    /api/tracking/public/{codigo} # Tracking público
POST   /api/tracking                # Agregar evento de tracking
```

### Arquitectura Frontend

#### Estructura de Componentes

```
App Component
├── Router Configuration
├── Auth Context Provider
├── Layout Components
│   ├── Header
│   ├── Sidebar
│   └── Footer
├── Page Components
│   ├── Dashboard
│   ├── Sucursales
│   ├── Usuarios
│   ├── Envios
│   └── Tracking
└── UI
    ├── Forms
    ├── Tables
    ├── Modals
    └── Cards
```

#### State Management

- **React Context**: Autenticación global
- **Local State**: Estados de componentes
- **Custom Hooks**: Lógica reutilizable

#### Routing

```typescript
/                    # Dashboard
/login              # Login page
/sucursales         # Gestión de sucursales
/usuarios           # Gestión de usuarios
/envios             # Gestión de envíos
/envios/nuevo       # Crear envío
/envios/:id         # Detalle de envío
/tracking           # Consulta de tracking
/tracking/:codigo   # Tracking específico
```

### Estados de Envío

1. **CREADO**: Envío registrado en el sistema
2. **RECOLECTADO**: Paquete recogido en sucursal origen
3. **EN_TRANSITO**: En camino hacia sucursal destino
4. **EN_DESTINO**: Llegó a sucursal destino
5. **ENTREGADO**: Entregado al destinatario final
6. **DEVUELTO**: Devuelto al remitente
7. **CANCELADO**: Envío cancelado

### Flujo de Datos

#### Crear Envío

1. Usuario operador selecciona cliente (usuario con rol CLIENTE)
2. Ingresa datos del envío (peso, observaciones)
3. Sistema genera código único
4. Se crea registro de tracking inicial
5. Estado inicial: CREADO

#### Seguimiento

1. Operadores registran eventos de tracking
2. Se actualiza estado del envío automáticamente
3. Usuario cliente puede consultar estado públicamente
4. Historial completo de movimientos

### Consideraciones de Performance

#### Backend

- Indexación en campos de búsqueda frecuente
- Paginación en listados grandes
- Lazy loading en relaciones JPA
- Cache en consultas frecuentes

#### Frontend

- Code splitting por rutas
- Lazy loading de componentes
- Optimización de renders con React.memo
- Manejo eficiente de estados

### Seguridad

#### Backend

- Validación de entrada en DTOs
- Sanitización de datos
- Rate limiting en endpoints públicos
- Logs de auditoría

#### Frontend

- Validación de formularios
- Sanitización de inputs
- Protección de rutas por roles
- Tokens seguros en localStorage

### Testing

#### Backend

- Unit tests para servicios
- Integration tests para controladores
- Repository tests con TestContainers

#### Frontend

- Component testing con React Testing Library
- E2E testing con Cypress
- Unit tests para utilities y hooks
