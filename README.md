# RPM_9

Учебное микросервисное приложение на Spring Cloud для работы с
пользователями, автомобилями и тест-драйвами.

## Архитектура

```text
                         ┌─────────────────┐
                         │   API Gateway   │ :8082
                         └────────┬────────┘
                                  │
       ┌──────────────────────────┼──────────────────────────┐
       │                          │                          │
┌──────▼──────┐           ┌───────▼──────┐           ┌───────▼────────┐
│ user-service│ :8084     │ car-service  │ :8083     │ test-drives    │ :8087
│             │           │              │           │ -service       │
└──────┬──────┘           └───────┬──────┘           └───────┬────────┘
       └──────────────────────────┴──────────────────────────┘
                                  │
                         ┌────────▼────────┐
                         │   PostgreSQL    │
                         └─────────────────┘

 Eureka Server :8761       Config Server :8888
```

Проект использует Java 17, Spring Boot 3.4.5 и Spring Cloud 2024.0.2.

## Структура

```text
practos_9/
├── api-gateway/                 # Единая точка входа и маршрутизация
├── car-service/                 # Модели автомобилей и карточки
├── config-server/               # Централизованная конфигурация
│   └── config-repo/
├── eureka-server/               # Регистрация и обнаружение сервисов
├── test-drives-service/         # Записи на тест-драйвы
├── user-service/                # Пользователи, роли и авторизация
├── client-service/              # Веб-интерфейс
└── README.md
```

## Реализовано

- три доменных микросервиса: `user-service`, `car-service`,
  `test-drives-service`;
- централизованная конфигурация через Config Server;
- регистрация и обнаружение сервисов через Eureka;
- маршрутизация запросов через API Gateway;
- резервные ответы Gateway при недоступности доменного сервиса;
- мониторинг через Spring Boot Actuator;
- базовое HTTP-логирование и ротация файлов логов;
- Swagger/OpenAPI для доменных сервисов;
- разграничение операций по ролям;
- CRUD для пользователей, ролей, автомобилей и тест-драйвов;
- статический клиент с авторизацией, профилем, каталогом автомобилей и
  страницей тест-драйвов;
- клиент обращается к backend через API Gateway на порту `8082`.

## Сервисы и порты

### Eureka Server

Адрес: `http://localhost:8761`

После запуска в панели должны отображаться:

- `API-GATEWAY`;
- `USER-SERVICE`;
- `CAR-SERVICE`;
- `TEST-DRIVES-SERVICE`.

### Config Server

Адрес: `http://localhost:8888`

Конфигурации находятся в `config-server/config-repo`.

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
| `/user-service/**` | `USER-SERVICE` |
| `/car-service/**` | `CAR-SERVICE` |
| `/test-drive-service/**` | `TEST-DRIVES-SERVICE` |

Пример маршрутизации:

```text
/test-drive-service/api/test-drives
        ↓
/api/test-drives в TEST-DRIVES-SERVICE
```

Проверка маршрутов:

```text
http://localhost:8082/actuator/gateway/routes
```

### User Service — порт 8084

Swagger: `http://localhost:8084/swagger-ui/index.html`

Основные маршруты:

| Метод | Путь |
|---|---|
| POST | `/api/auth/register` |
| POST | `/api/auth/login` |
| GET | `/api/auth/me` |
| CRUD | `/api/users`, `/api/users/{id}` |
| CRUD | `/api/roles`, `/api/roles/{id}` |

### Car Service — порт 8083

Swagger: `http://localhost:8083/swagger-ui/index.html`

Основные ресурсы:

- `/api/car-models`;
- `/api/car-complecttions`;
- `/api/cards`;
- `/api/card-details`.

### Test Drives Service — порт 8087

Swagger: `http://localhost:8087/swagger-ui/index.html`

Основные маршруты:

| Метод | Путь |
|---|---|
| GET | `/api/test-drives` |
| GET | `/api/test-drives/my` |
| GET | `/api/test-drives/{id}` |
| GET | `/api/test-drives/search?query=...` |
| POST | `/api/test-drives` |
| PUT | `/api/test-drives/{id}` |
| DELETE | `/api/test-drives/{id}` |

Пользователь с ролью `CUSTOMER` работает только со своими записями.
Пользователи с ролями `MANAGER` и `ADMIN` могут работать с общим списком
в соответствии со своими правами.

## Клиент

Клиент находится в папке `client-service` и запускается как статический сайт.
Все запросы к backend выполняются через `http://localhost:8082`.

Запуск:

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\client-service"
py -m http.server 5500
```

Открыть:

```text
http://localhost:5500/index.html
```

Страница входа:

```text
http://localhost:5500/login.html
```

Перед запуском клиента должны быть доступны Eureka, Config Server,
доменные сервисы и API Gateway.

## Логирование и мониторинг

Для сервисов настроены:

- вывод сообщений в консоль;
- запись в rolling-файлы;
- ежедневная ротация;
- архивирование логов;
- HTTP-логирование метода, пути, статуса и времени выполнения.

Проверка состояния сервисов:

```text
http://localhost:8084/actuator/health
http://localhost:8083/actuator/health
http://localhost:8087/actuator/health
```

Логи создаются в каталогах соответствующих сервисов, например:

```text
test-drives-service/logs/test-drives-service.log
```

## Порядок запуска

Каждый компонент запускайте в отдельном окне PowerShell.

### 1. PostgreSQL

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\OLD PROJECT"
docker compose up -d
```

### 2. Eureka Server

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\eureka-server"
.\mvnw.cmd spring-boot:run
```

### 3. Config Server

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\config-server"
.\mvnw.cmd spring-boot:run
```

### 4. User Service

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\user-service"
.\mvnw.cmd spring-boot:run
```

### 5. Car Service

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\car-service"
.\mvnw.cmd spring-boot:run
```

### 6. Test Drives Service

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\test-drives-service"
.\mvnw.cmd spring-boot:run
```

### 7. API Gateway

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\api-gateway"
.\mvnw.cmd spring-boot:run
```

После запуска проверьте Eureka и затем запустите клиент из раздела выше.

## Проверка

Сборка и тесты сервиса тест-драйвов:

```powershell
cd ".\test-drives-service"
.\mvnw.cmd test
```

Проверяются загрузка контекста, создание и просмотр записей, а также
разграничение операций для `CUSTOMER`, `MANAGER` и `ADMIN`.
