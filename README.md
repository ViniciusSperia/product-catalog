## Product Catalog API (Spring Boot + PostgreSQL)

A secure, role-based RESTful API for managing products, built with Spring Boot 3 and Java 17. Supports JWT authentication, dynamic filtering, and modular domain structure.

## Features

- JWT authentication and authorization
- Role-based access control with `@PreAuthorize`
- Public customer registration
- Product CRUD with soft delete
- Filtering, pagination, and sorting
- Wishlist system per authenticated customer
- Order module with product list and price summary
- Integration testing with H2 and TestRestTemplate
- Swagger documentation (OpenAPI 3)

## Core Modules

| Module   | Description                                |
|----------|--------------------------------------------|
| Auth     | JWT login, registration, role-based user creation |
| Product  | Full CRUD with filters, sorting, pagination, soft delete |
| Wishlist | Add/list/remove favorite products per user |
| Orders   | Create orders with item list and total cost |

## Getting Started

### 1. Clone the project

```bash
git clone https://github.com/ViniciusSperia/catalog-api.git
```

### 2. Configure database

Update `application.properties` with your local PostgreSQL setup:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/catalog_db
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### 3. Run the app

```bash
./mvnw spring-boot:run
```

### 4. Open Swagger

```
http://localhost:8080/swagger-ui.html
```

## Auth Flow

| Endpoint           | Access        | Description                    |
|--------------------|---------------|--------------------------------|
| `POST /auth/register` | Public     | Register as `CUSTOMER`        |
| `POST /auth/login`    | Public     | Returns JWT token             |
| `POST /auth/create`   | Authenticated | Create user by role          |

Role creation rules:
- `ADMIN` → all roles
- `SUPERVISOR` → `VENDOR`, `CUSTOMER`
- `VENDOR` → `CUSTOMER` only

## Project Structure

```
src/main/java/com/example/catalog
├── config        # Security filters, JWT, Swagger
├── module
│   ├── auth      # User, login, register, role logic
│   ├── product   # CRUD + filtering + specs
│   ├── wishlist  # Wishlist endpoints
│   └── order     # Order creation and validation
├── exception     # Global exception handling
```

## Testing

- Uses H2 for isolated tests
- Covers: Authentication, Product Filters, Wishlist, Orders
- Tools: `TestRestTemplate`, `@SpringBootTest`, `@Transactional`

## Key Concepts Implemented

- Spring Security with stateless JWT
- Clean modular architecture
- Global exception handler with custom error response
- DTO-based abstraction with validation
- Soft delete with `active = false`
- Integration testing with assertions and context isolation

## Author

- Developed by **Vinicius Speria**
- GitHub: [github.com/ViniciusSperia](https://github.com/ViniciusSperia)
- Email: [vinicius.speria.tech@gmail.com](mailto:vinicius.speria.tech@gmail.com)