# ⚙️ E-Commerce Website — Backend

A scalable Spring Boot backend powering a full-stack e-commerce platform that supports diverse product categories including electronics, clothing, toys, laptops, mobiles, and more. The backend is responsible for product management, order processing, validation, image storage, and database operations while exposing clean REST APIs.

---

## 🚀 Tech Stack

* **Java**
* **Spring Boot**
* **Spring Web MVC**
* **Spring Data JPA (Hibernate)**
* **PostgreSQL**
* **Lombok**
* **Jakarta Validation**
* **Multipart File Handling**
* **RESTful APIs**
* **DTO Architecture**

---

## 🧠 Backend Architecture Overview

The project follows a layered architecture:

**Controller → Service → Repository → Database**

This approach ensures separation of concerns, maintainability, and scalability.

---

## 🧭 Full Project Workflow

### ✅ Product Module

Handles the complete lifecycle of products across multiple categories.

**Capabilities:**

* Create products with images
* Retrieve all products
* Update product details
* Delete products
* Search products using keywords

---

### 🖼️ Image Handling

* Stores images as **byte arrays** in the database
* Uses large object support for efficient storage
* Handles multipart requests to accept both JSON data and image files

---

### ✅ Order Module (Core Business Logic)

#### Entity Relationships

* **Order → One-to-Many → OrderItems**
* **OrderItem → Many-to-One → Product**
* Cascade operations automatically persist dependent entities.

---

### 🔄 Order Processing Flow

1. Accept validated order request
2. Verify product availability
3. Check stock quantity
4. Update inventory
5. Create order items
6. Persist the order with cascade behavior
7. Convert entities into response DTOs
8. Return meaningful HTTP status codes

---

### ✅ DTO-Based Communication

* OrderRequest
* OrderItemRequest
* OrderResponse
* OrderItemResponse

Improves API clarity, flexibility, and security.

---

### ✅ Validation & Error Handling

* Request validation using Jakarta Validation
* Structured responses with `ResponseEntity`
* Conflict handling for insufficient stock
* Clear HTTP status codes for better client communication

---

## ⚙️ Running the Project Locally

### 🔗 Repository

https://github.com/Soumadeep1221/SpringEcommerce_Backend.git

---

### 1️⃣ Install JDK

Ensure **Java 17 or higher** is installed.

Verify with:

```bash
java -version
```

---

### 2️⃣ Recommended IDE

👉 **IntelliJ IDEA (Recommended)**
Other IDEs like Eclipse or VS Code can also be used.

---

### 3️⃣ Clone the Repository

```bash
git clone https://github.com/Soumadeep1221/SpringEcommerce_Backend.git
cd SpringEcommerce_Backend
```

---

### 4️⃣ Configure PostgreSQL

Create a database in PostgreSQL, then open:

```
src/main/resources/application.properties
```

Update the following properties with your local credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/YOUR_DB_NAME
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

⚠️ The default credentials included in the project will not work on other systems — you must provide your own database username and password.

---

### 5️⃣ Run the Application

Open the project in IntelliJ and run:

```
<YourProjectName>Application.java
```

Or use:

```bash
./mvnw spring-boot:run
```

The backend server will start at:

👉 **http://localhost:8080**

---

## 🔗 API Testing Tools

* Postman

---

⭐ If this project helped you learn something new, consider giving it a star!
