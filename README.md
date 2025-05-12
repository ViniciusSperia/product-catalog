## Product Catalog API (Spring Boot + PostgreSQL)

This is a RESTful API built with Spring Boot 3 and Java 17 for managing a simple product catalog.

## Features

- Create, retrieve, update, and delete (soft delete) products
- Field validation with meaningful error messages
- Persistence using PostgreSQL
- Timestamps for creation and updates (automatically managed)
- Global exception handling with custom error response structure
- Clean architecture with separation of concerns (DTO, Entity, Mapper, Service, Repository, Controller)
- Logging with SLF4J

## Technologies Used

- Java 17
- Spring Boot 3.2+
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Lombok
- Hibernate ORM
- SLF4J

## How to Run Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/ViniciusSperia/catalog-api.git
   ```

2. Configure your PostgreSQL database:

    - Create a database, e.g., `catalog_db`
    - Update the `application.properties` file with your database credentials:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/catalog_db
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

3. Build and run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Test the API endpoints using Postman or curl.

## API Endpoints

### Create a Product (POST)

`POST /api/products`

```json
{
  "name": "T-shirt",
  "description": "Black cotton T-shirt",
  "price": 49.99,
  "stock": 30
}
```

### Get All Products (GET)

`GET /api/products`

### Get Product by ID (GET)

`GET /api/products/{id}`

### Update Product (PUT)

`PUT /api/products/{id}`

```json
{
  "name": "Updated T-shirt",
  "description": "Updated description",
  "price": 59.99,
  "stock": 50
}
```

### Delete Product (DELETE)

`DELETE /api/products/{id}`  
(Performs soft delete by setting `active = false`)

## Project Structure

```
src/main/java/com/example/catalog
├── controller         # REST controllers
├── dto                # Request and response DTOs
│   ├── request
│   └── response
├── exception          # Global exception handling
├── mapper             # Entity to DTO mappers
├── model              # JPA entity
├── repository         # Spring Data JPA interfaces
├── service            # Business logic
└── CatalogApplication.java
```

## What I Learned in This Project

- Building a CRUD API with Spring Boot
- Using DTOs and mappers for abstraction
- Applying field validation and centralized error handling
- Logging and exception visibility
- PostgreSQL integration and schema auto-creation
- Soft deletes using an `active` flag
- Clean code principles and layered architecture

## Author 

- Developed by Vinicius Speria
- [github.com/ViniciusSperia](https://github.com/ViniciusSperia)
- Contact: [vinicius.speria.tech@gmail.com](vinicius.speria.tech@gmail.com)
