# RPM_9 — Spring Cloud микросервисное приложение

Учебный проект по изучению Spring Cloud. Приложение разделено на
микросервисы и использует централизованную конфигурацию, обнаружение
сервисов, API Gateway, JWT-аутентификацию, Circuit Breaker, Actuator,
логирование и Swagger/OpenAPI.

## 1. Архитектура

```text
                         ┌─────────────────┐
                         │   API Gateway   │ :8082
                         └────────┬────────┘
                                  │ Eureka LoadBalancer
       ┌──────────────────────────┼──────────────────────────┐
       │                          │                          │
┌──────▼──────┐           ┌───────▼──────┐           ┌───────▼────────┐
│ user-service│ :8084     │ car-service  │ :8083     │ test-drives    │ :8087
└──────┬──────┘           └───────┬──────┘           │ -service       │
       │                          │                  └───────┬────────┘
       └──────────────────────────┴──────────────────────────┘
                                  │
                         ┌────────▼────────┐
                         │   PostgreSQL    │
                         │  общая база     │
                         └─────────────────┘

 Eureka Server :8761       Config Server :8888
```

Сервисы используют Java 17, Spring Boot 3.4.5 и Spring Cloud 2024.0.2.
Каждый сервис является отдельным Maven-проектом.

## 2. Структура проекта

```text
practos_9/
├── api-gateway/                 # Единая точка входа, маршрутизация и fallback
├── car-service/                 # Автомобили и карточки автомобилей
├── config-server/               # Spring Cloud Config Server
│   └── config-repo/             # Централизованные properties
├── eureka-server/               # Service Discovery
├── test-drives-service/         # Записи на тест-драйвы
├── user-service/                # Пользователи, роли и JWT
├── OLD PROJECT/                 # Исходный монолит, не запускается в новой схеме
└── README.md
```

В текущей версии реализованы три доменных микросервиса:
`user-service`, `car-service` и `test-drives-service`. Этого достаточно для
обязательного требования задания о минимум трёх микросервисах.

## 3. Статус требований задания

| Требование | Статус | Реализация |
|---|---|---|
| Минимум 3 микросервиса | Выполнено | user, car, test-drives |
| Spring Cloud Config | Выполнено | config-server и config-repo |
| Eureka Discovery | Выполнено | eureka-server и Eureka Client |
| API Gateway | Выполнено | маршруты к трём сервисам |
| Circuit Breaker | Выполнено | Resilience4j в Gateway |
| Actuator | Выполнено | health/info, Gateway routes |
| Базовое логирование | Выполнено | консоль, rolling-файлы, архивы |
| Swagger/OpenAPI | Выполнено | у всех доменных сервисов |
| JWT и роли | Выполнено | user-service выдаёт JWT |
| Защищённый CRUD | Выполнено | пользователи, автомобили, тест-драйвы |

`dealer-service` и `news-service` в текущую реализацию не входят: они не
требуются для минимального условия о трёх микросервисах.

## 4. Сервисы

### User Service — порт 8084

Swagger: `http://localhost:8084/swagger-ui/index.html`
OpenAPI: `http://localhost:8084/v3/api-docs`

Основные маршруты:

| Метод | Путь | Назначение |
|---|---|---|
| POST | `/api/auth/register` | Регистрация |
| POST | `/api/auth/login` | Вход и получение JWT |
| GET | `/api/auth/me` | Текущий пользователь |
| CRUD | `/api/users`, `/api/users/{id}` | Пользователи |
| CRUD | `/api/roles`, `/api/roles/{id}` | Роли |

### Car Service — порт 8083

Swagger: `http://localhost:8083/swagger-ui/index.html`
OpenAPI: `http://localhost:8083/v3/api-docs`

Основные ресурсы:

- `/api/car-models`;
- `/api/car-complecttions`;
- `/api/cards`;
- `/api/card-details`.

### Test Drives Service — порт 8087

Файл запуска: `test-drives-service/src/main/java/com/example/testdrivesservice/TestDrivesServiceApplication.java`

Swagger: `http://localhost:8087/swagger-ui/index.html`
OpenAPI: `http://localhost:8087/v3/api-docs`

Сервис работает с таблицей `test_drives` общей PostgreSQL-базы.

| Метод | Путь | Доступ |
|---|---|---|
| GET | `/api/test-drives` | свои записи; ADMIN/MANAGER — все |
| GET | `/api/test-drives/my` | записи текущего пользователя |
| GET | `/api/test-drives/{id}` | своя запись; ADMIN/MANAGER — любая |
| GET | `/api/test-drives/search?query=...` | поиск с учётом прав |
| POST | `/api/test-drives` | любой авторизованный пользователь |
| PUT | `/api/test-drives/{id}` | ADMIN/MANAGER |
| DELETE | `/api/test-drives/{id}` | ADMIN/MANAGER |

При создании обычным пользователем `userId` берётся из JWT, поэтому его
нельзя подменить в JSON-запросе. CUSTOMER не может изменить или удалить
тест-драйв и не может получить чужую запись по идентификатору.

## 5. Spring Cloud компоненты

### Eureka Server

- Адрес: `http://localhost:8761`;
- порт: `8761`;
- отображает зарегистрированные `USER-SERVICE`, `CAR-SERVICE`,
  `TEST-DRIVES-SERVICE` и `API-GATEWAY`.

### Config Server

- Адрес: `http://localhost:8888`;
- порт: `8888`;
- источник настроек: `config-server/config-repo`.

Конфигурации:

- `application.properties`;
- `api-gateway.properties`;
- `user-service.properties`;
- `car-service.properties`;
- `test-drives-service.properties`.

Проверка:

```text
http://localhost:8888/user-service/default
http://localhost:8888/car-service/default
http://localhost:8888/test-drives-service/default
```

### API Gateway

Адрес: `http://localhost:8082`

| Префикс | Сервис |
|---|---|
| `/user-service/**` | USER-SERVICE |
| `/car-service/**` | CAR-SERVICE |
| `/test-drive-service/**` | TEST-DRIVES-SERVICE |

Gateway удаляет первый сегмент пути через `StripPrefix=1`. Например:

```text
/test-drive-service/api/test-drives
        ↓
/api/test-drives в TEST-DRIVES-SERVICE
```

Fallback при недоступности тест-драйвов:

```text
GET http://localhost:8082/fallback/test-drive
```

Actuator Gateway:

```text
http://localhost:8082/actuator/gateway/routes
```

## 6. Безопасность и секреты

- Все `/api/**` требуют JWT.
- Swagger и `/actuator/health` доступны без токена.
- Используется схема `Authorization: Bearer <JWT>`.
- Роли нормализуются в `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CUSTOMER`.
- JWT-секрет передаётся через `JWT_SECRET`.
- Данные PostgreSQL передаются через `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- JWT, пароли и полные Authorization-заголовки не записываются в логи.

Пример настройки окружения в PowerShell:

```powershell
$env:JWT_SECRET = "<base64-secret>"
$env:DB_URL = "jdbc:postgresql://127.0.0.1:5432/data"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<database-password>"
```

Секреты не должны добавляться в Git.

## 7. Логирование и мониторинг

Для сервисов настроены:

- вывод в консоль;
- rolling-файл лога;
- ежедневная ротация;
- ограничение размера;
- архивирование `.gz`;
- ограниченный срок хранения;
- HTTP-логирование метода, пути, статуса и времени выполнения.

Логи создаются локально в каталогах сервисов, например:

```text
test-drives-service/logs/test-drives-service.log
```

Файлы `logs/`, `*.log` и `target/` исключены из Git.

Проверка Actuator:

```text
http://localhost:8084/actuator/health
http://localhost:8083/actuator/health
http://localhost:8087/actuator/health
```

## 8. Порядок запуска

Перед запуском установите Java 17, Maven или используйте Maven Wrapper,
Docker и PostgreSQL.

### 1. PostgreSQL

```powershell
cd ".\OLD PROJECT"
docker compose up -d
cd ..
```

### 2. Eureka Server

```powershell
cd .\eureka-server
.\mvnw.cmd spring-boot:run
```

### 3. Config Server

```powershell
cd ..\config-server
.\mvnw.cmd spring-boot:run
```

### 4. User Service

```powershell
cd ..\user-service
.\mvnw.cmd spring-boot:run
```

### 5. Car Service

```powershell
cd ..\car-service
.\mvnw.cmd spring-boot:run
```

### 6. Test Drives Service

```powershell
cd ..\test-drives-service
.\mvnw.cmd spring-boot:run
```

### 7. API Gateway

```powershell
cd ..\api-gateway
.\mvnw.cmd spring-boot:run
```

Config Server и Eureka должны быть запущены до клиентов. После запуска
проверьте регистрации на `http://localhost:8761`.

## 9. Проверка

Сборка тест-драйвов:

```powershell
cd .\test-drives-service
.\mvnw.cmd -DskipTests package
.\mvnw.cmd test
```

В текущей версии проверяются:

- загрузка Spring-контекста;
- создание записи CUSTOMER со своим `userId`;
- запрет доступа к чужой записи;
- запрет изменения и удаления для CUSTOMER;
- изменение записи MANAGER;
- удаление записи ADMIN.

Проверка Gateway выполняется через:

```text
http://localhost:8082/user-service/api/auth/login
http://localhost:8082/car-service/api/car-models
http://localhost:8082/test-drive-service/api/test-drives
```

Эти URL нельзя проверять простым переходом по ссылке в браузере:
`/api/auth/login` принимает только `POST`, а защищённые маршруты требуют
заголовок `Authorization`. Для проверки через PowerShell используйте:

Для Swagger откройте
`http://localhost:8087/swagger-ui/index.html`, нажмите **Authorize** и
передайте `Bearer <JWT-токен>`. Ответ `405 Method Not Allowed` на URL login
означает, что был отправлен GET вместо POST. Ответ `401/403` на тест-драйвы
без токена означает, что защищённый endpoint вызван без авторизации.

Для проверки Circuit Breaker остановите соответствующий сервис и повторите
запрос через Gateway: должен вернуться HTTP 503 с JSON fallback.

## 10. Что осталось расширить

Обязательная Spring Cloud-часть выполнена на трёх микросервисах. Для
расширенной версии проекта можно добавить:

- `dealer-service`;
- `news-service`;
- агрегацию Swagger через Gateway;