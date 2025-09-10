## 🛠️ Setup Instructions (VS Code)

Follow these steps to run the CloudSim Priority Scheduling project:

### 1. Install Java
- Install **Java JDK 8 or higher**.  
- Verify installation:
  ```bash
  java -version
  ```

### 2. Download CloudSim
- Download CloudSim 3.0.3 JAR from:  
  CloudSim GitHub Releases
- Create a `lib` folder in your project and place the JAR file inside it.

### 3. Create Project Structure
```
CloudSim-Priority/
│── lib/
│   └── cloudsim-3.0.3.jar
│── src/
│   └── mycloudsim/
│       └── PriorityExample.java
│── README.md
```

### 4. Open Project in VS Code
- Open the `CloudSim-Priority` folder in VS Code.
- Install the Java Extension Pack from the VS Code Marketplace.

### 5. Compile the Code
Run this command in the project root:
```bash
javac -cp lib/cloudsim-3.0.3.jar -d out src/mycloudsim/PriorityExample.java
```

### 6. Run the Simulation
```bash
java -cp lib/cloudsim-3.0.3.jar:out mycloudsim.PriorityExample
```