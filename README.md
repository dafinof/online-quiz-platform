Online Quiz Platform

An interactive web application that allows users to take quizzes, track results, and compete on a global leaderboard.
Advance through levels, improve your score, and become a true quiz master.

Built with Spring Boot, Thymeleaf, and MySQL, the platform supports dynamic quiz creation, secure authentication, and smooth quiz execution.

🚀 Tech Stack
Backend

Java 17

Spring Boot 3.4.0

Spring Web (REST, MVC)

Spring Data JPA

Spring Security

Spring Validation

Hibernate

OpenFeign (Leaderboard REST integration)

Frontend

Thymeleaf

Thymeleaf Spring Security integration

HTML5 / CSS3 / JavaScript

Database

MySQL

MySQL Connector/J

Build & Tools

Maven

Lombok

Testing

Spring Boot Starter Test

Spring Security Test

🔧 Automatic Data Initialization (Test Users & Sample Quizzes)

To simplify first-time setup and grading, the application automatically creates default users and sample quizzes when the database starts empty.

This initialization runs only if no users exist in the system.

👤 Default Test Users
Username	  Role	      Password
admin	      ADMIN	      admin123
quizmaster	QUIZMASTER	qm123
player	    PLAYER	    player123

ADMIN and QUIZMASTER can create, take, and manage quizzes

PLAYER can take quizzes

A PLAYER can be promoted to QUIZMASTER automatically after reaching a required score threshold

📝 Sample Quizzes

A few example quizzes are also generated to speed up testing.
These quizzes can be taken immediately or used as templates for creating new ones.

📌 Features
✔ Quiz Management

Create, edit, and delete quizzes

Multiple questions per quiz

Answer options with correct/incorrect flags

Quiz metadata: categories, descriptions, images

DTO-based validation and safe mapping

Prevention of invalid quizzes (min. number of options, name constraints)

✔ Quiz Execution

Interactive UI built with Thymeleaf

Tracks:

Correct answers

User selections

Automatic scoring

Final results page

✔ User Management

Spring Security authentication

Role-based access:

ADMIN: full access

QUIZMASTER: quiz creation + taking quizzes

PLAYER: taking quizzes only

✔ Leaderboard Integration

Via OpenFeign, the app communicates with an external REST API to:

Submit user quiz scores

Retrieve top leaderboard entries

✔ Validation & Safety

JSR-380 / Jakarta Validation on DTOs

Clear error messages in UI

Strict server-side validation

Entity → DTO mappers

📡 Architecture Overview
Controller → Service → Repository → Database
       ↑          ↑          ↑
     DTO       Mapper     Thymeleaf UI
       ↑
External REST API (Leaderboard)

🗄️ Database Overview (Conceptual)
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

isSelected (used only during quiz execution)

▶️ Running the Application
Requirements

Java 17+

Maven 3+

MySQL running locally

Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/quizdb
spring.datasource.username=root
spring.datasource.password=*****
spring.jpa.hibernate.ddl-auto=update

Build the project
mvn clean install

Run the project
mvn spring-boot:run


The application will become available at:

👉 http://localhost:8080

🧪 Testing

Supports:

Unit tests

Integration tests

Security tests

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

Pull requests and improvements are welcome!
