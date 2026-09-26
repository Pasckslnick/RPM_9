# PostgreSQL в папке проекта

База данных хранится в папке:

`postgres-data`

Контейнер PostgreSQL поднимается из корня проекта через Docker Compose.

## Запуск

1. Открой терминал в папке проекта.
2. Выполни:

```powershell
docker compose up -d
```

3. После запуска контейнера стартуй приложение:

```powershell
.\mvnw.cmd spring-boot:run
```

## Параметры подключения

- База данных: `pr3p7_db`
- Пользователь: `postgres`
- Пароль: `postgres`
- Порт: `5432`

## Остановка

```powershell
docker compose down
```

Если нужно удалить именно данные БД из папки проекта, удали каталог `postgres-data` после остановки контейнера.
