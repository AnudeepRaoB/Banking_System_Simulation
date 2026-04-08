# Banking System Simulation

A web-based banking application that allows users to manage their accounts, perform transactions, and track their banking history in real-time.

## Features

- User Authentication: Secure registration and login system.
- Account Management: View current balance and account details.
- Banking Operations: Deposit, withdraw, and transfer funds.
- Transaction History: Detailed log of all activities including recipient names and transaction IDs.
- Data Validation: Prevents self-transfers and ensures recipient accounts exist.

## Technology Stack

- Backend: Java 21 with Javalin Framework.
- Frontend: HTML5, Vanilla CSS, and JavaScript.
- Database: MySQL.
- Build Tool: Maven.

## Prerequisites

- Java Development Kit (JDK) 21 or higher.
- Apache Maven.
- MySQL Server.

## Installation

1. Clone the repository to your local machine.
2. Ensure your MySQL server is running.
3. Create a database named `banking_sys` and execute the provided `banking_sys.sql` file to set up the tables.
4. Update the database credentials in `src/main/java/com/bank/util/DBUtil.java` if necessary.

## Running the Application

To start the application, run the following command in the project root directory:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.bank.ui.Bank"
```

Once started, the application will be available at: `http://localhost:8081`

## Project Structure

- `src/main/java`: Contains the backend logic and database access objects.
- `src/main/resources/static`: Contains the frontend web files (HTML, CSS, JS).
- `pom.xml`: Defines project dependencies and build configuration.
- `banking_sys.sql`: Database schema definition.
