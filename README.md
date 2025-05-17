## Product Catalog API (Spring Boot + PostgreSQL)

This is a RESTful API built with Spring Boot 3 and Java 17 for managing a secure product catalog system with user authentication, role-based access control, and dynamic filtering.

## Features

- Secure user authentication with JWT
- Role-based access control:
    - `ADMIN`: can create all roles
    - `SUPERVISOR`: can create `VENDOR` and `CUSTOMER`
    - `VENDOR`: can create `CUSTOMER`
    - `CUSTOMER`: self-registration only
- Product management with full CRUD (soft delete included)
- Protected endpoints by role using `@PreAuthorize`
- Public registration for `CUSTOMER` role
- Paginated, sortable, and filterable product listing
- Specification-based dynamic query filtering
- Field validation with meaningful error messages
- PostgreSQL persistence with JPA/Hibernate
- Global exception handling with a unified response structure
- Swagger UI for interactive API documentation
- Clean layered architecture with DTO, Entity, Service, Repository, and Controller
- Logging with SLF4J
- Integration tests for authentication, filtering, and product management

## Technologies Used

- Java 17
- Spring Boot 3.2+
- Spring Web
- Spring Data JPA
- Spring Security (JWT)
- Spring Validation (Jakarta)
- PostgreSQL
- Lombok
- Hibernate ORM
- SLF4J Logging
- Swagger (springdoc-openapi)
- JUnit 5 + TestRestTemplate (Integration Testing)
- H2 (for test environment)

## How to Run Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/ViniciusSperia/catalog-api.git
   ```

2. Set up a PostgreSQL database:

    - Create a database named `catalog_db`
    - Update your `src/main/resources/application.properties`:

      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/catalog_db
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Access Swagger UI:

   ```
   http://localhost:8080/swagger-ui.html
   ```

## Authentication Endpoints

### Register as CUSTOMER (Public)

```http
POST /auth/register
```

```json
{
  "name": "Alice Customer",
  "email": "alice@example.com",
  "password": "StrongPass123!"
}
```

Creates a user with the `CUSTOMER` role.

---

### Login

```http
POST /auth/login
```

```json
{
  "email": "alice@example.com",
  "password": "StrongPass123!"
}
```

Returns a JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
```

---

### Create User by Role (Authenticated)

```http
POST /auth/create
Authorization: Bearer <your_token>
```

```json
{
  "name": "Bob Vendor",
  "email": "bob@example.com",
  "password": "Vendor123!",
  "role": "VENDOR"
}
```

Role restrictions:
- ADMIN: can create all roles
- SUPERVISOR: can create `VENDOR`, `CUSTOMER`
- VENDOR: can create `CUSTOMER`

---

## Product Endpoints

All endpoints require authentication unless otherwise noted.

### List Products with Filters and Pagination

```http
GET /api/products/pageable
Authorization: Bearer <token>
```

Optional query params:
- `name`
- `minPrice`
- `minStock`
- `page`, `size`
- `sortField`, `direction`

---

### Get Product by ID

```http
GET /api/products/{id}
```

---

### Create Product (`ADMIN` or `SUPERVISOR`)

```http
POST /api/products
```

```json
{
  "name": "T-shirt",
  "description": "Black cotton T-shirt",
  "price": 49.99,
  "stock": 30
}
```

---

### Update Product (`ADMIN` or `SUPERVISOR`)

```http
PUT /api/products/{id}
```

```json
{
  "name": "Updated T-shirt",
  "description": "Updated description",
  "price": 59.99,
  "stock": 50
}
```

---

### Delete Product (Soft Delete) (`ADMIN` only)

```http
DELETE /api/products/{id}
```

Soft deletes the product (`active = false`).

---

## Project Structure

```
src/main/java/com/example/catalog
├── config
│   └── security          # JWT, filters, password encoding
│   └── swagger           # Swagger config
├── module
│   ├── auth              # Authentication and user logic
│   │   ├── controller
│   │   ├── dto
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   ├── product           # Product CRUD logic
│   │   ├── controller
│   │   ├── dto
│   │   ├── model
│   │   ├── repository
│   │   ├── service
│   │   └── spec          # Specification filters
├── exception             # Global exception handler
├── CatalogApplication.java
```

---

## What I Learned in This Project

- JWT authentication and filter chains in Spring Security
- Role-based access using `@PreAuthorize` and role resolution
- Clean architecture design with modular domains
- DTO abstraction and validation
- Exception handling with centralized controller advice
- Swagger documentation strategy (with selective `@Schema`)
- Soft delete design and filtering only active data
- Integration testing with isolated H2 and TestRestTemplate

---

## Author

- Developed by **Vinicius Speria**
- GitHub: [github.com/ViniciusSperia](https://github.com/ViniciusSperia)
- Email: [vinicius.speria.tech@gmail.com](mailto:vinicius.speria.tech@gmail.com)