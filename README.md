# Little Horizons Educational ERP System

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.9.6-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

An enterprise-grade Educational Resource Planning (ERP) platform developed in Java 17 for **Colegio Internacional Little Horizons S.A.S.** Built under strict **Model-View-Controller (MVC)** and **SOLID** architectural design patterns, the platform centralizes academic operations, faculty allocations, extracurricular clubs, inventory tracking (laboratories and library), executive analytics, and automated **DIAN-compliant Colombian Electronic Invoicing** with simulated **CUFE** cryptographic hashes and **ZXing QR codes**.

---

## Table of Contents
1. [Project Overview & Problem Statement](#1-project-overview--problem-statement)
2. [Authors & Academic Context](#2-authors--academic-context)
3. [Technology Stack & Dependencies](#3-technology-stack--dependencies)
4. [Prerequisites](#4-prerequisites)
5. [Database Architecture & Setup](#5-database-architecture--setup)
6. [Installation & Build Guide](#6-installation--build-guide)
7. [Running the Application](#7-running-the-application)
8. [Software Architecture (MVC Design)](#8-software-architecture-mvc-design)
9. [SOLID Principles & Software Design Patterns](#9-solid-principles--software-design-patterns)
10. [Core Functional Modules](#10-core-functional-modules)
    - [10.1 Academic Management & Enrollment](#101-academic-management--enrollment)
    - [10.2 Teacher Workload & Scheduling](#102-teacher-workload--scheduling)
    - [10.3 Extracurricular Clubs & Events](#103-extracurricular-clubs--events)
    - [10.4 Financial Billing & DIAN Electronic Invoices](#104-financial-billing--dian-electronic-invoices)
    - [10.5 Resource & Inventory Management (Library & Labs)](#105-resource--inventory-management-library--labs)
    - [10.6 Advanced Reporting & Analytics](#106-advanced-reporting--analytics)
11. [Defensive Programming, Error Handling & Logging](#11-defensive-programming-error-handling--logging)
12. [Terminal User Interface (TUI) & ASCII Demos](#12-terminal-user-interface-tui--ascii-demos)
13. [Unit & Integration Testing](#13-unit--integration-testing)
14. [Known Limitations & Operational Assumptions](#14-known-limitations--operational-assumptions)
15. [Future Roadmap & Enhancements](#15-future-roadmap--enhancements)
16. [License & Contact](#16-license--contact)

---

## 1. Project Overview & Problem Statement

### Institutional Context
**Colegio Internacional Little Horizons S.A.S.** (NIT: 901.482.391-7) is a bilingual educational institution in Colombia serving over 500 students from Preschool through 11th Grade (`Preescolar`, `1°` through `11°`), supported by more than 40 faculty members.

### The Operational Challenge
Prior to this ERP system, Little Horizons suffered from acute operational bottlenecks caused by fragmented paper registers, disparate spreadsheets, and disconnected manual records:
- **Academic Data Loss:** Physical grade notebooks were susceptible to physical damage, transcription disputes, and lost history during term finals.
- **Extracurricular Chaos:** No centralized enrollment existed for sports, arts, or robotics clubs, leading to oversubscribed spots and duplicate efforts.
- **Billing Vulnerabilities & Non-Compliance:** Manual tuition calculations were error-prone, lacked automated sibling discount rules, and failed to issue regulated Colombian electronic invoices under **DIAN (Dirección de Impuestos y Aduanas Nacionales)** guidelines.
- **Inventory Depletion:** High-value laboratory equipment (microscopes, digital scales) and library books were frequently misplaced due to the absence of a structured lending cycle and overdue tracking.
- **Fragmented Visibility:** Administrators lacked executive dashboards to identify at-risk students, persistent absentees, or unpaid tuitions in real time.

### The Solution
The **Little Horizons ERP** operates as the central nervous system of the institution. It consolidates relational persistence, business logic enforcement, mathematical validation, and cross-module reporting into a single robust Java platform.

---

## 2. Authors & Academic Context

- **Author / Developer:** Jesús Sánchez
- **GitHub Handle:** [@Jesussanchez2805](https://github.com/Jesussanchez2805)
- **Email:** `Jesussanchez2805.js@gmail.com`
- **Academic Purpose:** System developed as a graduation defense / thesis project showcasing enterprise Java architecture, modular design, JDBC relational persistence, and clean code principles.

---

## 3. Technology Stack & Dependencies

| Technology / Library | Version | Purpose in Little Horizons ERP |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | 17 LTS / 21 LTS | Modern Java runtime, Records, Streams, NIO.2, Switch Expressions |
| **Apache Maven** | 3.9.6+ | Dependency management, build lifecycle, and uber-JAR shading |
| **MySQL Database Engine** | 8.0+ | Relational persistence with foreign key constraints and transactional integrity |
| **MySQL Connector/J** | 8.3.0 | High-performance Type-4 JDBC driver |
| **OpenPDF (LibrePDF)** | 2.0.3 | LGPL/MPL-compliant PDF document generation for DIAN Electronic Invoices |
| **Google ZXing ("Zebra Crossing")** | 3.5.3 | Cryptographic 2D QR Code image generation embedded into PDF invoices |
| **JUnit Jupiter (JUnit 5)** | 5.10.2 | Unit testing suite for domain calculations and PDF invoice verification |
| **Maven Shade Plugin** | 3.5.2 | Assembles an all-in-one executable fat JAR with embedded dependencies |

---

## 4. Prerequisites

Before installing and executing the application, ensure your environment meets the following requirements:
1. **Java Runtime:** JDK 17 or higher installed (`java -version` and `javac -version`).
2. **Maven:** Apache Maven 3.8+ installed (`mvn -version`).
3. **MySQL Server:** MySQL 8.0 Community Server installed and running as a local or remote service.
4. **Git:** Git 2.30+ installed for repository cloning and version control.

---

## 5. Database Architecture & Setup

The ERP relies on a relational database schema named `erp_little_horizons` defined in [`docs/database.sql`](docs/database.sql). The script creates 20 normalized tables with primary keys, foreign key constraints (`ON DELETE CASCADE` / `RESTRICT`), checks, and 30+ seed rows of realistic Colombian school data.

### Database Initialization Steps

1. Open your terminal or MySQL command line client.
2. Log into MySQL with administrative privileges:
   ```bash
   mysql -u root -p
   ```
3. Execute the database script:
   ```sql
   SOURCE docs/database.sql;
   ```
   *Alternatively, run directly from your OS shell:*
   ```bash
   mysql -u root -p < docs/database.sql
   ```
4. Verify the database tables were created:
   ```sql
   USE erp_little_horizons;
   SHOW TABLES;
   ```

### Database Credentials Configuration
By default, the application connects using parameters defined in [`src/main/resources/db.properties`](src/main/resources/db.properties):
```properties
db.host=localhost
db.port=3306
db.name=erp_little_horizons
db.user=root
db.password=root
```
> **Interactive Override:** If your MySQL root password is different, launch the application and choose **Option 8 (Database Configuration)** from the main menu to dynamically update and test the host, port, database, user, and password without editing files or restarting.

---

## 6. Installation & Build Guide

### Step 1: Clone the Repository
```bash
git clone https://github.com/Jesussanchez2805/little-horizons-erp.git
cd little-horizons-erp
```

### Step 2: Compile and Run Unit Tests
```bash
mvn test
```

### Step 3: Package the Shaded Executable JAR
```bash
mvn package
```
This builds an executable fat JAR in the `target/` directory:
- `target/little-horizons-erp-1.0.0.jar`

---

## 7. Running the Application

### Option A: Using the Standalone Executable JAR (Recommended)
```bash
java -jar target/little-horizons-erp-1.0.0.jar
```

### Option B: Running via Maven
```bash
mvn exec:java
```

---

## 8. Software Architecture (MVC Design)

The project adheres strictly to the **Model-View-Controller (MVC)** architectural paradigm, decoupling domain data, data access, business orchestration, and terminal presentation:

```
c:\...\Proyecto_ERP_Colegio_Little_Horizont
│
├── docs/
│   └── database.sql                   # Complete DDL + 30+ Realistic DML Colombian seed rows
├── invoices/                          # Auto-generated DIAN PDF Invoices (with QR & CUFE)
├── src/
│   ├── main/
│   │   ├── java/com/littlehorizons/erp/
│   │   │   ├── Main.java              # Application bootstrap and diagnostic entrypoint
│   │   │   │
│   │   │   ├── model/                 # DOMAIN ENTITY LAYER (Plain Java Objects / POJOs)
│   │   │   │   ├── AcademicRecord.java
│   │   │   │   ├── Attendance.java
│   │   │   │   ├── Club.java
│   │   │   │   ├── ClubMembership.java
│   │   │   │   ├── Course.java
│   │   │   │   ├── CourseRegistration.java
│   │   │   │   ├── Enrollment.java
│   │   │   │   ├── EventParticipant.java
│   │   │   │   ├── GradeLevel.java
│   │   │   │   ├── Guardian.java
│   │   │   │   ├── Invoice.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── PaymentConcept.java
│   │   │   │   ├── ResourceCategory.java
│   │   │   │   ├── ResourceItem.java
│   │   │   │   ├── SchoolEvent.java
│   │   │   │   ├── Student.java
│   │   │   │   ├── StudentLoan.java
│   │   │   │   ├── Teacher.java
│   │   │   │   └── TeacherAssignment.java
│   │   │   │
│   │   │   ├── dao/                   # DATA ACCESS OBJECT LAYER (JDBC & SQL Transactions)
│   │   │   │   ├── AcademicDAO.java / AcademicDAOImpl.java
│   │   │   │   ├── ExtracurricularDAO.java / ExtracurricularDAOImpl.java
│   │   │   │   ├── FinancialDAO.java / FinancialDAOImpl.java
│   │   │   │   ├── GuardianDAO.java / GuardianDAOImpl.java
│   │   │   │   ├── ResourceDAO.java / ResourceDAOImpl.java
│   │   │   │   ├── StudentDAO.java / StudentDAOImpl.java
│   │   │   │   └── TeacherDAO.java / TeacherDAOImpl.java
│   │   │   │
│   │   │   ├── controller/            # BUSINESS LOGIC & ORCHESTRATION LAYER (Streams & Rules)
│   │   │   │   ├── AcademicController.java
│   │   │   │   ├── ExtracurricularController.java
│   │   │   │   ├── FinancialController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── ResourceController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   └── TeacherController.java
│   │   │   │
│   │   │   ├── view/                  # PRESENTATION LAYER (Console UI, Menus & Input Validation)
│   │   │   │   ├── AcademicView.java
│   │   │   │   ├── ExtracurricularView.java
│   │   │   │   ├── FinancialView.java
│   │   │   │   ├── MainMenuView.java
│   │   │   │   ├── ReportView.java
│   │   │   │   ├── ResourceView.java
│   │   │   │   ├── StudentView.java
│   │   │   │   └── TeacherView.java
│   │   │   │
│   │   │   └── util/                  # SHARED INFRASTRUCTURE & UTILITIES
│   │   │       ├── AppLogger.java            # Centralized file logging to error_log.txt
│   │   │       ├── ConsoleUtils.java         # Box borders, color/style formatting, dynamic tables
│   │   │       ├── DatabaseConnection.java   # Thread-safe Singleton managing MySQL connections
│   │   │       ├── InputValidator.java       # Resilient Scanner input validation & prompts
│   │   │       └── PdfInvoiceGenerator.java  # OpenPDF + ZXing DIAN Electronic Invoice engine
│   │   │
│   │   └── resources/
│   │       └── db.properties          # Externalized database credentials configuration
│   │
│   └── test/java/com/littlehorizons/erp/
│       ├── ModelValidationTest.java   # Domain entity logic & calculation tests
│       └── PdfInvoiceGeneratorTest.java # DIAN PDF invoice, CUFE, & QR generation tests
│
├── pom.xml                            # Maven build descriptor with shade plugin
└── README.md                          # Comprehensive system documentation
```

---

## 9. SOLID Principles & Software Design Patterns

The ERP codebase was designed as an exemplar of object-oriented principles:

### SOLID Principles
1. **Single Responsibility Principle (SRP):**
   - Each class owns a single cohesive responsibility. For example, `PdfInvoiceGenerator` only generates PDF documents; it does not query SQL or handle console inputs. `DatabaseConnection` only creates and manages JDBC connections.
2. **Open/Closed Principle (OCP):**
   - The DAO layer exposes clean interfaces (`StudentDAO`, `FinancialDAO`, etc.). If the persistence mechanism changes from MySQL to PostgreSQL or MongoDB, only the DAO implementations change without altering controllers or views.
3. **Liskov Substitution Principle (LSP):**
   - Controllers depend on interface abstractions (`AcademicDAO dao = new AcademicDAOImpl()`). Any compliant implementation can be substituted without breaking business rules.
4. **Interface Segregation Principle (ISP):**
   - Domain operations are partitioned into specific, fine-grained DAO interfaces (`GuardianDAO` separate from `StudentDAO`) rather than a monolithic repository interface.
5. **Dependency Inversion Principle (DIP):**
   - High-level business controllers depend on abstractions (DAO interfaces) rather than concrete SQL statements.

### Design Patterns
- **Singleton Pattern:** Used in `DatabaseConnection` to ensure a single, thread-safe database connection factory throughout the JVM lifecycle.
- **Data Access Object (DAO) Pattern:** Encapsulates all raw JDBC operations (`PreparedStatement`, `ResultSet`, resource management via `try-with-resources`) to prevent SQL leakage into business logic.
- **Factory / Builder Strategy:** Implemented in `PdfInvoiceGenerator` for composing PDF tables, font styles, and ZXing matrix buffers.
- **Java Stream API:** Extensively utilized in `ReportController` and `AcademicController` for functional aggregations (computing student GPAs, filtering active memberships, finding top performing students, and grouping resource loan histories).

---

## 10. Core Functional Modules

### 10.1 Academic Management & Enrollment
- **Student Profile Management:** Register, update, and search students by document ID or full name.
- **Guardian (Acudiente) Associations:** Associate guardians with students; allows one guardian to manage multiple siblings.
- **Grade Progression:** Enroll students into grades from Preschool to 11th Grade for specific academic years (`2026`).
- **Course Enrollments:** Register students into grade-specific subjects (Mathematics, Spanish, Sciences, English, etc.).
- **Grading Scale (0.0 – 5.0):** Record term grades (Terms I, II, III, IV, Final) strictly validated between `0.0` and `5.0` with one decimal place.
- **Attendance Tracking:** Record daily attendance statuses (`Presente`, `Ausente`, `Tardanza`, `Excusa Médica`).

### 10.2 Teacher Workload & Scheduling
- **Faculty Directory:** Maintain teacher profiles, national identification, email, phone, and professional specialties.
- **Subject Allocation:** Assign teachers to courses with assigned weekly schedules and designated classrooms.
- **Schedule Conflict Prevention:** Verify active assignments to prevent overlapping classroom or teacher allocations.

### 10.3 Extracurricular Clubs & Events
- **Club Catalog:** Manage school clubs (Robotics, Arts, Debate, Soccer, Chess, Science) with assigned faculty mentors.
- **Capacity Enforcement:** Enforce maximum quota constraints; the system prevents over-enrollment when a club reaches capacity (`currentMembers < maxCapacity`).
- **School Events & Competitions:** Register institutional events (Science Fair, Cultural Day, Sports Tournament) and log student participation along with award positions (`1st Place`, `2nd Place`, `Participant`).

### 10.4 Financial Billing & DIAN Electronic Invoices
- **Billing Concepts:** Standard fee catalog (Tuition, Annual Enrollment, Uniforms, Field Trips, Cafeteria).
- **Payment Processing:** Record receipts with payment methods (`Efectivo`, `Transferencia`, `Tarjeta`).
- **Simulated DIAN Electronic Invoicing:**
  - Computes a simulated **CUFE** (*Código Único de Factura Electrónica*) using SHA-256 cryptographic hashing of the invoice number, school NIT, client document, date, and monetary totals.
  - Generates a **2D QR Code** with embedded metadata using Google ZXing.
  - Compiles an executive PDF invoice saved automatically to the `invoices/` folder via OpenPDF.
- **Delinquency Tracking:** Generates reports of students with overdue accounts and pending balances.

### 10.5 Resource & Inventory Management (Library & Labs)
- **Asset Catalog:** Track physical resources across four categories: `Libro`, `Equipo de Laboratorio`, `Equipo Deportivo`, and `Dispositivo Tecnológico`.
- **Borrowing Workflow:** Issue loans to both students and teachers with expected return dates.
- **Automated Availability State:** Borrowing an item automatically transitions its state to `Prestado`; returning it resets the status to `Disponible`.
- **Overdue Monitoring:** Flags unreturned loans past their due date to assist administrative recovery.

### 10.6 Advanced Reporting & Analytics
- **Honor Roll (Top Academic Performers):** Computes student grade point averages across all courses and ranks top-performing students.
- **At-Risk Student Alerts:** Flags students with averages below `3.0` for academic counseling.
- **Attendance Deficiency Audit:** Identifies students exceeding 3 absences within an evaluation window.
- **Financial Revenue Summaries:** Calculates monthly income, total invoiced amounts, and outstanding balances.
- **Asset Utilization Reports:** Summarizes active vs. returned loans and identifies damaged or maintenance-flagged equipment.

---

## 11. Defensive Programming, Error Handling & Logging

The system is engineered for stability and enterprise resilience:
- **Centralized Application Logger (`AppLogger`):** All SQL exceptions, system warnings, and diagnostic information are recorded with ISO-8601 timestamps and severity levels (`INFO`, `WARNING`, `SEVERE`) in `error_log.txt`.
- **Defensive Console Validation (`InputValidator`):** Prevents `InputMismatchException` crashes through recursive validation loops for integers, decimals, dates, and non-empty text.
- **Automatic Resource Management:** All database statements and streams use Java `try-with-resources` blocks to prevent connection leaks.
- **Graceful Database Degradation:** If the MySQL database is unreachable upon startup, the system does not crash; it logs a warning, alerts the operator, and provides an interactive credential configuration screen.

---

## 12. Terminal User Interface (TUI) & ASCII Demos

The console interface utilizes clean box-drawing borders and structured headers:

```
┌────────────────────────────────────────────────────────────────┐
│   ERP COLEGIO INTERNACIONAL LITTLE HORIZONS                    │
│   Sistema Central de Planificación y Gestión Educativa         │
└────────────────────────────────────────────────────────────────┘

 1. Gestión de Estudiantes y Matrículas (Students & Guardians)
 2. Gestión Académica (Courses, Grades 0.0-5.0 & Attendance)
 3. Gestión de Profesores y Carga Académica (Faculty)
 4. Actividades Extracurriculares (Clubs & Events)
 5. Gestión Financiera (Payments & DIAN PDF Invoices)
 6. Gestión de Recursos (Library, Labs & Loan System)
 7. Reportes y Estadísticas (Executive Analytics & GPA)
 8. Configuración de Base de Datos (Database Connection)
 9. Salir del Sistema (Exit)

Seleccione una opción (1-9):
```

### Dynamic ASCII Table Output Example
```
====================================================================================================
 ID   | CÓDIGO      | NOMBRE                     | GRADO        | DOCUMENTO       | ESTADO    
====================================================================================================
 1    | STU-2026-001| Sofía Valentina Ramírez    | 10°          | 1012345678      | Activo    
 2    | STU-2026-002| Mateo Alejandro Morales    | 11°          | 1012345679      | Activo    
 3    | STU-2026-003| Isabella Gómez Herrera     | 9°           | 1012345680      | Activo    
====================================================================================================
```

---

## 13. Unit & Integration Testing

The test suite in `src/test/java/com/littlehorizons/erp/` validates critical non-UI components:
- **`ModelValidationTest`:** Tests domain integrity, student code generation format, grade boundary checks (`0.0` - `5.0`), and club membership limit rules.
- **`PdfInvoiceGeneratorTest`:** Verifies SHA-256 CUFE generation, ZXing QR matrix rendering, and end-to-end PDF invoice writing to disk.

To run all automated tests:
```bash
mvn test
```

Test Results Summary:
```
[INFO] Running com.littlehorizons.erp.ModelValidationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.littlehorizons.erp.PdfInvoiceGeneratorTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 14. Known Limitations & Operational Assumptions

1. **Terminal Console Interface:** The primary interface is console-based (CLI/TUI) designed for standard UTF-8 terminal emulators. Windows PowerShell, Command Prompt, or Unix terminals should use UTF-8 (`chcp 65001` on legacy Windows if accents appear distorted).
2. **DIAN Electronic Invoicing Simulation:** Invoices strictly follow the visual, cryptographic (SHA-256 CUFE), and QR layout required by DIAN Resolution rules, but do not transmit real-time XML SOAP/REST envelopes to DIAN's live production servers.
3. **Single School Campus:** Assumes a single primary campus entity ("Colegio Internacional Little Horizons S.A.S.") with centralized fiscal NIT.

---

## 15. Future Roadmap & Enhancements

- [ ] **REST API Layer:** Expose endpoints using Spring Boot or Javalin for mobile app integration.
- [ ] **Web Frontend:** Develop an interactive dashboard with React/Vue.js and Tailwind CSS.
- [ ] **Direct DIAN SOAP Web Services:** Implement live XML signing with X.509 digital certificates.
- [ ] **Parent Portal & Online Payments:** Integrate PSE, PayU, and Wompi payment gateways.
- [ ] **Automated Email Notifications:** Dispatch PDF invoices and attendance alert emails to guardians via JavaMail SMTP.

---

## 16. License & Contact

### License
This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### Contact Information
- **Student Developer:** Jesús Sánchez
- **GitHub:** [Jesussanchez2805](https://github.com/Jesussanchez2805)
- **Email:** `Jesussanchez2805.js@gmail.com`
- **Institution:** Colegio Internacional Little Horizons S.A.S.
- **Project Repository:** [https://github.com/Jesussanchez2805/little-horizons-erp](https://github.com/Jesussanchez2805/little-horizons-erp)
