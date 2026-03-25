# Expense Tracker with Budget Alerts

A Java CLI application for university students to track daily expenses, set monthly budgets, and receive budget alerts — built as a course project demonstrating core Java concepts.

## Tech Stack

- **Language**: Java (JDK 8+)
- **Database**: SQLite (via JDBC)
- **Build**: Manual compilation (no Maven/Gradle)

## Project Structure

```
src/com/expensetracker/
├── model/       → Data classes (User, Expense, Category)
├── service/     → Business logic layer
├── dao/         → Database access (JDBC)
├── util/        → DB connection & helpers
├── exception/   → Custom exceptions
└── main/        → CLI entry point
```

## Course Concepts Covered

| Concept | Where |
|---------|-------|
| OOP (Encapsulation, Enums, Singleton) | Model & Util classes |
| Exception Handling | Custom `ExpenseTrackerException` |
| Collections Framework | Service layer (ArrayList, HashMap) |
| JDBC | DAO layer + DBConnection |
| Concurrency | BudgetMonitor thread |

## How to Compile & Run

```bash
# 1. Compile all sources (from project root)
javac -encoding UTF-8 -cp "lib\sqlite-jdbc-3.46.1.3.jar" -d out src/com/expensetracker/model/*.java src/com/expensetracker/exception/*.java src/com/expensetracker/util/*.java src/com/expensetracker/main/*.java

# 2. Run
java -cp "out;lib\sqlite-jdbc-3.46.1.3.jar" com.expensetracker.main.Main
```

> **Note**: On Linux/Mac, replace `;` with `:` in the classpath separator.

## Database

SQLite — no server setup needed. The database file `expensetracker.db` is auto-created on first run.

**Tables**:
- `users(id, name, monthly_budget)`
- `expenses(id, user_id, category, amount, date)`
