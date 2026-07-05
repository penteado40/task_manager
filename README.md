# Task Manager API

> A RESTful API for managing tasks and custom statuses, built with Spring Boot.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-informational)

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Running the App](#running-the-app)
- [API Documentation](#api-documentation)
- [API Reference](#api-reference)
  - [Statuses](#statuses)
  - [Tasks](#tasks)
  - [Field Rules](#field-rules)
  - [Error Responses](#error-responses)
- [Running Tests](#running-tests)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + AssertJ |
| Utilities | Lombok |

---

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL running locally

---

## Database Setup

Run the following in your PostgreSQL client to create the database and user:

```sql
CREATE DATABASE task_manager_db;
CREATE USER task_manager_dev WITH PASSWORD 'task_manager123';
GRANT ALL PRIVILEGES ON DATABASE task_manager_db TO task_manager_dev;
GRANT ALL ON SCHEMA public TO task_manager_dev;
```

---

## Running the App

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

On first startup, Hibernate creates all tables automatically and seeds four default statuses:

| # | Status |
|---|--------|
| 1 | To Do |
| 2 | Scheduled |
| 3 | Doing |
| 4 | Done |

---

## API Documentation

Interactive Swagger UI is available after starting the app:

```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

### Statuses

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/statuses` | List all statuses |
| `GET` | `/statuses/{id}` | Get a status by ID |
| `POST` | `/statuses` | Create a new status |
| `PUT` | `/statuses/{id}` | Rename a status |
| `DELETE` | `/statuses/{id}` | Delete a status |

**Request body** (create / rename):
```json
{ "name": "In Review" }
```

> **Note:** The four default statuses (`To Do`, `Scheduled`, `Doing`, `Done`) can be **renamed but not deleted**. Deleting a user-created status automatically reassigns its tasks to **To Do**.

---

### Tasks

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/tasks` | List all tasks |
| `GET` | `/tasks?status={id}` | Filter tasks by status |
| `GET` | `/tasks/priority?priority={1-5}` | Filter tasks by priority |
| `GET` | `/tasks/priority?priority={1-5}&status={id}` | Filter by priority and status |
| `GET` | `/tasks/{id}` | Get a task by ID |
| `POST` | `/tasks` | Create a task |
| `PUT` | `/tasks/{id}` | Update all task fields |
| `PATCH` | `/tasks/{id}/status` | Update task status only |
| `DELETE` | `/tasks/{id}` | Delete a task |

**Request body** (create / full update):
```json
{
  "title": "Fix login bug",
  "description": "Users can't log in on Safari",
  "statusId": 1,
  "priority": 5
}
```

**Request body** (status update only):
```json
{ "statusId": 3 }
```

**Response shape:**
```json
{
  "id": 1,
  "title": "Fix login bug",
  "description": "Users can't log in on Safari",
  "status": { "id": 1, "name": "To Do" },
  "priority": 5,
  "createdAt": "2026-07-05T10:00:00",
  "updatedAt": null
}
```

---

### Field Rules

| Field | Required | Constraints |
|-------|----------|-------------|
| `title` | ✅ | Non-blank string |
| `description` | ❌ | Any string |
| `statusId` | ✅ | Must reference an existing status |
| `priority` | ❌ | Integer between `1` and `5` |

---

### Error Responses

All errors return a consistent JSON envelope:

```json
{
  "status": 404,
  "error": "Task not found",
  "message": "No task with id 99"
}
```

| Status | Scenario |
|--------|----------|
| `400` | Validation failure, malformed JSON, missing required field, wrong parameter type |
| `404` | Task or status not found |
| `409` | Attempting to delete a default status |
| `500` | Unexpected server error |

---

## Running Tests

```bash
mvn test
```

The test suite covers:

- **Unit tests** — service layer and data seeder logic
- **Controller tests** — slice tests using `@WebMvcTest`
- **Repository tests** — integration tests with H2 in-memory database

> No running PostgreSQL instance is required to execute the tests.