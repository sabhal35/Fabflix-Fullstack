## Summary
Fabflix is a full-stack, enterprise-grade e-commerce platform engineered for high-concurrency traffic and high-availability data management. This repository showcases the transition from a monolithic architecture to a **containerized, orchestrated ecosystem** utilizing **Kubernetes (K8s)** and **Docker**. The system is optimized for low-latency response times under heavy load, validated through comprehensive JMeter performance benchmarking.

---

## Core Technical Features

* **Orchestrated Deployment:** Engineered a multi-node deployment strategy using **Kubernetes**, defining complex service topologies via `fabflix-deployment.yaml` and `fabflix-service.yaml`.
* **Containerization:** Developed an optimized **Docker** pipeline to ensure environment parity across development, staging, and production environments.
* **Traffic Management:** Implemented an **Ingress Controller** (`ingress.yaml`) to manage external cluster access, providing a unified entry point and efficient load distribution.
* **Performance Engineering:** Utilized **Apache JMeter** (`Project 5 Test Plan.jmx`) to conduct rigorous stress testing, identifying and resolving bottlenecks in JDBC connection pooling and CPU utilization.
* **Data Architecture:** Architected a relational schema optimized for full-text search and transactional integrity using **MySQL** (`create-table.sql`).
* **Backend Engineering:** Built a robust **Java-based** backend leveraging **Maven** (`pom.xml`) for dependency management and automated build lifecycles.

---

## System Architecture



The application is architected to separate concerns across the following layers:
1.  **Presentation Layer:** Dynamic web content served via the `WebContent` directory.
2.  **Logic Layer:** Java-based microservices located in `src`, handling core business logic, API routing, and security.
3.  **Orchestration Layer:** Kubernetes cluster managing pod lifecycles, self-healing, and horizontal scaling.
4.  **Database Layer:** A persistent MySQL storage layer designed for rapid retrieval and high data consistency.

---

## Tech Stack
* **Languages:** Java, SQL, XML, YAML
* **DevOps & Infrastructure:** Kubernetes, Docker, Ingress
* **Build Tools:** Maven
* **Testing:** Apache JMeter (Performance & Load Testing)
* **Database:** MySQL
