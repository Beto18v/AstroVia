# Guía Completa de Pruebas para AstroVia

Esta guía está pensada para alguien que comienza con pruebas en un backend Spring Boot y explica **qué tipos de tests existen en este proyecto, por qué se hacen, cómo funcionan y cómo ejecutarlos**.

---

## 1. Objetivos de la Estrategia de Pruebas

1. Asegurar que la lógica de negocio funcione correctamente (unit tests).
2. Validar integración de componentes reales: controladores, servicios, seguridad, base de datos (integration tests).
3. Verificar consultas y reglas de persistencia (repository tests).
4. Prevenir regresiones y facilitar refactorizaciones.
5. Mantener una cobertura mínima objetivo del **80%** de instrucciones (no es absoluto, pero orienta a buena salud del código).

> La cobertura NO garantiza ausencia de bugs, pero baja cobertura suele correlacionarse con riesgo alto de regresiones.

---

## 2. Tipos de Pruebas en el Proyecto

| Tipo              | Qué prueba                                          | Aislamiento                        | Herramientas                                | Velocidad  | Ejemplos                                     |
| ----------------- | --------------------------------------------------- | ---------------------------------- | ------------------------------------------- | ---------- | -------------------------------------------- |
| Unit Tests        | Métodos/lógica de una clase                         | Muy alto (se mockean dependencias) | JUnit 5 + Mockito                           | Muy rápida | `EnvioServiceImpl.generateCodigo()`          |
| Repository Tests  | Consultas JPA / SQL y mapeos                        | Usa DB en memoria (H2)             | Spring Test + H2                            | Rápida     | `EnvioRepository.findEnviosSinTracking24h()` |
| Integration Tests | Flujo completo HTTP + seguridad + persistencia real | Baja (se usan beans reales)        | Spring Boot Test + MockMvc + Testcontainers | Más lenta  | `POST /api/envios` con JWT                   |
| Security Tests    | Restricciones de acceso y roles                     | Parcial                            | Spring Security Test                        | Media      | Acceso 401 / 403                             |

---

## 3. Librerías y Herramientas Usadas

- **JUnit 5**: Framework base de pruebas.
- **Mockito**: Mockear dependencias para unit tests.
- **Spring Boot Test**: Carga el contexto de Spring para tests de integración.
- **MockMvc**: Simular peticiones HTTP a controladores sin desplegar servidor real.
- **Testcontainers (PostgreSQL)**: Levanta un contenedor Docker real para reproducir comportamiento de la base de datos productiva.
- **H2 (modo tests)**: Base de datos en memoria rápida para repository tests.
- **JaCoCo**: Reportes de cobertura (`target/site/jacoco/index.html`).

---

## 4. Organización de Archivos de Test

```
backend/
  src/
    test/
      java/
        com/astrovia/
          support/        <- Builders / utilidades de datos
          unit/           <- Pruebas unitarias (Mockito)
          repository/     <- Pruebas de repositorio (H2)
          integration/    <- Pruebas integración + MockMvc + Testcontainers
```

> Esta estructura explícita separa claramente el propósito de cada suite y ayuda a ejecutar subconjuntos si es necesario.

---

## 5. Unit Tests (Servicios)

### Qué se prueba

- Cálculos o transformaciones (`precio = peso * 10`).
- Generación de código único (`generateCodigo()`): debe reintentar si colisiona.
- Flujo de actualización de estado crea `Tracking`.
- Excepciones esperadas: `ResourceNotFoundException`, `BusinessException`.

### Cómo se hacen

- Se mockean los repositorios (`@Mock` / `@MockBean`).
- Se inyecta la clase real (`@InjectMocks`).
- Se definen `when(...).thenReturn(...)` para controlar el escenario.
- Se verifican interacciones (`verify(envioRepository).save(...)`).

### Ejemplo (esquemático)

```java
@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {
  @Mock EnvioRepository envioRepository;
  @Mock UsuarioRepository usuarioRepository;
  @Mock SucursalRepository sucursalRepository;
  @Mock TrackingRepository trackingRepository;
  @InjectMocks EnvioServiceImpl service;

  @Test
  void save_creaEnvioConTrackingInicial() {
     // arrange mocks
     // act: service.save(request)
     // assert: verify(trackingRepository).save(any())
  }
}
```

### Buenas prácticas

- 1 test = 1 comportamiento claro.
- Nombrar métodos: `metodo_condicion_resultado()`.
- Evitar lógica dentro del test (mínimas ramas, usar builders).

---

## 6. Repository Tests (H2)

### Por qué no se mockean aquí

Queremos validar:

- Sintaxis de queries nativas / JPQL.
- Mapeo de columnas a entidades.
- Reglas de relaciones (cascade, fetch lazy/eager relevante para persistir).

### Cómo funcionan

- Usamos `@DataJpaTest` (opcional) o configuración manual.
- H2 se inicializa automáticamente (Spring Boot detecta dependencia test `h2`).
- Se insertan entidades con `TestEntityManager` o repositorios y luego se invoca el método.

### Ejemplos a cubrir

- `findEnviosSinTracking24h()`: crear 2 envíos, uno con tracking reciente (excluir) y uno antiguo (incluir).
- `topUsuariosUltimoMes()`: crear envíos dentro y fuera del rango de 30 días.

### Consideraciones

- Ajustar timestamps manualmente (`LocalDateTime.now().minusDays(31)`).
- Limitar datos al mínimo para cada caso.

---

## 7. Integration Tests (Testcontainers + MockMvc)

### Objetivo

Validar el comportamiento de la aplicación “como un cliente real”: endpoints, filtros de seguridad, serialización JSON, persistencia en PostgreSQL real.

### Flujo típico

1. `@SpringBootTest` + `@AutoConfigureMockMvc`.
2. Iniciar contenedor PostgreSQL:

```java
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
```

3. Usar `@DynamicPropertySource` para inyectar URL/credenciales.
4. Generar JWT usando `JwtTokenProvider` (bean real) o crear util auxiliar.
5. Invocar `mockMvc.perform(post("/api/envios")...)` con header `Authorization: Bearer <token>`.
6. Validar `status`, contenido JSON y efectos en DB (consultar repositorio).

### Casos clave

- Crear envío -> 200 + tracking inicial.
- Actualizar estado -> nuevo tracking.
- Acceso sin token -> 401.
- Token con rol no autorizado -> 403.
- Buscar por código inexistente -> 404 (propagado por `ResourceNotFoundException`).

### Por qué Testcontainers

- Evita discrepancias entre H2 y PostgreSQL (índices, tipos, SQL dialect).
- Aísla cada ejecución (estado limpio).
- Reproducible en cualquier máquina con Docker.

---

## 8. Security Tests

Validan que las anotaciones `@PreAuthorize` y el filtro JWT funcionan:

- Sin encabezado Authorization -> 401.
- Token con rol `CLIENTE` accediendo a endpoint admin-only -> 403.
- Token válido con rol permitido -> 200.

Usos de `spring-security-test`:

- También se puede usar `with(jwt())` (para Resource Server) o construir manualmente el header.
- Aquí generamos el token real con `JwtTokenProvider` para mayor realismo.

---

## 9. Datos de Prueba (Builders)

Se usarán clases helper en `src/test/java/com/astrovia/support`:

- `UsuarioTestDataBuilder`
- `SucursalTestDataBuilder`
- `EnvioTestDataBuilder`
- `TrackingTestDataBuilder`

Patrón:

```java
public class UsuarioTestDataBuilder {
  private Long id = 1L; private String username = "user1"; // ...
  public Usuario build() { Usuario u = new Usuario(); u.setId(id); u.setUsername(username); return u; }
  public UsuarioTestDataBuilder withId(Long id){ this.id=id; return this; }
}
```

Beneficios:

- Reduce duplicación.
- Claridad en tests (expresivo y declarativo).
- Fácil de extender sin romper tests antiguos.

---

## 10. Manejo de Excepciones en Tests

Para validar errores:

```java
assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
```

En MockMvc (controladores):

```java
mockMvc.perform(get("/api/envios/999").header("Authorization", token()))
       .andExpect(status().isNotFound())
       .andExpect(jsonPath("$.message").value("Envío no encontrado"));
```

---

## 11. Cobertura con JaCoCo

Tras ejecutar:

```bash
./mvnw clean test
```

Se genera el reporte en:

```
backend/target/site/jacoco/index.html
```

Indicadores:

- INSTRUCTION: porcentaje de instrucciones ejecutadas.
- BRANCH: útil para condicionales (incrementar haciendo tests de ramas true/false).

> Objetivo: >= 80% global. No forces cobertura artificial (tests vacíos). Prioriza riesgo y criticidad.

### Cómo aumentar cobertura de ramas

- Incluir caminos de excepción.
- Probar entradas nulas o límites.
- Probar loops con listas vacías y no vacías.

---

## 12. Ejecución Selectiva

Solo unit tests (por paquete):

```bash
./mvnw -Dtest="*ServiceImplTest" test
```

Solo integración (si se siguen nombres `*IT` o paquete):

```bash
./mvnw -Dtest="**/integration/**" test
```

> También se puede usar perfiles Maven separados (`-P integration-tests`) si se configura en el futuro.

---

## 13. Buenas Prácticas Generales

1. Tests deterministas (sin depender de `System.currentTimeMillis()` salvo que se controle).
2. Nombrar claramente: `save_creaTrackingInicial()`.
3. Evitar dependencias entre tests (cada uno genera sus propios datos).
4. Limpiar efectos secundarios en integración (normalmente el contenedor se recrea solo).
5. No testear frameworks (no repetir que `@Getter` de Lombok funciona, por ejemplo).
6. Minimizar asserts por test (si hay muchos, dividir en casos).

---

## 14. Preguntas Frecuentes

### ¿Por qué usar H2 y también Testcontainers?

- H2: rápido para feedback inmediato en repository tests.
- Testcontainers: confianza real en queries nativas y dialecto de PostgreSQL.

### ¿Puedo usar solo mocks y no integración?

No recomendado: perderías validación de seguridad, mapeos JPA reales y configuración.

### ¿Es obligatorio 80% en todas las clases?

No. Es una **meta global**. Algunas clases (DTOs, excepciones) no requieren test directo.

### ¿Qué no deberíamos testear?

- Código autogenerado / trivial (getters/setters puros).
- Implementaciones de librerías externas.

---

## 15. Próximos Pasos (Roadmap de Mejora)

- Añadir perfil Maven `integration-tests` para separar suites.
- Parallel test execution (Surefire + config) para reducir tiempo.
- Añadir mutation testing (Pitest) para medir calidad real de tests.
- Integración CI (GitHub Actions) con badge de cobertura.

---

## 16. Resumen Rápido (Cheat Sheet)

| Acción                                     | Comando                                 |
| ------------------------------------------ | --------------------------------------- |
| Ejecutar todos los tests                   | `./mvnw clean test`                     |
| Ver reporte cobertura                      | Abrir `target/site/jacoco/index.html`   |
| Ejecutar solo unit                         | `./mvnw -Dtest="*ServiceImplTest" test` |
| Ejecutar integración (si se nombran `*IT`) | `./mvnw -Dtest="*IT" test`              |
| Limpiar + cobertura                        | `./mvnw clean verify`                   |

---

## 17. Glosario Breve

- **Mock**: Objeto simulado que imita el comportamiento de una dependencia real.
- **Fixture**: Conjunto de datos preparado para un test.
- **Assertion**: Verificación de un resultado esperado.
- **Contexto de Spring**: Conjunto de beans gestionados por el contenedor.
- **JWT**: Token firmado para autenticación/autoriza­ción.
- **Test de caja negra**: Evalúa entradas/salidas sin mirar implementación interna.

---

Si necesitas que esta guía incluya ejemplos concretos de código de cada suite, indícalo y los agregaré junto con los archivos de prueba reales.
