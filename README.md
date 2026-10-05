# ShareWay - Payments & Settlement Service

Microservicio de ShareWay (plataforma universitaria de viajes compartidos programados con conductores verificados) que gestiona los métodos de pago del pasajero, la autorización del pago al reservar, el cobro al finalizar el viaje, las penalidades por cancelación y la liquidación de ganancias al conductor.

Fuera de alcance: login y usuarios (Driver Management) y cálculo de tarifas (Booking & Pricing; el monto llega ya calculado).

## Stack

- Java 25, Spring Boot 4.1 (Spring Security 7, Hibernate 7, Jackson 3)
- PostgreSQL 16 (H2 en memoria para los tests)
- springdoc-openapi 3, jjwt 0.12, Cucumber 7

## Cómo correr

```bash
docker compose up -d db
./mvnw spring-boot:run
```

Todo con Docker:

```bash
docker compose up --build
```

- API: http://localhost:8085
- Swagger UI: http://localhost:8085/swagger-ui.html
- Health: http://localhost:8085/actuator/health

Tests (no necesitan Docker, usan el perfil `test` con H2):

```bash
./mvnw -B clean verify
```

## Variables de configuración

| Variable | Por defecto | Descripción |
|---|---|---|
| `SERVER_PORT` | `8085` | Puerto HTTP |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5437/payment_settlement_db` | URL JDBC |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `shareway` / `shareway` | Credenciales de BD |
| `JWT_SECRET` | valor de desarrollo compartido con Driver Management | Secreto HS256 para validar tokens |
| `INTERNAL_API_KEY` | valor de desarrollo | Clave del header `X-Internal-Api-Key` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Orígenes permitidos |
| `COMMISSION_RATE` | `0.15` | Comisión de la plataforma sobre el bruto |
| `PENALTY_RATE` | `0.50` | Porcentaje del monto autorizado cobrado como penalidad |
| `PENALTY_WINDOW_MINUTES` | `30` | Minutos antes de la salida dentro de los cuales cancelar genera penalidad |

Los valores por defecto de secretos son solo para desarrollo.

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/actuator/health` | Público | Estado del servicio |
| GET | `/v3/api-docs`, `/swagger-ui.html` | Público | Documentación OpenAPI |
| POST | `/api/v1/payment-methods` | PASSENGER | Registra una tarjeta tokenizada (201; vencida 400; token duplicado 409) |
| GET | `/api/v1/payment-methods` | PASSENGER | Lista mis tarjetas activas |
| DELETE | `/api/v1/payment-methods/{id}` | PASSENGER | Desactivación lógica (204; ajena 404) |

## Autenticación

Este servicio **no emite tokens**. Los emite Driver Management (HS256, mismo `JWT_SECRET`) con los claims `sub` (email), `userId` (Long) y `role` (`PASSENGER`, `DRIVER` o `ADMIN`).

- `JwtAuthenticationFilter` valida firma y expiración y arma el usuario autenticado solo con los claims; aquí no existe tabla de usuarios.
- Un token expirado, mal firmado o sin `userId`/`role` devuelve 401.
- Si un pasajero se vuelve conductor, debe volver a iniciar sesión para que su token traiga el rol `DRIVER`.

## Datos de tarjeta

Nunca se recibe ni se guarda el número completo de la tarjeta: solo un `cardToken` (simulado), la marca y los últimos 4 dígitos. Las respuestas no devuelven el `cardToken`.

## Errores

Todas las respuestas de error tienen el cuerpo `{timestamp, status, error, message, path, fieldErrors[]}`:

| Código | Caso |
|---|---|
| 400 | Validación de entrada |
| 401 | Sin token, token inválido/expirado o API key inválida |
| 402 | Pago rechazado por la pasarela |
| 403 | Rol insuficiente |
| 404 | Recurso no encontrado o ajeno |
| 409 | Duplicado o estado inválido |
| 503 | Pasarela no disponible |

## Decisiones de diseño

- Arquitectura por capas DDD: `interfaces`, `application`, `domain`, `infrastructure`, `shared`.
- Atajo pragmático: las entidades de dominio llevan anotaciones JPA en lugar de tener un modelo de persistencia separado.

## Flujo de ramas (GitFlow)

- `main`: base del proyecto.
- `develop`: integración.
- `feature/*`: se crean desde `develop` y se integran con `git merge --no-ff`. No se borran.
- `fix/*`: correcciones posteriores, mismo flujo.

Orden: `feature/project-setup`, `feature/payment-methods`, `feature/us04-payment-authorization`, `feature/us25-payment-capture`, `feature/us12-cancellation-penalty`, `feature/driver-earnings`.

## Convención de commits

[Conventional Commits](https://www.conventionalcommits.org/) en inglés: `type(scope): description`, en imperativo y con asunto de 72 caracteres o menos. Tipos: `feat`, `fix`, `test`, `docs`, `chore`, `ci`, `build`, `refactor`.
