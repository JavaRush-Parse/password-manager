# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building and Running
- `./mvnw clean compile` - Clean build and compile including frontend assets (installs Node.js 20.9.0, npm dependencies, builds Tailwind CSS)
- `./mvnw spring-boot:run` - Run the Spring Boot application (starts on port 8080)
- `docker-compose up -d postgres` - Start PostgreSQL database (required for development)

### Testing and Quality
- `./mvnw test` - Run all tests
- `./mvnw verify` - Run tests and code formatting (includes fmt-maven-plugin)
- `./mvnw jacoco:report` - Generate test coverage report

### Frontend Development
- `cd src/main/frontend && npm run build` - Build Tailwind CSS once
- `cd src/main/frontend && npm run watch` - Watch for CSS changes and rebuild automatically

## Architecture Overview

This is a Spring Boot 3.5.5 password manager application with a layered architecture:

### Core Layers
- **Controllers**: REST API (`/api/v1/vault`) and Web controllers (`/`, `/vault-item/*`)
- **Services**: Business logic layer (VaultItemService, UserService, CollectionService)
- **Repositories**: JPA data access layer with Spring Data JPA
- **Entities**: JPA entities with soft delete support

### Key Architectural Components

#### Authentication & Security
- JWT-based authentication with custom security configuration
- Spring Security with custom AuthenticationEntryPoint and UserDetailsService
- Role-based access control (User, Role entities)
- Authentication endpoints: `/api/v1/auth/login`, `/api/v1/auth/register`

#### Frontend Architecture
- **Thymeleaf** templates with server-side rendering
- **Tailwind CSS 4.0.17** for styling (built via Maven frontend plugin)
- **HTMX 2.0.7** for dynamic interactions
- Component-based template structure (header, footer, vault-table components)

#### Data Layer
- **PostgreSQL** primary database (Docker Compose setup)
- **H2** available for testing/development
- Hibernate with `ddl-auto: update`
- Soft delete pattern implemented on entities

#### Import/Export Features
- Vault item import functionality with duplicate handling
- Custom exceptions for import validation (EmptyLoginException, ImportEntryDuplicateException)
- Collection-based organization of vault items

### Project Structure Notes
- Main package: `ua.com.javarush.parse.m5.passwordmanager`
- Frontend assets in `src/main/frontend/` (separate npm project)
- MapStruct for DTO mapping
- Lombok for reducing boilerplate
- OpenAPI/Swagger documentation enabled

### Configuration
- Database config in `application.yaml` (PostgreSQL by default)
- JWT settings configured with secret and expiration
- Spring profiles supported (test profile available)
- Docker Compose for PostgreSQL with data persistence

### Development Workflow
1. Start database: `docker-compose up -d postgres`
2. Build frontend: `./mvnw clean compile`
3. Run application: `./mvnw spring-boot:run`
4. For frontend changes: `cd src/main/frontend && npm run watch`

### Testing Strategy
- Basic Spring Boot test structure in place
- JaCoCo configured for coverage reporting
- Security test dependencies included

## Git Commit Guidelines
- Do not include claude postfix in the commit messages