# 🛒 ShopArea - Spring Boot Backend

ShopArea is a full-stack e-commerce application backend built using Spring Boot.  
It provides REST APIs for authentication, product management, cart management, and business analytics.

---

## 🚀 Features

### 🔐 Authentication
- JWT-based authentication
- Role-based access (ADMIN / CUSTOMER)
- Secure endpoints using filter
- Login / Logout functionality

### 👤 Admin Functionalities
- Add Product with Image
- Delete Product
- Modify User
- View User Details
- Monthly Business Analytics
- Daily Business Analytics
- Yearly Business Analytics
- Overall Revenue Report

### 🛍 Customer Functionalities
- View Products
- Filter by Category
- Add to Cart
- Cart Item Count
- Order Placement

---

## 🛠 Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- JWT Authentication
- Maven

---

## 📂 Project Structure


src/main/java/com.example.demo
├── admincontroller
├── adminservices
├── controller
├── service
├── repository
├── entity
├── filter


---

## ⚙️ Configuration

Update your `application.properties`:


spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update


⚠ Do NOT commit real database passwords.

---

## ▶️ How To Run

1. Clone repository

git clone https://github.com/your-username/ShopArea-Backend.git


2. Open in IDE (Spring Tool Suite / IntelliJ)

3. Run:

mvn spring-boot:run


Server runs on:

http://localhost:9092


---

## 🔑 API Base URLs


/api/auth
/api/products
/api/cart
/admin/products
/admin/user
/admin/business


---

## 📊 Business Analytics APIs

- Monthly Business
- Daily Business
- Yearly Business
- Overall Revenue

---

## 🧠 Future Enhancements

- Image upload to cloud storage
- Payment gateway integration
- Docker deployment
- CI/CD pipeline
- Swagger API documentation

---

## 👨‍💻 Author

Nithin Sai  
Full Stack Developer  
