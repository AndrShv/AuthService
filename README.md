# 🔐 Auth Service

Это микросервис, отвечающий за **регистрацию пользователей**, **аутентификацию через JWT**, и **отправку событий** другим сервисам через **RabbitMQ**.

---

## 📌 Возможности

- Регистрация пользователя с валидацией
- Аутентификация с помощью JWT (вход)
- Шифрование паролей (BCrypt)
- Отправка событий через RabbitMQ:
  - `user.registered` → другим сервисам после входа
  - `video.create` → автоматическое создание приветственного видео
- Проверка JWT токена через HTTP фильтр
- Интеграция с MySQL

---

## 📦 Технологии

- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL 8
- RabbitMQ
- Docker Compose

---

## 🧩 Архитектура

```
+--------------------+         +------------------------+
|     Auth Service   | ----->  |   User Profile Service |
|                    | ----->  |     (через RabbitMQ)   |
+--------------------+         +------------------------+
        |
        |
        v
+--------------------+
|   Video Service    |
| (создает видео)    |
+--------------------+
```

---

## 🧪 Эндпоинты

### 🔸 `POST /api/auth/register`

Регистрация нового пользователя:
```json
{
  "email": "user@example.com",
  "phone": "+380123456789",
  "username": "username",
  "password": "password",
  "confirmPassword": "password"
}
```

### 🔸 `POST /api/auth/login`

Возвращает JWT токен при успешном входе:
```json
{
  "identifier": "user@example.com или телефон",
  "password": "password"
}
```

---

## 🪝 Интеграция с RabbitMQ

| Exchange               | Routing Key           | Queue Name              | Событие                    |
|------------------------|------------------------|---------------------------|----------------------------|
| `user.exchange`        | `user.registered`      | `user.registered.queue`   | `UserRegisteredEvent`      |
| `video.exchange`       | `video.create`         | `video.create.queue`      | `VideoCreatingEvent`       |
| `user.profile.exchange`| `user.profile.create`  | `user.profile.queue`      | `UserProfileCreatingEvent` |

Сервис настраивает следующие объекты RabbitMQ:

- **Exchanges (Topic)**:
  - `user.exchange`
  - `video.exchange`
  - `user.profile.exchange`

- **Queues**:
  - `user.registered.queue`
  - `video.create.queue`
  - `user.profile.queue`

- **Bindings**:
  - `user.registered.queue` привязана к `user.exchange` с routing key `user.registered`
  - `video.create.queue` привязана к `video.exchange` с routing key `video.create`
  - `user.profile.queue` привязана к `user.profile.exchange` с routing key `user.profile.create`

---

## 📁 Важные компоненты проекта

- `AuthController` — обрабатывает `/register` и `/login`
- `AuthService` — логика регистрации, шифрования, отправки событий
- `JwtService`, `JwtUtil` — генерация и валидация токена
- `JwtAuthFilter` — фильтр JWT в Spring Security
- `RabbitMqConfig` — конфигурация очередей и биндингов
- `MessageSending` — отправка событий в RabbitMQ
- `CustomUserDetailsService` — поиск пользователя для Spring Security

---

## 🐬 MySQL (Docker)

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: authservice-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 1111
      MYSQL_DATABASE: authservice
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## ⚙️ Пример конфигурации (`application.yml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3307/authservice
    username: root
    password: 1111
  jpa:
    hibernate:
      ddl-auto: update
  rabbitmq:
    host: localhost
    username: guest
    password: guest

jwt:
  secret: your_jwt_secret_key_here
  expirationMs: 86400000

queue:
  name: user.registered.queue
```

---
