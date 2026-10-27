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
| POST | `/api/v1/internal/payments/authorizations` | API key | Autoriza el pago de una reserva (201; repetido 200; rechazado 402; pasarela caída 503) |
| GET | `/api/v1/payments/me` | PASSENGER | Lista mis pagos |
| GET | `/api/v1/payments/me/{id}` | PASSENGER | Detalle de un pago propio (ajeno 404) |
| POST | `/api/v1/internal/events/trip-completed` | API key | Evento TripCompleted: captura los pagos y crea la liquidación (201; repetido 200; 409 sin cambios) |
| POST | `/api/v1/internal/events/booking-cancelled` | API key | Evento BookingCancelled: cobra la penalidad o anula la autorización (200; sin pago 404; pago no autorizado 409) |
| GET | `/api/v1/settlements/me` | DRIVER | Lista mis liquidaciones |
| GET | `/api/v1/settlements/me/{id}` | DRIVER | Detalle de una liquidación propia (ajena 404) |
| GET | `/api/v1/settlements/me/summary` | DRIVER | `{totalGross, totalCommission, totalNet, pendingNet, paidNet}` |
| GET | `/api/v1/admin/settlements?status=PENDING\|PAID` | ADMIN | Lista liquidaciones, con filtro opcional por estado |
| PATCH | `/api/v1/admin/settlements/{id}/mark-paid` | ADMIN | `PENDING → PAID` con `paidAt` (ya pagada 409) |

## Endpoints internos

`/api/v1/internal/**` no usa JWT. Lo llaman otros microservicios (Booking & Pricing, Trip Execution) con el header `X-Internal-Api-Key`; sin header o con una clave incorrecta responde 401. La clave se compara en tiempo constante.

La autorización es idempotente por `bookingId`: si la reserva ya tiene un pago `AUTHORIZED` o `CAPTURED` se devuelve el mismo pago (200) sin volver a llamar a la pasarela; si estaba `REJECTED` se reintenta sobre el mismo registro.

### Recepción de eventos de otros servicios

Mientras no exista un broker de mensajes, los endpoints `/api/v1/internal/events/*` representan la recepción de los Domain Events de otros servicios (`TripCompleted` de Trip Execution, `BookingCancelled` de Booking & Pricing). Cuando haya broker, el mismo servicio de aplicación se llamará desde un consumidor de eventos.

### Captura al finalizar el viaje (US25)

En una sola transacción se captura cada pago (`CAPTURED`, `capturedAmount = finalAmount`) y se crea un `DriverSettlement` `PENDING` con:

- `gross` = suma capturada
- `commission` = `gross × COMMISSION_RATE` (`CommissionPolicy`, redondeo HALF_UP a 2 decimales)
- `net` = `gross − commission`, de modo que siempre `gross = commission + net`

Si un `finalAmount` supera lo autorizado o alguna reserva no tiene pago `AUTHORIZED`, responde 409 y no cambia nada. Es idempotente por `tripId`.

### Penalidad por cancelación (US12)

`PenaltyPolicy` (Strategy, implementada por `DefaultPenaltyPolicy`):

| Caso | Resultado |
|---|---|
| `PASSENGER_CANCELLED` con menos de `PENALTY_WINDOW_MINUTES` antes de la salida | Penalidad = `authorizedAmount × PENALTY_RATE`; se captura solo ese monto y se publica `PenaltyApplied` |
| `NO_SHOW` | Igual que el caso anterior |
| `PASSENGER_CANCELLED` con tiempo suficiente | Se anula la autorización (`VOIDED`) y se publica `PaymentVoided` |
| `NO_DRIVER_AVAILABLE` | Se anula la autorización, sin penalidad |

Repetir la cancelación de un mismo `bookingId` devuelve el mismo resultado (idempotente).

> Los valores de penalidad (`PENALTY_RATE = 0.50`, `PENALTY_WINDOW_MINUTES = 30`) son configurables y quedan **por validar con el equipo** (concern AC-05 del informe).

## Pasarela simulada

`SimulatedPaymentGatewayAdapter` implementa el puerto `PaymentGateway` sin llamadas reales y de forma determinista:

| `cardToken` | Resultado |
|---|---|
| `tok_declined` | Rechazado (402, el pago queda `REJECTED`) |
| `tok_unavailable` | Pasarela no disponible (503, no se guarda un pago autorizado) |
| cualquier otro | Aprobado con `providerRef = sim_<uuid>` |

## Eventos de dominio

Se publican con `DomainEventPublisher` sobre el `ApplicationEventPublisher` de Spring y, por ahora, un listener solo los registra en el log: `PaymentAuthorized`, `PaymentRejected`, `PaymentCaptured`, `PaymentVoided`, `PenaltyApplied`, `DriverSettlementCreated`.

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
- Los repositorios y la pasarela son puertos en `domain/repository`; `infrastructure` contiene los adaptadores.
- Atajo pragmático: las entidades de dominio llevan anotaciones JPA en lugar de tener un modelo de persistencia separado.
- Dinero siempre como `Money` (`BigDecimal`, 2 decimales, HALF_UP). Moneda única `PEN`; otra moneda responde 400.
- `passenger_id` y `driver_id` son referencias por id a Driver Management, sin claves foráneas.
- Tablas: `payment_methods`, `payments`, `penalties`, `driver_settlements`.

## Pruebas

- Escenarios Cucumber (Gherkin en inglés) en `src/test/resources/features`, ejecutados con MockMvc sobre H2 y con la BD limpia antes de cada escenario.
- `TestTokenFactory` firma tokens de prueba (`sub`, `userId`, `role`) con el secreto del perfil `test`, incluidos tokens expirados y sin `role`.
- Pruebas unitarias del dominio: redondeo de comisión y neto, `gross = commission + net`, penalidad dentro y fuera de la ventana y transiciones inválidas de `Payment`.

## Flujo de ramas (GitFlow)

- `main`: base del proyecto.
- `develop`: integración.
- `feature/*`: se crean desde `develop` y se integran con `git merge --no-ff`. No se borran.
- `fix/*`: correcciones posteriores, mismo flujo.

Orden: `feature/project-setup`, `feature/payment-methods`, `feature/us04-payment-authorization`, `feature/us25-payment-capture`, `feature/us12-cancellation-penalty`, `feature/driver-earnings`.

## Convención de commits

[Conventional Commits](https://www.conventionalcommits.org/) en inglés: `type(scope): description`, en imperativo y con asunto de 72 caracteres o menos. Tipos: `feat`, `fix`, `test`, `docs`, `chore`, `ci`, `build`, `refactor`.
