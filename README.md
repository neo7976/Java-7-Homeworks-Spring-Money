# Курсовой проект "Сервис перевода денег"

## Описание проекта

 - Первым делом нам надо собрать jar архивы с нашими spring boot приложениями. Для этого в терминале в корне нашего проект выполните команду:

**Для gradle:** ./gradlew clean build (если пишет Permission denied тогда сначала выполните chmod +x ./gradlew)

**Для maven:** ./mvnw clean package (если пишет Permission denied тогда сначала выполните chmod +x ./mvnw)

- Для Создания образа используйте команду `docker build -t moneyapp:latest .`; 
- Запуск контейнеров `docker-compose up -d`;
- Настройки создания контейнеров и образов описаны в файлах `./`  `docker-compose.yaml` и `Dockerfile`;
- Front не обрабатывает получаемые backend id запросов, поэтому стоит заглушка на `null` и проведение всех операций в очереди `(в тестах проблем не возникает и все работает корректно)`;
- Данные по картам обрабатываются, ждут подтверждения оплаты и хранятся в [репозитории](/src/main/java/sobinda/moneybysobin/repository);
- Присутствует логирование с записью в [файл](/src/main/java/sobinda/moneybysobin/log);
- Обработка [исключений](/src/main/java/sobinda/moneybysobin/advice);
- Настройка доступа с fronted к нашему [серверу](/src/main/java/sobinda/moneybysobin/config);
- Наши модели, для получения POST запросов, обработка [->](/src/main/java/sobinda/moneybysobin/model);
- Контроллер [TransferController.java](/src/main/java/sobinda/moneybysobin/controller);
- Сервис [TransferService.java](/src/main/java/sobinda/moneybysobin/service).

## Тестирование

- В проекте присутствуют JUnit [тесты](/src/test/java/sobinda/moneybysobin/repository/TransferRepositoryTest.java)
- Интеграционные тесты с использованием [testcontainers](/src/test/java/sobinda/moneybysobin/repository/DemoApplicationTest.java)
- POST [запросы](/src/test/java/sobinda/moneybysobin/request.http)

**Возможны изменения `http://192.168.99.100:5500/` и `"http://localhost::5500"` от разных версий docker и локального/запуска контейнера.**

