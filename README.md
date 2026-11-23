## Инструкция по подготовке и запуску автотестов проекта (ОС Windows)

### Подготовка инструментов:

1. Запустить Docker Desktop.
2. Запустить проект в Intellij IDEA (с дополнительно установленными плагинами Docker и Lombok).
3. Подключить Docker в IDEA через *services*.

### Подготовка окружения:
1. Создать и запустить контейнеры с БД (MySQL и PostgreSQL) командой *docker-compose up*.
2. Запустить приложение командой:
   - *java -jar artifacts/aqa-shop.jar* - для подключения к БД по умолчанию (MySQL) или
   - *java "-Ddb.url=jdbc:postgresql://localhost:5432/db_postgres" "-Ddb.user=user" "-Ddb.pass=user" -jar artifacts/aqa-shop.jar* - для подключения к БД PostgreSQL.

### Запуск тестов:

1. При работе с БД по умолчанию (MySQL) - команда *./gradlew clean test*.
2. При работе с БД PostgreSQL - команда *./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/db_postgres" "-Ddb.user=user" "-Ddb.pass=user*.

### Формирование отчёта AllureReport:

1. Сгенерировать отчёт по итогам тестирования командой *./gradlew allureReport*