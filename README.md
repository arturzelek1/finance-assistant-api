# Finance Assistant API 📈

[![Java 21](https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F.svg?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Keycloak](https://img.shields.io/badge/Keycloak-26.0.0-EB5424?style=for-the-badge&logo=keycloak&logoColor=white)](https://www.keycloak.org/)
[![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)](LICENSE)

A robust REST API for personal finance management, featuring intelligent spending predictions based on time-series modeling.

## 🚀 Key Features

- **Transaction Management**: Full CRUD operations with category-based expense tracking.
- **Multi-Model Predictions**: Advanced forecasting engine for the upcoming month using swappable strategies:
    - **OLS (Ordinary Least Squares)**: Linear regression for detecting long-term trends.
    - **Holt-Winters (Double Exponential Smoothing)**: A sophisticated model accounting for both level and trend (Alpha/Beta smoothing).
    - **Moving Average**: Stable averaging for consistent, recurring expenses.
- **Reliability Analysis**: Every prediction includes a `modelFit` parameter (R-squared / Coefficient of Variation), quantifying the mathematical confidence of the result.
- **Dynamic Configuration**: Switch models and fine-tune parameters (e.g., Alpha/Beta) on the fly via `application.properties`.
- **Security**: Fully integrated with **Keycloak** (OAuth2 Resource Server + JWT).
- **High-Performance Architecture**: Result caching with **Redis** and a centralized, environment-aware error handler (Dev/Prod modes).

## 🛠 Tech Stack

- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 4.0.3 (Spring Web, Data JPA, Security, Redis, Actuator)
- **Database**: PostgreSQL 16
- **Cache**: Redis
- **Identity Provider**: Keycloak
- **Tooling**: Lombok, Maven, Docker & Docker Compose

## 📊 Forecasting Strategies
The engine utilizes the Strategy Design Pattern for time-series modeling. Current strategies include:

`Holt-Winters (Double Exponential Smoothing)`: Best for data with trends; uses recursive Alpha/Beta smoothing.

`Seasonal Persistence:` Predicts values based on the same month from the previous year.

`Weighted Moving Average (WMA):` Recent months have a higher impact on the forecast.

`Naive with Drift:` Baseline model that accounts for the average growth/decline over time.

`OLS (Ordinary Least Squares):` Linear regression for detecting long-term historical trends.

`Moving Average:` Simple arithmetic mean for stable, recurring expenses.



## ⚙️ Environment Configuration

The project supports environment separation (Dev/Prod). In `application.properties`, you can define:
- `app.environment`: Controls error detail level (returns full stack traces on `dev`).

## 🏗 Quick Start

### 1) Infrastructure (Postgres, Keycloak, Redis)
```bash
docker compose up -d
```
Tip: Import keycloak-export.json in the Keycloak Admin Console (port 8080) to automatically set up the realm and clients.

### 2) Build and Run

Bash
./mvnw clean install
./mvnw spring-boot:run
The API will be available at: http://localhost:8081

## 📡 Key Endpoints
| Method   | Endpoint                         | Description                                 |
|:---------|:---------------------------------|:--------------------------------------------|
| **GET**  | `/api/v1/transactions`           | Retrieve transaction list (cached in Redis) |
| **POST** | `/api/v1/predictions/next-month` | Generate an expense forecast for a category |

```JSON
{
  "category": "FOOD"
}
```
Example Response:
```
JSON
{
  "predictedAmount": 1543.50,
  "category": "FOOD",
  "targetDate": "2026-03-01T00:00:00",
  "modelFit": 0.9821
}
```

## 🛡 Security
The API acts as an OAuth2 Resource Server. Ensure your `Authorization` header contains a valid JWT issued by Keycloak:

```HTTP
Authorization: Bearer <YOUR_JWT_TOKEN>
```
## 🧪 Testing & Monitoring
Tests:

Run

```
 ./mvnw test (utilizes Testcontainers for PostgreSQL).
```

Health Check: Monitor system status at http://localhost:8081/actuator/health

### Developed by Artiz as part of a Financial Intelligence system.
