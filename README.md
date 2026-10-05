# ShopFlow Microservices Platform

ShopFlow is a robust, event-driven e-commerce platform built with Spring Boot, Spring Cloud, and React. It leverages a modern microservices architecture emphasizing scalability, observability, security, and asynchronous processing.

## Architecture Diagram

```text
       +-------------+
       |   Frontend  | (React / Next.js)
       +------+------+
              |
       +------v------+      +-----------+
       | API Gateway |----->| Keycloak  | (Identity & Access)
       +------+------+      +-----------+
              |
    +---------+---------+---------+---------+
    |         |         |         |         |
+---v---+ +---v---+ +---v---+ +---v---+ +---v---+
|Product| | Order | |Payment| | Stock | | Notif |
|Service| |Service| |Service| |Service| |Service|
+---+---+ +---+---+ +---+---+ +---+---+ +---+---+
    |         |         |         |         |
    |         |         |         |         |
    v         v         v         v         v
+---+---+ +---+---+ +---+---+ +---+---+ +---+---+
| DB 1  | | DB 2  | | DB 3  | | DB 4  | | DB 5  | (PostgreSQL)
+-------+ +-------+ +-------+ +-------+ +-------+
    |         |         |         |         |
    +---------+----+----+---------+---------+
                   |
             +-----v-----+
             |   Kafka   | (Event Bus)
             +-----------+
```

## Tech Stack

| Component | Technology |
|---|---|
| Languages | Java 21, TypeScript |
| Framework | Spring Boot 3.3.x, Spring Cloud 2023.x, Next.js |
| Persistence | PostgreSQL 16, Redis 7.2 |
| Messaging | Apache Kafka 7.6.1 |
| Security | Keycloak (OAuth2 / OIDC) |
| Tracing | OpenTelemetry, Jaeger |
| Monitoring | Prometheus, Grafana |
| Resiliency | Resilience4j |

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local dev)
- Maven 3.9+ (for local dev)
- Node.js 20+ (for frontend)

## Getting Started

1. Clone the repository to your local machine.
2. Start the entire infrastructure using Docker Compose:

```bash
docker-compose up -d
```

3. Wait for all services to initialize. Keycloak, Kafka, and the Spring Boot applications may take a minute or two to be fully ready.

## Service URLs

| Service | Address |
|---|---|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Keycloak Admin | http://localhost:8180 |
| Kafka UI | http://localhost:8090 |
| Jaeger UI | http://localhost:16686 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3001 |

## Demo Credentials

- **Keycloak & Grafana Admin:** `admin` / `admin`
- **ShopFlow Admin (App):** `admin@shopflow.com` (username: `admin`) / `admin123`
- **ShopFlow Customer (App):** `customer@shopflow.com` (username: `customer`) / `customer123`

## Kafka Events

| Event | Publisher | Subscribers | Description |
|---|---|---|---|
| `OrderCreated` | Order Service | Payment, Stock Service | Emitted when a new order is initially placed |
| `PaymentCompleted` | Payment Service | Order, Notification Service | Emitted upon successful order payment processing |
| `PaymentFailed` | Payment Service | Order, Notification Service | Emitted if payment processing fails |
| `StockReserved` | Stock Service | Order Service | Emitted when inventory is successfully reserved |
| `OrderCancelled` | Order Service | Stock, Notification Service | Emitted to rollback or cancel an order |

## Architecture Decisions

- **Event-Driven Microservices:** Services are decoupled and communicate asynchronously via Apache Kafka. This ensures high throughput, resilience, and independent scaling capabilities.
- **Saga Pattern (Choreography):** Managing distributed transactions across services without a centralized coordinator. A workflow (e.g., ordering) triggers a sequence of events. If any part of the sequence fails (like a failed payment), compensating events (like releasing stock) are published to revert to a consistent state.
- **Circuit Breaker Pattern:** Resilience4j safeguards any synchronous service-to-service communication from cascading failures by breaking the circuit when latencies or error rates spike.
- **Centralized Observability:** Distributed tracing is achieved with OpenTelemetry exporting spans to Jaeger, combined with comprehensive system and custom metrics scraped by Prometheus and visualized in Grafana.
