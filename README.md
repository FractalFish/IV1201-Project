# IV1201 Recruitment Application

A recruitment system for managing job applications, built with Spring Boot.

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
git clone https://github.com/sarahsaleh00/IV1201-Project.git
cd IV1201-Project
```

### 2. Set up environment
```bash
cp .env.example .env
# Edit .env if you want to change default credentials
```

### 3. Start the database
```bash
docker-compose up -d
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
docker-compose down
```

### Reset database (delete all data)
```bash
docker-compose down -v
docker-compose up -d
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

## Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| DB_URL | PostgreSQL JDBC URL | jdbc:postgresql://localhost:5432/recruitment |
| DB_USERNAME | Database username | postgres |
| DB_PASSWORD | Database password | postgres |
