# jbot 🤖

A Spring Boot-powered chatbot backend that integrates the **AIML (Artificial Intelligence Markup Language)** engine via [Program AB](https://code.google.com/archive/p/program-ab/) (Alice bot). It supports multiple named bots, persistent conversation sessions, and exposes a REST API for chat interactions.

---

## Features

- 🧠 **AIML-based AI** — Uses Program AB (Alice 2 / Alice 3) bots powered by AIML files
- 💬 **Session management** — Tracks active chat sessions per user with configurable timeouts and automatic cleanup
- 🗄️ **Persistent storage** — Saves conversations and sessions to a PostgreSQL database via Spring Data JPA
- 📋 **Liquibase migrations** — Database schema managed with Liquibase changelogs
- 🔐 **Spring Security + OAuth2** — Built-in authentication and authorization support
- 📊 **Actuator & Prometheus** — Health, info, metrics and Prometheus endpoints exposed out of the box
- 🐳 **Docker Compose** — Ready-to-run local environment with PostgreSQL and the app container

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 25                             |
| Framework   | Spring Boot 4.0.3                   |
| AI Engine   | Program AB (Alice bot / AIML)       |
| Database    | PostgreSQL 17                       |
| Migrations  | Liquibase                           |
| Security    | Spring Security, OAuth2             |
| Monitoring  | Micrometer, Prometheus, Actuator    |
| Build       | Maven (Maven Wrapper)               |
| Containers  | Docker, Docker Compose              |

---

## Prerequisites

- Java 25+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- Docker & Docker Compose (for local infrastructure)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-org/jbot.git
cd jbot
```

### 2. Start infrastructure with Docker Compose

```bash
docker compose up -d postgres
```

This starts a PostgreSQL instance on port `15431` with the following defaults:

| Setting  | Value       |
|----------|-------------|
| Database | `jbot`      |
| User     | `jbot_user` |
| Password | `jbot_pass` |

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080**.

### 4. Run everything with Docker Compose

To run both the database and the application:

```bash
docker compose up --build
```

---

## Configuration

Configuration is managed through `src/main/resources/application.yaml`. Key properties can be overridden via environment variables:

| Property                        | Environment Variable            | Default                              |
|---------------------------------|---------------------------------|--------------------------------------|
| Database URL                    | `SPRING_DATASOURCE_URL`         | `jdbc:postgresql://localhost:15431/jbot` |
| Database username               | `SPRING_DATASOURCE_USERNAME`    | `jbot_user`                          |
| Database password               | `SPRING_DATASOURCE_PASSWORD`    | `jbot_pass`                          |
| Session timeout (ms)            | —                               | `30000`                              |
| Session cleanup interval (ms)   | —                               | `60000`                              |
| Server port                     | —                               | `8080`                               |

---

## API Reference

Base URL: `http://localhost:8080/api/v1`

### Send a chat message

```
POST /api/v1/chat
```

**Request body:**

```json
{
  "message": "Hello!",
  "sessionId": "optional-existing-session-uuid",
  "botName": "alice2",
  "username": "john"
}
```

**Response:**

```json
{
  "response": "Hi there! How can I help you?",
  "botName": "alice2",
  "sessionId": "generated-session-uuid",
  "username": "john"
}
```

---

### Get active sessions

```
GET /api/v1/sessions
```

**Response:**

```json
[
  {
    "sessionId": "abc-123",
    "botName": "alice2"
  }
]
```

---

### Reload a bot

```
GET /api/v1/reload/{botName}
```

**Response:** `Bot alice2 reloaded successfully.`

---

### Actuator endpoints

| Endpoint                          | Description          |
|-----------------------------------|----------------------|
| `GET /actuator/health`            | Application health   |
| `GET /actuator/info`              | Application info     |
| `GET /actuator/metrics`           | Metrics              |
| `GET /actuator/prometheus`        | Prometheus scrape    |

---

## Bot Configuration

AIML bots are stored under `src/main/resources/bots/`. Each bot directory follows the structure:

```
bots/
└── alice2/
    ├── aiml/       # AIML knowledge base files
    ├── aimlif/     # Compiled AIML intermediate format
    ├── config/     # Bot configuration (predicates, properties, etc.)
    ├── maps/       # Map files for AIML lookups
    └── sets/       # Set files for AIML matching
```

To add a new bot, create a directory under `bots/` following the same structure and reference its name in API requests.

---

## Project Structure

```
src/
├── main/
│   ├── java/pl/knck/jbot/
│   │   ├── config/         # Spring & bot configuration
│   │   ├── controller/     # REST controllers
│   │   ├── dto/            # Request/response DTOs
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Spring Data repositories
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic (BotService)
│   └── resources/
│       ├── application.yaml
│       ├── bots/           # AIML bot data
│       └── db/changelog/   # Liquibase migrations
└── test/
    └── java/pl/knck/jbot/
```

---

## License

This project is licensed under the **Sustainable Use License**. See [LICENSE](LICENSE) for details.

