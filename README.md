## Product Catalog API (Spring Boot + PostgreSQL)

This is a RESTful API built with Spring Boot 3 and Java 17 for managing a simple product catalog.

## Features

- Create, retrieve, update, and delete (soft delete) products
- Field validation with meaningful error messages
- PostgreSQL persistence with JPA/Hibernate
- Timestamps for creation and update (auto-managed)
- Global exception handling with custom response format
- Clean architecture (DTO, Entity, Mapper, Service, Repository, Controller)
- Logging with SLF4J
- Swagger UI for interactive API documentation
- Paginated and sortable product listing via `/api/products/pageable`
- Dynamic filtering by name (contains), minPrice (>=), and minStock (>=)
- Integration tests for product filtering, sorting and soft delete validation

## Technologies Used

- Java 17
- Spring Boot 3.2+
- Spring Web + Spring Data JPA
- Spring Validation
- PostgreSQL
- Lombok
- Hibernate ORM
- SLF4J Logging
- Swagger (springdoc-openapi)
- JUnit 5 + TestRestTemplate (Integration Testing)

## How to Run Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/ViniciusSperia/catalog-api.git
   ```

2. Create and configure your PostgreSQL database:

   - Create a database named `catalog_db` (or any name)
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

4. Access the Swagger UI:

   ```
   http://localhost:8080/swagger-ui.html
   ```

## API Endpoints

### Create Product (POST)

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

### Get Product by ID (GET)

```http
GET /api/products/{id}
```

### List Products with Filters and Pagination (GET)

```http
GET /api/products/pageable
```

Query Parameters (optional):

- `name` – substring to match in product name
- `minPrice` – minimum price
- `minStock` – minimum stock
- `page` – page number (default `0`)
- `size` – number of items per page (default `10`)
- `sortField` – field to sort by (default `name`)
- `direction` – `asc` or `desc`

### Update Product (PUT)

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

### Delete Product (Soft Delete)

```http
DELETE /api/products/{id}
```

Sets `active = false`.

## Project Structure

```
src/main/java/com/example/catalog
├── config         # Swagger configuration
├── controller     # REST Controllers (with Swagger + logging)
├── dto            # Request/Response DTOs
│   ├── request
│   └── response
├── exception      # Global exception handling
├── logging        # Custom log events or filters
├── mapper         # Entity ↔ DTO mapping
├── model          # JPA entity with audit + soft delete
├── repository     # Spring Data interfaces
├── service        # Business logic (with logging)
├── util           # Utility classes
└── CatalogApplication.java
```

## What I Learned in This Project

- RESTful API development with Spring Boot 3
- DTO abstraction and model mapping
- Validation and exception handling
- Logging with SLF4J
- PostgreSQL and Hibernate integration
- Swagger/OpenAPI for documentation
- Soft delete design
- Integration tests for filtered and paginated endpoints
- Code layering and clean separation of concerns

## Author

- Developed by **Vinicius Speria**
- GitHub: [github.com/ViniciusSperia](https://github.com/ViniciusSperia)
- Email: [vinicius.speria.tech@gmail.com](mailto:vinicius.speria.tech@gmail.com)
