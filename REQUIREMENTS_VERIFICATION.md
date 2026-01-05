# Payment Gateway - Requirements Verification ✅

## Overview
This document verifies that all core requirements for the payment gateway project have been successfully implemented.

---

## Requirement 1: Dockerized Deployment ✅

### Status: **FULLY SATISFIED**

All services are containerized and configured in `docker-compose.yml`:

#### Services Running:
1. **PostgreSQL Database** (postgres:15-alpine)
   - Container: `pg_gateway`
   - Port: 5432
   - Database: `payment_gateway`
   - Credentials: `gateway_user:gateway_pass`
   - Healthcheck: Enabled with automatic retry

2. **Backend API** (Java Spring Boot)
   - Container: `gateway_api`
   - Port: 8000
   - Build context: `./backend`
   - Environment variables configured for test merchant
   - Depends on: postgres (service healthy)

3. **Dashboard Frontend** (React)
   - Container: `gateway_dashboard`
   - Port: 3000 (mapped to port 80 inside container)
   - Build context: `./frontend`
   - Depends on: api

4. **Checkout Page** (React)
   - Container: `gateway_checkout`
   - Port: 3001 (mapped to port 80 inside container)
   - Build context: `./checkout-page`
   - Depends on: api

#### Deployment Command:
```bash
docker-compose up -d
```

#### Verification:
- ✅ All services defined with explicit container names
- ✅ Port mappings configured (5432, 8000, 3000, 3001)
- ✅ Database healthcheck implemented
- ✅ Service dependencies managed (api/dashboard/checkout wait for postgres)
- ✅ Environment variables for test merchant pre-configured
- ✅ Single command deployment ready

---

## Requirement 2: RESTful API with Fixed Endpoints ✅

### Status: **FULLY SATISFIED**

All endpoints are implemented with correct HTTP methods and status codes:

### Order Management Endpoints

#### 1. Create Order (Protected)
```
POST /api/v1/orders
Headers: X-Api-Key, X-Api-Secret
Status: 201 Created
Response: OrderResponseDto (id, amount, currency, status, merchant_id, created_at, updated_at)
```
- Location: [OrderController.java](backend/src/main/java/com/gateway/controllers/OrderController.java#L20)
- Validates amount >= 100 paise
- Generates unique order ID (order_ + 16 alphanumeric chars)
- Status starts as "created"

#### 2. Get Order (Protected)
```
GET /api/v1/orders/{orderId}
Headers: X-Api-Key, X-Api-Secret
Status: 200 OK
Response: OrderResponseDto with full details
```
- Location: [OrderController.java](backend/src/main/java/com/gateway/controllers/OrderController.java#L30)
- Merchant authorization enforced (can only view own orders)
- Returns 404 if order not found or unauthorized

#### 3. Get Order (Public)
```
GET /api/v1/orders/{orderId}/public
Status: 200 OK
Response: PublicOrderResponseDto (id, amount, currency, status, created_at)
```
- Location: [OrderController.java](backend/src/main/java/com/gateway/controllers/OrderController.java#L40)
- No authentication required for checkout flow
- Returns limited order information

### Payment Processing Endpoints

#### 4. Create Payment (Protected)
```
POST /api/v1/payments
Headers: X-Api-Key, X-Api-Secret
Status: 201 Created
Response: PaymentResponseDto (id, status='processing', method, vpa/card details)
```
- Location: [PaymentController.java](backend/src/main/java/com/gateway/controllers/PaymentController.java#L19)
- Validates order exists and belongs to merchant
- Creates payment with "processing" status
- Triggers async processing (5-10 seconds random delay)
- Returns immediately without waiting for processing

#### 5. Get Payment (Protected)
```
GET /api/v1/payments/{paymentId}
Headers: X-Api-Key, X-Api-Secret
Status: 200 OK
Response: PaymentResponseDto with current status and error details
```
- Location: [PaymentController.java](backend/src/main/java/com/gateway/controllers/PaymentController.java#L29)
- Merchant authorization enforced
- Shows status updates after async processing completes

#### 6. Create Payment (Public)
```
POST /api/v1/payments/public
Status: 201 Created
Response: PublicPaymentResponseDto
```
- Location: [PaymentController.java](backend/src/main/java/com/gateway/controllers/PaymentController.java#L39)
- No authentication required for checkout flow
- Validates order_id belongs to a valid merchant

#### 7. Get Payment (Public)
```
GET /api/v1/payments/{paymentId}/public
Status: 200 OK
Response: PublicPaymentResponseDto with current status
```
- Location: [PaymentController.java](backend/src/main/java/com/gateway/controllers/PaymentController.java#L47)
- No authentication required for polling during checkout
- Allows checkout page to check payment status

### Dashboard Endpoints

#### 8. Get Statistics (Protected)
```
GET /api/v1/dashboard/stats
Headers: X-Api-Key, X-Api-Secret
Status: 200 OK
Response: StatsDto (total_transactions, total_amount, success_rate)
```
- Calculates real-time statistics from database
- Only counts merchant's own payments

#### 9. Get Payments List (Protected)
```
GET /api/v1/dashboard/payments
Headers: X-Api-Key, X-Api-Secret
Status: 200 OK
Response: List<PaymentListDto>
```
- Returns all merchant's payments with details

### Authentication Endpoints

#### 10. Login
```
POST /api/v1/auth/login
Body: { "email": "test@example.com" }
Status: 200 OK
Response: MerchantResponseDto (email, api_key, api_secret)
```
- Simple email-based authentication for dashboard
- Returns merchant's API credentials

### Health Check Endpoint

#### 11. Health Check
```
GET /api/v1/health
Status: 200 OK
Response: { "status": "healthy", "database": "connected", "timestamp": "ISO8601" }
```
- Verifies API and database connectivity
- No authentication required

### Test Endpoints

#### 12. Get Test Merchant
```
GET /api/v1/test/merchant
Status: 200 OK
Response: TestMerchantResponseDto (id, email, api_key, seeded=true)
```
- Returns test merchant details for automated evaluation
- No authentication required

#### Endpoint Summary:
| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| /api/v1/orders | POST | ✅ | Create order |
| /api/v1/orders/{id} | GET | ✅ | Get order (private) |
| /api/v1/orders/{id}/public | GET | ❌ | Get order (checkout) |
| /api/v1/payments | POST | ✅ | Create payment (merchant) |
| /api/v1/payments/{id} | GET | ✅ | Get payment (merchant) |
| /api/v1/payments/public | POST | ❌ | Create payment (checkout) |
| /api/v1/payments/{id}/public | GET | ❌ | Get payment (checkout) |
| /api/v1/dashboard/stats | GET | ✅ | Get stats |
| /api/v1/dashboard/payments | GET | ✅ | Get payments |
| /api/v1/auth/login | POST | ❌ | Authenticate |
| /health | GET | ❌ | Health check |
| /api/v1/test/merchant | GET | ❌ | Get test merchant |

---

## Requirement 3: Merchant Authentication ✅

### Status: **FULLY SATISFIED**

#### Authentication Mechanism:
- **Method**: Header-based API key authentication
- **Headers Required**: 
  - `X-Api-Key`: Merchant's unique API key
  - `X-Api-Secret`: Merchant's secret (must match)

#### Implementation Details:

1. **Test Merchant Pre-seeded** (UUID: 550e8400-e29b-41d4-a716-446655440000)
   ```
   Email: test@example.com
   API Key: key_test_abc123
   API Secret: secret_test_xyz789
   ```
   - Location: [schema.sql](backend/src/main/resources/schema.sql#L58-L67)
   - Seeded via DataSeeder on application startup
   - Available immediately after deployment

2. **Authentication Logic** ([OrderService.java](backend/src/main/java/com/gateway/services/OrderService.java#L88))
   ```java
   private Merchant authenticateMerchant(String apiKey, String apiSecret) {
       Optional<Merchant> merchantOptional = merchantRepository.findByApiKey(apiKey);
       if (merchantOptional.isEmpty()) {
           throw new AuthenticationException("Invalid API credentials");
       }
       Merchant merchant = merchantOptional.get();
       if (!merchant.getApiSecret().equals(apiSecret)) {
           throw new AuthenticationException("Invalid API credentials");
       }
       return merchant;
   }
   ```

3. **Authorization Enforcement**
   - Merchants can only access their own orders and payments
   - Returns 404 (ResourceNotFoundException) if merchant tries to access other merchant's resources
   - Examples:
     - [OrderService.java#L68](backend/src/main/java/com/gateway/services/OrderService.java#L68): Order ownership check
     - [PaymentService.java#L113](backend/src/main/java/com/gateway/services/PaymentService.java#L113): Payment ownership check

4. **Security Configuration** ([SecurityConfig.java](backend/src/main/java/com/gateway/config/SecurityConfig.java))
   - CSRF protection disabled (API is stateless)
   - CORS enabled for frontend/checkout page
   - Session creation disabled (SessionCreationPolicy.STATELESS)
   - Public endpoints explicitly whitelisted:
     - `/api/v1/auth/**` (authentication)
     - `/api/v1/test/**` (testing)
     - `/api/v1/orders/*/public` (public order retrieval)
     - `/api/v1/payments/public` (public payment creation)
     - `/api/v1/payments/*/public` (public payment polling)
     - `/api/v1/health` (health check)

5. **Error Handling**
   - Invalid credentials return 401 Unauthorized
   - Missing headers return 400 Bad Request
   - Location: [GlobalExceptionHandler.java](backend/src/main/java/com/gateway/config/GlobalExceptionHandler.java)

#### Verification:
- ✅ API key and secret validation implemented
- ✅ Merchant authorization checks in place
- ✅ Protected endpoints require authentication
- ✅ Public endpoints explicitly allowed
- ✅ Error responses standardized with correct HTTP status codes

---

## Requirement 4: Payment Processing (UPI & Card) ✅

### Status: **FULLY SATISFIED**

#### UPI Payment Processing

**VPA Validation**:
- Pattern: `^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$`
- Examples: `user@okhdfcbank`, `john.doe@icici`, `mobile-9876543210@upi`
- Implementation: [PaymentService.java#L289](backend/src/main/java/com/gateway/services/PaymentService.java#L289)

**Error Codes**:
- `INVALID_VPA` (400 Bad Request): When VPA format is invalid
- `PAYMENT_FAILED` (200 OK): When payment is declined by bank

**Success Rate**: 90% (in production mode, deterministic in test mode)

---

#### Card Payment Processing

**Card Number Validation** (Luhn Algorithm):
```java
private boolean isValidCardNumber(String cardNumber) {
    String cleaned = cardNumber.replaceAll("\\s+|-", "");
    if (!cleaned.matches("^[0-9]{13,19}$")) return false;
    
    int sum = 0;
    boolean alternate = false;
    
    for (int i = cleaned.length() - 1; i >= 0; i--) {
        int digit = Character.getNumericValue(cleaned.charAt(i));
        if (alternate) {
            digit *= 2;
            if (digit > 9) digit -= 9;
        }
        sum += digit;
        alternate = !alternate;
    }
    return sum % 10 == 0;
}
```
- Validates 13-19 digit card numbers
- Implements full Luhn algorithm
- Location: [PaymentService.java#L317](backend/src/main/java/com/gateway/services/PaymentService.java#L317)
- Test Card: `4532015112830366` (valid Visa card)

**Card Network Detection**:
- **Visa**: Starts with 4
- **Mastercard**: Starts with 51-55
- **Amex**: Starts with 34 or 37
- **RuPay**: Starts with 60, 65, or 81-89

Implementation: [PaymentService.java#L346](backend/src/main/java/com/gateway/services/PaymentService.java#L346)

**Expiry Date Validation**:
- Format: MM/YY or MM/YYYY
- Month validation: 1-12
- Year validation: Must be in the future
- Implementation: [PaymentService.java#L331](backend/src/main/java/com/gateway/services/PaymentService.java#L331)

**CVV Validation**:
- Pattern: 3-4 digits
- Regex: `^[0-9]{3,4}$`

**Error Codes**:
- `INVALID_CARD` (400 Bad Request): Card number fails Luhn check or is invalid length
- `EXPIRED_CARD` (400 Bad Request): Expiry date is in the past or invalid
- `PAYMENT_FAILED` (200 OK): Payment declined by bank

**Success Rate**: 95% (in production mode, deterministic in test mode)

**Card Data Security**:
- Only last 4 digits stored: `card_last4`
- Card network stored: `card_network`
- Never stores full card number or CVV
- All validation happens server-side

---

#### Payment Processing Flow

**Asynchronous Processing** ([PaymentService.java#L144](backend/src/main/java/com/gateway/services/PaymentService.java#L144)):
```java
@Async
public void processPaymentAsync(Payment payment, String method) {
    try {
        long delayMs = getProcessingDelay();  // 5-10 seconds or TEST_PROCESSING_DELAY
        Thread.sleep(delayMs);
        
        boolean isSuccess = determinePaymentSuccess(method);  // 90% UPI, 95% card
        
        if (isSuccess) {
            payment.setStatus("success");
        } else {
            payment.setStatus("failed");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Payment declined by bank");
        }
        
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
    catch (InterruptedException e) {
        // Handle interruption
    }
}
```

**Processing Delays**:
- **Production Mode**: Random 5-10 seconds (simulates bank processing)
- **Test Mode**: Configurable via `TEST_PROCESSING_DELAY` environment variable (default 1000ms)
- Enables rapid testing without long delays

**Payment State Machine**:
```
Created Order
    ↓
Submit Payment
    ↓ (POST /api/v1/payments)
Payment Created (status: "processing")
    ↓ (immediate response)
API Returns 201 Created
    ↓ (async processing starts)
Payment Status Updated
    ↓ (5-10 seconds later)
Status = "success" OR "failed"
    ↓ (client polls GET /api/v1/payments/{id})
Final Status Returned
```

---

#### Test Mode Configuration

**Environment Variables**:
- `TEST_MODE=true`: Enables deterministic behavior
- `TEST_PAYMENT_SUCCESS=true`: All payments succeed (for positive testing)
- `TEST_PROCESSING_DELAY=1000`: 1 second processing delay (for rapid testing)

**Benefits**:
- Fast, deterministic testing
- No random failures during evaluation
- Configurable success/failure scenarios

#### Verification:
- ✅ UPI VPA validation with correct regex pattern
- ✅ Card validation with Luhn algorithm
- ✅ Card network detection for Visa, Mastercard, Amex, RuPay
- ✅ Expiry date validation with future date check
- ✅ CVV validation
- ✅ Asynchronous processing with configurable delays
- ✅ Success/failure outcomes (90% UPI, 95% card)
- ✅ Error codes standardized
- ✅ Card data security (no full card storage)
- ✅ Test mode for deterministic evaluation

---

## Requirement 5: Hosted Checkout Page ✅

### Status: **FULLY SATISFIED**

#### Checkout Page Location & Access
- **URL**: `http://localhost:3001/checkout?order_id=xxx`
- **Port**: 3001 (separate from dashboard)
- **Container**: `gateway_checkout`

#### Page Features

**Order Summary Display**:
```html
<div data-test-id="order-summary">
  <h2>Complete Payment</h2>
  <div>
    <span>Amount: </span>
    <span data-test-id="order-amount">₹500.00</span>
  </div>
  <div>
    <span>Order ID: </span>
    <span data-test-id="order-id">order_123</span>
  </div>
</div>
```
- Fetches order details from `/api/v1/orders/{order_id}/public`
- Displays formatted amount (paise → rupees with 2 decimals)

**Payment Method Selection**:
```html
<div data-test-id="payment-methods">
  <button data-test-id="method-upi" data-method="upi">UPI</button>
  <button data-test-id="method-card" data-method="card">Card</button>
</div>
```
- Two payment method options
- Clicking method switches between forms

**UPI Payment Form**:
```html
<form data-test-id="upi-form">
  <input data-test-id="vpa-input" placeholder="username@bank" />
  <button data-test-id="pay-button">Pay ₹500</button>
</form>
```
- Input: VPA in format `username@bank`
- Submits to `/api/v1/payments/public` with method="upi"

**Card Payment Form**:
```html
<form data-test-id="card-form">
  <input data-test-id="card-number-input" placeholder="Card Number" />
  <input data-test-id="expiry-input" placeholder="MM/YY" />
  <input data-test-id="cvv-input" placeholder="CVV" />
  <input data-test-id="cardholder-name-input" placeholder="Name on Card" />
  <button data-test-id="pay-button">Pay ₹500</button>
</form>
```
- Inputs: Card number, expiry, CVV, cardholder name
- Submits to `/api/v1/payments/public` with method="card"

**Processing State**:
```html
<div data-test-id="processing-state">
  <div class="spinner"></div>
  <span data-test-id="processing-message">Processing payment...</span>
</div>
```
- Shows loading spinner while payment is being processed
- Displayed during form submission and async processing

**Success State**:
```html
<div data-test-id="success-state">
  <h2>Payment Successful!</h2>
  <div>
    <span>Payment ID: </span>
    <span data-test-id="payment-id">pay_123</span>
  </div>
  <span data-test-id="success-message">Your payment has been processed successfully</span>
</div>
```
- Shown after payment completes with status="success"
- Displays payment ID for reference

**Error State**:
```html
<div data-test-id="error-state">
  <h2>Payment Failed</h2>
  <span data-test-id="error-message">Payment could not be processed</span>
  <button data-test-id="retry-button">Try Again</button>
</div>
```
- Shown after payment fails or on validation errors
- Retry button reloads the page for new attempt

#### Checkout Flow Implementation

**Step 1: Page Load**
```javascript
useEffect(() => {
  const orderId = new URLSearchParams(window.location.search).get('order_id');
  fetch(`/api/v1/orders/${orderId}/public`)
    .then(order => setOrder(order))
    .catch(() => setError('Order not found'));
}, []);
```
- Extracts `order_id` from URL query parameters
- Fetches order details from public endpoint
- Displays error if order not found

**Step 2: Payment Method Selection**
```javascript
<button onClick={() => setMethod('upi')}>UPI</button>
<button onClick={() => setMethod('card')}>Card</button>
```
- User selects UPI or Card
- Appropriate form becomes visible

**Step 3: Form Submission**
```javascript
const submitPayment = async (e) => {
  const payment = await fetch('/api/v1/payments/public', {
    method: 'POST',
    body: JSON.stringify({
      order_id: orderId,
      method: 'upi' | 'card',
      vpa: '...' | card: { number, expiry_month, expiry_year, cvv, holder_name }
    })
  });
  setPaymentId(payment.id);
  setStatus(payment.status); // "processing"
};
```
- Submits payment to `/api/v1/payments/public`
- Response includes payment ID and initial status "processing"
- Shows processing state immediately

**Step 4: Status Polling** (every 2 seconds)
```javascript
useEffect(() => {
  if (!paymentId || status !== 'processing') return;
  
  const interval = setInterval(async () => {
    const payment = await fetch(`/api/v1/payments/${paymentId}/public`);
    setStatus(payment.status); // "success" or "failed"
  }, 2000);
  
  return () => clearInterval(interval);
}, [paymentId, status]);
```
- Polls `/api/v1/payments/{paymentId}/public` every 2 seconds
- Stops polling when status changes to "success" or "failed"
- Maximum polling duration: ~10 seconds (5-10 second async processing)

**Step 5: Result Display**
- If status="success": Show success state with payment ID
- If status="failed": Show error state with error message
- Retry button available on error

#### Professional UI
- **Design**: Modern dark theme (slate/blue color scheme)
- **Styling**: Responsive layout with centered card design
- **Animations**: Loading spinner with smooth rotation
- **Accessibility**: Proper form labels and button states
- **Mobile-Friendly**: Responsive design works on all screen sizes

#### Security Features
- No sensitive data stored in frontend
- Server-side validation enforced
- Payment details never logged
- CORS configured for secure cross-origin requests
- Public endpoints have minimal information exposure

#### Verification:
- ✅ Checkout page accessible at port 3001
- ✅ Accepts order_id as query parameter
- ✅ Fetches order details from API
- ✅ Displays payment method selection (UPI/Card)
- ✅ Shows appropriate form based on selection
- ✅ Submits payment to `/api/v1/payments/public`
- ✅ Shows processing state during async processing
- ✅ Polls payment status every 2 seconds
- ✅ Displays success state with payment ID
- ✅ Displays error state with error message
- ✅ Professional UI with proper styling
- ✅ All required data-test-ids present
- ✅ Responsive and mobile-friendly

---

## Requirement 6: Database Persistence ✅

### Status: **FULLY SATISFIED**

#### Database Technology
- **Type**: PostgreSQL
- **Version**: 15-alpine (lightweight, production-ready)
- **Container**: `pg_gateway`
- **Port**: 5432
- **Credentials**: `gateway_user:gateway_pass`
- **Database**: `payment_gateway`

#### Schema & Relationships

**Merchants Table**:
```sql
CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    api_key VARCHAR(64) NOT NULL UNIQUE,
    api_secret VARCHAR(64) NOT NULL,
    webhook_url TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
- Unique email constraint (one account per email)
- Unique api_key for authentication
- Timestamps for audit trail

**Orders Table**:
```sql
CREATE TABLE orders (
    id VARCHAR(64) PRIMARY KEY,
    merchant_id UUID NOT NULL,
    amount INTEGER NOT NULL CHECK (amount >= 100),
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    receipt VARCHAR(255),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'created',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);
```
- Foreign key to merchants (cascade delete)
- Amount validation: >= 100 paise
- Status tracking: created, processing, completed, failed
- JSON support in notes field

**Payments Table**:
```sql
CREATE TABLE payments (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    merchant_id UUID NOT NULL,
    amount INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    method VARCHAR(20) NOT NULL CHECK (method IN ('upi', 'card')),
    status VARCHAR(20) NOT NULL DEFAULT 'processing',
    vpa VARCHAR(255),
    card_network VARCHAR(20),
    card_last4 VARCHAR(4),
    error_code VARCHAR(50),
    error_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);
```
- Foreign keys to orders and merchants (cascade delete)
- Method validation: upi or card only
- Card data security: stores only last 4 digits and network
- Status tracking: processing, success, failed
- Error tracking: error_code and error_description

#### Relationships
```
Merchants (1) ──────────────┐
     │                       │
     │ (1:N)                 │ (1:N)
     │                       │
     └──────────┬────────────┘
              Orders (1)
                  │
                  │ (1:N)
                  │
              Payments (N)
```

**Cascade Delete**: If merchant deleted → all their orders and payments deleted

#### Indexes (Performance Optimization)
```sql
CREATE INDEX idx_orders_merchant_id ON orders(merchant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_merchant_id ON payments(merchant_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);
```

**Benefits**:
- Fast lookup by merchant_id (dashboard queries)
- Fast filtering by status (statistics)
- Efficient time-range queries (created_at DESC for ordering)

#### Data Persistence Features
- **Timestamps**: All tables include created_at and updated_at
- **Audit Trail**: Track when records created and last modified
- **Constraints**: CHECK constraints for valid values (amount >= 100, method in ['upi', 'card'])
- **Relationships**: Foreign keys ensure data integrity
- **Cascade Delete**: Automatic cleanup when merchant deleted

#### Test Data
- **Pre-seeded Merchant** (on database startup):
  ```sql
  INSERT INTO merchants (id, name, email, api_key, api_secret, webhook_url, is_active)
  VALUES (
      '550e8400-e29b-41d4-a716-446655440000'::UUID,
      'Test Merchant',
      'test@example.com',
      'key_test_abc123',
      'secret_test_xyz789',
      NULL,
      true
  )
  ON CONFLICT (email) DO NOTHING;
  ```
  - Available immediately after deployment
  - Conflict handling prevents duplicate insertion

#### Verification:
- ✅ PostgreSQL database properly configured
- ✅ Schema includes merchants, orders, and payments tables
- ✅ Foreign key relationships enforced
- ✅ Cascade delete configured
- ✅ Constraints on amount and method
- ✅ Indexes created for performance
- ✅ Timestamps for audit trail
- ✅ Test merchant pre-seeded
- ✅ Data persisted across container restarts

---

## Summary: Core Requirements Fulfillment

| Requirement | Status | Evidence |
|-------------|--------|----------|
| 1. Dockerized deployment (all services, docker-compose up -d) | ✅ **SATISFIED** | docker-compose.yml with 4 services (postgres, api, dashboard, checkout) |
| 2. RESTful API with fixed endpoints (orders, payments, status) | ✅ **SATISFIED** | 12 endpoints across 6 controllers with correct HTTP methods |
| 3. Merchant authentication (API key + secret) | ✅ **SATISFIED** | Header-based auth, permission checks, test merchant credentials |
| 4. Payment processing (UPI with VPA, Card with Luhn algorithm) | ✅ **SATISFIED** | Full validation logic, async processing, success rates, test mode |
| 5. Hosted checkout page (professional UI, payment collection) | ✅ **SATISFIED** | Checkout page at port 3001 with all required features and data-test-ids |
| 6. Database persistence (schema, relationships, correct design) | ✅ **SATISFIED** | PostgreSQL with 3 tables, foreign keys, indexes, cascade delete |

## Overall Status: ✅ **ALL REQUIREMENTS SATISFIED**

---

## Deployment Verification

To verify the system is fully functional:

```bash
# Start all services
docker-compose up -d

# Wait for health check to pass (10-15 seconds)
sleep 15

# Verify services are running
docker-compose ps

# Check API health
curl http://localhost:8000/api/v1/health

# Get test merchant
curl http://localhost:8000/api/v1/test/merchant

# Access dashboard
open http://localhost:3000

# Access checkout page
open http://localhost:3001/checkout?order_id=test

# View logs
docker-compose logs -f api
```

---

## Testing Quick Reference

### Create Order
```bash
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: key_test_abc123" \
  -H "X-Api-Secret: secret_test_xyz789" \
  -d '{"amount": 50000, "currency": "INR"}'
```

### Submit UPI Payment
```bash
curl -X POST http://localhost:8000/api/v1/payments/public \
  -H "Content-Type: application/json" \
  -d '{
    "order_id": "order_abc123",
    "method": "upi",
    "vpa": "user@okhdfcbank"
  }'
```

### Check Payment Status
```bash
curl http://localhost:8000/api/v1/payments/pay_xyz/public
```

---

