# Explore With Me (EWM)
Учебный проект.

Приложение позволяет пользователям делиться информацией о событиях и находить компанию для участия в них.

## Структура проекта
### Infra
#### 1) Discovery server

Eureka Server at http://localhost:8761/
```
GET http://localhost:8761/eureka/v2/apps
```

#### 2) Config Server
Spring Cloud Config Server

Получить конфиг для сервиса по API:
```
GET localhost:8888/service-name/default
```

#### 3) Gateway Server
Spring Cloud GateWay


### Core
#### 1) event-service
#### 2) request-service
#### 3) user-service
#### 4) comment-service
#### 5) interaction-api

### Stats
### 1) Collector:
* Принимает сообщения о действиях пользователей, используя gRPC. 
* Записывает полученные данные в Kafka-топик `stats.user-actions.v1` для дальнейшей обработки.
### 2) Aggregator:
* читает данные из топика `stats.user-actions.v1`,
* рассчитывает сходство мероприятий,
* записывает результаты в топик `stats.events-similarity.v1.`
### 3) Analyzer:
* Читает данные из топиков:
  * `stats.user-actions.v1` — для хранения информации о последней оценке (максимальном весе действия) пользователей 
  мероприятий;
  * `stats.events-similarity.v1` — для хранения информации о сходстве мероприятий.
* Обновляет базу данных на основе поступающих данных.
* Обрабатывает запросы от других сервисов по gRPC и предоставляет:
  * список рекомендуемых мероприятий для конкретного пользователя на основе сходства мероприятий;
  * список мероприятий, похожих на указанное, с которыми пользователь ещё не взаимодействовал.

Прочитать сообщения из топика Kafka в докере:
```bash
docker exec -it kafka bash
 /bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic stats.user-actions.v1 --from-beginning
```