# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Running the Application
- **Full Docker setup**: `docker compose up --build` (builds and runs app + PostgreSQL database)
- **Local app with dockerized database**: 
  1. `docker compose up db` (only the database)
  2. `./gradlew bootRun` (runs the Spring Boot application locally)

### Testing
- **Run all tests**: `./gradlew test`
- **Run specific test class**: `./gradlew test --tests "com.fiuba_groups.fiuba_groups_back.service.GroupServiceTest"`

### Build Commands
- **Build project**: `./gradlew build`
- **Clean build**: `./gradlew clean build`

### Java Requirements
- **Java Version**: Java 17 or newer required (build.gradle specifies Java 21)
- If encountering Java version issues, ensure correct Java version is available

## Architecture Overview

This is a Spring Boot REST API for managing university course groups, built with:

### Tech Stack
- **Framework**: Spring Boot 3.5.6 with Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Documentation**: OpenAPI/Swagger UI (available at http://localhost:8080/swagger-ui/index.html)
- **Testing**: JUnit 5 + Mockito
- **Build Tool**: Gradle

### Core Domain Models
- **CourseOffering**: Represents a course offering (quarter, subject, course)
- **Group**: Student groups within a course offering
- **Student**: Students that can be members of groups

### Package Structure
```
com.fiuba_groups.fiuba_groups_back/
├── controller/          # REST controllers
├── service/            # Business logic services
│   └── dto/           # Data transfer objects for requests
├── model/             # JPA entities
├── repository/        # Spring Data JPA repositories
└── exception/         # Custom exceptions
```

### Key Relationships
- CourseOffering → Groups (One-to-Many)
- Group → Students (Many-to-Many via join table `group_members`)

### Database Configuration
- **Local development**: PostgreSQL on localhost:5432
- **Docker**: Uses postgres:16-alpine image
- **Schema management**: Hibernate auto-update enabled
- Default credentials: myuser/mypassword, database: mydatabase

### API Endpoints
- **Groups**: `/groups` - CRUD operations for group management
- **Course Offerings**: `/course-offerings` - Manage course offerings

### Testing Approach
Tests use Mockito for unit testing services with mocked repositories. Test classes follow naming convention: `{ServiceClass}Test`.

## Development Workflow
When asked to make changes to the codebase:
1. **Analyze Impact**: Search for all files that use the components being changed
2. **Provide Complete Plan**: List all files that need changes and what changes are required
3. **Get Approval**: Wait for user confirmation before making any changes
4. **Execute Changes**: Make all changes at once for user review

## Testing Policy
- **NEVER run tests automatically** - The user will run tests themselves
- You may suggest test commands or create test scripts, but do not execute them
- Only create or modify test files when explicitly requested