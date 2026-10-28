# Spring Cloud: карта проекта и порядок изучения

Учебный проект микросервисного приложения для работы с пользователями и
автомобилями. Проект уже содержит основные компоненты Spring Cloud и
реализует защищённый CRUD API. Этот файл предназначен как быстрый маршрут
для изучения проекта и подготовки демонстрации.

## 1. Дерево проекта

```text
practos_9/
├── eureka-server/       # Service Discovery: реестр микросервисов
├── config-server/       # Spring Cloud Config Server
│   └── config-repo/     # Централизованные настройки сервисов
├── api-gateway/         # Единая точка входа и маршрутизация
├── user-service/        # Пользователи, роли, регистрация и JWT
├── car-service/         # Модели автомобилей, комплектации и карточки
├── test-drives-service/ # Записи на тест-драйвы
├── OLD PROJECT/         # Предыдущий монолит, не часть текущего запуска
└── README.md            # Этот файл
```

Сервисы используют Java 17, Spring Boot 3.4.5 и Spring Cloud 2024.0.2.
Каждый сервис является отдельным Maven-проектом со своим `pom.xml`.

## 2. Что уже готово

| Требование | Статус | Где смотреть |
|---|---|---|
| Минимум 3 микросервиса | Готово | `user-service`, `car-service`, `test-drives-service` |
| Spring Cloud Config | Готово(не полностью) | `config-server` и `config-server/config-repo` |
| Eureka Discovery | Готово | `eureka-server`, настройки в `config-repo/application.properties` |
| API Gateway | Готово(необходимо добавить новые(не сделанные сервисы)) | `api-gateway`, маршруты в `config-repo/api-gateway.properties` |
| Circuit Breaker | Готово | Gateway-фильтры CircuitBreaker + Resilience4j |
| Actuator и базовое логирование | Готово | Actuator и rolling file logging подключены к доменным сервисам |
| Swagger/OpenAPI | Готово для доменных сервисов | `user-service`, `car-service` и `test-drives-service` |
| JWT-аутентификация | Готово | `user-service` выдаёт токен, оба доменных сервиса его проверяют |
| Защищённый CRUD всей предметной базы | Готово в рамках текущих сущностей | CRUD-контроллеры users/roles и car-сущностей |
| Общая Swagger-документация через Gateway | Не добавлена | Swagger доступен напрямую у `user-service` и `car-service` |
| Централизованный сбор логов | Не добавлен | Есть обычное логирование, но нет ELK/Loki/Zipkin |

## 3. Как изучать проект по порядку

### Шаг 1. Eureka Server

Файл запуска: `eureka-server/src/main/java/com/example/eurekaserver/EurekaServerApplication.java`

- Порт: `8761`.
- Это сервер регистрации и обнаружения сервисов.
- Сам Eureka Server не регистрируется в собственном реестре:
  `eureka.client.register-with-eureka=false`.
- После запуска открыть [http://localhost:8761](http://localhost:8761) и
  проверить список зарегистрированных приложений.

### Шаг 2. Config Server

Файл запуска: `config-server/src/main/java/com/example/configserver/ConfigServerApplication.java`

- Порт: `8888`.
- Используется native profile и локальная папка `config-repo`.
- Общие настройки находятся в
  `config-server/config-repo/application.properties`.
- Настройки конкретных приложений:
  - `api-gateway.properties`;
  - `user-service.properties`;
  - `car-service.properties`;
  - `test-drives-service.properties`.
- Клиенты подключаются через
  `spring.config.import=configserver:http://localhost:8888`.
- Быстрая проверка: открыть
  `http://localhost:8888/user-service/default` и
  `http://localhost:8888/car-service/default`.


### Шаг 3. User Service

Файл запуска: `user-service/src/main/java/com/example/userservice/UserServiceApplication.java`

- Порт: `8084`.
- Регистрируется в Eureka как `USER-SERVICE`.
- Использует PostgreSQL, Spring Data JPA и JWT.
- Swagger UI: `http://localhost:8084/swagger-ui/index.html`.
- OpenAPI JSON: `http://localhost:8084/v3/api-docs`.

Основные маршруты:

| Метод | Путь | Назначение |
|---|---|---|
| POST | `/api/auth/register` | Регистрация |
| POST | `/api/auth/login` | Проверка логина и выдача Bearer JWT |
| GET | `/api/auth/me` | Данные текущего пользователя |
| GET/POST/PUT/DELETE | `/api/users`, `/api/users/{id}` | CRUD пользователей |
| GET/POST/PUT/DELETE | `/api/roles`, `/api/roles/{id}` | CRUD ролей |

Для CRUD пользователей и ролей требуется JWT. Чтение доступно ролям
`ADMIN` и `MANAGER`, операции создания/изменения/удаления — `ADMIN`.

### Шаг 4. Car Service

Файл запуска: `car-service/src/main/java/com/example/carservice/CarServiceApplication.java`

- Порт: `8083`.
- Регистрируется в Eureka как `CAR-SERVICE`.
- Swagger UI: `http://localhost:8083/swagger-ui/index.html`.
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`.
- Все `/api/**` требуют Bearer JWT.

CRUD-контроллеры:

| Ресурс | Базовый путь |
|---|---|
| Модели автомобилей | `/api/car-models` |
| Комплектации | `/api/car-complecttions` |
| Карточки автомобилей | `/api/cards` |
| Детали карточек | `/api/card-details` |

У каждого ресурса есть операции чтения, создания, изменения и удаления.
Операции с карточками дополнительно проверяют роли `ADMIN`/`MANAGER`.

### Шаг 5. Test Drives Service

Файл запуска: `test-drives-service/src/main/java/com/example/testdrivesservice/TestDrivesServiceApplication.java`

- Порт: `8087`.
- Регистрируется в Eureka как `TEST-DRIVES-SERVICE`.
- Использует общую таблицу PostgreSQL `test_drives`.
- Swagger UI: `http://localhost:8087/swagger-ui/index.html`.
- OpenAPI JSON: `http://localhost:8087/v3/api-docs`.

Основные маршруты:

| Метод | Путь | Доступ |
|---|---|---|
| GET | `/api/test-drives`, `/api/test-drives/{id}` | свои записи; `ADMIN`/`MANAGER` — все |
| GET | `/api/test-drives/my` | текущий пользователь |
| GET | `/api/test-drives/search?query=...` | свои записи; `ADMIN`/`MANAGER` — все |
| POST | `/api/test-drives` | авторизованный пользователь |
| PUT/DELETE | `/api/test-drives/{id}` | `ADMIN`/`MANAGER` |

Для обычного пользователя `userId` при создании берётся из JWT и не может
быть подменён в теле запроса. Для связи с общей таблицей `users` сервис
сопоставляет `username` из JWT с числовым идентификатором пользователя.

### Шаг 6. API Gateway

Файл запуска: `api-gateway/src/main/java/com/example/gateway/ApiGatewayApplication.java`

- Порт: `8082`.
- Gateway получает маршруты из Config Server и ищет экземпляры через Eureka.
- `/car-service/**` перенаправляется в `CAR-SERVICE` после удаления первого
  сегмента пути.
- `/user-service/**` перенаправляется в `USER-SERVICE` после удаления первого
  сегмента пути.
- `/test-drive-service/**` перенаправляется в `TEST-DRIVES-SERVICE` после удаления
  первого сегмента пути.
- При недоступности сервиса Circuit Breaker направляет запрос на:
  `/fallback/car`, `/fallback/user` или `/fallback/test-drive`.
- Проверка маршрутизации:
  `http://localhost:8082/user-service/api/auth/login` и
  `http://localhost:8082/car-service/api/car-models`.
- Actuator Gateway:
  `http://localhost:8082/actuator/gateway/routes`.

## 4. Правильный порядок запуска

Открыть отдельное окно терминала для каждого процесса:

```powershell
# 1. PostgreSQL (из OLD PROJECT)
cd ".\OLD PROJECT"
docker compose up -d

# 2. Eureka
cd ..\eureka-server
.\mvnw.cmd spring-boot:run

# 3. Config Server
cd ..\config-server
.\mvnw.cmd spring-boot:run

# 4. User Service
cd ..\user-service
.\mvnw.cmd spring-boot:run

# 5. Car Service
cd ..\car-service
.\mvnw.cmd spring-boot:run

# 6. Test Drives Service
cd ..\test-drives-service
.\mvnw.cmd spring-boot:run

# 7. API Gateway
cd ..\api-gateway
.\mvnw.cmd spring-boot:run
```

Порядок важен: Eureka должен быть доступен до регистрации клиентов, а
Config Server — до запуска `user-service`, `car-service`, `test-drives-service`
и `api-gateway`.