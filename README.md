# 🧑‍💻 Spring Boot Social Network API - Training Project

This is a training project that simulates the core features of a social network like Facebook. It is built using Spring Boot and designed to practice backend development skills including authentication, user management, posting, social interaction, and reporting.

## 🚀 Features

### ✅ Authentication & User Management
- Register with email and password (default role: USER)
- Login with OTP flow (OTP returned directly for demo purposes)
- Forgot password with reset token (returned via API instead of email)
- Change password using the reset token

### ✅ Profile
- Update personal information: full name, birthday, occupation, location, etc.
- Upload avatar image

### ✅ Social Features
- Create posts with text, image, or both
- Edit existing posts
- Comment on own or others' posts
- Like/unlike any post
- Send/accept friend requests
- View timeline (recent posts from friends)

### ✅ Weekly Report (Excel)
- Export Excel report with:
  - Number of posts in the past week
  - Number of new friends
  - Number of new likes and comments

## 🛠️ Technologies Used

- ⚙️ Spring Boot
- 🔐 Spring Security (JWT, OTP)
- 💾 MySQL
- 📦 Spring Data JPA
- 📤 Multipart file upload
- 📈 Apache POI (Excel)
- 📮 Postman (API Testing)
- 💻 IntelliJ IDEA / Eclipse
- 🐙 Git

## 📂 Project Structure (Simplified)

## 📦 Getting Started

### 1. Clone the repository
git clone https://github.com/DangDangHoccOdE/training.git

### 2. Configure your database in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/db-name
spring.datasource.username=your_username
spring.datasource.password=your_password

### 3. Run the application
./mvnw spring-boot:run
