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


// Нужно добавить обновление participantConfirmed в ивентах при подтверждении запросов