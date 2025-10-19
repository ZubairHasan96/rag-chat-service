# Rag Chat Service

## Overview
Rag Chat Service is a backend microservice built with Java Spring Boot and MySQL. It is designed to store and manage chat sessions and messages, providing a robust API for session management and message handling.

## Features
- Create, rename, mark as favorite, and delete chat sessions.
- Add messages to sessions and retrieve message history.
- API key authentication for secure access.
- Rate limiting to prevent abuse of the APIs.
- Centralized logging and global error handling.
- Dockerized for easy local setup.

## Technologies Used
- Java 17
- Spring Boot
- MySQL
- Maven
- Docker

## Project Structure
```
rag-chat-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── assessment
│   │   │           └── ragchat
│   │   │               ├── RagChatApplication.java
│   │   │               ├── controller
│   │   │               │   ├── SessionController.java
│   │   │               │   └── MessageController.java
│   │   │               ├── service
│   │   │               │   ├── SessionService.java
│   │   │               │   └── MessageService.java
│   │   │               ├── repository
│   │   │               │   ├── SessionRepository.java
│   │   │               │   └── MessageRepository.java
│   │   │               ├── model
│   │   │               │   ├── Session.java
│   │   │               │   └── Message.java
│   │   │               ├── dto
│   │   │               │   ├── SessionDto.java
│   │   │               │   └── MessageDto.java
│   │   │               ├── config
│   │   │               │   ├── ApiKeyAuthFilter.java
│   │   │               │   ├── ApiKeyAuthenticationToken.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── OpenApiConfig.java
│   │   │               │   ├── RateLimitConfig.java
│   │   │               │   └── RateLimitInterceptor.java
│   │   │               ├── exception
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── ApiError.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   └── InvalidInputException.java
│   │   │               └── util
│   │   │                   ├── SessionFactory.java
│   │   │                   ├── MessageFactory.java
│   │   │                   └── Mapper.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── logback-spring.xml
│   │       └── db
│   │           └── changelog
│   │               ├── db.changelog-master.yaml
│   │               ├── init_session.yaml
│   │               └── init_message.yaml
│   └── test
│       └── java
│           └── com
│               └── assessment
│                   └── ragchat
│                       ├── controller
│                       │   ├── SessionControllerTest.java
│                       │   └── MessageControllerTest.java
│                       ├── service
│                       │   ├── SessionServiceTest.java
│                       │   └── MessageServiceTest.java
│                       └── RagChatApplicationTests.java
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env
├── .dockerignore
├── .gitignore
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone https://github.com/ZubairHasan96/rag-chat-service
   cd rag-chat-service
   ```

2. Create a MySQL database and update the `.env` file with your database credentials.

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

5. Alternatively, you can run the application using Docker Compose (recommended for local dev):
   ```
   docker compose up --build
   ```

6. Adminer (DB UI) — optional, for local DB browsing:
   - Adminer will be available at: http://localhost:8081
   - Login values (use the values from your `.env`):
     - Server: db
     - Username: ${DB_USERNAME}
     - Password: ${DB_PASSWORD}
     - Database: rag_chat

## API Documentation
API endpoints are documented using OpenAPI (springdoc). Access the documentation at:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
