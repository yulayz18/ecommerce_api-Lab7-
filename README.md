# E-Commerce API - Lab 7

A Spring Boot-based REST API for managing products, categories, orders, and order items in an e-commerce platform. This project demonstrates enterprise-level application architecture with layered design patterns.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Status Codes](#status-codes)
- [Error Handling](#error-handling)
- [Validation Rules](#validation-rules)
- [Known Limitations](#known-limitations)

---

## Project Overview

**E-Commerce API** is a RESTful web service built with Spring Boot 4.0.6 and Spring Data JPA. It provides endpoints for:
- **Product Management**: CRUD operations with filtering and search capabilities
- **Category Management**: Organize products by categories
- **Order Processing**: Create and manage customer orders
- **Order Items**: Track products within orders

### Technology Stack
- **Framework**: Spring Boot 4.0.6
- **Language**: Java 17
- **Build Tool**: Gradle
- **ORM**: Spring Data JPA with Hibernate
- **Database**: MySQL (Production) / H2 (Testing)
- **Validation**: Jakarta Bean Validation
- **Library**: Project Lombok for boilerplate reduction

---

## Architecture

The application follows a **three-tier layered architecture**:

```
Controller Layer (/api/v1/*)
     ↓
Service Layer (Business Logic)
     ↓
Repository Layer (Data Access)
     ↓
Database (MySQL)
```

### Project Structure

```
src/main/java/com/ws101/cunajarito/EcommerceApi/
├── Controller/           # HTTP request handlers
│   ├── ProductController.java
│   ├── CategoryController.java
│   ├── OrderController.java
│   └── OrderItemController.java
├── service/              # Business logic
│   ├── ProductService.java
│   ├── CategoryService.java
│   ├── OrderService.java
│   └── OrderItemService.java
├── model/                # Domain entities
│   ├── Product.java
│   ├── Category.java
│   ├── Order.java
│   └── OrderItem.java
├── repository/           # Data access layer
│   ├── ProductRepository.java
│   ├── CategoryRepository.java
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── entity/               # Additional entities
│   └── FoodItems.java
├── exception/            # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ProductNotFoundException.java
│   └── (other custom exceptions)
├── config/               # Configuration
│   └── WebConfig.java
└── EcommerceApiApplication.java  # Main entry point
```

---

## Setup Instructions

### Prerequisites
- **Java 17** or higher
- **Gradle 7.0+** (or use the included wrapper `gradlew`)
- **MySQL 8.0+** (for production environment)
- **Git**

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yulayz18/ecommerce_api-Lab7-.git
   cd ecommerce_api-Lab7-
   ```

2. **Configure Database (MySQL)**

   Create a MySQL database:
   ```sql
   CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'password123';
   GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Update Application Properties**

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.application.name=EcommerceApi
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
   spring.datasource.username=ecommerce_user
   spring.datasource.password=password123
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=false
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   server.port=8080
   ```

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

5. **Run the Application**
   ```bash
   ./gradlew bootRun
   ```

   The API will start on `http://localhost:8080`

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

### Product Endpoints

#### 1. Get All Products
```http
GET /products
```
**Status**: 200 OK  
**Response**: List of all products

---

#### 2. Get Product by ID
```http
GET /products/{id}
```
**Status**: 200 OK (found), 404 Not Found  
**Path Parameters**: 
- `id` (Long, required) - Product unique identifier

---

#### 3. Filter Products
```http
GET /products/filter?filterType=<type>&filterValue=<value>
```
**Query Parameters**:
- `filterType` (String): `name`, `category`, or `priceRange`
- `filterValue` (String): The value to filter by

**Examples**:
- `/products/filter?filterType=name&filterValue=laptop`
- `/products/filter?filterType=category&filterValue=Electronics`
- `/products/filter?filterType=priceRange&filterValue=50.0,500.0`

**Status**: 200 OK

---

#### 4. Create Product
```http
POST /products
Content-Type: application/json
```
**Request Body**:
```json
{
  "name": "Wireless Headphones",
  "description": "Premium noise-cancelling headphones",
  "price": 129.99,
  "category": {
    "name": "Electronics"
  },
  "stockQuantity": 50,
  "imageUrl": "https://example.com/image.jpg"
}
```
**Status**: 201 Created  
**Validation**:
- `name`: Required, must not be blank
- `price`: Required, must be positive (> 0)
- `category`: Required, must not be null
- `stockQuantity`: Must be >= 0

---

#### 5. Update Product (Full Replacement)
```http
PUT /products/{id}
Content-Type: application/json
```
**Request Body**: Same as Create Product  
**Status**: 200 OK, 404 Not Found

---

#### 6. Partially Update Product
```http
PATCH /products/{id}
Content-Type: application/json
```
**Request Body** (only include fields to update):
```json
{
  "price": 99.99,
  "stockQuantity": 75,
  "description": "Updated description"
}
```
**Status**: 200 OK, 404 Not Found

---

#### 7. Delete Product
```http
DELETE /products/{id}
```
**Status**: 204 No Content, 404 Not Found

---

### Category Endpoints

#### 1. Get All Categories
```http
GET /categories
```
**Status**: 200 OK

---

#### 2. Get Category by ID
```http
GET /categories/{id}
```
**Status**: 200 OK, 404 Not Found

---

#### 3. Create Category
```http
POST /categories
Content-Type: application/json
```
**Request Body**:
```json
{
  "name": "Electronics",
  "description": "Electronic devices"
}
```
**Status**: 201 Created

---

### Order Endpoints

#### 1. Get All Orders
```http
GET /orders
```
**Status**: 200 OK

---

#### 2. Create Order
```http
POST /orders
Content-Type: application/json
```
**Request Body**:
```json
{
  "customerId": 1,
  "orderDate": "2025-01-15"
}
```
**Status**: 201 Created

---

## Request/Response Examples

### Example 1: Create Product

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance laptop for gaming",
    "price": 1299.99,
    "category": {"name": "Computers"},
    "stockQuantity": 10,
    "imageUrl": "https://example.com/gaming-laptop.jpg"
  }'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "description": "High-performance laptop for gaming",
  "price": 1299.99,
  "category": {
    "id": 1,
    "name": "Computers",
    "description": null
  },
  "stockQuantity": 10,
  "imageUrl": "https://example.com/gaming-laptop.jpg"
}
```

---

### Example 2: Get All Products

**Request**:
```bash
curl -X GET http://localhost:8080/api/v1/products
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "category": {
      "id": 1,
      "name": "Accessories",
      "description": null
    },
    "stockQuantity": 100,
    "imageUrl": "https://example.com/mouse.jpg"
  },
  {
    "id": 2,
    "name": "USB-C Cable",
    "description": "High-speed USB-C cable",
    "price": 9.99,
    "category": {
      "id": 1,
      "name": "Accessories",
      "description": null
    },
    "stockQuantity": 200,
    "imageUrl": "https://example.com/cable.jpg"
  }
]
```

---

### Example 3: Filter by Price Range

**Request**:
```bash
curl -X GET "http://localhost:8080/api/v1/products/filter?filterType=priceRange&filterValue=50.0,200.0"
```

**Response** (200 OK):
```json
[
  {
    "id": 3,
    "name": "Bluetooth Speaker",
    "description": "Portable Bluetooth speaker",
    "price": 79.99,
    "category": {
      "id": 2,
      "name": "Audio",
      "description": null
    },
    "stockQuantity": 45,
    "imageUrl": "https://example.com/speaker.jpg"
  }
]
```

---

### Example 4: Partial Update Product

**Request**:
```bash
curl -X PATCH http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "price": 89.99,
    "stockQuantity": 120
  }'
```

**Response** (200 OK):
```json
{
  "id": 1,
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 89.99,
  "category": {
    "id": 1,
    "name": "Accessories",
    "description": null
  },
  "stockQuantity": 120,
  "imageUrl": "https://example.com/mouse.jpg"
}
```

---

## Status Codes

| HTTP Status | Meaning | Example Scenario |
|------------|---------|------------------|
| **200 OK** | Request succeeded | GET product, successful update |
| **201 Created** | Resource created successfully | POST new product |
| **204 No Content** | Successful deletion | DELETE product |
| **400 Bad Request** | Invalid input data | Missing required field, negative price |
| **404 Not Found** | Resource not found | GET non-existent product ID |
| **500 Internal Server Error** | Server error | Database connection failure |

---

## Error Handling

The API implements a global exception handler that returns consistent error responses.

### Error Response Format
```json
{
  "timestamp": "2025-01-15T10:30:45.123456",
  "status": 404,
  "message": "Product with id 999 not found"
}
```

### Common Error Scenarios

#### 1. Product Not Found (404)
```bash
curl -X GET http://localhost:8080/api/v1/products/999
```
**Response**:
```json
{
  "timestamp": "2025-01-15T10:35:20.5678",
  "status": 404,
  "message": "Product with id 999 not found"
}
```

---

#### 2. Invalid Input Data (400)
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -50
  }'
```
**Response**:
```json
{
  "timestamp": "2025-01-15T10:40:15.9876",
  "status": 400,
  "message": "Product name must not be blank. Price must be a positive value"
}
```

---

## Validation Rules

### Product Entity Validation

| Field | Type | Rules | Example |
|-------|------|-------|---------|
| `name` | String | Required, not blank | "Gaming Laptop" |
| `description` | String | Optional | "High-performance laptop" |
| `price` | Double | Required, must be > 0 | 1299.99 |
| `category` | Category | Required, not null | { "id": 1 } |
| `stockQuantity` | Integer | Must be >= 0 | 50 |
| `imageUrl` | String | Optional | "https://..." |

### Category Entity Validation

| Field | Type | Rules | Example |
|-------|------|-------|---------|
| `name` | String | Required | "Electronics" |
| `description` | String | Optional | "Electronic devices" |

---

## Known Limitations

1. **In-Memory Product Filtering**: While the API uses a database, advanced filtering operations are performed in-memory for flexibility.

2. **Price Range Format**: The price range filter requires a comma-separated string: `"min,max"` (e.g., `"50.0,200.0"`).

3. **Category Resolution**: When creating/updating products, if a category doesn't exist, it will be automatically created.

4. **No Pagination**: List endpoints return all results. For large datasets, consider implementing pagination in future versions.

5. **Concurrent Updates**: The application does not implement optimistic locking. Concurrent updates may overwrite each other.

6. **Image URL Storage**: Image URLs are stored as strings. Actual image upload functionality is not implemented.

---

## Testing the API

### Using Postman

1. Import the API into Postman
2. Set base URL: `http://localhost:8080/api/v1`
3. Test endpoints using the provided examples

### Using curl

All examples in this README use curl. You can run them directly in your terminal.

### Using Thunder Client (VS Code Extension)

1. Install Thunder Client extension
2. Create requests for each endpoint
3. Test with the provided examples

---

## Development Notes

- **Transaction Management**: The `@Transactional` annotation on service methods ensures ACID compliance.
- **Lazy Loading**: Relationships use `FetchType.LAZY` to optimize query performance.
- **JSON Identity Info**: Jackson annotations prevent circular references in JSON serialization.
- **Validation**: All input is validated using Jakarta Bean Validation annotations.

---

## Future Enhancements

- [ ] Implement pagination for list endpoints
- [ ] Add pagination and sorting
- [ ] Implement JWT authentication
- [ ] Add user roles and permissions
- [ ] Implement image upload functionality
- [ ] Add API rate limiting
- [ ] Generate Swagger/OpenAPI documentation
- [ ] Implement caching for frequently accessed data

---

## Contributors

- **Cuna-Jarito Team**

---

## License

This project is part of the WS101 course at Perpetual Help College of Pangasinan.

---

## Support

For issues or questions, please open a GitHub issue or contact the development team.
