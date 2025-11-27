Online Quiz Platform

An interactive web application that allows users to take quizzes, track results, and compete on a global leaderboard. Reach new levels and become a quiz master.
Built with Spring Boot, Thymeleaf, and MySQL, it supports dynamic quiz creation, execution, scoring, and secure user management.

🚀 Tech Stack
Backend

Java 17

Spring Boot 3.4.0

Spring Web (REST, MVC)

Spring Data JPA

Spring Security

Spring Validation

Hibernate (via Spring Data JPA)

OpenFeign (REST client for leaderboard integration)

Frontend

Thymeleaf templating engine

Thymeleaf Spring Security integration

HTML5 / CSS3 / JavaScript

Database

MySQL with MySQL Connector/J

Build & Tools

Maven

Lombok

Testing

Spring Boot Starter Test

Spring Security Test

📌 Features
✔ Quiz Management

Create, edit, and delete quizzes

Add multiple questions per quiz

Each question supports multiple answer options

Support for marking correct answers

Categories, descriptions, images, and metadata

DTO-based validation (name length, number of options, etc.)

✔ Quiz Execution

Interactive browser-based quiz UI (Thymeleaf)

Tracks:

Correct answers (isCorrect)

User selections (isSelected)

Dynamic rendering of questions and options

Automatic scoring after submission

Display final result to the user

✔ User Management

Integrated Spring Security

Role-based access for:

Admins (quiz management, users management, taking quizzes)

QuizMasters (quiz creation, taking quizzes)

Users (taking quizzes)

✔ Leaderboard Integration

External REST API communication using OpenFeign

Submission of quiz results to external Leaderboard service

Live ranking information retrieval

✔ Validation & Safety

Server-side validation (JSR-380 / Jakarta Validation)

Detailed error feedback in UI

DTO-to-entity mappers

Prevention of invalid quizzes (minimum 2 options, name constraints, etc.)

📡 Architecture Overview
Controller → Service → Repository → Database
                 ↑
            DTO Mapper
                 ↑
            Thymeleaf UI
                 ↑
       External REST API (Leaderboard)

🔌 External Integrations
Leaderboard REST API

Used to:

Submit a user’s quiz score

Retrieve top leaderboard entries

Display ranking to users

Integrated using Spring Cloud OpenFeign.

🗄️ Database Structure (Conceptual)
Quiz

id (UUID)

name

description

imageUrl

category

Question

id (UUID)

quiz_id

text

QuestionOption

id (UUID)

question_id

text

isCorrect

isSelected (query-only field used at quiz execution)

▶️ Running the Application
Requirements

Java 17+

Maven 3+

MySQL running locally

1. Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/quizdb
spring.datasource.username=root
spring.datasource.password=*****
spring.jpa.hibernate.ddl-auto=update

2. Build the project
mvn clean install

3. Run the project
mvn spring-boot:run


App is available at:

http://localhost:8080

🧪 Testing

Run full test suite:

mvn test


Supports:

Unit tests

Security tests

Integration tests

📁 Project Structure
src/
 ├─ main/
 │   ├─ java/.../controller
 │   ├─ java/.../service
 │   ├─ java/.../repository
 │   ├─ java/.../model
 │   ├─ java/.../dto
 │   ├─ java/.../mapper
 │   └─ resources/templates (Thymeleaf)
 └─ test/

🤝 Contributing

Pull requests and improvements are welcome.
