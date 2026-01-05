# Backend - Payment Gateway API

Java Spring Boot backend for the Payment Gateway application.

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **Maven / Gradle**

## Project Structure

```
backend/
├── src/
│   └── main/
│       ├── java/com/gateway/
│       │   ├── PaymentGatewayApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controllers/
│       │   │   ├── HealthController.java
│       │   │   ├── OrderController.java
│       │   │   └── PaymentController.java
│       │   ├── models/
│       │   │   ├── Merchant.java
│       │   │   ├── Order.java
│       │   │   └── Payment.java
│       │   ├── repositories/
│       │   │   ├── MerchantRepository.java
│       │   │   ├── OrderRepository.java
│       │   │   └── PaymentRepository.java
│       │   ├── services/
│       │   │   ├── OrderService.java
│       │   │   ├── PaymentService.java
│       │   │   └── ValidationService.java
│       │   └── dto/
│       │       ├── OrderRequest.java
│       │       ├── OrderResponse.java
│       │       ├── PaymentRequest.java
│       │       └── PaymentResponse.java
│       └── resources/
│           ├── application.properties
│           └── schema.sql
├── pom.xml
├── build.gradle
└── Dockerfile
```

## Prerequisites

- Java 21 or higher
- Maven 3.9+ or Gradle 8+
- Docker (optional)

## Getting Started

### Using Maven

```bash
# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

### Using Gradle

```bash
# Install dependencies
gradle build

# Run the application
gradle bootRun
```

### Using Docker

```bash
# Build the image
docker build -t payment-gateway-backend .

# Run the container
docker run -p 5000:5000 payment-gateway-backend
```

## Configuration

Edit `src/main/resources/application.properties` to configure:

- Database connection
- Server port
- CORS settings
- Logging levels
- Payment gateway settings

## API Endpoints

### Health Check
- `GET /api/health` - Check API health status

### Orders
- `POST /api/orders` - Create a new order
- `GET /api/orders/{orderId}` - Get order details
- `GET /api/orders/merchant/{merchantId}` - Get merchant orders
- `PUT /api/orders/{orderId}/status` - Update order status

### Payments
- `POST /api/payment/process` - Process a payment
- `POST /api/payment/hosted/initiate` - Initiate hosted checkout
- `GET /api/payment/hosted/{sessionId}` - Get hosted session details
- `POST /api/payment/hosted/{sessionId}/complete` - Complete hosted payment
- `POST /api/payment/webhook` - Handle payment webhooks
- `GET /api/payment/{paymentId}` - Get payment details
- `GET /api/payment/order/{orderId}` - Get order payments

## Database

The application uses PostgreSQL. Schema is automatically created/updated using Hibernate DDL auto-update feature.

See `src/main/resources/schema.sql` for the complete database schema.

## Development

The application runs on port 5000 by default. To change the port, update the `server.port` property in `application.properties`.

## Testing

```bash
# Run tests with Maven
mvn test

# Run tests with Gradle
gradle test
```

## Building for Production

```bash
# Maven
mvn clean package -DskipTests

# Gradle
gradle build -x test
```

The JAR file will be created in `target/` (Maven) or `build/libs/` (Gradle) directory.
