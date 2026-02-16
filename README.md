# IV1201 Recruitment Application

A recruitment system for managing job applications, built with Spring Boot.

**Live Application:** https://iv1201-recruitment.azurewebsites.net/

## Team
- Sarah
- Khalil  
- Ante
- Usman

## Tech Stack
- Java 21
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL 16
- Maven

## Prerequisites
- Java 21 (JDK)
- Docker & Docker Compose (for local database)
- Git

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/FractalFish/IV1201-Project.git
cd IV1201-Project
```

### 2. Set up environment
```bash
cp .env.example .env
# Edit .env if you want to change default credentials
```

### 3. Start the database
```bash
docker compose up -d
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```
On Windows:
```bash
mvnw.cmd spring-boot:run
```

### 5. Access the application
Open http://localhost:8080 in your browser.

## Development

### Build
```bash
./mvnw clean compile
```

### Run tests
```bash
./mvnw test
```

### Stop database
```bash
docker compose down
```

### Reset database (delete all data)
```bash
docker compose down -v
docker compose up -d
```

## Project Structure
```
src/main/java/com/iv1201/recruitment/
├── config/          # Security, web configuration
├── controller/      # HTTP request handlers
├── service/         # Business logic
├── repository/      # Data access layer
├── domain/          # JPA entities
│   └── dto/         # Data transfer objects
└── util/            # Utilities (logging, etc.)
```

## Development with Docker

For development with live reload of templates and static files:

```bash
docker compose -f docker-compose.dev.yml up
```

Access at http://localhost:8080. Changes to HTML/CSS will be reflected immediately.

## CI/CD & Deployment

The project uses **GitHub Actions** for automated CI/CD:

1. **Static Analysis** - Checkstyle for code quality
2. **Unit Tests** - JUnit with JaCoCo coverage
3. **Build** - Docker image pushed to GitHub Container Registry
4. **Deploy** - Automatic deployment to Azure Web App (main branch only)

**Production:** https://iv1201-recruitment.azurewebsites.net/

Pipeline runs on every push. All tests must pass before deployment.

## Database Migrations

Database schema is managed with **Flyway**. Migrations run automatically when the app starts.

Migration files: `src/main/resources/db/migration/`
- `V1__schema.sql` - Initial schema and seed data
- `V2__password_migration.sql` - BCrypt password hashing
- `V3__application_table.sql` - Application submissions
- `V4__test_data.sql` - Additional test data

## Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| DB_URL | PostgreSQL JDBC URL | jdbc:postgresql://localhost:5432/recruitment |
| DB_USERNAME | Database username | postgres |
| DB_PASSWORD | Database password | postgres |

## Documentation

- **HANDOVER.md** - Comprehensive handover documentation
- **project-docs/ADR/** - Architecture Decision Records
- **project-docs/reports/** - Technical reports and guides
