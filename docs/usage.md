# Руководство по использованию LiteCRM

## Как работать через Postman
* Убедитесь, что проект запущен в Docker (`docker compose up`).
* URL для всех запросов: `http://localhost:8080`
* Для `POST` и `PUT` запросов устанавливайте заголовок `Content-Type: application/json`.
* Выставите корректные параметры для отправки json запросов

![img.png](img.png)

---

## Методы LiteCRM, эндпоинты и примеры JSON

### Управление продавцами

* **`GET /sellers`** — Получить список всех продавцов.
* **`GET /sellers/{id}`** — Информация о конкретном продавце.
* **`POST /sellers`** — Создать нового продавца.
    * *Пример тела запроса:*
      ```json
      {
        "name": "Иван Иванов",
        "contactInfo": "+7 (999) 111-22-33",
        "registrationDate": "2026-05-19T12:00:00"
      }
      ```
* **`PUT /sellers/{id}`** — Обновить контактные данные продавца.
* **`DELETE /sellers/{id}`** — Удалить продавца.

### Учет транзакций

* **`GET /transactions`** — Список всех транзакций в системе.
* **`GET /transactions/{id}`** — Просмотр конкретного чека по id.
* **`GET /sellers/{id}/transactions`** — Посмотреть историю продаж конкретного сотрудника.
* **`POST /transactions`** — Создать новую транзакцию.
    * Пример тела запроса:
      ```json
      {
        "sellerId": 1,
        "amount": 15500,
        "paymentType": "CASH",
        "transactionDate": "2026-05-19T14:30:00"
      }
      ```

### Аналитика

* **`GET /analytics/most-productive/{option}`** — Найти лучшего продавца с максимальной суммой продаж за указанный период option=(day, month, quarter, year).
* **`GET /analytics/less-than?from=yyyy-mm-ddThh:mm:ss&to=yyyy-mm-ddThh:mm:ss&amount=AMOUNT_NUM`** — Список сотрудников, чья общая выручка ниже указанного лимита (параметр `amount`).
* **`GET /analytics/best-period/{Seller id}`** — Вычислить самое продуктивное время для конкретного сотрудника.

## Примеры использования

Ниже приведены примеры использования для каждого метода

**Sellers**

**`GET /sellers`**
![img_12.png](img_12.png)

**`GET /sellers/{id}`**
![img_11.png](img_11.png)

**`POST /sellers`**
![img_13.png](img_13.png)

**`PUT /sellers/{id}`**
![img_14.png](img_14.png)

**`DELETE /sellers/{id}`**
![img_15.png](img_15.png)
И вот результат:
![img_16.png](img_16.png)

**Transactions**

**`GET /transactions`**
![img_18.png](img_18.png)

**`GET /transactions/{id}`**
![img_19.png](img_19.png)

**`GET /sellers/{id}/transactions`**
![img_22.png](img_22.png)

**`POST /transactions`**
![img_20.png](img_20.png)

**Analytics**

**`GET /analytics/most-productive/{option}`**
![img_21.png](img_21.png)

**`GET /analytics/less-than?from=yyyy-mm-ddThh:mm:ss&to=yyyy-mm-ddThh:mm:ss&amount=AMOUNT_NUM`**
![img_23.png](img_23.png)

**`GET /analytics/best-period/{Seller id}`**
![img_24.png](img_24.png)