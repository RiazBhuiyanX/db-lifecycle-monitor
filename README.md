# 🛡️ DB Lifecycle Sentinel

**DB Lifecycle Sentinel** is a professional-grade monitoring solution designed to track the health, connectivity, and lifecycle of PostgreSQL databases. Built with **Java 21**, **PostgreSQL 16**, and **Docker**, it implements modern Cloud-Native standards for observability and reliability.

---

## ⚡ Quick Start (The "Golden" Path)

If you have **Docker Desktop** installed, you can launch the entire infrastructure in seconds without needing Java or Maven locally.

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/RiazBhuiyanX/db-lifecycle-monitor.git
    cd db-lifecycle-monitor
    ```
2.  **Configure Environment:**
    * Copy `.env.example` to a new file named `.env`.
    * Fill in your desired database credentials (User, Password, DB Name).
3.  **Run the Stack:**
    ```bash
    docker-compose up --build -d
    ```
4.  **Verify Monitoring:**
    * **Check application logs:** `docker logs -f sentinel-monitor`
    * **Check database health:** Connect your preferred DB tool to `localhost:5431`.

---

## 🏗️ Technical Stack

* **Runtime:** Java 21 (Eclipse Temurin)
* **Database:** PostgreSQL 16
* **Infrastructure:** Docker & Docker Compose
* **Logging:** SLF4J + Logback (Cloud-native STDOUT approach)
* **Build Tool:** Maven (Containerized Multi-stage builds)

---

## 🚀 The Journey: From Local Code to Cloud-Native Sentinel

This project represents an evolution in software lifecycle management, moving from a standard local setup to a resilient, containerized system.

### 1. The Local Foundation
The project began as a standard Java application connecting to a local PostgreSQL instance.
* **The Learning:** Managing database states and drivers manually is prone to human error and environmental inconsistencies.

### 2. Enter Docker: Isolation & Networking
I migrated the infrastructure into Docker to ensure the "it works on my machine" guarantee.
* **DNS & Networking:** Implemented a dedicated Docker network. Inside the network, the app communicates with the database via its service name (`db:5432`).
* **Persistent Storage:** Used **Docker Volumes** (`postgres_data`) to ensure historical "Heartbeat" records survive container restarts.

### 3. Reliability via Automated Healthchecks
To prevent the application from attempting to connect to a database that is still booting up, I implemented **Docker Healthchecks**.
* The database uses `pg_isready` to report its internal state.
* The application container is configured to wait for a "Healthy" signal before starting its lifecycle.

### 4. Optimization: Multi-stage Builds
To reach production standards, I utilized a **Multi-stage Dockerfile**.
* **Stage 1 (Builder):** Uses a Maven image to compile and run unit tests.
* **Stage 2 (Runtime):** Uses a lightweight `jre-alpine` image to run the final JAR, reducing the attack surface and image size.

---

## 🔍 Observability & Monitoring

Instead of relying on fragile local `.log` files, the Sentinel follows **SAP Observability Standards**:
* **Live Stream:** All logs are piped to `STDOUT`. Docker captures this stream, making it accessible via `docker logs` without filling up local disk space with text files.
* **Heartbeat Audit:** Every 10 seconds, a status record is inserted into the `heartbeats` table, providing a historical audit trail of database availability.

---

## 🛠️ Advanced Development Mode

The architecture supports **Horizontal Scaling** simulations. You can run the Docker stack and a local instance in your IDE simultaneously:
1.  Set `.env` to `POSTGRES_HOST=localhost` and `POSTGRES_PORT=5431`.
2.  Run `Main.java` locally from your IDE.
3.  **Observation:** You will see two "Sentinels" populating the same database in real-time—one from your IDE and one from the Docker container.

---

## 🔒 Security & Repository Hygiene
* **Environment Secrets:** All credentials are managed via `.env`, which is strictly excluded from Git via `.gitignore`.
* **Clean Repository:** Build artifacts (`target/`) and local logs are ignored to maintain a professional source code repository.

---
*Developed as part of a SAP Cloud & Lifecycle Management training project.*