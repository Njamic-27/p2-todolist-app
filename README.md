# To Do List App

ToDoList app using Spring Boot, Thymeleaf templates, Spring Data JPA, and H2 database.

The About page in the app currently shows:
- Version `1.0.1`
- Release date `24/03/2026`

## Table of Contents

- [Requirements](#requirements)
- [Execution](#execution)
- [Main Routes](#main-routes)
- [Functionalities](#functionalities)
  - [Authentication and Session](#authentication-and-session)
  - [Task Management](#task-management)
  - [Admin User (Optional)](#admin-user-optional)
  - [Navigation and Pages](#navigation-and-pages)
- [Testing](#testing)
- [Project Planning](#project-planning)
- [Project Structure](#project-structure)

## Requirements

You need to install on your system:

- Java 8 SDK or higher
- Maven 3.6+

## Execution

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
- `/registro` -> registration form with optional admin checkbox
- `/logout` -> logout and redirect to login
- `/about` -> about page with dynamic navbar
- `/usuarios/{id}/tareas` -> task list for a user (user route)
- `/usuarios/{id}/tareas/nueva` -> create new task form
- `/tareas/{id}/editar` -> edit task form
- `/tareas/{id}` (`DELETE`) -> delete task
- `/registered` -> registered users list (**admin-only**)
- `/registered/{id}` -> user description page (**admin-only**)
- `/registered/{id}/block` (`POST`) -> disable user login (**admin-only**)
- `/registered/{id}/unblock` (`POST`) -> enable user login (**admin-only**)

## Functionalities

### Authentication and Session

- **Login with email and password**: Three possible outcomes:
  - `LOGIN_OK` - Successful login; user redirected to task list or admin list
  - `USER_NOT_FOUND` - Email doesn't exist in database
  - `ERROR_PASSWORD` - Correct email but incorrect password
  - Regular users are redirected to `/usuarios/{id}/tareas` after successful login
  - Admin users are redirected to `/registered` (user list) after successful login
- **New user registration with validation**: Email format validation and required field checks
- **Duplicate email protection during registration**: Prevents registering with an email already in use
- **Session-based user tracking**: Managed through `ManagerUserSession` component

### Task Management

- **Create, list, edit, and delete tasks**: Full CRUD operations
- **Task ownership**: Each task belongs to a single user
- **Ownership validation**: User in session must match task owner for edit/delete operations
- **HTTP error behavior**:
  - `401 Unauthorized` for non-authenticated access (`UsuarioNoLogeadoException`)
  - `404 Not Found` for missing tasks (`TareaNotFoundException`)

### Admin User (Optional)

When signing up (registering), users have the option to register as an administrator.

#### Admin Registration Rules

- A checkbox labeled "Registrarse como administrador" appears on the registration form
- **Only if no administrator exists in the system** - The checkbox is hidden if an admin user is already registered
- **One admin maximum** - The system enforces that only one administrator user can exist
- If a user attempts to register as admin when one already exists, they receive an error message: "Ya existe un administrador en el sistema"

#### Admin Behavior

- Admin users are redirected, after logging in, to the **users list** (`/registered`) instead of their personal task list
- This allows admins to view and manage all users in the system
- Admin status is stored in the database (`is_admin` field in `usuarios` table)
- User listing and user description pages are protected for admin users only
- If a non-admin user tries to access `/registered` or `/registered/{id}`, the app returns `401 Unauthorized` with reason `Permisos insuficientes`
- Admin can disable/enable each non-admin user from the registered users page
- Disabled users cannot log in and receive: `Usuario deshabilitado. Contacte con el administrador`

#### Admin Functionality Details

- `UsuarioRepository.findByIsAdminTrue()` - Query to find the admin user
- `UsuarioService.existsAdmin()` - Check if admin exists
- `UsuarioService.getAdmin()` - Retrieve the admin user details
- `UsuarioService.registrar(UsuarioData, boolean)` - Register user with admin flag
- `UsuarioService.bloquearUsuario(Long)` - Disable user login access
- `UsuarioService.desbloquearUsuario(Long)` - Re-enable user login access

### Navigation and Pages

- **Shared navbar fragments** for guests and logged-in users (`fragments.html`)
- **Guest navbar** on About page: `Login`, `Register`
- **User navbar** on protected pages: `ToDoList`, `Tasks`, user dropdown with email, `Log out`
- **Registered users list page** with links to each user's description page
- **Dynamic admin checkbox** on registration form that appears/disappears based on admin existence

## Testing

The project includes comprehensive controller/web, service, and repository tests.

Run all tests with:

```powershell
mvn test -q
```

### Test Summary

| Test Class | Total Tests | Description |
|------------|-------------|-------------|
| `AboutPageTest` | 3 | About page rendering and navbar visibility |
| `TareaWebTest` | 5 | Task creation, listing, editing, deletion |
| `UsuarioWebTest` | 18 | User authentication, registration, admin functionality, admin-only page protection and block/unblock flows |
| `TareaServiceTest` | 5 | Task service business logic |
| `UsuarioServiceTest` | 20 | User service including admin registration, validation, admin-role checks and block/unblock logic |
| `TareaTest` (Repository) | 9 | Task persistence and relationships |
| `UsuarioTest` (Repository) | 8 | User persistence and queries (including blocked state) |

**Total: 68 tests** (`Failures: 0, Errors: 0, Skipped: 0`)

### New Admin-Related Tests

#### Service Tests (`UsuarioServiceTest`)

1. **`servicioRegistroAdministrador`**
   - Tests that a user can register as an administrator when no admin exists
   - Verifies `isAdmin` flag is set correctly
   - Validates `existsAdmin()` returns true after registration
   - Confirms admin can be retrieved via `getAdmin()`

2. **`servicioExistenceAdminRetornaFalsoCuandoNoHayAdmin`**
   - Ensures `existsAdmin()` returns false when no administrator is registered

3. **`servicioRegistroAdministradorExcepcionSiYaExisteAdmin`**
   - Validates that registering as admin when one already exists throws `UsuarioServiceException`
   - Prevents multiple administrators in the system

4. **`servicioRegistroUsuarioNoAdminFuncionaAunqueExistaAdmin`**
   - Confirms regular users can register normally even when an admin already exists
   - Regular registration is not affected by existing admin

5. **`servicioGetAdminRetornaNull`**
   - Tests that `getAdmin()` returns null when no admin is registered

#### Controller Tests (`UsuarioWebTest`)

1. **`servicioLoginAdministradorRedirigeAListaUsuarios`**
   - Verifies admin users are redirected to `/registered` instead of `/usuarios/{id}/tareas`
   - Tests that `LoginStatus.LOGIN_OK` with admin user redirects correctly

2. **`formularioRegistroMuestraCheckboxAdminCuandoNoExisteAdmin`**
   - Tests that registration form displays admin checkbox when no admin exists
   - Checks for presence of "isAdmin" field and "administrador" label

3. **`formularioRegistroNOMuestraCheckboxAdminCuandoExisteAdmin`**
   - Validates that admin checkbox is NOT shown when an admin already exists
   - Verifies "administrador" label is missing from form

4. **`registroAdministradorFuncionaCorrectamente`**
   - Tests successful admin registration via POST to `/registro`
   - Confirms POST with `isAdmin=true` parameter works correctly
   - Validates redirect to login page after successful registration

5. **`paginaUsuariosRegistradosDevuelve401SiUsuarioNoEsAdmin`**
   - Verifies non-admin users cannot access `/registered`
   - Asserts `401 Unauthorized` with reason `Permisos insuficientes`

6. **`paginaDescripcionUsuarioDevuelve401SiUsuarioNoEsAdmin`**
   - Verifies non-admin users cannot access `/registered/{id}`
   - Asserts `401 Unauthorized` with reason `Permisos insuficientes`

7. **`paginaUsuariosRegistradosDevuelve401SiNoHayUsuarioLogeado`**
   - Verifies anonymous access to `/registered` is blocked
   - Asserts `401 Unauthorized` with reason `Usuario no autorizado`

8. **`paginaDescripcionUsuarioDevuelve401SiNoHayUsuarioLogeado`**
   - Verifies anonymous access to `/registered/{id}` is blocked
   - Asserts `401 Unauthorized` with reason `Usuario no autorizado`

9. **`servicioLoginUsuarioBloqueado`**
   - Verifies blocked users cannot log in
   - Asserts blocked-login message in login form

10. **`adminPuedeBloquearUsuarioDesdeListado`**
   - Verifies admin can disable a user through `/registered/{id}/block`
   - Asserts redirection back to users list

11. **`adminPuedeDesbloquearUsuarioDesdeListado`**
   - Verifies admin can enable a user through `/registered/{id}/unblock`
   - Asserts redirection back to users list

12. **`noAdminNoPuedeBloquearUsuario`**
   - Verifies non-admin users cannot execute block action
   - Asserts `401 Unauthorized` with reason `Permisos insuficientes`

#### Service Tests for Protection Support (`UsuarioServiceTest`)

1. **`servicioEsAdministradorRetornaTrueParaAdmin`**
   - Validates admin-role check returns `true` for administrator users

2. **`servicioEsAdministradorRetornaFalseParaUsuarioNormal`**
   - Validates admin-role check returns `false` for non-admin users

3. **`servicioEsAdministradorRetornaFalseSiUsuarioNoExiste`**
   - Validates admin-role check safely returns `false` for unknown user IDs

4. **`servicioLoginUsuarioBloqueadoDevuelveUserBlocked`**
   - Verifies login returns `USER_BLOCKED` for blocked users

5. **`servicioBloquearYDesbloquearUsuario`**
   - Verifies blocked flag updates correctly after disable/enable operations

6. **`servicioNoPermiteBloquearAdministrador`**
   - Verifies the admin account cannot be blocked

7. **`servicioBloquearUsuarioNoExistenteLanzaExcepcion`**
   - Verifies blocking unknown user IDs throws `UsuarioServiceException`

### Test Coverage Highlights

- **Authentication**: Login success/failure scenarios
- **Admin Registration**: Registration with and without existing admin
- **Admin Redirect**: Admin login redirects to user list
- **Admin UI**: Checkbox visibility based on admin existence
- **Admin-only protection**: `/registered` and `/registered/{id}` blocked for non-admin users
- **User blocking by admin**: disable/enable actions from users list and blocked login denial
- **Data Validation**: Service layer prevents multiple admins
- **User Operations**: Task CRUD and permission checks
- **Exception Handling**: Proper error messages and HTTP status codes

## Database Schema

### Usuarios Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY | Auto-increment user ID |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Email address |
| `nombre` | VARCHAR(255) | | Full name |
| `password` | VARCHAR(255) | | User password |
| `fecha_nacimiento` | DATE | | Birth date (optional) |
| `is_admin` | BOOLEAN | DEFAULT FALSE | Admin flag |
| `is_blocked` | BOOLEAN | DEFAULT FALSE | Login enabled/disabled flag |

### Tareas Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY | Auto-increment task ID |
| `titulo` | VARCHAR(255) | NOT NULL | Task title |
| `usuario_id` | BIGINT | FOREIGN KEY | Reference to Usuario |

## Project Planning

- [Trello board](https://trello.com/invite/b/69b8392e314edc5e78153591/ATTIb9faf682c49f33f3b63bc01d06bd8e6865DEB8FB/p2-to-do-list-app)

## Project Structure

```
p2-todolist-app/
  src/
    main/
      java/todolist/
        authentication/
          ManagerUserSession.java
        config/
        controller/
          LoginController.java      - Login/registration with admin logic and blocked-login handling
          TareaController.java      - Task operations
          UsuarioController.java    - Admin-only user listing/viewing and block/unblock actions
          HomeController.java       - Home redirection
        dto/
          LoginData.java            - Login form data
          RegistroData.java         - Registration form (with isAdmin field)
          TareaData.java            - Task transfer object
          UsuarioData.java          - User transfer object (with isAdmin and isBlocked fields)
        model/
          Usuario.java              - User entity (with isAdmin and isBlocked fields)
          Tarea.java                - Task entity
        repository/
          UsuarioRepository.java    - User queries including admin and blocked lookups
          TareaRepository.java      - Task queries
        service/
          UsuarioService.java       - User business logic (admin checks plus block/unblock)
          TareaService.java         - Task business logic
          UsuarioServiceException.java - Custom exception
          TareaServiceException.java   - Custom exception
        controller/exception/
          PermisosInsuficientesException.java - 401 for non-admin access to protected user pages
      resources/
        templates/
          formLogin.html            - Login form
          formRegistro.html         - Registration form with admin checkbox
          formNuevaTarea.html       - New task form
          formEditarTarea.html      - Edit task form
          listaTareas.html          - User tasks list
          listaUsuarios.html        - All users list with enable/disable buttons
          descripcionUsuario.html   - User detail page
          about.html                - About page
          fragments.html            - Shared components (navbar, etc.)
        static/
          css/
          js/
        application.properties
    test/
      java/todolist/
        controller/
          AboutPageTest.java        - About page tests
          TareaWebTest.java         - Task web controller tests
          UsuarioWebTest.java       - User/auth controller tests (admin and block/unblock tests)
        service/
          TareaServiceTest.java     - Task service tests
          UsuarioServiceTest.java   - User service tests (admin and block/unblock tests)
        repository/
          TareaTest.java            - Task repository tests
          UsuarioTest.java          - User repository tests (including blocked state)
      resources/
        application.properties
        clean-db.sql
  pom.xml
  README.md
```

## Technologies Used

- **Spring Boot 2.7.14** - Java framework for building applications
- **Spring Data JPA** - Object-relational mapping and database access
- **Thymeleaf** - Server-side Java template engine
- **H2 Database** - In-memory relational database
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for tests
- **Maven** - Build automation tool
- **ModelMapper** - Object mapping library

## Notes

- The application uses H2 in-memory database, so data is lost when the application restarts
- Session is stored in memory; users must re-login after app restart
- Passwords are stored in plain text (for development only; use encryption in production)
- All Spanish comments and strings are preserved for original language support

