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

## Архитектура

```text
trading-system
├── src
│   ├── main
│   │   ├── java/ru/seleznev
│   │   │   ├── app                       # REST-слой
│   │   │   │   ├── controller            # HTTP-endpoints
│   │   │   │   ├── dto
│   │   │   │   │   ├── request           # Входные данные
│   │   │   │   │   └── response          # Ответы API
│   │   │   │   ├── exception             # Обработка ошибок
│   │   │   │   └── mapper                # Entity → Response
│   │   │   ├── domain
│   │   │   │   ├── enums                 # Типы, стороны и статусы
│   │   │   │   └── exception             # Доменные исключения
│   │   │   ├── persistence
│   │   │   │   ├── entity                # Hibernate-сущности
│   │   │   │   └── repository            # Spring Data JPA
│   │   │   ├── service
│   │   │   │   ├── OrderService          # Работа с заявками
│   │   │   │   ├── MatchingService       # Сопоставление и сделки
│   │   │   │   ├── QuoteService          # Биржевой стакан
│   │   │   │   └── RegistryService       # Реестры заявок и сделок
│   │   │   └── TradingSystemApplication  # Точка входа
│   │   └── resources
│   │       ├── db/migration               # Flyway-миграции
│   │       └── application.properties     # Настройки приложения
│   └── test/java/ru/seleznev              # Интеграционный и unit-тесты
├── compose.yaml                           # PostgreSQL в Docker
└── pom.xml                                # Maven-зависимости
```

## Запуск

```bash
git clone https://github.com/1Fiolet1/trading-system.git
cd trading-system
docker compose up -d
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

## Тестирование

```bash
./mvnw test
```

- `shouldCreateOrder` — проверяет создание заявки через REST API и PostgreSQL.
- `shouldCombineOrdersWithSamePrice` — проверяет объединение заявок в один ценовой уровень.
- `shouldThrowExceptionWhenInstrumentNotFound` — проверяет ошибку для неизвестного инструмента.
