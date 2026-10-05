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

### Option A: One-click scripts (Recommended)
```bash
# On Windows (PowerShell)
.\start.ps1

# On Linux / macOS / Git Bash
chmod +x start.sh && ./start.sh
```

### Option B: Manual Docker Compose
```bash
# 1. Copy frontend environment configuration
cp frontend/.env.local.example frontend/.env.local

# 2. Build and launch all containers
docker compose up -d --build
```

Wait a minute or two for Keycloak, Kafka, and Spring Boot to complete initialization.

## Service URLs

| Service | Address | Swagger / Docs |
|---|---|---|
| Frontend (Store) | http://localhost:3000 | - |
| Frontend (Admin Panel) | http://localhost:3000/admin | - |
| API Gateway | http://localhost:8080 | - |
| Product Service | http://localhost:8081 | http://localhost:8081/swagger-ui/index.html |
| Order Service | http://localhost:8082 | http://localhost:8082/swagger-ui/index.html |
| Payment Service | http://localhost:8083 | http://localhost:8083/swagger-ui/index.html |
| Keycloak Admin | http://localhost:8180 | - |
| Kafka UI | http://localhost:8090 | - |
| Jaeger UI (Tracing) | http://localhost:16686 | - |
| Prometheus | http://localhost:9090 | - |
| Grafana (Dashboards) | http://localhost:3001 | Pre-loaded dashboard: `ShopFlow — Overview` |

## Demo Credentials

- **Keycloak & Grafana Admin:** `admin` / `admin`
- **ShopFlow Admin (App):** `admin@shopflow.com` (username: `admin`) / `admin123` (Access to `/admin` panel & management endpoints)
- **ShopFlow Customer (App):** `customer@shopflow.com` (username: `customer`) / `customer123`

## Running Tests

To run the unit tests across services using Maven:

```bash
# Run unit tests on individual services
cd product-service && mvn test
cd order-service && mvn test
cd payment-service && mvn test
cd stock-service && mvn test
cd notification-service && mvn test
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/ci.yml`) running on push and PR to `main`:
- Automated build & unit testing for all microservices (Java 21, Maven)
- TypeScript verification & Next.js production build check
- Docker Compose configuration linting & validation

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
