## **DriverLink Database Setup Guide**

This guide will walk you through setting up the **PostgreSQL database** for the **DriverLink** project.

---

### **Prerequisites**
Before you begin, ensure you have the following installed:
- **PostgreSQL** (version 13 or higher).
- **pgAdmin** or any PostgreSQL client for database management.

---

### **Step 1: Install PostgreSQL**
1. **Download and Install PostgreSQL:**
   - Download PostgreSQL from the [official website](https://www.postgresql.org/download/).
   - Follow the installation instructions for your operating system.

2. **Set Up PostgreSQL:**
   - During installation, set a username and password for the PostgreSQL superuser (e.g., `postgres`).
   - Note down the **port number** (default is `5432`).

---

### **Step 2: Create the Database**
1. **Open PostgreSQL Client:**
   - Use **pgAdmin** or the **psql** command-line tool.

2. **Create a New Database:**
   - Run the following SQL command to create the `driverlink_db` database:
     ```sql
     CREATE DATABASE driverlink_db;
     ```

3. **Verify the Database:**
   - Connect to the `driverlink_db` database:
     ```sql
     \c driverlink_db;
     ```

---

### **Step 3: Set Up Tables**
1. **Run SQL Scripts:**
   - Use the SQL scripts provided in the `database/` folder to create tables and populate initial data.
   - Example:
     ```sql
     CREATE TABLE users (
         user_id SERIAL PRIMARY KEY,
         name VARCHAR(100) NOT NULL,
         email VARCHAR(100) UNIQUE NOT NULL,
         password_hash VARCHAR(255) NOT NULL,
         phone_number VARCHAR(15),
         area_of_operation VARCHAR(100),
         language_preference VARCHAR(50)
     );
     ```

2. **Verify Tables:**
   - Check that the tables are created successfully:
     ```sql
     \dt
     ```

---

### **Step 4: Connect to the Backend**
1. **Update Backend Configuration:**
   - Open the `application.properties` file in the `backend` folder.
   - Update the database connection details:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/driverlink_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

2. **Test the Connection:**
   - Start the Spring Boot application and verify that it connects to the database without errors.

---

### **Step 5: Backup and Restore**
1. **Backup the Database:**
   - Use the following command to create a backup:
     ```bash
     pg_dump -U your_username -d driverlink_db -f backup.sql
     ```

2. **Restore the Database:**
   - Use the following command to restore from a backup:
     ```bash
     psql -U your_username -d driverlink_db -f backup.sql
     ```

---

### **Contact**
For any questions or issues, please contact:
- **Immaculate Nyoni** - sthwaloe@gmail.com
- **GitHub Issues** - [Open an Issue](https://github.com/your-username/DriverLink/issues)

---
