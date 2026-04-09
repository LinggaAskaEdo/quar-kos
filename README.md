# Quar-Kos

A microservice-based application for user and order management built with Quarkus framework.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Modules](#modules)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Kafka Events](#kafka-events)
- [Building](#building)
- [Testing](#testing)
- [Makefile Commands](#makefile-commands)
- [Docker](#docker)

---

## Overview

Quar-Kos is a distributed system consisting of microservices for managing users and orders. It demonstrates a modern microservices architecture using Quarkus with PostgreSQL for data persistence and Apache Kafka for event-driven communication.

### Key Features

- **User Management**: CRUD operations for users, profiles, and roles
- **Order Management**: Create orders, manage products, track order status
- **Event-Driven**: Kafka-based communication between services
- **Search & Filtering**: Advanced search capabilities with pagination
- **Correlation Tracking**: Request tracing via correlation IDs

---

## Architecture

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                           Quar-Kos System                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────────────┐         ┌──────────────────────┐              │
│  │    user-service      │         │    order-service     │              │
│  │    (Port: 8082)      │         │    (Port: 8081)      │              │
│  ├──────────────────────┤         ├──────────────────────┤              │
│  │  - User CRUD         │         │  - Order CRUD        │              │ 
│  │  - User Profiles     │         │  - Products          │              │
│  │  - Role Management   │         │  - Order Items       │              │
│  │  - Search/Filtering  │         │  - Search/Filtering  │              │
│  │  - Kafka Producer    │         │  - Kafka Consumer    │              │
│  └──────────┬───────────┘         └──────────┬───────────┘              │
│             │                                │                          │
│             │         ┌──────────────┐       │                          │
│             │         │     Kafka    │       │                          │
│             └────────►│ (user-events)│ ◄─────┘                          │
│                       └──────────────┘                                  │
│                                                                         │
│  ┌──────────────────────┐                                               │
│  │     common-lib       │                                               │
│  │  (Shared Library)    │                                               │
│  ├──────────────────────┤                                               │
│  │  - Exceptions        │                                               │
│  │  - DTOs              │                                               │
│  │  - Utilities         │                                               │
│  │  - Constants         │                                               │
│  └──────────────────────┘                                               │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
            ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
            │ PostgreSQL  │ │ PostgreSQL  │ │    Kafka    │
            │ (userdb)    │ │ (orderdb)   │ │  (9092)     │
            │  (5432)     │ │  (5433)     │ │             │
            └─────────────┘ └─────────────┘ └─────────────┘
```

---

## Modules

| Module | Port | Description |
| --- | --- | --- |
| [common-lib](#common-lib) | - | Shared utilities, exceptions, and DTOs |
| [user-service](#user-service) | 8082 | User management, profiles, roles |
| [order-service](#order-service) | 8081 | Order management, products |

---

## Technology Stack

| Component | Technology |
| --- | --- |
| Framework | Quarkus 3.34.3 |
| Language | Java 21 |
| Build Tool | Maven |
| Database | PostgreSQL 15 |
| Messaging | Apache Kafka |
| Migration | Flyway |
| Testing | JUnit 5 + Mockito |

---

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 14+** (2 instances: 5432 and 5433)
- **Apache Kafka** with Zookeeper
- **Docker** (optional)

---

## Project Structure

```text
quar-kos/
├── common-lib/                      # Shared library
│   ├── pom.xml
│   └── src/main/java/com/otis/common/
│       ├── dto/                     # ErrorResponse, UserEvent
│       ├── exception/               # Custom exceptions & mappers
│       ├── preference/              # Constants (FilterKey, DatabaseColumns, etc.)
│       └── util/                    # CorrelationIdFilter, SqlQueryLoader, etc.
│
├── user-service/                    # User management service
│   ├── pom.xml
│   └── src/main/java/com/otis/usersvc/
│       ├── dto/                     # UserDTO, UserProfileDTO, etc.
│       ├── model/                   # User, UserProfile, Role entities
│       ├── repository/              # Data access layer
│       ├── resource/                # REST endpoints & Kafka producer
│       ├── service/                 # Business logic
│       └── util/                    # DTO/Model mappers
│
├── order-service/                   # Order management service
│   ├── pom.xml
│   └── src/main/java/com/otis/ordersvc/
│       ├── dto/                     # OrderDTO, ProductDTO, etc.
│       ├── model/                   # Order, Product, OrderItem entities
│       ├── repository/              # Data access layer
│       ├── resource/                # REST endpoints & Kafka consumer
│       ├── service/                 # Business logic
│       └── util/                    # DTO/Model mappers
│
└── Makefile                         # Build automation
```

---

## Quick Start

### 1. Start Infrastructure

```bash
# PostgreSQL for user-service
docker run -d \
  --name postgres-userdb \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=quar-kos-userdb \
  -p 5432:5432 \
  postgres:15

# PostgreSQL for order-service
docker run -d \
  --name postgres-orderdb \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=quar-kos-orderdb \
  -p 5433:5432 \
  postgres:15

# Kafka (requires Zookeeper)
docker run -d \
  --name zookeeper \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  confluentinc/cp-zookeeper:7.5.0

docker run -d \
  --name kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.5.0
```

### 2. Set Environment Variables

```bash
export POSTGRES_DOCKER_PASSWORD=postgres
```

### 3. Build All Modules

```bash
# Using Makefile (recommended)
make clean build

# Or build individual modules
make build-common
make build-user
make build-order
```

### 4. Run Services

```bash
# Using Makefile (recommended)
make run-user     # Terminal 1
make run-order    # Terminal 2

# Or using Quarkus dev mode
make debug-user   # Terminal 1 (with hot reload)
make debug-order  # Terminal 2 (with hot reload)
```

Services will be available at:

- **user-service**: <http://localhost:8082>
- **order-service**: <http://localhost:8081>

---

## Configuration

### Environment Variables

| Variable | Description | Required |
| --- | --- | --- |
| `POSTGRES_DOCKER_PASSWORD` | PostgreSQL password | Yes |

### Service Ports

| Service | Port |
| --- | --- |
| user-service | 8082 |
| order-service | 8081 |

### Databases

| Service | Database | Port |
| --- | --- | --- |
| user-service | quar-kos-userdb | 5432 |
| order-service | quar-kos-orderdb | 5433 |

---

## API Documentation

### User Service (Port 8082)

**Base URL**: `http://localhost:8082/api/users`

| Method | Endpoint | Description | Request Body | Response |
| --- | --- | --- | --- | --- |
| `GET` | `/` | Get all users | - | `List<UserDTO>` |
| `GET` | `/{id}` | Get user by ID | - | `UserDTO` or 404 |
| `POST` | `/` | Create user | `CreateUserRequest` | `UserDTO` (201) |
| `GET` | `/{id}/with-profile` | Get user with profile | - | `UserWithProfileDTO` |
| `POST` | `/{id}/profile` | Create user profile | `CreateProfileRequest` | `UserProfileDTO` (201) |
| `GET` | `/{id}/roles` | Get user roles | - | `List<RoleDTO>` |
| `POST` | `/{userId}/roles/{roleId}` | Assign role | - | 204 No Content |
| `GET` | `/search` | Search users | Query params | `SearchResponse` |

#### Search Parameters

- `username`, `email`, `id` - Filter fields
- `sortBy` (default: `created_at`)
- `sortDirection` (default: `DESC`)
- `limit` (default: 100), `offset` (default: 0)

#### Example: Create User

```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "email": "john@example.com"}'
```

---

### Order Service (Port 8081)

**Base URL**: `http://localhost:8081/api`

#### Orders

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/orders` | Create order |
| `GET` | `/orders` | Get all orders |
| `GET` | `/orders/{id}` | Get order by ID |
| `GET` | `/orders/user/{userId}` | Get orders by user |
| `GET` | `/orders/search` | Search orders |

**Search Parameters for Orders:**

- `status`, `userId`, `username`
- `minAmount`, `maxAmount`
- `sortBy` (default: `created_at`), `sortDirection` (default: `DESC`)
- `limit` (default: 100), `offset` (default: 0)

#### Products

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/products` | Get all products |
| `GET` | `/products/{id}` | Get product by ID |
| `GET` | `/products/search` | Search products |

**Search Parameters for Products:**

- `name`, `description`
- `minPrice`, `maxPrice`, `stock`
- `sortBy` (default: `id`), `sortDirection` (default: `DESC`)
- `limit` (default: 100), `offset` (default: 0)

#### Example: Create Order

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "johndoe",
    "items": [
      {"productId": "660e8400-e29b-41d4-a716-446655440001", "quantity": 2}
    ]
  }'
```

---

## Kafka Events

### Topic: `user-events`

#### Event: USER_CREATED

Published by user-service when a new user is created.

```json
{
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "event": "USER_CREATED",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "email": "john@example.com",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Event: USER_PROFILE_CREATED

Published by user-service when a user profile is created.

```json
{
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "event": "USER_PROFILE_CREATED",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Event Handling (order-service)

When order-service receives events:

- **USER_CREATED**: Creates order with "Book Note" product
- **USER_PROFILE_CREATED**: Creates order with "Key Chain" product

---

## Database Schema

### User Service Tables

#### users

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| username | VARCHAR(100) | NOT NULL, UNIQUE |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| created_at | TIMESTAMP | NOT NULL |

#### user_profiles

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| user_id | UUID | NOT NULL, UNIQUE, FK |
| first_name | VARCHAR(100) | |
| last_name | VARCHAR(100) | |
| phone | VARCHAR(20) | |
| address | TEXT | |

#### roles

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| name | VARCHAR(50) | NOT NULL, UNIQUE |
| description | TEXT | |

#### user_roles

| Column | Type | Constraints |
| --- | --- | --- |
| user_id | UUID | FK |
| role_id | UUID | FK |
| assigned_at | TIMESTAMP | |

**Default Roles**: ADMIN, USER, MANAGER

---

### Order Service Tables

#### orders

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| user_id | UUID | NOT NULL |
| username | VARCHAR(100) | |
| total_amount | DECIMAL(10,2) | NOT NULL |
| status | VARCHAR(50) | NOT NULL |
| correlation_id | UUID | |
| created_at | TIMESTAMP | |

#### products

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| name | VARCHAR(255) | NOT NULL |
| description | TEXT | |
| price | DECIMAL(10,2) | NOT NULL |
| stock | INTEGER | NOT NULL |

#### order_items

| Column | Type | Constraints |
| --- | --- | --- |
| id | UUID | PRIMARY KEY |
| order_id | UUID | FK |
| product_id | UUID | FK |
| quantity | INTEGER | NOT NULL |
| price | DECIMAL(10,2) | NOT NULL |

**Sample Products**: Laptop ($1299.99), Mouse ($79.99), Keyboard ($149.99), Monitor ($449.99), Headphones ($299.99)

---

## Building

### Quick Commands

```bash
make clean build              # Clean and build all modules
make build                    # Build all modules (without clean)
make build-common             # Build common library only
make build-user               # Build user service only
make build-order              # Build order service only
make build-native             # Build native executables for all services
make build-native-user        # Build user service native executable
make build-native-order       # Build order service native executable
```

### Run Packaged Application

```bash
# Using Makefile (recommended)
make run-user
make run-order

# Or manually
java -jar user-service/target/quarkus-app/quarkus-run.jar
java -jar order-service/target/quarkus-app/quarkus-run.jar
```

### Native Executables

```bash
# Build all native executables
make build-native

# Run native executables
make run-native-user
make run-native-order
```

---

## Testing

### Run Tests

```bash
# Using Makefile (recommended)
make test                     # Run tests for all modules
make test-common              # Test common library only
make test-user                # Test user service only
make test-order               # Test order service only

# Or manually
cd common-lib && ./mvnw test
cd user-service && ./mvnw test
cd order-service && ./mvnw test
```

### Run Tests with Coverage

```bash
./mvnw test jacoco:report
```

---

## Makefile Commands

The project includes an optimized Makefile for common development tasks. Run `make help` to see all available commands.

### Build Commands

| Command | Description |
| --- | --- |
| `make build` | Build all modules (common-lib + services) |
| `make build-common` | Build common library only |
| `make build-user` | Build user service only |
| `make build-order` | Build order service only |
| `make build-native` | Build native executables for all services |
| `make build-native-user` | Build user service native executable |
| `make build-native-order` | Build order service native executable |

### Clean Commands

| Command | Description |
| --- | --- |
| `make clean` | Clean all modules |
| `make clean-common` | Clean common library only |
| `make clean-user` | Clean user service only |
| `make clean-order` | Clean order service only |

### Run Commands

| Command | Description |
| --- | --- |
| `make run-user` | Run user service (JVM) |
| `make run-order` | Run order service (JVM) |
| `make run-native-user` | Run user service native executable |
| `make run-native-order` | Run order service native executable |

### Debug Commands

| Command | Description |
| --- | --- |
| `make debug-user` | Debug user service with Quarkus dev mode (hot reload) |
| `make debug-order` | Debug order service with Quarkus dev mode (hot reload) |

### Test Commands

| Command | Description |
| --- | --- |
| `make test` | Run tests for all modules |
| `make test-common` | Test common library only |
| `make test-user` | Test user service only |
| `make test-order` | Test order service only |

### Other Commands

| Command | Description |
| --- | --- |
| `make help` | Show all available commands |
| `make upgrade` | Update Quarkus dependencies to latest versions |

---

## Docker

### Build JVM Images

```bash
# user-service
docker build -f user-service/src/main/docker/Dockerfile.jvm -t quar-kos/user-service:jvm user-service/

# order-service
docker build -f order-service/src/main/docker/Dockerfile.jvm -t quar-kos/order-service:jvm order-service/
```

### Run Containers

```bash
# user-service
docker run -p 8082:8082 \
  -e POSTGRES_DOCKER_PASSWORD=postgres \
  quar-kos/user-service:jvm

# order-service
docker run -p 8081:8081 \
  -e POSTGRES_DOCKER_PASSWORD=postgres \
  quar-kos/order-service:jvm
```

---

## Module Details

### common-lib

Shared library providing common utilities across all services.

**Key Components:**

- **Exceptions**: `EntityNotFoundException`, `CreationFailedException`, `DataAccessException`, `KafkaException`
- **DTOs**: `ErrorResponse`, `UserEvent`
- **Utilities**: `CorrelationIdFilter`, `SqlQueryLoader`, `DynamicQueryBuilder`
- **Constants**: `FilterKey`, `DatabaseColumns`, `DatabaseOperator`

---

### user-service

Manages users, profiles, and roles.

**Key Features:**

- User CRUD operations
- User profile management (one-to-one with users)
- Role assignment (many-to-many)
- User search with filtering and pagination
- Kafka event publishing for user events

**Database**: `quar-kos-userdb` (Port 5432)

---

### order-service

Manages orders, products, and order items.

**Key Features:**

- Order creation with multiple items
- Product catalog management
- Order status tracking
- Kafka event consumption (responds to user events)
- Order and product search with filtering

**Database**: `quar-kos-orderdb` (Port 5433)

---

## Troubleshooting

### Database Connection Issues

1. Ensure PostgreSQL containers are running
2. Verify correct ports are exposed
3. Check `POSTGRES_DOCKER_PASSWORD` environment variable

### Kafka Connection Issues

1. Ensure Kafka and Zookeeper are running
2. Verify port 9092 is accessible
3. Check if `user-events` topic exists

### Migration Failures

```bash
# View Flyway history
psql -h localhost -U postgres -d <database> -c "SELECT * FROM flyway_schema_history;"
```

---

## License

Proprietary - OTIS
