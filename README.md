# Sistema ERP Educativo Little Horizons

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.9.6-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Licencia](https://img.shields.io/badge/Licencia-MIT-green.svg?style=for-the-badge)](LICENSE)

Plataforma integral de Planificación de Recursos Educativos (**Educational Resource Planning - ERP**) de nivel empresarial desarrollada en **Java 17** para el **Colegio Internacional Little Horizons S.A.S.**. Construida bajo una rigurosa arquitectura **Modelo-Vista-Controlador (MVC)** y fundamentada en los principios **SOLID**, esta solución centraliza y optimiza las operaciones académicas, la asignación de carga docente, los clubes extracurriculares, el control de inventario institucional (laboratorios y biblioteca), la analítica directiva en tiempo real y la emisión automatizada de **Facturas Electrónicas con lineamientos DIAN**, incluyendo generación criptográfica de **CUFE** (SHA-256) y códigos **QR con Google ZXing**.

---

## Tabla de Contenidos
1. [Descripción General del Proyecto y Planteamiento del Problema](#1-descripción-general-del-proyecto-y-planteamiento-del-problema)
2. [Autor y Contexto Académico](#2-autor-y-contexto-académico)
3. [Stack Tecnológico y Dependencias](#3-stack-tecnológico-y-dependencias)
4. [Requisitos Previos](#4-requisitos-previos)
5. [Arquitectura y Configuración de la Base de Datos](#5-arquitectura-y-configuración-de-la-base-de-datos)
6. [Guía de Instalación y Compilación](#6-guía-de-instalación-y-compilación)
7. [Ejecución de la Aplicación](#7-ejecución-de-la-aplicación)
8. [Arquitectura de Software (Patrón MVC)](#8-arquitectura-de-software-patrón-mvc)
9. [Principios SOLID y Patrones de Diseño](#9-principios-solid-y-patrones-de-diseño)
10. [Módulos Funcionales Principales](#10-módulos-funcionales-principales)
    - [10.1 Gestión Académica y Matrículas](#101-gestión-académica-y-matrículas)
    - [10.2 Carga Docente y Asignación de Horarios](#102-carga-docente-y-asignación-de-horarios)
    - [10.3 Clubes Extracurriculares y Eventos](#103-clubes-extracurriculares-y-eventos)
    - [10.4 Facturación Electrónica DIAN y Gestión Financiera](#104-facturación-electrónica-dian-y-gestión-financiera)
    - [10.5 Gestión de Recursos e Inventario (Biblioteca y Laboratorios)](#105-gestión-de-recursos-e-inventario-biblioteca-y-laboratorios)
    - [10.6 Reportes Avanzados y Analítica Ejecutiva](#106-reportes-avanzados-y-analítica-ejecutiva)
11. [Programación Defensiva, Manejo de Errores y Logging](#11-programación-defensiva-manejo-de-errores-y-logging)
12. [Interfaz de Usuario en Terminal (TUI) y Demostración ASCII](#12-interfaz-de-usuario-en-terminal-tui-y-demostración-ascii)
13. [Pruebas Unitarias y de Integración](#13-pruebas-unitarias-y-de-integración)
14. [Limitaciones Conocidas y Supuestos Operativos](#14-limitaciones-conocidas-y-supuestos-operativos)
15. [Hoja de Ruta (Roadmap) y Mejoras Futuras](#15-hoja-de-ruta-roadmap-y-mejoras-futuras)
16. [Licencia y Contacto](#16-licencia-y-contacto)

---

## 1. Descripción General del Proyecto y Planteamiento del Problema

### Contexto Institucional
El **Colegio Internacional Little Horizons S.A.S.** (NIT: 901.482.391-7) es una institución educativa bilingüe de Colombia que brinda formación integral a más de 500 estudiantes desde Preescolar hasta Grado Once (`Preescolar`, `1°` a `11°`), respaldada por una planta profesoral superior a 40 docentes y personal administrativo.

### El Desafío Operativo
Con anterioridad a la implementación de este sistema ERP, la institución experimentaba serios cuellos de botella y riesgos operativos ocasionados por la fragmentación de la información en libros físicos de papel, hojas de cálculo aisladas y registros manuales no integrados:
- **Pérdida y Vulnerabilidad de Datos Académicos:** Las planillas de calificaciones físicas estaban expuestas a deterioro material, inconsistencias en transcripciones y falta de trazabilidad histórica durante los cierres de periodo.
- **Descontrol en Actividades Extracurriculares:** No existía una gestión centralizada para las inscripciones en clubes deportivos, artísticos o de robótica, originando sobrecupos, cruces de horarios y duplicidad de esfuerzos.
- **Riesgos Financieros e Incumplimiento Normativo:** Los cálculos de pensiones y matrículas manuales eran propensos a errores aritméticos, carecían de políticas automáticas de descuento por hermanos y no generaban facturación electrónica bajo los estándares normativos de la **DIAN (Dirección de Impuestos y Aduanas Nacionales de Colombia)**.
- **Pérdida de Inventario y Recursos:** Equipos de laboratorio de alto costo (microscopios, balanzas de precisión) y material bibliográfico se extraviaban con frecuencia por la falta de un ciclo de préstamos estructurado con control de devoluciones y alertas de mora.
- **Visibilidad Directiva Fragmentada:** El equipo directivo no disponía de reportes consolidados ni tableros en tiempo real para identificar tempranamente estudiantes en riesgo académico, ausentismo reiterado o cartera morosa.

### La Solución
El **ERP Little Horizons** se posiciona como el núcleo tecnológico de la institución. Centraliza la persistencia relacional, la validación estricta de reglas de negocio, la auditoría continua y la generación de reportes intermodulares dentro de un entorno Java robusto, seguro y escalable.

---

## 2. Autor y Contexto Académico

- **Autor / Desarrollador:** Jesús Sánchez
- **Perfil de GitHub:** [@Jesussanchez2805](https://github.com/Jesussanchez2805)
- **Correo Electrónico:** `Jesussanchez2805.js@gmail.com`
- **Propósito Académico:** Proyecto desarrollado como trabajo de grado / sustentación académica profesional, demostrando dominio de arquitectura empresarial en Java, diseño modular desacoplado, persistencia relacional con JDBC, principios de código limpio (*Clean Code*) y patrones de software de la industria.

---

## 3. Stack Tecnológico y Dependencias

| Tecnología / Librería | Versión | Rol en el ERP Little Horizons |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | 17 LTS / 21 LTS | Runtime moderno de Java, Records, Stream API, NIO.2, Switch Expressions |
| **Apache Maven** | 3.9.6+ | Gestión de dependencias, ciclo de vida de construcción y empaquetado (*fat JAR*) |
| **Motor de Base de Datos MySQL** | 8.0+ | Persistencia relacional, integridad referencial con claves foráneas y soporte transaccional |
| **MySQL Connector/J** | 8.3.0 | Driver JDBC Tipo 4 de alto rendimiento |
| **OpenPDF (LibrePDF)** | 2.0.3 | Motor de generación de documentos PDF con licencias LGPL/MPL para facturación DIAN |
| **Google ZXing ("Zebra Crossing")** | 3.5.3 | Generación de matrices gráficas de códigos QR 2D embebidos en facturas PDF |
| **JUnit Jupiter (JUnit 5)** | 5.10.2 | Suite de pruebas unitarias automatizadas para entidades de dominio y validación de PDF |
| **Maven Shade Plugin** | 3.5.2 | Construcción de un artefacto ejecutable autocontenido (*shaded fat JAR*) con dependencias |

---

## 4. Requisitos Previos

Antes de instalar y poner en marcha la aplicación, verifique que su entorno cumpla con las siguientes herramientas instaladas y configuradas:
1. **Java Runtime:** JDK 17 o superior instalado y configurado en el PATH (`java -version` y `javac -version`).
2. **Maven:** Apache Maven 3.8+ instalado (`mvn -version`).
3. **Servidor MySQL:** Servidor MySQL 8.0 Community activo localmente o accesible por red.
4. **Git:** Git 2.30+ instalado para la clonación del repositorio y control de versiones.

---

## 5. Arquitectura y Configuración de la Base de Datos

El sistema se fundamenta en un esquema de base de datos relacional denominado `erp_little_horizons`, estructurado en el archivo [`docs/database.sql`](docs/database.sql). Dicho script define 20 tablas normalizadas con claves primarias, restricciones de integridad referencial (`ON DELETE CASCADE` / `RESTRICT`), restricciones de dominio (*CHECK constraints*) y más de 30 registros iniciales (*seed data*) con datos contextualizados a la realidad escolar colombiana.

### Pasos para la Inicialización de la Base de Datos

1. Abra una terminal o el cliente de consola de MySQL.
2. Inicie sesión en MySQL con privilegios administrativos:
   ```bash
   mysql -u root -p
   ```
3. Ejecute el script SQL del repositorio:
   ```sql
   SOURCE docs/database.sql;
   ```
   *Alternativamente, puede ejecutarlo directamente desde la terminal del sistema operativo:*
   ```bash
   mysql -u root -p < docs/database.sql
   ```
4. Compruebe la correcta creación de las tablas:
   ```sql
   USE erp_little_horizons;
   SHOW TABLES;
   ```

### Configuración de Credenciales de Conexión
Por defecto, la aplicación se conecta a la base de datos utilizando los parámetros establecidos en [`src/main/resources/db.properties`](src/main/resources/db.properties):
```properties
db.host=localhost
db.port=3306
db.name=erp_little_horizons
db.user=root
db.password=root
```
> **Ajuste Dinámico e Interactivo:** Si la contraseña de su usuario MySQL es diferente a la predeterminada, no es necesario modificar manualmente los archivos de configuración. Al iniciar la aplicación, seleccione la **Opción 8 (Configuración de Base de Datos)** en el menú principal para actualizar interactivamente el host, puerto, base de datos, usuario y clave, probando la conexión de inmediato sin reiniciar.

---

## 6. Guía de Instalación y Compilación

### Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/Jesussanchez2805/little-horizons-erp.git
cd little-horizons-erp
```

### Paso 2: Compilar y Ejecutar Pruebas Automatizadas
```bash
mvn test
```

### Paso 3: Empaquetar el JAR Ejecutable Autocontenido
```bash
mvn package
```
Este comando generará el artefacto ejecutable (*fat JAR*) en el directorio `target/`:
- `target/little-horizons-erp-1.0.0.jar`

---

## 7. Ejecución de la Aplicación

### Opción A: Mediante el JAR Ejecutable Autocontenido (Recomendada)
```bash
java -jar target/little-horizons-erp-1.0.0.jar
```

### Opción B: Ejecución Directa mediante Maven
```bash
mvn exec:java
```

---

## 8. Arquitectura de Software (Patrón MVC)

El proyecto implementa con rigurosidad el patrón arquitectónico **Modelo-Vista-Controlador (MVC)**, garantizando una separación clara entre las entidades de dominio, el acceso a datos, la lógica de negocio y la interfaz de terminal:

```
c:\...\Proyecto_ERP_Colegio_Little_Horizont
│
├── docs/
│   └── database.sql                   # DDL completo + más de 30 registros DML de prueba colombianos
├── invoices/                          # Facturas electrónicas DIAN generadas en PDF (con QR y CUFE)
├── src/
│   ├── main/
│   │   ├── java/com/littlehorizons/erp/
│   │   │   ├── Main.java              # Clase de inicio y diagnóstico de conexión del sistema
│   │   │   │
│   │   │   ├── model/                 # CAPA DE ENTIDADES DE DOMINIO (POJOs con encapsulación)
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
│   │   │   │   ├── TeacherAssignment.java
│   │   │   │   └── TeacherLoan.java
│   │   │   │
│   │   │   ├── dao/                   # CAPA DE ACCESO A DATOS (Interfaces JDBC y transacciones SQL)
│   │   │   │   ├── AcademicDAO.java / AcademicDAOImpl.java
│   │   │   │   ├── ExtracurricularDAO.java / ExtracurricularDAOImpl.java
│   │   │   │   ├── FinancialDAO.java / FinancialDAOImpl.java
│   │   │   │   ├── GuardianDAO.java / GuardianDAOImpl.java
│   │   │   │   ├── ResourceDAO.java / ResourceDAOImpl.java
│   │   │   │   ├── StudentDAO.java / StudentDAOImpl.java
│   │   │   │   └── TeacherDAO.java / TeacherDAOImpl.java
│   │   │   │
│   │   │   ├── controller/            # CAPA DE CONTROLADORES Y LÓGICA DE NEGOCIO (Streams y Reglas)
│   │   │   │   ├── AcademicController.java
│   │   │   │   ├── ExtracurricularController.java
│   │   │   │   ├── FinancialController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── ResourceController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   └── TeacherController.java
│   │   │   │
│   │   │   ├── view/                  # CAPA DE PRESENTACIÓN (Interfaz de consola y menús interactivos)
│   │   │   │   ├── AcademicView.java
│   │   │   │   ├── ExtracurricularView.java
│   │   │   │   ├── FinancialView.java
│   │   │   │   ├── MainMenuView.java
│   │   │   │   ├── ReportView.java
│   │   │   │   ├── ResourceView.java
│   │   │   │   ├── StudentView.java
│   │   │   │   └── TeacherView.java
│   │   │   │
│   │   │   └── util/                  # UTILIDADES COMPARTIDAS E INFRAESTRUCTURA
│   │   │       ├── AppLogger.java            # Logger centralizado con escritura en error_log.txt
│   │   │       ├── ConsoleUtils.java         # Marcos decorativos en consola, tablas dinámicas y estilos
│   │   │       ├── DatabaseConnection.java   # Patrón Singleton thread-safe para conexión a MySQL
│   │   │       ├── InputValidator.java       # Validador robusto de entradas Scanner por consola
│   │   │       └── PdfInvoiceGenerator.java  # Motor de Factura Electrónica DIAN con OpenPDF y ZXing
│   │   │
│   │   └── resources/
│   │       └── db.properties          # Archivo externo con parámetros de conexión a la base de datos
│   │
│   └── test/java/com/littlehorizons/erp/
│       ├── ModelValidationTest.java   # Pruebas de lógica de entidades, promedios y límites
│       └── PdfInvoiceGeneratorTest.java # Pruebas de generación de facturas PDF, CUFE y QR DIAN
│
├── pom.xml                            # Descriptor Maven del proyecto con configuración del shade plugin
└── README.md                          # Documentación técnica completa del sistema
```

---

## 9. Principios SOLID y Patrones de Diseño

El diseño de la base de código del ERP refleja buenas prácticas de ingeniería de software orientada a objetos:

### Principios SOLID
1. **Single Responsibility Principle (SRP - Responsabilidad Única):**
   - Cada clase responde a una única responsabilidad claramente definida. Por ejemplo, `PdfInvoiceGenerator` se encarga exclusivamente de construir el documento PDF; no realiza consultas SQL ni interactúa con la terminal. `DatabaseConnection` se ocupa únicamente de suministrar y gestionar la conexión JDBC.
2. **Open/Closed Principle (OCP - Abierto/Cerrado):**
   - La capa de persistencia se articula mediante contratos e interfaces (`StudentDAO`, `FinancialDAO`, etc.). Si en el futuro se migra el motor de almacenamiento de MySQL hacia PostgreSQL o MongoDB, basta con crear una nueva implementación sin alterar controladores ni vistas.
3. **Liskov Substitution Principle (LSP - Sustitución de Liskov):**
   - Los controladores interactúan con abstracciones de interfaces (`AcademicDAO dao = new AcademicDAOImpl()`). Cualquier implementación compatible puede sustituirse sin romper el comportamiento esperado de la lógica de negocio.
4. **Interface Segregation Principle (ISP - Segregación de Interfaces):**
   - Las operaciones de datos se encuentran particionadas en interfaces específicas y cohesivas (`GuardianDAO` independiente de `StudentDAO`), evitando interfaces monolíticas y sobrecargadas.
5. **Dependency Inversion Principle (DIP - Inversión de Dependencias):**
   - Los módulos de alto nivel (controladores de negocio) dependen de abstracciones (interfaces DAO) y no de sentencias SQL concretas o acoplamientos rígidos.

### Patrones de Diseño Aplicados
- **Patrón Singleton:** Implementado en `DatabaseConnection` para asegurar una única instancia segura en entornos multihilo (*thread-safe*) que administre el pool y ciclo de vida de la conexión a MySQL en la JVM.
- **Patrón Data Access Object (DAO):** Aísla por completo las operaciones JDBC (`PreparedStatement`, `ResultSet`, transacciones y cierre de recursos con `try-with-resources`), previniendo la fuga de consultas SQL hacia las capas superiores.
- **Patrón Factory / Builder:** Utilizado en `PdfInvoiceGenerator` para la parametrización estructurada de tablas, fuentes tipográficas y codificación matricial de imágenes con ZXing.
- **Java Stream API:** Empleado ampliamente en `ReportController` y `AcademicController` para realizar agregaciones funcionales (cálculo de promedios ponderados, filtros de membresías activas, detección de cuadro de honor y agrupación de préstamos).

---

## 10. Módulos Funcionales Principales

### 10.1 Gestión Académica y Matrículas
- **Directorio de Estudiantes:** Registro, actualización y búsqueda ágil de estudiantes por número de identificación o nombre completo.
- **Asociación de Acudientes / Padres:** Vinculación de acudientes con estudiantes, soportando la gestión de múltiples hermanos por parte de un mismo titular.
- **Proceso de Matrícula:** Asignación formal de grado para el año lectivo vigente (`2026`), cubriendo desde Preescolar hasta Grado Once.
- **Inscripción en Asignaturas:** Vinculación de estudiantes a cursos académicos correspondientes a su nivel (Matemáticas, Español, Ciencias, Inglés, etc.).
- **Escala de Calificaciones (0.0 a 5.0):** Registro y validación estricta de notas por periodo (Periodos I, II, III, IV y Nota Definitiva), con validación de rango numérico a un decimal.
- **Control de Asistencia:** Registro de asistencias con estados tipificados (`Presente`, `Ausente`, `Tardanza`, `Excusa Médica`).

### 10.2 Carga Docente y Asignación de Horarios
- **Directorio Profesoral:** Registro y consulta de docentes con datos de contacto, documento de identidad y áreas de especialidad pedagógica.
- **Asignación Académica:** Vinculación de profesores a asignaturas y grados específicos, detallando salón de clases y franja horaria semanal.
- **Prevención de Conflictos de Horario:** Verificación activa que previene colisiones de disponibilidad tanto de aulas como de docentes en el mismo bloque horario.

### 10.3 Clubes Extracurriculares y Eventos
- **Catálogo de Clubes:** Administración de actividades lúdicas y formativas (Robótica, Artes, Debate, Fútbol, Ajedrez, Ciencias) con docente orientador asignado.
- **Control Estricto de Cupos:** Regla de negocio que valida el aforo máximo (`currentMembers < maxCapacity`), rechazando inscripciones cuando el cupo esté lleno.
- **Eventos Escolares y Concursos:** Registro de actividades institucionales (Feria de la Ciencia, Día Cultural, Torneo Deportivo), con inscripción de participantes y registro de reconocimientos obtenidos (`1° Lugar`, `2° Lugar`, `Participante`).

### 10.4 Facturación Electrónica DIAN y Gestión Financiera
- **Catálogo de Conceptos de Pago:** Tarifas estandarizadas de la institución (Matrícula Anual, Pensión Mensual, Uniformes, Salidas Pedagógicas, Cafetería).
- **Procesamiento de Pagos:** Registro de recaudos con múltiples modalidades de pago (`Efectivo`, `Transferencia`, `Tarjeta`).
- **Simulación de Factura Electrónica DIAN:**
  - Generación de **CUFE** (*Código Único de Factura Electrónica*) mediante hashing criptográfico SHA-256 combinando número de factura, NIT del colegio, documento del pagador, fecha y montos.
  - Creación de **Código QR 2D** con metadatos embebidos utilizando Google ZXing.
  - Renderizado automático del documento ejecutivo en formato PDF almacenado en la carpeta `invoices/` mediante OpenPDF.
- **Control de Cartera:** Generación de listados de estudiantes con cuentas vencidas, saldos pendientes y estado de morosidad.

### 10.5 Gestión de Recursos e Inventario (Biblioteca y Laboratorios)
- **Inventario Institucional:** Seguimiento y catalogación de bienes en cuatro categorías: `Libro`, `Equipo de Laboratorio`, `Equipo Deportivo` y `Dispositivo Tecnológico`.
- **Flujo de Préstamos:** Emisión y registro de préstamos de recursos dirigidos tanto a estudiantes como a docentes con fecha estimada de entrega.
- **Transición Automática de Estados:** Al realizar un préstamo el recurso cambia a `Prestado`; al registrar su devolución vuelve automáticamente a `Disponible`.
- **Control de Recursos en Mora:** Detección y reporte oportuno de artículos con fecha límite de devolución vencida.

### 10.6 Reportes Avanzados y Analítica Ejecutiva
- **Cuadro de Honor (Top Académico):** Cálculo en tiempo real del promedio ponderado general de calificaciones para destacar y premiar a los mejores estudiantes.
- **Alerta de Estudiantes en Riesgo Académico:** Identificación oportuna de alumnos con promedio inferior a `3.0` para remitir a tutorías y acompañamiento.
- **Auditoría de Inasistencias:** Reporte especializado de estudiantes que acumulan más de 3 fallas injustificadas en el periodo evaluado.
- **Consolidados Financieros:** Cálculo del balance global: montos facturados, recaudos efectivos y cartera pendiente por cobrar.
- **Uso y Estado de Recursos:** Balance del inventario: recursos en préstamo, devueltos, dañados o en mantenimiento.

---

## 11. Programación Defensiva, Manejo de Errores y Logging

El sistema fue concebido bajo directrices de alta estabilidad y tolerancia a fallos:
- **Logger Centralizado del Sistema (`AppLogger`):** Captura todas las excepciones SQL, advertencias y sucesos diagnósticos relevantes en formato ISO-8601 con niveles de severidad (`INFO`, `WARNING`, `SEVERE`) en el archivo `error_log.txt`.
- **Validación Defensiva en Terminal (`InputValidator`):** Impide caídas imprevistas por `InputMismatchException` mediante ciclos de reintento interactivo para números enteros, decimales, fechas y cadenas no vacías.
- **Gestión Automática de Recursos:** Todas las operaciones de conexión, sentencias preparadas y flujos de datos utilizan bloques `try-with-resources`, garantizando la liberación de recursos sin fugas de memoria o de conexiones.
- **Tolerancia a Indisponibilidad de Base de Datos:** Si el motor MySQL no se encuentra disponible al arrancar, el sistema no se detiene de forma abrupta; emite una alerta explicativa al operador y despliega una interfaz asistida para verificar o corregir las credenciales.

---

## 12. Interfaz de Usuario en Terminal (TUI) y Demostración ASCII

La consola presenta una experiencia visual estructurada y ordenada mediante marcos decorativos y tablas con caracteres Unicode/ASCII:

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

### Ejemplo de Tabla Dinámica ASCII en Consola
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

## 13. Pruebas Unitarias y de Integración

La suite de pruebas automatizadas ubicada en `src/test/java/com/littlehorizons/erp/` certifica el correcto funcionamiento de los componentes críticos:
- **`ModelValidationTest`:** Valida la integridad de las entidades del modelo, formato de generación de códigos de estudiante, validación de notas en el rango `0.0` - `5.0` y límites de aforo en membresías de clubes.
- **`PdfInvoiceGeneratorTest`:** Verifica el cálculo correcto del hash CUFE mediante SHA-256, el renderizado de la matriz gráfica QR con ZXing y la correcta creación del archivo PDF en el sistema de archivos.

Para ejecutar todas las pruebas automatizadas:
```bash
mvn test
```

Resultado esperado de la ejecución:
```
[INFO] Running com.littlehorizons.erp.ModelValidationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.littlehorizons.erp.PdfInvoiceGeneratorTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 14. Limitaciones Conocidas y Supuestos Operativos

1. **Interfaz en Consola (CLI/TUI):** La interfaz primaria está diseñada para ejecutarse en terminales de texto compatibles con codificación UTF-8. En Windows PowerShell o CMD, asegúrese de contar con soporte de fuentes Unicode (puede ejecutar `chcp 65001` si nota caracteres especiales afectados).
2. **Simulación de Factura Electrónica:** Las facturas emiten fielmente la representación gráfica, el código QR y el algoritmo del CUFE requeridos por la DIAN, pero no envían paquetes XML en tiempo real mediante SOAP a los servidores de producción de la autoridad tributaria.
3. **Entidad Institucional Única:** La arquitectura actual contempla la operación de una sola sede educativa principal ("Colegio Internacional Little Horizons S.A.S.") con un único NIT tributario consolidado.

---

## 15. Hoja de Ruta (Roadmap) y Mejoras Futuras

- [ ] **Capa de Servicios API REST:** Exposición de endpoints seguros con Spring Boot o Javalin para consumo desde aplicaciones móviles.
- [ ] **Portal Web Interactivo:** Desarrollo de un panel web responsivo para acudientes y docentes utilizando React/Vue.js y Tailwind CSS.
- [ ] **Conexión Directa con Web Services DIAN:** Integración de firma digital XML basada en certificados digitales X.509 y transmisión SOAP directa.
- [ ] **Pasarela de Pagos en Línea para Acudientes:** Integración de canales de pago electrónico (PSE, PayU, Wompi).
- [ ] **Notificaciones Automáticas por Correo:** Envío automatizado de facturas PDF y reportes de inasistencias a los acudientes a través de JavaMail SMTP.

---

## 16. Licencia y Contacto

### Licencia
Este proyecto se encuentra publicado bajo los términos de la **Licencia MIT**. Para mayores detalles, consulte el archivo [LICENSE](LICENSE).

### Información de Contacto
- **Desarrollador:** Jesús Sánchez
- **GitHub:** [@Jesussanchez2805](https://github.com/Jesussanchez2805)
- **Correo Electrónico:** `Jesussanchez2805.js@gmail.com`
- **Institución:** Colegio Internacional Little Horizons S.A.S.
- **Repositorio Oficial:** [https://github.com/Jesussanchez2805/little-horizons-erp](https://github.com/Jesussanchez2805/little-horizons-erp)
