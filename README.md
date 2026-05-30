# tournament_aggregator

Spring Boot MVC/MPA проект с REST API, интеграцией с OpenDota, CRUD для команд и авторизацией через форму.

## Основные страницы

- `/login` — вход в систему
- `/register` — регистрация
- `/auth/steam` — вход через Steam
- `/match-check` — проверка матча OpenDota (после авторизации)
- `/admin` — админ-панель для создания команд и турниров (только `ADMIN`)
