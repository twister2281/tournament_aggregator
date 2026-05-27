# tournament_aggregator

Spring Boot MVC/MPA проект с REST API, интеграцией с OpenDota, CRUD для команд и авторизацией через форму.

## Основные страницы

- `/login` — вход в систему
- `/register` — регистрация
- `/match-check` — проверка матча OpenDota (после авторизации)

## Авторизация

Сейчас используется база данных PostgreSQL и `Spring Security` с form login. Пользователь регистрируется через страницу `/register`, после чего входит через `/login`.

## Запуск

```bash
mvn test
```

