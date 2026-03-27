# Expense Tracker with Budget Alerts

A Java CLI application for tracking daily expenses, setting monthly budgets, and getting budget alerts. Built as a course project covering core Java concepts.

## Tech Stack

- **Java** (JDK 17)
- **SQLite** via JDBC (no server setup needed)
- **Manual compilation** (no Maven/Gradle)

## Project Structure

```
src/com/expensetracker/
├── model/       -> User, Expense, Category (data classes)
├── service/     -> ExpenseService, UserService (business logic)
├── dao/         -> UserDAO, ExpenseDAO (JDBC database access)
├── util/        -> DBConnection (singleton connection manager)
├── exception/   -> ExpenseTrackerException (custom checked exception)
└── main/        -> Main.java (CLI entry point)
```

## How to Compile & Run

```bash
# compile
javac -encoding UTF-8 -cp "lib\sqlite-jdbc-3.46.1.3.jar" -d out src/com/expensetracker/model/*.java src/com/expensetracker/exception/*.java src/com/expensetracker/util/*.java src/com/expensetracker/dao/*.java src/com/expensetracker/service/*.java src/com/expensetracker/main/*.java

# run
java -cp "out;lib\sqlite-jdbc-3.46.1.3.jar" com.expensetracker.main.Main
```

On Linux/Mac, replace `;` with `:` in the classpath.

## Database

SQLite stores everything in a single file (`expensetracker.db`, auto-created on first run).

**Tables:**
- `users(id INTEGER PK, name TEXT, monthly_budget REAL)`
- `expenses(id INTEGER PK, user_id INTEGER FK, category TEXT, amount REAL, date TEXT)`

## Architecture & Design Decisions

The app follows a **layered architecture**:

```
Main (CLI) --> Service (business logic) --> DAO (database) --> SQLite
```

Each layer has a single responsibility:
- **Model** layer holds plain data objects that map to DB tables. `Category` is an enum to restrict inputs to valid types only (instead of raw strings which could have typos).
- **DAO** layer handles all SQL operations using PreparedStatements (prevents SQL injection). Each DAO method maps ResultSet rows back to Java objects.
- **Service** layer contains the business rules (validation, budget calculations). It doesn't know about SQL at all - it just calls DAO methods. This means we could swap SQLite for MySQL by only changing DAOs.
- **Exception** layer uses a single custom checked exception (`ExpenseTrackerException`) that wraps lower-level errors (like `SQLException`) so upper layers don't need to know about DB details. The `Throwable cause` constructor preserves the original error for debugging.

## Key Java Concepts Used

**OOP:** Encapsulation (private fields + getters/setters with validation), enums with fields and constructors, constructor overloading, Singleton pattern (DBConnection), equals/hashCode contracts for Collections compatibility.

**Collections Framework:** `ArrayList` for storing lists of expenses/users, `HashMap` for category-wise spending breakdowns, `Optional<T>` instead of null for "not found" cases.

**Streams API:** Used for filtering/aggregating data - `filter()`, `mapToDouble()`, `sum()`, `collect()`. Method references like `Expense::getAmount` and `Double::sum` as shorthand for lambdas.

**Exception Handling:** Custom checked exception with cause chaining. try-with-resources for auto-closing JDBC Statements. try-catch-finally for connection cleanup.

**JDBC:** `DriverManager` for connections, `PreparedStatement` for parameterized queries (prevents SQL injection), `Statement.RETURN_GENERATED_KEYS` to get auto-increment IDs, `ResultSet` for reading query results.

**Concurrency:** `synchronized` keyword on `getConnection()` to prevent race conditions. BudgetMonitor thread (Day 5) for background alerts.
