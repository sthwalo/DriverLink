# **DriverLink Backend Setup Guide**

This guide will walk you through setting up and running the **DriverLink** backend, built with **Spring Boot** and **Gradle**.

---

## **Prerequisites**
Before you begin, ensure you have the following installed:
- **Java Development Kit (JDK) 17** or higher.
- **Gradle** (version 7.x or higher).
- **PostgreSQL** (for database connectivity).
- **IDE** (e.g., IntelliJ IDEA, Eclipse, or VS Code).

---

## **Step 1: Clone the Repository**
Clone the **DriverLink** repository to your local machine:
```bash
git clone https://github.com/your-username/DriverLink.git
cd DriverLink/backend
```

---

## **Step 2: Set Up the Database**
1. **Install PostgreSQL:**
   - Download and install PostgreSQL from [here](https://www.postgresql.org/download/).
   - Create a new database named `driverlink_db`.

2. **Update Database Configuration:**
   - Open the `application.properties` file in `src/main/resources`.
   - Update the following properties with your PostgreSQL credentials:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/driverlink_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
     ```

---

## **Step 3: Build and Run the Project**
1. **Build the Project:**
   - Run the following command to build the project:
     ```bash
     ./gradlew build
     ```

2. **Run the Application:**
   - Start the Spring Boot application using:
     ```bash
     ./gradlew bootRun
     ```
   - Alternatively, you can run the `DriverLinkApplication.java` file directly from your IDE.

3. **Verify the Setup:**
   - Open your browser or a tool like **Postman** and navigate to:
     ```
     http://localhost:8080
     ```
   - If the application is running, you should see a default Spring Boot welcome page or a custom endpoint response.

---

## **Step 4: Project Structure**
The backend project is organized as follows:
```
src/main/java/com/driverlink
├── config        # Configuration classes
├── controller    # REST controllers
├── service       # Business logic
├── repository    # Data access layer
├── model         # Entity classes
├── exception     # Custom exception handling
└── DriverLinkApplication.java # Main application class
```

---

## **Step 5: Development Workflow**
1. **Running Tests:**
   - To run unit tests, use:
     ```bash
     ./gradlew test
     ```

2. **Code Formatting:**
   - Use the built-in Gradle wrapper to format your code:
     ```bash
     ./gradlew spotlessApply
     ```

3. **Continuous Integration:**
   - The project is configured with GitHub Actions for CI/CD. Ensure all tests pass before pushing to the `main` branch.

---

## **Step 6: Contributing**
1. **Create a New Branch:**
   - Before making changes, create a new branch:
     ```bash
     git checkout -b feature/your-feature-name
     ```

2. **Commit Your Changes:**
   - Commit your changes with a descriptive message:
     ```bash
     git commit -m "Add your commit message here"
     ```

3. **Push Your Changes:**
   - Push your branch to the remote repository:
     ```bash
     git push origin feature/your-feature-name
     ```

4. **Create a Pull Request:**
   - Open a pull request on GitHub and request a review from the team.

---

## **Step 7: Troubleshooting**
- **Build Failures:**
  - Ensure all dependencies are correctly installed by running:
    ```bash
    ./gradlew clean build
    ```
- **Database Connection Issues:**
  - Verify that PostgreSQL is running and the credentials in `application.properties` are correct.

---

## **Step 8: Documentation**
- For API documentation, refer to the `API_DOCS.md` file (to be added later).
- For database schema details, refer to the `DATABASE_SCHEMA.md` file (to be added later).

---

## **Contact**
For any questions or issues, please contact:
- **Immaculate Nyoni** - sthwaloe@gmail.com
- **GitHub Issues** - [Open an Issue](https://github.com/your-username/DriverLink/issues)
