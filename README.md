# Knowledge Service

A Spring Boot microservice for managing knowledge articles, categories, tags, and ratings following clean architecture principles.

## Features

- **Article Management**: Create, update, publish, archive, and delete knowledge articles
- **Versioning System**: Automatic version tracking for article changes
- **Categories & Tags**: Organize articles with categories and tags
- **Ratings & Views**: Track article ratings and view counts
- **Full-text Search**: Search articles by keyword, category, tag, or status
- **Kafka Integration**: Event-driven architecture with Kafka producers/consumers
- **JWT Security**: Role-based access control (ADMIN, MANAGER, ENGINEER)
- **Pagination**: Paginated list and search APIs

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Apache Kafka**
- **Spring Security (JWT)**
- **MapStruct**
- **Lombok**
- **JaCoCo** (80% minimum coverage)

## Project Structure

```
src/main/java/com/cognizant/knowledge_service/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/
│   ├── request/     # Request DTOs
│   └── response/    # Response DTOs
├── entity/          # JPA entities
├── enums/           # Enumerations
├── exception/       # Custom exceptions & handlers
├── kafka/           # Kafka producers & consumers
├── mapper/          # MapStruct mappers
├── repository/      # JPA repositories
├── security/        # JWT security components
└── service/
    └── impl/        # Service implementations
```

## API Endpoints

### Knowledge Articles
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/knowledge` | Create article | ADMIN, MANAGER |
| GET | `/api/knowledge/{id}` | Get article by ID | All authenticated |
| GET | `/api/knowledge` | Get all articles (paginated) | All authenticated |
| GET | `/api/knowledge/search` | Search articles | All authenticated |
| PUT | `/api/knowledge/{id}` | Update article | ADMIN, MANAGER |
| DELETE | `/api/knowledge/{id}` | Delete article | ADMIN |
| PUT | `/api/knowledge/{id}/publish` | Publish article | ADMIN, MANAGER |
| PUT | `/api/knowledge/{id}/archive` | Archive article | ADMIN, MANAGER |
| POST | `/api/knowledge/{id}/view` | Track view | All authenticated |

### Categories
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/categories` | Create category | ADMIN |
| GET | `/api/categories/{id}` | Get category | All authenticated |
| GET | `/api/categories` | Get all categories | All authenticated |
| PUT | `/api/categories/{id}` | Update category | ADMIN |
| DELETE | `/api/categories/{id}` | Delete category | ADMIN |

### Tags
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/tags` | Create tag | ADMIN, MANAGER |
| GET | `/api/tags/{id}` | Get tag | All authenticated |
| GET | `/api/tags` | Get all tags | All authenticated |
| DELETE | `/api/tags/{id}` | Delete tag | ADMIN |

### Ratings
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/ratings` | Create/update rating | All authenticated |
| GET | `/api/ratings/article/{id}` | Get ratings for article | All authenticated |
| GET | `/api/ratings/article/{id}/average` | Get average rating | All authenticated |
| DELETE | `/api/ratings/{id}` | Delete rating | Owner only |

### Internal APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/internal/tickets/{id}/knowledge` | Get articles by ticket ID |
| GET | `/internal/solutions/{id}/knowledge` | Get articles by solution ID |

## Kafka Events

### Produced Events
- `knowledge.created` - When a new article is created
- `knowledge.updated` - When an article is updated
- `knowledge.published` - When an article is published
- `knowledge.rated` - When an article is rated

### Consumed Events
- `ticket.resolved` - Logs ticket resolution events
- `solution.approved` - Auto-creates knowledge article from approved solution

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Apache Kafka

### Configuration

Update `application.yml` with your settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/knowledge_service_db
    username: postgres
    password: root
  kafka:
    bootstrap-servers: localhost:9092

jwt:
  secret: your-256-bit-secret-key
  expiration: 86400000
```

### Running Locally

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Running with Docker

```bash
# Build and start all services
docker-compose up -d

# Stop services
docker-compose down
```

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# View coverage report at target/site/jacoco/index.html
```

## Security

The service uses JWT tokens for authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

Required headers:
- `Authorization`: Bearer token
- `X-User-Id`: UUID of the current user

## Swagger Documentation

Access the API documentation at:
- Swagger UI: `http://localhost:8084/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8084/v3/api-docs`

## CI/CD

GitHub Actions workflow includes:
1. **Build**: Compile and package the application
2. **Test**: Run unit and integration tests
3. **Coverage**: Generate JaCoCo coverage report (80% minimum)
4. **Docker**: Build and push Docker image
5. **Deploy**: Deploy to INT, UAT, and PROD environments

## License

Copyright © 2024 Cognizant. All rights reserved.
