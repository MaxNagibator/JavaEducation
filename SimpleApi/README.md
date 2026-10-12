# SimpleApi

Небольшое REST API для создания и пополнения счетов. Стек: Java 25, Spring Boot,
PostgreSQL, Flyway, Maven.

## Запуск

Нужен только Docker Desktop:

```powershell
docker compose up --build
```

API будет доступно по адресу `http://localhost:8081`.

## Создать счёт

```powershell
$account = Invoke-RestMethod -Method Post `
  -Uri http://localhost:8081/api/accounts
$account
```

Пример ответа:

```json
{
  "id": "e3b9a939-75f5-4da2-999d-20f5679fd82f",
  "balance": 0.00
}
```

## Пополнить счёт

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/accounts/$($account.id)/deposits" `
  -ContentType "application/json" `
  -Body '{"amount": 150.25}'
```

Сумма должна быть больше нуля и содержать не более двух знаков после запятой.
Пополнение выполняется в транзакции с блокировкой строки счёта, поэтому параллельные
запросы не теряют обновления баланса.

## Получить баланс

Адрес можно открыть прямо в браузере:

```text
http://localhost:8081/api/accounts/<account-id>
```

## Остановка

```powershell
docker compose down
```

Чтобы дополнительно удалить данные PostgreSQL:

```powershell
docker compose down --volumes
```
