## **DriverLink Frontend Setup Guide**

This guide will walk you through setting up the **Angular frontend** for the **DriverLink** project.

---

### **Prerequisites**
Before you begin, ensure you have the following installed:
- **Node.js** (version 16 or higher).
- **Angular CLI** (version 13 or higher).
- **IDE** (e.g., VS Code, WebStorm).

---

### **Step 1: Clone the Repository**
Clone the **DriverLink** repository to your local machine:
```bash
git clone https://github.com/your-username/DriverLink.git
cd DriverLink/frontend
```

---

### **Step 2: Install Dependencies**
1. **Install Node.js:**
   - Download and install Node.js from the [official website](https://nodejs.org/).

2. **Install Angular CLI:**
   - Run the following command to install Angular CLI globally:
     ```bash
     npm install -g @angular/cli
     ```

3. **Install Project Dependencies:**
   - Navigate to the `frontend` folder and install dependencies:
     ```bash
     npm install
     ```

---

### **Step 3: Configure the Frontend**
1. **Environment Variables:**
   - Create an `environment.ts` file in `src/environments/`:
     ```typescript
     export const environment = {
       production: false,
       apiUrl: 'http://localhost:8080/api' // Backend API URL
     };
     ```

2. **Proxy Configuration (Optional):**
   - If you encounter CORS issues, set up a proxy in `proxy.conf.json`:
     ```json
     {
       "/api": {
         "target": "http://localhost:8080",
         "secure": false
       }
     }
     ```
   - Update `angular.json` to use the proxy:
     ```json
     "architect": {
       "serve": {
         "options": {
           "proxyConfig": "src/proxy.conf.json"
         }
       }
     }
     ```

---

### **Step 4: Run the Frontend**
1. **Start the Development Server:**
   - Run the following command to start the Angular development server:
     ```bash
     ng serve
     ```

2. **Access the Application:**
   - Open your browser and navigate to:
     ```
     http://localhost:4200
     ```
   - You should see the DriverLink frontend running.

---

### **Step 5: Project Structure**
The frontend project is organized as follows:
```
src/app/
├── components/   # Reusable UI components
├── pages/        # Application pages
├── services/     # API services
├── models/       # Data models
├── guards/       # Route guards
├── interceptors/ # HTTP interceptors
└── app.module.ts # Root module
```

---

### **Step 6: Contributing**
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

### **Step 7: Troubleshooting**
- **Dependency Issues:**
  - If you encounter dependency issues, delete the `node_modules` folder and reinstall dependencies:
    ```bash
    rm -rf node_modules
    npm install
    ```
- **Build Failures:**
  - Ensure all dependencies are correctly installed by running:
    ```bash
    ng build
    ```

---

### **Contact**
For any questions or issues, please contact:
- **Immaculate Nyoni** - sthwaloe@gmail.com
- **GitHub Issues** - [Open an Issue](https://github.com/your-username/DriverLink/issues)

---
