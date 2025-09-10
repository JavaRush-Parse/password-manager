# Password Manager

[<img alt="JavaRush University" src="https://img.shields.io/badge/java-rush-orange?style=for-the-badge&color=orange&cacheSeconds=3600">](https://javarush.com/)
[<img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge">](https://spring.io/projects/spring-boot)
[<img alt="Java" src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge">](https://openjdk.org/projects/jdk/21/)
[<img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge">](https://www.postgresql.org/)
[<img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind%20CSS-4.0.17-38B2AC?style=for-the-badge">](https://tailwindcss.com/)

A secure web-based password manager built with Spring Boot, featuring a modern UI with Tailwind CSS and PostgreSQL database integration.

## 🚀 Features

- **Secure Password Storage**: Store and manage your passwords safely
- **Modern Web Interface**: Clean, responsive UI built with Tailwind CSS
- **Database Integration**: PostgreSQL for reliable data persistence
- **RESTful API**: Full REST API support for programmatic access
- **Soft Delete**: Safe deletion with data recovery capabilities
- **Docker Support**: Easy deployment with Docker Compose

## 🛠️ Technology Stack

### Backend
- **Java 21** - Modern Java with latest features
- **Spring Boot 3.5.5** - Rapid application development framework
- **Spring Data JPA** - Data persistence layer
- **Spring HATEOAS** - RESTful API with hypermedia
- **Spring Web** - Web layer for MVC and REST controllers
- **Thymeleaf** - Server-side template engine
- **Lombok** - Reduces boilerplate code
- **Maven** - Build and dependency management

### Frontend
- **Tailwind CSS 4.0.17** - Utility-first CSS framework
- **Thymeleaf Templates** - Server-side rendering
- **Responsive Design** - Mobile-first approach
- **HyperUI Components** - Modern UI components from [HyperUI](https://www.hyperui.dev/)

### Database
- **PostgreSQL 17** - Primary production database
- **H2 Database** - In-memory database for development/testing

### DevOps & Tools
- **Docker Compose** - Container orchestration
- **Maven Frontend Plugin** - Node.js and npm integration
- **Spring Boot DevTools** - Development productivity tools

## 📁 Project Structure

```
password-manager/
├── compose.yaml                    # Docker Compose configuration
├── pom.xml                        # Maven project configuration
├── mvnw, mvnw.cmd                 # Maven wrapper scripts
├── README.MD                      # Project documentation
│
├── src/
│   ├── main/
│   │   ├── java/ua/com/javarush/parse/m5/passwordmanager/
│   │   │   ├── PasswordManagerApplication.java    # Main Spring Boot application
│   │   │   ├── controller/
│   │   │   │   ├── rest/
│   │   │   │   │   └── VaultItemController.java   # REST API controller
│   │   │   │   └── web/
│   │   │   │       ├── HomeController.java        # Web controller
│   │   │   │       └── VaultControllerWeb.java    # Vault web controller
│   │   │   ├── entity/
│   │   │   │   └── VaultItem.java                 # JPA entity
│   │   │   ├── repository/
│   │   │   │   └── VaultItemRepository.java       # Data access layer
│   │   │   └── service/
│   │   │       └── VaultItemService.java          # Business logic layer
│   │   │
│   │   ├── frontend/                              # Frontend assets
│   │   │   ├── package.json                       # Node.js dependencies
│   │   │   ├── package-lock.json                  # NPM lock file
│   │   │   ├── styles.css                         # Additional CSS styles
│   │   │   ├── tailwind.config.js                 # Tailwind CSS configuration
│   │   │   └── src/
│   │   │       └── tailwind.css                   # Tailwind CSS source
│   │   │
│   │   └── resources/
│   │       ├── application.yaml                   # Application configuration
│   │       ├── static/
│   │       │   └── img/
│   │       │       └── password-manager-logo.png  # Application logo
│   │       └── templates/                         # Thymeleaf templates
│   │           ├── component/
│   │           │   ├── footer.html                # Footer component
│   │           │   ├── header.html                # Header component
│   │           │   └── vault-table.html           # Vault table component
│   │           ├── create-vault.html              # Create vault item page
│   │           ├── edit-vault.html                # Edit vault item page
│   │           ├── home.html                      # Home page
│   │           └── vault.html                     # Vault page
│   │
│   └── test/
│       └── java/ua/com/javarush/parse/m5/passwordmanager/
│           └── PasswordManagerApplicationTests.java  # Application tests
```

## 🚀 Local Setup

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **Node.js 20.9.0** (automatically installed via Maven plugin)
- **Docker & Docker Compose** (for database)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd password-manager
   ```

2. **Start the PostgreSQL database**
   ```bash
   docker-compose up -d postgres
   ```
   **Note**: You don't necessarily need to start it manually. There is a dependency that does it automatically when you run the application.
   
   This will start PostgreSQL on port 5432 with:
   - Database: `password-manager`
   - Username: `root`
   - Password: `root`

3. **Build the frontend assets**
   ```bash
   ./mvnw clean compile
   ```
   This will:
   - Install Node.js 20.9.0 (automatically via Maven plugin)
   - Install npm dependencies (Tailwind CSS 4.0.17, autoprefixer, postcss)
   - Build Tailwind CSS from `src/main/frontend/src/tailwind.css` to `src/main/resources/static/main.css`

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   - Web Interface: http://localhost:8080
   - REST API: http://localhost:8080/api/v1/vault/all

### Alternative Setup (Without Docker)

If you prefer to use H2 in-memory database:

1. **Modify `application.yaml`** to use H2:
   ```yaml
   spring:
     application:
       name: password-manager
     datasource:
       url: jdbc:h2:mem:testdb
       driver-class-name: org.h2.Driver
       username: sa
       password: 
     jpa:
       hibernate:
         ddl-auto: create-drop
   ```

2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

**Note**: The current `application.yaml` only contains basic JPA configuration. The application will automatically use PostgreSQL when Docker Compose is running, or you can configure it manually for other databases.

## 🎯 Usage

### Web Interface

**Available Routes**:
- `GET /` - Home page displaying all vault items in a table
- `GET /vault-item/create` - Create new vault item form
- `POST /vault-item/save` - Save new vault item
- `GET /vault-item/edit/{id}` - Edit existing vault item form
- `POST /vault-item/update/{id}` - Update existing vault item
- `GET /vault-item/{id}` - View individual vault item details

**Features**:
1. **Home Page**: View all stored password items in a responsive table
2. **Add New Item**: Click "Add New Item" to create a new password entry
3. **Edit Item**: Click "Edit" on any item to modify its details
4. **View Item**: Click on any item to view its full details
5. **Soft Delete**: Items are soft-deleted and can be recovered (via REST API)

### REST API

The application provides a RESTful API for programmatic access:

**Base URL**: `/api/v1/vault`

- `GET /api/v1/vault/all` - Get all vault items
- `GET /api/v1/vault/{id}` - Get specific vault item by ID
- `GET /api/v1/vault?login={login}` - Get vault items by login
- `POST /api/v1/vault/create` - Create new vault item
- `PUT /api/v1/vault/{id}` - Update vault item
- `DELETE /api/v1/vault/{id}` - Delete vault item (soft delete)

## 🔧 Development

### Frontend Development

**Available npm scripts** (in `src/main/frontend/`):
- `npm run build` - Build Tailwind CSS once
- `npm run watch` - Watch for changes and rebuild CSS automatically

**Development workflow**:
```bash
cd src/main/frontend
npm run watch
```

This will watch for changes in `src/tailwind.css` and automatically rebuild the CSS to `../resources/static/main.css`.

**Frontend Structure**:
- `src/tailwind.css` - Main Tailwind CSS source file
- `styles.css` - Additional custom styles
- `tailwind.config.js` - Tailwind configuration (watches `../resources/templates/**/*.{html,js}`)

### Database Schema

The `VaultItem` entity includes:
- `id` - Primary key (auto-generated, Long)
- `name` - Display name for the item (String, required)
- `resource` - Website or application name (String)
- `description` - Additional notes (String)
- `login` - Username or email (String, required)
- `password` - Password (String, required)

**Repository Methods**:
- `findAll()` - Get all vault items
- `findById(Long id)` - Find by ID
- `findVaultItemByLogin(String login)` - Find items by login
- `save(VaultItem)` - Save/update item
- `deleteById(Long id)` - Soft delete item

**Note**: The entity uses `@SoftDelete` annotation for safe deletion. Passwords are stored as plain text - implement encryption for production use.

### Adding New Features

1. **Backend**: Add controllers, services, and entities in the appropriate packages
2. **Frontend**: Create Thymeleaf templates and update Tailwind CSS
3. **Database**: Modify entities and run migrations

## 🐳 Docker Deployment

To run the entire application with Docker:

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d
```

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

**Note**: The current test suite only includes a basic context load test. Add more comprehensive tests for controllers, services, and repositories as needed.

## ⚠️ Troubleshooting

### Common Issues

1. **Java Runtime Not Found**
   ```bash
   # Install Java 21 (required)
   # macOS with Homebrew:
   brew install openjdk@21
   
   # Or download from Oracle/OpenJDK website
   ```

2. **Maven Wrapper Issues**
   ```bash
   # Make wrapper executable
   chmod +x mvnw
   
   # Or use system Maven if available
   mvn clean compile
   ```

3. **Database Connection Issues**
   - Ensure Docker is running
   - Check if PostgreSQL container is up: `docker ps`
   - Verify database credentials in `compose.yaml`

4. **Frontend Build Issues**
   ```bash
   # Clean and rebuild
   ./mvnw clean
   ./mvnw compile
   
   # Or manually build frontend
   cd src/main/frontend
   npm install
   npm run build
   ```

5. **Port Already in Use**
   - Change port in `application.yaml` or stop conflicting services
   - Default port: 8080

## 📝 Configuration

Key configuration files:

- `application.yaml` - Spring Boot configuration
- `compose.yaml` - Docker services configuration
- `tailwind.config.js` - Tailwind CSS configuration
- `pom.xml` - Maven dependencies and plugins

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 🔗 Links

- **GitHub Repository**: [JavaRush-Parse/password-manager](https://github.com/JavaRush-Parse/password-manager)
- **JavaRush University**: Part of the JavaRush curriculum

## 👥 Authors

- @oleksandr-jr
- @mykhailokasiian14
- @AGuliaiev
- @MetaM0rf-student

## 📄 License

This project is part of JavaRush University curriculum.

---

**Note**: This is a learning project. For production use, implement proper password encryption, user authentication, and security best practices.
