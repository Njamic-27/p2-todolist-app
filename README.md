# To Do List app

ToDoList app using Spring Boot and Thymeleaf templates.

## Requirements

You need to install on your system:

- Java 8 SDK

## Ejecución

You can run the app using the goal `run` from Maven's _plugin_ 
on Spring Boot:

```
$ ./mvn spring-boot:run 
```   

You can already create a `jar` file and run it:

```
$ ./mvn package
$ java -jar target/todolist-inicial-0.0.1-SNAPSHOT.jar 
```


Once the app is running, you can open your favourite browser and connect to:

- [http://localhost:8080/login](http://localhost:8080/login)

---

## Trello board

- [Trello board](https://trello.com/invite/b/69b8392e314edc5e78153591/ATTIb9faf682c49f33f3b63bc01d06bd8e6865DEB8FB/p2-to-do-list-app)

---

## In progress: Version 1.1.0

### Menu Bar Feature

A common navigation bar (Bootstrap Navbar) has been added to the application.

#### Functionality

- The navbar is displayed on all pages except:
    - Login page
    - Registration page

- The navbar contains:
    - **ToDoList** → link to the About page
    - **Tasks** → link to the user's task list
    - **Username (dropdown)**:
        - Account (future functionality)
        - Log out `<username>`

#### Dynamic behaviour

- On the **About page**:
    - If the user is **logged in** → full navbar is displayed
    - If the user is **not logged in** → navbar shows:
        - Login
        - Register

- On protected pages (tasks, create/edit task):
    - The full navbar with user information is always displayed

---

## Tests

New tests were added to verify the correct behavior of the Menu Bar:

### Controller / Web tests

- **About page tests**:
    - Verifies that guest users see:
        - Login
        - Register
    - Verifies that the page content is rendered correctly


### Notes

- No additional service-layer tests were required for this feature because:
    - The menu bar does not introduce new business logic
    - It relies on existing session and user services

---

## Summary

