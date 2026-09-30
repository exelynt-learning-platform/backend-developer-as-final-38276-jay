# Resource Booking System API

A clean, production-ready RESTful Resource Booking System built using **Spring Boot 3.4.1**, **Java 17**, **Spring Security 6 (JWT)**, and **PostgreSQL / MySQL**.

---

## ⚙️ Configuration & Environment Variables

You can dynamically configure this application using standard environment variables. If you do not provide any, the system automatically falls back to these pre-configured values tailored for local standalone development:

| Environment Variable | Description | Default Fallback Value |
|:---|:---|:---|
| `DATABASE_URL` | JDBC database connection string | `jdbc:postgresql://localhost:5432/booking_db` |
| `DATABASE_USER` | Your database connection username | `postgres` |
| `DATABASE_PASSWORD` | Your database connection password | `postgres` |
| `DB_POOL_MAX` | Maximum active Hikari database connections | `10` |
| `JWT_SECRET_KEY` | HS256 Hex-encoded token signing key (min 256-bit) | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |
| `JWT_EXPIRATION` | How long a login token remains valid | `86400000` (24 Hours in milliseconds) |
| `ALLOWED_HOSTS` | Allowed frontend origins for CORS safety rules | `*` |

> ⚠️ **Security Note:** In the `CorsConfig.java` configuration layer, the system is currently setup to accept incoming traffic from any host. Never publish a wildcard mapping (`*`) into production environments. Ensure you replace the fallback configuration bounds with your explicit, trusted production frontend domains to protect against unauthorized cross-site data extraction.

---

## 🏗️ Step-by-Step Setup & Installation

### 1. Database Initialization
Before launching the application context, open your database manager or terminal and create the system database instance:
```sql
CREATE DATABASE booking_db;
```
*Note: You do not need to manually run any SQL schema tables. Hibernate automatically maps and updates your database structures (`ddl-auto: update`) the moment the application boots.*

### 2. Build the Application
Open your terminal at the root directory of this project and execute the Maven lifecycle command to fetch dependencies, compile your Java classes, and package them:
```bash
mvn clean package -DskipTests
```

### 3. Launch the Local Server
Once the build script outputs a success message, execute the generated JAR file to start your application on the default port `8080`:
```bash
java -jar target/resource-booking-system-0.0.1-SNAPSHOT.jar
```

---

## 🔑 Mapped Test Profiles
The database automatically seeds two default profiles onto your persistence layer during the server boot lifecycle, making evaluation workflows quick and straightforward:

| Role Authority | Mapped Identity Email | Cipher Password |
|:---|:---|:---|
| **ADMIN** | `admin@booking.local` | `admin123` |
| **USER** | `user@booking.local` | `user123` |

---

## 🗺️ Complete API Architecture & cURL Catalog

Every API route built into the system is documented below alongside fully executable terminal cURL commands.

### 1. Authentication Hub

#### 🔹 Authenticate Profile Identity
Exchanges user credentials for a stateless JWT Bearer Token required to authorize secure transactions.
- **URL**: `POST /api/v1/auth/login`
- **Access**: Public
- **cURL Command**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@booking.local", "password": "user123"}'
```

---

### 2. Resource Management Engine

#### 🔹 Browse Inventory Catalog
Retrieves a complete list of all available bookable assets inside the catalog pool.
- **URL**: `GET /api/v1/resources`
- **Access**: Public
- **cURL Command**:
```bash
curl -X GET http://localhost:8080/api/v1/resources \
  -H "Accept: application/json"
```

#### 🔹 Fetch Resource by ID
Retrieves explicit detailed attribute metadata for a single specific resource.
- **URL**: `GET /api/v1/resources/{id}`
- **Access**: Public
- **cURL Command**:
```bash
curl -X GET http://localhost:8080/api/v1/resources/1 \
  -H "Accept: application/json"
```

#### 🔹 Create New Resource
Instantiates a new bookable asset (e.g., Rooms, Equipment) within the application system.
- **URL**: `POST /api/v1/resources`
- **Access**: **ADMIN Only**
- **cURL Command**:
```bash
curl -X POST http://localhost:8080/api/v1/resources \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <PASTE_YOUR_ADMIN_TOKEN_HERE>" \
  -d '{"name": "Conference Room A", "type": "ROOM", "pricePerHour": 75.50, "available": true}'
```

#### 🔹 Update Resource Details
Mutates operational attributes or adjusts specific target hourly pricing calculations.
- **URL**: `PUT /api/v1/resources/{id}`
- **Access**: **ADMIN Only**
- **cURL Command**:
```bash
curl -X PUT http://localhost:8080/api/v1/resources/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <PASTE_YOUR_ADMIN_TOKEN_HERE>" \
  -d '{"name": "Updated Conference Room A", "type": "ROOM", "pricePerHour": 90.00, "available": true}'
```

#### 🔹 Delete Resource Item
Evicts an item partition permanently out of the accessible catalog inventory.
- **URL**: `DELETE /api/v1/resources/{id}`
- **Access**: **ADMIN Only**
- **cURL Command**:
```bash
curl -X DELETE http://localhost:8080/api/v1/resources/1 \
  -H "Authorization: Bearer <PASTE_YOUR_ADMIN_TOKEN_HERE>"
```

---

### 3. Reservation Transaction Hub

#### 🔹 Commit New Reservation Transaction
Instantiates a new booking layout. Ownership attributes are resolved implicitly by parsing the token data context.
- **URL**: `POST /api/v1/reservations`
- **Access**: **USER Only**
- **cURL Command**:
```bash
curl -X POST http://localhost:8080/api/v1/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <PASTE_YOUR_USER_TOKEN_HERE>" \
  -d '{"resourceId": 1, "startTime": "2026-10-15T09:00:00", "endTime": "2026-10-15T12:00:00"}'
```

#### 🔹 Query Paginated Transactions
Queries transaction records windowed with clear pagination parameters and multi-attribute filters.
- **Rules**: ADMIN accounts read all existing transactions; USER profiles are isolated strictly to their own booking context.
- **URL**: `GET /api/v1/reservations`
- **Access**: Mapped Session User
- **cURL Command**:
```bash
curl -X GET "http://localhost:8080/api/v1/reservations?status=PENDING&page=0&size=10&sortBy=id&sortDir=ASC" \
  -H "Authorization: Bearer <PASTE_YOUR_AUTH_TOKEN_HERE>"
```

#### 🔹 Fetch Reservation by ID
Performs a deep attributes lookup for a specific transaction while executing data boundary isolation rules.
- **URL**: `GET /api/v1/reservations/{id}`
- **Access**: **ADMIN** or the specific **Reservation Owner**
- **cURL Command**:
```bash
curl -X GET http://localhost:8080/api/v1/reservations/1 \
  -H "Authorization: Bearer <PASTE_YOUR_AUTH_TOKEN_HERE>"
```

#### 🔹 Update Reservation Lifecycle Status
Modifies states safely within business rule boundaries.
- **Rules**: ADMIN can assign any state; USER profiles can transition their own bookings *only* to `CANCELLED`.
- **URL**: `PUT /api/v1/reservations/{id}/status`
- **Access**: Mapped Session User
- **cURL Command**:
```bash
curl -X PUT http://localhost:8080/api/v1/reservations/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <PASTE_YOUR_AUTH_TOKEN_HERE>" \
  -d '{"status": "CANCELLED"}'
```

---

## 🚨 Common Hurdles & Troubleshooting

### 1. `java.lang.UnsupportedClassVersionError`
- **Why it happens**: Your computer's terminal is trying to compile or run this system using an outdated global Java version.
- **How to fix**: Run `java -version` in your terminal and update your environmental system variables configuration to target JDK 17 or 21.

### 2. Database Connection Issues
- **Why it happens**: Spring Boot fails to start up because it cannot locate your database instance on the default ports.
- **How to fix**: Verify that your local PostgreSQL or MySQL application service engine is active, and confirm you didn't forget to execute the `CREATE DATABASE booking_db;` command.

### 3. `WeakKeyException` on Token Operations
- **Why it happens**: You replaced the default `JWT_SECRET_KEY` string with a word or phrase that is too short. The secure HS256 algorithm demands a signing key length of at least 256 bits (32 bytes).
- **How to fix**: Retain our robust 64-character default Hex string or swap it with a similarly secure long key vector.

---

## 📊 Interactive Browser Documentation Sandbox
Once your application starts up successfully, you can easily evaluate all JSON request structures, trace data schemas, and trigger live requests directly from your browser interface window:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**
