# ¿Qué vamos a construir?

Se construirá “HotelBook”, una aplicación web funcional de reservación de habitaciones que integra vistas MVC con Thymeleaf, API REST documentada con OpenAPI/Swagger, validación de negocio (p. ej. evitar traslapes), seguridad por roles y persistencia con Spring Data JPA sobre PostgreSQL, con Actuator habilitado y ejecución en contenedores.

## Arranque y fundamentos de Spring Boot

- Proyecto base “hotelbook” con estructura por capas (controller, service, repository, dto, config).
- `application.yml` con perfiles (dev/test) y datasource a PostgreSQL del compose.
- Bean Validation en DTOs clave (Rooms, Guests, Bookings).
- Actuator habilitado (health/info/metrics mínimas) y un @ControllerAdvice para errores uniformes.

## Spring MVC: REST esencial y vistas

- Controladores REST para Rooms, Guests y Bookings (CRUD, paginación/ordenación, filtros simples).
- Controladores MVC y vistas Thymeleaf: layouts, listados y formularios de alta/edición.
- Serialización JSON consistente (fechas ISO), i18n básico y CSRF en formularios.

## Persistencia con Spring Data JPA

- Entidades y relaciones: Room, Guest, Booking (con restricciones e índices esenciales).
- Repositorios `JpaRepository` con consultas derivadas.
- Migraciones con Flyway (`V1__init.sql`) y semillas mínimas para desarrollo.
- Filtros de búsqueda típicos (por tipo/capacidad/precio en Rooms; por nombre/email en Guests).

## Consultas y rendimiento en Data

- `@Query` (JPQL/nativo) y proyecciones para listados.
- Specifications/Criteria para filtros combinables.
- Control del N+1 en Booking↔Guest/Room con `fetch join`/`EntityGraph`.
- Servicios transaccionales (`@Transactional`) para crear/editar/cancelar reservas sin solapes.

## Seguridad y API avanzada

- `SecurityFilterChain` con rutas públicas/privadas y roles (ADMIN, USER).
- Autenticación con formulario para la UI; (opcional) JWT para /api/**.
- Autorización con `@PreAuthorize` por operación; CORS para la API.
- Buenas prácticas REST (versionado /api/v1); concurrencia optimista (`@Version`) y ETag donde aplique.

## Pruebas y observabilidad

- Pruebas de servicios con JUnit 5 y Mockito (reglas de reserva y validaciones).
- Pruebas de controladores con MockMvc (CRUD y disponibilidad).
- `@DataJpaTest` para repositorios/consultas.
- Actuator verificado (health/metrics/info) y empaquetado por perfiles para ejecución.

## Diagrama Entidad Relación

```
  +---------+          +-----------+          +---------+
  |  ROOMS  |<-------->|  BOOKINGS |<-------->| GUESTS  |
  +---------+    1..*  +-----------+   *..1   +---------+
                           ^
                           |
                           |
                           |

  +---------+        +--------------+        +--------+
  | USERS   |<------>| USER_ROLES   |<------>| ROLES  |
  +---------+   1..* +--------------+ *..1   +--------+
```

## ¿Qué modela cada entidad y cómo se relacionan?

ROOMS: catálogo operable de habitaciones del hotel. Cada habitación tiene número único, tipo, capacidad, precio y estado.
Relación: una habitación puede aparecer en muchas reservas (ROOMS 1..* BOOKINGS).

GUESTS: registro de huéspedes (personas que reservan). Identificados principalmente por email único.
Relación: un huésped puede tener muchas reservas (GUESTS 1..* BOOKINGS).

BOOKINGS: reserva que vincula a un huésped con una habitación en un rango de fechas (check_in, check_out). Incluye total, estado y un campo de versión para concurrencia optimista.
Relaciones: muchas reservas pertenecen a una habitación y a un huésped (ROOMS 1..* BOOKINGS *..1 GUESTS).

USERS: cuentas de acceso a la aplicación (operadores/administradores). No son huéspedes; sirven para autenticación y autorización.
Relación: un usuario puede tener varios roles (USERS 1..* USER_ROLES).

ROLES: catálogo de roles del sistema (ADMIN, USER).
Relación: un rol puede estar asignado a muchos usuarios (ROLES 1..* USER_ROLES).

USER_ROLES: tabla puente para la relación muchos-a-muchos entre USERS y ROLES.
Relación: cada fila une un usuario con un rol (USERS .. ROLES).
