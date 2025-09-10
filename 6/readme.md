# Round Robin Load Balancing in CloudSim

This project demonstrates **Round Robin load balancing** in CloudSim by implementing a custom broker (`RoundRobinBroker`) that assigns cloudlets to VMs cyclically.

---

## 📌 Prerequisites
- Java JDK 8 or higher (`java -version` to check).  
- Visual Studio Code with **Java Extension Pack**.  
- CloudSim JAR file (e.g., `cloudsim-3.x.jar`).  
- Git (optional, for version control).

---

## 📂 Project Structure
roundrobin-cloudsim/
├─ src/
│ └─ mycloudsim/
│ └─ RoundRobinLoadBalancer.java
├─ lib/
│ └─ cloudsim-3.x.jar
└─ .vscode/
├─ settings.json
├─ launch.json
└─ tasks.json

markdown
Copy code

---

## ⚙️ Setup in VS Code

1. **Create folders**  
   - `src/mycloudsim/` → put your `RoundRobinLoadBalancer.java` here.  
   - `lib/` → place `cloudsim-3.x.jar` here.

2. **Add CloudSim JAR to classpath**  
   Create `.vscode/settings.json`:
   ```json
   {
     "java.project.referencedLibraries": [
       "lib/**/*.jar"
     ]
   }
Run the program from VS Code

Open RoundRobinLoadBalancer.java.

Click the Run ▶️ button above the main method, or press F5 to debug.

▶️ Compile & Run from Terminal
Linux / macOS
bash
Copy code
mkdir -p bin
javac -cp lib/cloudsim-3.x.jar -d bin src/mycloudsim/*.java
java -cp lib/cloudsim-3.x.jar:bin mycloudsim.RoundRobinLoadBalancer
Windows (cmd)
cmd
Copy code
mkdir bin
javac -cp lib\cloudsim-3.x.jar -d bin src\mycloudsim\*.java
java -cp lib\cloudsim-3.x.jar;bin mycloudsim.RoundRobinLoadBalancer
📊 Output
The program prints results in the format:

sql
Copy code
========== OUTPUT ==========
Cloudlet ID    STATUS    Data center ID    VM ID    Time    Start Time    Finish Time
    0          SUCCESS   2                 1        12.34   0.0           12.34
    1          SUCCESS   2                 2        11.90   0.0           11.90
...
Each line shows cloudlet ID, VM ID, execution time, start time, and finish time.

🛠️ Troubleshooting
ClassNotFoundException → CloudSim JAR not in classpath.

Package errors → Ensure file path matches package mycloudsim;.

NoSuchMethodError → Check CloudSim JAR version.

