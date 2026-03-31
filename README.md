# To Do List App

A Task Management application built with Spring Boot and Thymeleaf templates. This application allows users to create, manage, and organize their daily tasks with a clean and intuitive user interface.

## Table of Contents

- [Requirements](#requirements)
- [Installation & Execution](#installation--ejecución)
- [Running the Application](#running-the-application)
- [Features](#features)
- [Project Planning](#project-planning)
- [Testing](#testing)
- [Project Structure](#project-structure)

---

## Requirements

You need to install on your system:

- Java 8 SDK or higher
- Maven 3.6+ (for building the project)

---

## Installation & Ejecución

### Using Maven Plugin

You can run the app using the goal `run` from Maven's _plugin_ on Spring Boot:

```bash
$ ./mvn spring-boot:run 
```   

### Creating and Running a JAR File

Alternatively, you can create a `jar` file and run it:

```bash
$ ./mvn package
$ java -jar target/todolist-nikola-1.0.1-SNAPSHOT.jar 
```

---

## Running the Application

Once the app is running, you can open your favorite browser and navigate to:

- **Login Page**: [http://localhost:8080/login](http://localhost:8080/login)

From here, you can:
- Register a new account
- Log in with existing credentials
- Access your task management dashboard

---

## Features

### Current Features (Version 1.0.0)

- **User Authentication**: Secure login and registration system
- **Task Management**: Create, edit, delete, and view personal tasks
- **Task Status Tracking**: Mark tasks as complete or pending
- **User Dashboard**: View all your tasks in one place
- **About Page**: Information about the application

### In Progress: Version 1.1.0

#### Menu Bar Feature

A common navigation bar (Bootstrap Navbar) has been added to the application for improved navigation and user experience.

##### Functionality

- The navbar is displayed on all pages except:
    - Login page
    - Registration page

- The navbar contains:
    - **ToDoList** → link to the About page
    - **Tasks** → link to the user's task list
    - **Username (dropdown)**:
        - Account (future functionality)
        - Log out `<username>`

##### Dynamic Behaviour

- On the **About page**:
    - If the user is **logged in** → full navbar is displayed
    - If the user is **not logged in** → navbar shows:
        - Login
        - Register

- On protected pages (tasks, create/edit task):
    - The full navbar with user information is always displayed

#### User List Feature

A new page has been added to display the list of registered users in the system.

##### Functionality

- The page is available at: `/registered`
- It displays the following information for each registered user:
    - User identifier
    - Email address

---

## Testing

Comprehensive tests have been implemented to ensure code quality and correct behavior across all features.

### Controller / Web Tests

- **About Page Tests** (`AboutPageTest`):
    - Verifies that guest users see:
        - Login link
        - Register link
    - Verifies that authenticated users see the full navigation bar
    - Verifies that the page content is rendered correctly

- **Task Web Tests** (`TareaWebTest`):
    - Verifies task creation, editing, and deletion
    - Ensures proper user authentication on protected routes
    - Tests task list display and filtering

- **User Web Tests** (`UsuarioWebTest`):
    - Tests user registration and login flows
    - Verifies authentication and authorization
    - Tests the registered users list page (`/registered`)

### Service Layer Tests

- **Task Service Tests** (`TareaServiceTest`):
    - Service-layer retrieval of all registered tasks
    - Task creation and persistence
    - Task update and deletion operations

- **User Service Tests** (`UsuarioServiceTest`):
    - Service-layer retrieval of all registered users
    - User registration and validation
    - Password handling and user account management

### Repository Tests

- **Task Repository Tests** (`TareaTest`):
    - Database operations for tasks
    - Query functionality and data persistence

- **User Repository Tests** (`UsuarioTest`):
    - Database operations for users
    - Query functionality and data persistence

All tests can be run using:

```bash
$ ./mvn test
```

---

## Project Planning

Track the development progress and upcoming features on our Trello board:

📋 **[Trello Board - P2 To Do List App](https://trello.com/invite/b/69b8392e314edc5e78153591/ATTIb9faf682c49f33f3b63bc01d06bd8e6865DEB8FB/p2-to-do-list-app)**

---

## Project Structure

```
p2-todolist-app/
├── src/
│   ├── main/
│   │   ├── java/todolist/
│   │   │   ├── Application.java
│   │   │   ├── authentication/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── messages.properties
│   │       ├── static/ (CSS, JS)
│   │       └── templates/ (Thymeleaf HTML templates)
│   └── test/
│       ├── java/todolist/
│       └── resources/
├── pom.xml
└── README.md
```

---

## Summary

This To Do List application demonstrates a complete full-stack web development approach using Spring Boot, with proper separation of concerns (MVC architecture), comprehensive testing, and a user-friendly interface. The project is actively being developed with new features such as the enhanced navigation bar and user management features in version 1.1.0.
