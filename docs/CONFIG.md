# CONFIG.md - Recomendaciones de Configuración

## Sistema de Gestión Logística - Configuraciones de Desarrollo

### Configuración Backend - Spring Boot

#### Spring Initializr Settings

```
Project: Maven
Language: Java
Spring Boot: 3.2.x (última estable)
Project Metadata:
  - Group: com.logistica
  - Artifact: logistica-backend
  - Name: logistica-backend
  - Package name: com.logistica
  - Packaging: Jar
  - Java: 17

Dependencies:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - PostgreSQL Driver
  - Spring Boot DevTools
  - Validation
  - Spring Boot Actuator
```

#### application.yml - Desarrollo

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: logistica-system

  datasource:
    url: jdbc:postgresql://localhost:5432/logistica_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true

  sql:
    init:
      mode: always
      data-locations: classpath:data.sql

  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  level:
    com.logistica: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

jwt:
  secret: ${JWT_SECRET:myVerySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm}
  expiration: 86400000 # 24 horas
  refresh-expiration: 604800000 # 7 días

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

#### application-prod.yml - Producción

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    com.logistica: INFO
    org.springframework.security: WARN
    org.hibernate: WARN
  file:
    name: logs/logistica-app.log

jwt:
  secret: ${JWT_SECRET}

management:
  endpoints:
    web:
      exposure:
        include: health
```

#### pom.xml - Dependencies Adicionales

```xml
<dependencies>
    <!-- Agregar estas después de las dependencias base -->

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- ModelMapper para DTOs -->
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>3.1.1</version>
    </dependency>

    <!-- OpenAPI/Swagger para documentación -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Configuración Frontend - Vite + React

#### Creación del Proyecto

```bash
# Crear proyecto con Vite
npm create vite@latest logistica-frontend -- --template react-ts

cd logistica-frontend

# Instalar dependencias adicionales
npm install react-router-dom axios react-hook-form @hookform/resolvers yup
npm install @tanstack/react-query lucide-react react-hot-toast
npm install -D tailwindcss postcss autoprefixer @types/node

# Configurar Tailwind
npx tailwindcss init -p
```

#### vite.config.ts

```typescript
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
  build: {
    outDir: "dist",
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ["react", "react-dom"],
          router: ["react-router-dom"],
          forms: ["react-hook-form", "@hookform/resolvers", "yup"],
          query: ["@tanstack/react-query"],
          utils: ["axios", "lucide-react"],
        },
      },
    },
  },
});
```

#### tailwind.config.js

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#eff6ff",
          500: "#3b82f6",
          600: "#2563eb",
          700: "#1d4ed8",
        },
        success: {
          50: "#f0fdf4",
          500: "#22c55e",
          600: "#16a34a",
        },
        warning: {
          50: "#fffbeb",
          500: "#f59e0b",
          600: "#d97706",
        },
        danger: {
          50: "#fef2f2",
          500: "#ef4444",
          600: "#dc2626",
        },
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
      },
      boxShadow: {
        soft: "0 2px 15px 0 rgba(0, 0, 0, 0.1)",
      },
    },
  },
  plugins: [],
};
```

#### package.json - Scripts Adicionales

```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint src --ext ts,tsx --fix",
    "type-check": "tsc --noEmit",
    "test": "vitest",
    "test:ui": "vitest --ui"
  }
}
```

#### .env.development

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=Sistema de Logística
VITE_APP_VERSION=1.0.0
```

#### .env.production

```env
VITE_API_URL=https://tu-dominio.com/api
VITE_APP_NAME=Sistema de Logística
VITE_APP_VERSION=1.0.0
```

### Configuración Base de Datos

#### PostgreSQL Setup

```sql
# Crear base de datos
CREATE DATABASE astrovia_db;

# Crear usuario específico (opcional)
CREATE USER astrovia_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE astrovia_db TO astrovia_user;

-- Extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
```

#### Docker Setup (Opcional)

```yaml
# docker-compose.yml
version: "3.8"
services:
  postgres:
    image: postgres:15-alpine
    container_name: astrovia_db
    environment:
      POSTGRES_DB: astrovia_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql

  pgadmin:
    image: dpage/pgadmin4
    container_name: astrovia_pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@astrovia.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Recomendaciones de Desarrollo

#### IDE Setup - IntelliJ IDEA / VS Code

**IntelliJ IDEA Plugins:**

- Lombok Plugin
- Spring Boot Assistant
- Database Navigator
- GitToolBox
- SonarLint

**VS Code Extensions:**

- Extension Pack for Java
- Spring Boot Extension Pack
- PostgreSQL (cweijan)
- ES7+ React/Redux/React-Native snippets
- Tailwind CSS IntelliSense
- Auto Rename Tag
- Bracket Pair Colorizer

#### Git Configuration

```gitignore
# Backend (.gitignore)
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

# Logs
logs/
*.log

# Environment variables
.env
.env.local
.env.prod

# Frontend (.gitignore adicional)
# Logs
logs
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*
pnpm-debug.log*
lerna-debug.log*

node_modules
dist
dist-ssr
*.local

# Editor directories and files
.vscode/*
!.vscode/extensions.json
.idea
.DS_Store
*.suo
*.ntvs*
*.njsproj
*.sln
*.sw?
```

#### Scripts de Utilidad

**start-dev.sh** (Linux/Mac)

```bash
#!/bin/bash
echo "Iniciando servicios de desarrollo..."

# Iniciar PostgreSQL con Docker
docker-compose up -d postgres

# Esperar a que PostgreSQL esté listo
sleep 5

# Iniciar backend en background
cd backend
mvn spring-boot:run &
BACKEND_PID=$!

# Iniciar frontend
cd ../frontend
npm run dev &
FRONTEND_PID=$!

echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo "Servicios iniciados. Presiona Ctrl+C para detener."

# Cleanup function
cleanup() {
    echo "Deteniendo servicios..."
    kill $BACKEND_PID $FRONTEND_PID
    docker-compose down
    exit
}

trap cleanup INT
wait
```

**start-dev.bat** (Windows)

```batch
@echo off
echo Iniciando servicios de desarrollo...

REM Iniciar PostgreSQL con Docker
docker-compose up -d postgres

REM Esperar a que PostgreSQL esté listo
timeout /t 5

REM Iniciar backend
start "Backend" cmd /k "cd backend && mvn spring-boot:run"

REM Iniciar frontend
start "Frontend" cmd /k "cd frontend && npm run dev"

echo Servicios iniciados en ventanas separadas
pause
```

### Testing Configuration

#### Backend Testing

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true

logging:
  level:
    com.logistica: DEBUG
```

#### Frontend Testing Setup

```javascript
// vitest.config.ts
import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    setupFiles: ["./src/test/setup.ts"],
    globals: true,
  },
});
```

### Performance & Security

#### Backend Optimizations

- Usar @Transactional appropriately
- Implementar cache con @Cacheable
- Configurar pool de conexiones Hikari
- Lazy loading para relaciones JPA
- Paginación en endpoints que retornan listas
- Rate limiting con Bucket4j
- CORS restrictivo en producción

#### Frontend Optimizations

- Code splitting por rutas
- Lazy loading de componentes
- React.memo para componentes puros
- Virtual scrolling para listas grandes
- Image optimization
- Bundle analysis regulares

#### Security Checklist

- [ ] JWT tokens con expiración corta
- [ ] Refresh tokens seguros
- [ ] HTTPS en producción
- [ ] Validación de entrada en backend
- [ ] SQL injection protection (JPA)
- [ ] XSS protection en frontend
- [ ] CORS configurado correctamente
- [ ] Rate limiting en endpoints públicos
- [ ] Logs de seguridad (login attempts)
- [ ] Environment variables para secretos

### Deployment Recommendations

#### Backend Deployment

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Frontend Deployment

```dockerfile
# Multi-stage build
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
```

#### Environment Variables - Producción

```env
# Backend
DATABASE_URL=jdbc:postgresql://db-host:5432/astrovia_prod
DB_USERNAME=astrovia_prod_user
DB_PASSWORD=super_secure_password
JWT_SECRET=ultra_secure_jwt_secret_key_256_bits_minimum
SPRING_PROFILES_ACTIVE=prod

# Frontend
VITE_API_URL=https://api.astrovia.com
```

### Monitoring & Logging

#### Backend Monitoring

- Spring Boot Actuator endpoints
- Micrometer + Prometheus (opcional)
- Structured logging con Logback
- Health checks personalizados
- Database connection monitoring

#### Frontend Monitoring

- Error boundary para React
- Performance monitoring
- User analytics (opcional)
- Console error tracking
- Network request monitoring

Esta configuración proporciona una base sólida para desarrollar el sistema AstroVía con las mejores prácticas de la industria.
