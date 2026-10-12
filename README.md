# FYNXT Order Booking & Portfolio API

Java 17 + Spring Boot + Spring Data JPA + PostgreSQL. Implements all six endpoints described in the take-home assignment.

## Run locally

Requirements: Java 17+, Maven 3.9+, Docker.

```bash
docker compose up -d
mvn spring-boot:run
```

The app starts at `http://localhost:8080`. `schema.sql` creates the relational schema; Hibernate validates it rather than creating tables. To run tests: `mvn test`.

Override `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `PORT` through environment variables.

## Endpoints

1. `POST /api/orders` — place a BUY/SELL order.
2. `POST /api/traders/{traderId}/orders/{orderId}/fill` — fill a pending order.
3. `POST /api/traders/{traderId}/orders/{orderId}/cancel` — cancel a pending order.
4. `GET /api/traders/{traderId}/portfolio` — holdings by stock and sector totals.
5. `GET /api/traders/{traderId}/sector-overlap` — compare held symbols with benchmark baskets.
6. `POST /api/traders/{traderId}/portfolio/holdings` — directly add holdings.

### Place a BUY

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"traderId":"T001","stock":"AAPL","sector":"TECH","quantity":50,"side":"BUY"}'
```

Save the `orderId` from the response. The order starts as `PENDING`; it does not increase holdings until filled.

### Fill an order

```bash
curl -X POST http://localhost:8080/api/traders/T001/orders/ORDER_UUID/fill
```

Replace `ORDER_UUID` with the returned UUID.

### Cancel an order

```bash
curl -X POST http://localhost:8080/api/traders/T001/orders/ORDER_UUID/cancel
```

### Add initial holdings

```bash
curl -X POST http://localhost:8080/api/traders/T001/portfolio/holdings \
  -H 'Content-Type: application/json' \
  -d '{"stock":"NVDA","sector":"TECH","quantity":100}'
```

### Portfolio and overlap

```bash
curl http://localhost:8080/api/traders/T001/portfolio
curl http://localhost:8080/api/traders/T001/sector-overlap
```

