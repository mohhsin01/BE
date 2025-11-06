# Sales Insight Backend

Spring Boot backend application for seller performance insights and analytics.

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 21** or higher
  - Download from: https://www.oracle.com/java/technologies/downloads/
  - Verify installation: `java -version`
  
- **Apache Maven 3.6+**
  - Download from: https://maven.apache.org/download.cgi
  - Verify installation: `mvn -version`
  
- **SQLite Database**
  - The application uses SQLite (included with JDBC driver)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd BE
```

### 2. Configure Database

Update the database path in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:YOUR_PATH/sellers.db
```

**Note:** The default path is `D:/Job/LTV Funds Assessment/Db/sellers.db`. Update this to match your database location.

### 3. Database Schema

Ensure your database has the following tables:

**sellers table:**
```sql
CREATE TABLE sellers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  region TEXT
);
```

**sales table:**
```sql
CREATE TABLE sales (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  seller_id INTEGER,
  date TEXT,
  quantity INTEGER,
  price REAL,
  returned INTEGER,
  FOREIGN KEY (seller_id) REFERENCES sellers(id)
);
```

## Running the Application

Using Maven:

```bash
mvn spring-boot:run
```

The application will start on **port 8080** by default.

## API Endpoints

### Get Seller Summary

**Endpoint:** `GET /api/seller/{id}/summary`

**Description:** Returns total sales, revenue, return rate, and alerts for a specific seller this week.

**Parameters:**
- `id` (path parameter): Seller ID (Long)

**Response Example:**
```json
{
  "totalSalesThisWeek": 16,
  "totalRevenueThisWeek": 3520.09,
  "returnRate": 12.5,
  "alerts": [
    "Return rate above 10%"
  ]
}
```

**Example Request:**
```bash
curl http://localhost:8080/api/seller/1/summary
```

**Response Codes:**
- `200 OK`: Success
- `404 Not Found`: Seller does not exist
- `500 Internal Server Error`: Server error

## Features

### 1. Total Sales This Week
- Counts all non-returned sales for the current week (Monday-Sunday)

### 2. Total Revenue This Week
- Calculates revenue as: `sum(price × quantity)` for all non-returned sales

### 3. Return Rate
- Calculates as: `(returns ÷ sales) × 100`
- Returns as percentage

### 4. Alerts
Two alert rules are implemented:
- **Sales Drop Alert**: Triggers if sales dropped by more than 30% vs last week
- **Return Rate Alert**: Triggers if return rate is above 10%

### 5. Performance Optimizations
- **Single Optimized Query**: Fetches all data in one query
- **Stream Operations**: Uses Java 8 streams for efficient in-memory processing
- **Caching**: Results cached for 30 seconds using Caffeine cache

### 6. Error Handling
- **404 Not Found**: Invalid seller ID
- **500 Internal Server Error**: Unexpected errors
- Consistent JSON error response format

## Configuration

Key configuration in `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:sqlite:D:/Job/LTV Funds Assessment/Db/sellers.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.sqlite.hibernate.dialect.SQLiteDialect

# Server
server.port=8080

# Cache
spring.cache.type=simple
```

## Troubleshooting

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:

```properties
server.port=8081
```

### Database Not Found

Ensure the database file exists at the specified path, or update the path in `application.properties`.

### Java Version Issues

Verify Java version:
```bash
java -version
```

Must be Java 21 or higher.

### Maven Build Fails

Clean and rebuild:
```bash
mvn clean install
```
