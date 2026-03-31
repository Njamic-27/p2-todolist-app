# To Do List App

ToDoList app using Spring Boot, Thymeleaf templates, Spring Data JPA, and H2.

The About page in the app currently shows:
- Version `1.0.1`
- Release date `24/03/2026`

## Table of Contents

- [Requirements](#requirements)
- [Ejecucion](#ejecucion)
- [Main Routes](#main-routes)
- [Functionalities](#functionalities)
- [Testing](#testing)
- [Project Planning](#project-planning)
- [Project Structure](#project-structure)

## Requirements

You need to install on your system:

- Java 8 SDK or higher
- Maven 3.6+

## Ejecucion

Run the app with Spring Boot:

```powershell
mvn spring-boot:run
```

Build and run the generated JAR:

```powershell
mvn clean package
java -jar target/todolist-nikola-1.0.1-SNAPSHOT.jar
```

When the app is running, open:
- [http://localhost:8080/login](http://localhost:8080/login)

## Main Routes

- `/` -> redirects to `/login`
- `/login` -> login form
- `/registro` -> registration form
- `/logout` -> logout and redirect to login
- `/about` -> about page with dynamic navbar
- `/usuarios/{id}/tareas` -> task list for one user
- `/usuarios/{id}/tareas/nueva` -> create task form
- `/tareas/{id}/editar` -> edit task form
- `/tareas/{id}` (`DELETE`) -> delete task
- `/registered` -> registered users list
- `/registered/{id}` -> user description page

## Functionalities

### Authentication and session

- Login with email and password (`LOGIN_OK`, `USER_NOT_FOUND`, `ERROR_PASSWORD` flows are implemented and tested)
- New user registration with validation
- Duplicate email protection during registration
- Session-based user tracking through `ManagerUserSession`

### Task management

- Create, list, edit, and delete tasks
- Each task belongs to a user
- Ownership check before task operations (user in session must match route/task owner)
- HTTP error behavior:
  - `401 Unauthorized` for non-authorized user access (`UsuarioNoLogeadoException`)
  - `404 Not Found` for missing tasks (`TareaNotFoundException`)

### Navigation and pages

- Shared navbar fragments for guests and logged-in users (`fragments.html`)
- Guest navbar on About page: `Login`, `Register`
- User navbar on protected pages: `ToDoList`, `Tasks`, user dropdown, `Log out`
- Registered users list page with links to each user description

## Testing

The project includes controller/web, service, and repository tests.

Verified with:

```powershell
mvn test -q
```

Current surefire summary:

- `AboutPageTest`: 3 tests
- `TareaWebTest`: 5 tests
- `UsuarioWebTest`: 6 tests
- `TareaServiceTest`: 5 tests
- `UsuarioServiceTest`: 8 tests
- `TareaTest`: 9 tests
- `UsuarioTest`: 6 tests

Total: **42 tests** (`Failures: 0, Errors: 0, Skipped: 0`).

### Test coverage highlights

- About page rendering and guest navbar visibility
- Login success/failure cases
- Registered users pages (`/registered`, `/registered/{id}`)
- Task web flows: list, create, edit, delete
- User and task service logic, including validation exceptions
- Repository/entity behavior for user/task persistence and relationships

## Project Planning

- [Trello board](https://trello.com/invite/b/69b8392e314edc5e78153591/ATTIb9faf682c49f33f3b63bc01d06bd8e6865DEB8FB/p2-to-do-list-app)

## Project Structure

```
p2-todolist-app/
  src/
    main/
      java/todolist/
        authentication/
        config/
        controller/
        dto/
        model/
        repository/
        service/
      resources/
        static/
        templates/
    test/
      java/todolist/
      resources/
  pom.xml
  README.md
```

