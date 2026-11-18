# Trading System

REST API для выставления, сопоставления и исполнения биржевых заявок.

## Стек

- Java 21 — язык разработки.
- Spring Boot 3 — запуск и конфигурация приложения.
- Spring Web — REST API.
- Spring Data JPA и Hibernate — работа с базой данных.
- PostgreSQL 15.20 — хранение данных.
- Flyway — создание таблиц и тестовых данных.
- Maven — сборка проекта.
- Docker Compose — запуск PostgreSQL.
- OpenAPI и Swagger UI — проверка API.
- JUnit, Mockito и Testcontainers — тестирование.
- Lombok — сокращение шаблонного кода.

## Запуск

```bash
git clone https://github.com/1Fiolet1/trading-system.git
cd trading-system
docker compose up -d
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html
