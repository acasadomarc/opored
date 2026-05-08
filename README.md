# OpoRed

OpoRed is the backend service for a comprehensive platform designed for individuals preparing for Spanish civil service examinations (`oposiciones`). It provides a robust set of features for managing users, educational content, community forums, and automated announcements.

## Features

*   **User & Role Management**: Supports multiple user roles including Students, Professors, Moderators, and Administrators, each with specific permissions.
*   **Authentication & Security**: Secure JWT-based authentication with refresh tokens, password encryption, and brute-force login protection.
*   **Course & Content Management**: Professors can create and manage courses containing various types of content such as videos, documents, and quizzes.
*   **E-commerce**: Students can purchase courses. The system tracks purchases and provides access to enrolled content.
*   **Rating System**: Students can rate courses and professors to provide feedback to the community.
*   **Community Forums**: Integrated forums allow users to create topics and post messages, fostering a collaborative learning environment.
*   **Moderation Tools**: Moderators can manage forum content by hiding or deleting inappropriate topics and messages.
*   **Public Examination Hub**: Centralized management of public examinations, categorized and linked to specific bulletin boards and forums.
*   **Automated Announcement Scraping**: Integrates with Apache Kafka to consume data from external scraping services (e.g., `opored-scraping`). It automatically classifies and publishes relevant announcements from official bulletins (BOE, BOCYL, BOR) to the appropriate bulletin boards.
*   **File Storage**: Handles file uploads for user profile pictures and course materials (PDFs, videos).

## Technology Stack

*   **Backend**: Java 21, Spring Boot 3
*   **Data Persistence**: Spring Data JPA, Hibernate, MariaDB
*   **Messaging**: Spring for Apache Kafka
*   **Security**: Spring Security (JWT, Bcrypt)
*   **API Documentation**: SpringDoc (OpenAPI 3)
*   **Build & Dependency Management**: Apache Maven
*   **Testing**: JUnit 5, Mockito, H2 Database, ArchUnit
*   **Containerization**: Docker, Docker Compose

## Getting Started

### Prerequisites

*   Git
*   Docker
*   Docker Compose

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/acasadomarc/opored.git
    cd opored
    ```

2.  **Create an environment file:**
    Create a `.env` file in the root directory and populate it with the required environment variables. The backend service relies on several variables for database connection, JWT signing, and inter-service communication.

    ```dotenv
    # Database Configuration
    DB_HOST=opodb
    DB_PORT=3306
    DB_NAME=opored
    DB_USER=oporeduser
    DB_PASSWORD=oporedpass
    DB_ROOT_PASSWORD=rootpass

    # JWT Secret Key
    JWT_PRIVATE_KEY=your-super-secret-jwt-key_and_it_should_be_long

    # Frontend URL (for CORS and other configurations)
    NEXT_PUBLIC_API_URL=http://localhost:8080
    ```

3.  **Run with Docker Compose:**
    The `docker-compose.yml` file is configured to set up the entire application stack, including the `opored` backend, a MariaDB database, Apache Kafka, the scraping service, and the frontend. The database will be initialized automatically using the SQL scripts in the `/db-init` directory.

    ```bash
    docker-compose up -d --build
    ```

The application will be accessible at `http://localhost:8080`.

## Project Structure

The repository is structured as a standard Spring Boot application:

```
├── db-init/              # SQL scripts for DB schema and initial data
├── src/
│   ├── main/
│   │   ├── java/         # Main application source code
│   │   │   ├── config/       # Spring, Security, CORS, and OpenAPI configurations
│   │   │   ├── controller/   # REST API endpoints
│   │   │   ├── dto/          # Data Transfer Objects
│   │   │   ├── enumeration/  # Application-specific enums
│   │   │   ├── exception/    # Custom exceptions and global handler
│   │   │   ├── model/        # JPA entity classes
│   │   │   ├── repository/   # Spring Data JPA repositories
│   │   │   ├── security/     # JWT utilities, brute-force protection
│   │   │   └── service/      # Business logic and services
│   │   └── resources/    # Application properties
│   └── test/             # Unit and integration tests
├── .github/workflows/    # CI/CD pipeline configuration
├── Dockerfile            # Docker configuration for building the backend image
└── docker-compose.yml    # Docker Compose file for running the entire stack
```

## API Documentation

The API is documented using OpenAPI. Once the application is running, you can access the interactive Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Continuous Integration

This project uses GitHub Actions for its CI/CD pipeline, defined in `.github/workflows/ci.yml`. The pipeline automates the following steps on every push to the `main` branch:
1.  **Checkout Code**: Fetches the latest code.
2.  **Set up Java**: Configures the JDK 21 environment.
3.  **Run Tests**: Executes the Maven test suite.
4.  **Analyze Code**: Performs static code analysis and coverage reporting with SonarQube.
5.  **Build Project**: Packages the application into a JAR file.
6.  **Build and Push Docker Image**: Builds a Docker image of the application and pushes it to GitHub Container Registry (GHCR).

## License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for more details.
