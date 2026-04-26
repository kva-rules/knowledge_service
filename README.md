# Knowledge Service

Knowledge-base article microservice. Articles, tags, categories (hierarchical), ratings, and view tracking. Follows a clean-architecture split (controller → service → repository → entity). All endpoints require JWT; writes need `ENGINEER` / `MANAGER` / `ADMIN` roles.

---

## At a glance
| | |
|---|---|
| **Port** | 8085 |
| **Database** | postgres-knowledge (`knowledge_db`) |
| **Kafka topics (out)** | `knowledge.created`, `knowledge.rated` |
| **Kafka topics (in)** | `solution.approved` (auto-creates KB article) |
| **Swagger UI (direct)** | http://localhost:8085/swagger-ui.html |
| **Swagger UI (via gateway)** | http://localhost:8080/swagger-ui.html?urls.primaryName=knowledge-service |
| **OpenAPI JSON** | http://localhost:8085/v3/api-docs |
| **Java** | 21 (Temurin) |
| **Spring Boot** | 3.2.0 |

---

## What it does
- **Articles**: CRUD + full-text search + view tracking (`POST /api/knowledge/{id}/view`)
- **Categories**: hierarchical (parent/child) classification
- **Tags**: many-to-many tagging
- **Ratings**: one rating per user per article (1-5 stars)
- **Internal**: reward-service looks up articles by author

---

## API surface

### Articles (`/api/knowledge/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/knowledge` | JWT | Create article (+20 pts) |
| GET | `/api/knowledge` | JWT | List / paginate |
| GET | `/api/knowledge/search` | JWT | Full-text search |
| GET | `/api/knowledge/{id}` | JWT | Fetch one article |
| PUT | `/api/knowledge/{id}` | JWT | Update article |
| DELETE | `/api/knowledge/{id}` | JWT + ADMIN | Delete |
| POST | `/api/knowledge/{id}/view` | JWT | Increment view count |
| GET | `/api/knowledge/by-author/{userId}` | JWT | Articles by one author |
| GET | `/api/knowledge/statistics` | JWT | Aggregate counts |

### Tags (`/api/tags/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/tags` | JWT | Create tag |
| GET | `/api/tags` | JWT | List tags |
| GET | `/api/tags/{id}` | JWT | Fetch one tag |
| DELETE | `/api/tags/{id}` | JWT + ADMIN | Delete |

### Categories (`/api/categories/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/categories` | JWT | Create category |
| GET | `/api/categories` | JWT | List categories |
| GET | `/api/categories/tree` | JWT | Full hierarchical tree |
| GET | `/api/categories/{id}` | JWT | Fetch one category |
| PUT | `/api/categories/{id}` | JWT | Update |
| DELETE | `/api/categories/{id}` | JWT + ADMIN | Delete |

### Ratings (`/api/ratings/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/ratings` | JWT | Submit rating (+5 pts upvote) |
| GET | `/api/ratings/by-article/{articleId}` | JWT | Ratings for an article |
| PUT | `/api/ratings/{id}` | JWT | Update own rating |
| DELETE | `/api/ratings/{id}` | JWT | Delete own rating |

### Internal (`/internal/**`) — service-to-service
| Method | Path | Purpose |
|---|---|---|
| GET | `/internal/knowledge/{id}` | Lookup by ID |
| GET | `/internal/knowledge/by-author/{userId}` | Articles by author |

Live: **http://localhost:8085/swagger-ui.html**.

---

## Configuration
| Env var | Yaml key | Default | Purpose |
|---|---|---|---|
| `SERVER_PORT` | `server.port` | `8085` | |
| `SPRING_DATASOURCE_URL` | | `jdbc:postgresql://postgres-knowledge:5432/knowledge_db` | |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | | `kafka:9092` | |
| `JWT_SECRET` | `jwt.secret` | (shared) | |

---

## Kafka events consumed
- **`solution.approved`** (from solution-service) → `SolutionApprovedConsumer` auto-creates a KB article
  titled `"Solution for Ticket #<ticketId>"` with status PUBLISHED. This completes the
  `solution.approved → knowledge article` chain verified by the e2e test suite.

## Kafka events produced
- **`knowledge.created`** — on POST /api/knowledge (triggers reward +20 pts to author)
- **`knowledge.rated`** — on rating submission (triggers reward +5 pts for upvotes)

---

## Build & run
```bash
./services.sh start knowledge-service
```

## Docker / K8s
- Manifest: `k8s/knowledge-service.yaml`
- Service: `knowledge-service`

---

## Troubleshooting

**Search returns empty even with matching articles**
Full-text search relies on Postgres. Ensure columns are indexed (`tsvector`); optionally install the `pg_trgm` extension for fuzzier matching.

**Categories tree endpoint returns a flat list**
The category seed data might have missing `parent_id`. Check `categories.parent_id IS NOT NULL` for the child rows.

**`solution.approved` events not producing KB articles**
Three things must align:
1. Solution service `KafkaConfig.java` `genericProducerFactory` must have `ADD_TYPE_INFO_HEADERS = true`
   so the `__TypeId__` header (`com.library.common.event.SolutionApprovedEvent`) is present.
2. Knowledge service `application-local.yaml` must have `spring.json.use.type.headers: true` under
   the consumer properties so the deserializer reads the header.
3. Only `SolutionApprovedConsumer` should have `@KafkaListener` on `solution.approved`.
   `KnowledgeEventConsumer.handleSolutionApproved` must NOT carry `@KafkaListener` — a competing
   listener with a locally-defined (UUID-typed) `SolutionApprovedEvent` causes `ClassCastException`
   and silently drops every message.

**Kafka consumer crash-loops on `knowledge.created` topic**
`KnowledgeEvent` is an internal type. The consumer factory bean in `KafkaConfig.java` must use
`ErrorHandlingDeserializer` (wrapping `JsonDeserializer`) so a single unresolvable payload is logged
and skipped rather than retried indefinitely.

---

## Tech stack
- Java 21 (Temurin)
- Spring Boot 3.2.0
- Spring Security + JJWT
- Spring Data JPA + PostgreSQL 16
- Spring Kafka
- springdoc-openapi 2.6.0
- Lombok 1.18.34
- `com.kva:common-library` 1.0.0
