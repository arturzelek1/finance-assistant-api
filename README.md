# Finance Assistant API 📈

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

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
- **Framework**: Spring Boot 4.0.2 (Spring Web, Data JPA, Security, Redis, Actuator)
- **Database**: PostgreSQL 16
- **Cache**: Redis
- **Identity Provider**: Keycloak
- **Tooling**: Lombok, Maven, Docker & Docker Compose

## 📊 Prediction Models (Strategy Pattern)

The application utilizes the **Strategy Design Pattern**, allowing for seamless addition of new algorithms. The active model is selected via a configuration key:
`app.prediction-model=HOLT_WINTERS` (Available options: `OLS`, `HOLT_WINTERS`, `MOVING_AVERAGE`).



## ⚙️ Environment Configuration

The project supports environment separation (Dev/Prod). In `application.properties`, you can define:
- `app.environment`: Controls error detail level (returns full stack traces on `dev`).
- **Statistical Parameters**: Fine-tune `ALPHA` and `BETA` for the Holt-Winters model without code changes.

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