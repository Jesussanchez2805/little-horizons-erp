-- ============================================================================
-- Little Horizons Educational ERP - Complete Database Script
-- Institution: Colegio Internacional Little Horizons S.A.S.
-- Author: Jesús Sánchez (Jesussanchez2805)
-- Database Engine: MySQL 8.0+ (InnoDB, UTF8mb4)
-- Contains:
--   Section 1: Data Definition Language (DDL)
--   Section 2: Data Manipulation Language (DML) - Seed Data (30+ rows per main table)
-- ============================================================================


-- Anexo: Script de Base de Datos

-- A continuación se presenta el script completo DDL de MySQL para crear todas las tablas necesarias. 
-- Este script debe ejecutarse en una base de datos llamada `little_horizons_erp`.
-- Si encuentra detalles que modificar o agregar, puede editar el script siempre y cuando pueda justificar sus cambios.


-- ============================================
-- Little Horizons ERP - Esquema de Base de Datos
-- Sistema de Gestión Educativa
-- Versión: 2.0 (Corregida y Mejorada)
-- Motor: MySQL 8.0 o superior (REQUERIDO)
-- ============================================

DROP DATABASE IF EXISTS erp_little_horizons;
CREATE DATABASE erp_little_horizons CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_little_horizons;

-- ============================================
-- 1. MÓDULO DE ESTUDIANTES Y ACUDIENTES
-- ============================================

-- Tabla de grados académicos
CREATE TABLE grados (
    id_grado INT AUTO_INCREMENT PRIMARY KEY,
    nombre_grado VARCHAR(50) NOT NULL UNIQUE COMMENT 'Ej: Preescolar, Primero, Segundo, ... Once',
    nivel_numerico INT NOT NULL UNIQUE COMMENT 'Nivel numérico para ordenamiento (0 para preescolar, 1-11 para grados)',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Catálogo de grados académicos del colegio';

-- Tabla de estudiantes
CREATE TABLE estudiantes (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('TI', 'CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'TI',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    fecha_nacimiento DATE NOT NULL,
    genero ENUM('M', 'F', 'Otro') NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo_electronico VARCHAR(100),
    tipo_sangre VARCHAR(5),
    alergias TEXT,
    condiciones_medicas TEXT,
    fecha_ingreso DATE NOT NULL,
    estado ENUM('Activo', 'Graduado', 'Retirado', 'Suspendido') DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_estado (estado),
    INDEX idx_nombre (primer_apellido, segundo_apellido, primer_nombre)
) ENGINE=InnoDB COMMENT='Información completa de estudiantes';

-- Tabla de acudientes/tutores legales
CREATE TABLE acudientes (
    id_acudiente INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'CC',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    parentesco VARCHAR(50) NOT NULL COMMENT 'Padre, Madre, Abuelo, Tío, Tutor Legal, etc.',
    telefono_principal VARCHAR(20) NOT NULL,
    telefono_secundario VARCHAR(20),
    correo_electronico VARCHAR(100),
    direccion VARCHAR(255),
    ocupacion VARCHAR(100),
    es_responsable_financiero BOOLEAN DEFAULT TRUE COMMENT 'Indica si es responsable de pagos',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_nombre (primer_apellido, segundo_apellido)
) ENGINE=InnoDB COMMENT='Acudientes y tutores legales';

-- Relación estudiantes-acudientes (muchos a muchos)
CREATE TABLE estudiante_acudiente (
    id_estudiante INT NOT NULL,
    id_acudiente INT NOT NULL,
    es_principal BOOLEAN DEFAULT FALSE COMMENT 'Acudiente principal para comunicaciones',
    es_contacto_emergencia BOOLEAN DEFAULT TRUE COMMENT 'Disponible para emergencias',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_estudiante, id_acudiente),
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_acudiente) REFERENCES acudientes(id_acudiente) ON DELETE CASCADE,
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_acudiente (id_acudiente)
) ENGINE=InnoDB COMMENT='Relación entre estudiantes y sus acudientes';

-- Tabla de matrículas (historial de grados por año)
CREATE TABLE matriculas (
    id_matricula INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_grado INT NOT NULL,
    año_academico INT NOT NULL COMMENT 'Año académico (ej: 2024)',
    estado_matricula ENUM('Activa', 'Completada', 'Retirada') DEFAULT 'Activa',
    fecha_matricula DATE NOT NULL,
    fecha_retiro DATE,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_grado) REFERENCES grados(id_grado),
    UNIQUE KEY matricula_unica (id_estudiante, año_academico),
    INDEX idx_año_grado (año_academico, id_grado),
    INDEX idx_estado (estado_matricula)
) ENGINE=InnoDB COMMENT='Historial de matrículas de estudiantes por año y grado';

-- ============================================
-- 2. MÓDULO ACADÉMICO
-- ============================================

-- Tabla de materias/asignaturas
CREATE TABLE materias (
    id_materia INT AUTO_INCREMENT PRIMARY KEY,
    codigo_materia VARCHAR(20) NOT NULL UNIQUE COMMENT 'Código único de la materia (ej: MAT-5, ESP-3)',
    nombre_materia VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_grado INT NOT NULL COMMENT 'Grado al que pertenece la materia',
    horas_semanales INT DEFAULT 2 COMMENT 'Horas semanales asignadas',
    esta_activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_grado) REFERENCES grados(id_grado),
    INDEX idx_grado (id_grado),
    INDEX idx_activa (esta_activa),
    INDEX idx_codigo (codigo_materia)
) ENGINE=InnoDB COMMENT='Catálogo de materias por grado';

-- Tabla de profesores
CREATE TABLE profesores (
    id_profesor INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'CC',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    especializacion VARCHAR(100) COMMENT 'Área de especialización (Matemáticas, Español, etc.)',
    fecha_contratacion DATE NOT NULL,
    estado ENUM('Activo', 'Inactivo', 'Licencia') DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_estado (estado),
    INDEX idx_nombre (primer_apellido, segundo_apellido)
) ENGINE=InnoDB COMMENT='Información de profesores';

-- Asignación de materias a profesores
CREATE TABLE asignaciones_profesor (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_profesor INT NOT NULL,
    id_materia INT NOT NULL,
    año_academico INT NOT NULL,
    seccion VARCHAR(10) COMMENT 'Sección o grupo (A, B, C)',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    UNIQUE KEY asignacion_unica (id_profesor, id_materia, año_academico, seccion),
    INDEX idx_profesor_año (id_profesor, año_academico),
    INDEX idx_materia_año (id_materia, año_academico)
) ENGINE=InnoDB COMMENT='Asignación de profesores a materias por año académico';

-- Inscripción de estudiantes en materias
CREATE TABLE inscripciones_materia (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    año_academico INT NOT NULL,
    periodo_academico ENUM('I', 'II', 'III', 'IV') NOT NULL COMMENT 'Período académico',
    seccion VARCHAR(10) COMMENT 'Sección o grupo asignado',
    fecha_inscripcion DATE NOT NULL,
    estado ENUM('Activa', 'Retirada', 'Completada') DEFAULT 'Activa',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    UNIQUE KEY inscripcion_unica (id_estudiante, id_materia, año_academico, periodo_academico),
    INDEX idx_estudiante_año (id_estudiante, año_academico),
    INDEX idx_materia_periodo (id_materia, periodo_academico)
) ENGINE=InnoDB COMMENT='Inscripción de estudiantes en materias específicas';

-- Tabla de registros académicos (calificaciones/notas)
CREATE TABLE registros_academicos (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    id_profesor INT NOT NULL,
    año_academico INT NOT NULL,
    periodo_academico ENUM('I', 'II', 'III', 'IV', 'Final') NOT NULL,
    valor_nota DECIMAL(2,1) NOT NULL COMMENT 'Nota de 0.0 a 5.0 según estándar colombiano',
    observaciones TEXT,
    fecha_registro DATE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor),
    CHECK (valor_nota >= 0.0 AND valor_nota <= 5.0),
    UNIQUE KEY registro_unico (id_estudiante, id_materia, año_academico, periodo_academico),
    INDEX idx_estudiante_año (id_estudiante, año_academico),
    INDEX idx_materia_periodo (id_materia, periodo_academico),
    INDEX idx_valor_nota (valor_nota)
) ENGINE=InnoDB COMMENT='Registro de calificaciones de estudiantes';

-- Tabla de asistencia
CREATE TABLE asistencias (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    fecha_asistencia DATE NOT NULL,
    estado_asistencia ENUM('Presente', 'Ausente', 'Tardanza', 'Excusa Médica') NOT NULL,
    observaciones TEXT,
    registrado_por INT COMMENT 'ID del profesor que registró la asistencia',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    FOREIGN KEY (registrado_por) REFERENCES profesores(id_profesor),
    UNIQUE KEY asistencia_unica (id_estudiante, id_materia, fecha_asistencia),
    INDEX idx_estudiante_fecha (id_estudiante, fecha_asistencia),
    INDEX idx_materia_fecha (id_materia, fecha_asistencia),
    INDEX idx_estado (estado_asistencia)
) ENGINE=InnoDB COMMENT='Registro de asistencia diaria de estudiantes';

-- ============================================
-- 3. MÓDULO DE ACTIVIDADES EXTRACURRICULARES
-- ============================================

-- Tabla de clubes
CREATE TABLE clubes (
    id_club INT AUTO_INCREMENT PRIMARY KEY,
    nombre_club VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    id_profesor_supervisor INT COMMENT 'Profesor encargado del club',
    capacidad_maxima INT DEFAULT 30 COMMENT 'Cupo máximo de estudiantes',
    horario_reunion VARCHAR(100) COMMENT 'Días y horario de reunión (ej: Martes 3-5 PM)',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_profesor_supervisor) REFERENCES profesores(id_profesor),
    INDEX idx_activo (esta_activo),
    INDEX idx_nombre (nombre_club)
) ENGINE=InnoDB COMMENT='Catálogo de clubes extracurriculares';

-- Membresías en clubes
CREATE TABLE membresias_club (
    id_membresia INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_club INT NOT NULL,
    fecha_ingreso DATE NOT NULL,
    estado_membresia ENUM('Activa', 'Retirada') DEFAULT 'Activa',
    fecha_retiro DATE,
    motivo_retiro TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_club) REFERENCES clubes(id_club) ON DELETE CASCADE,
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_club_estado (id_club, estado_membresia)
) ENGINE=InnoDB COMMENT='Membresías de estudiantes en clubes';

-- Tabla de eventos
CREATE TABLE eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nombre_evento VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo_evento ENUM('Académico', 'Cultural', 'Deportivo', 'Social', 'Científico') NOT NULL,
    fecha_evento DATE NOT NULL,
    lugar VARCHAR(150),
    tiene_competencia BOOLEAN DEFAULT FALSE COMMENT 'Indica si el evento tiene competencia con posiciones',
    id_organizador INT COMMENT 'Profesor organizador del evento',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_organizador) REFERENCES profesores(id_profesor),
    INDEX idx_fecha (fecha_evento),
    INDEX idx_tipo (tipo_evento),
    INDEX idx_activo (esta_activo)
) ENGINE=InnoDB COMMENT='Catálogo de eventos del colegio';

-- Participantes en eventos
CREATE TABLE participantes_evento (
    id_participacion INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_estudiante INT NOT NULL,
    rol_participacion ENUM('Participante', 'Competidor', 'Organizador', 'Asistente') DEFAULT 'Participante',
    posicion_obtenida VARCHAR(20) COMMENT 'Posición obtenida si hay competencia (1er Lugar, 2do Lugar, 3er Lugar, Participante)',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    UNIQUE KEY participacion_unica (id_evento, id_estudiante),
    INDEX idx_evento (id_evento),
    INDEX idx_estudiante (id_estudiante)
) ENGINE=InnoDB COMMENT='Registro de participación de estudiantes en eventos';

-- ============================================
-- 4. MÓDULO FINANCIERO
-- ============================================

-- Conceptos de pago (catálogo)
CREATE TABLE conceptos_pago (
    id_concepto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_concepto VARCHAR(20) NOT NULL UNIQUE,
    nombre_concepto VARCHAR(100) NOT NULL,
    descripcion TEXT,
    monto_base DECIMAL(10,2) NOT NULL COMMENT 'Valor base del concepto en pesos colombianos',
    es_recurrente BOOLEAN DEFAULT FALSE COMMENT 'Indica si es un pago recurrente (ej: pensión mensual)',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_activo (esta_activo),
    INDEX idx_codigo (codigo_concepto)
) ENGINE=InnoDB COMMENT='Catálogo de conceptos de pago del colegio';

-- Tabla de pagos
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_acudiente INT COMMENT 'Acudiente que realizó el pago',
    id_concepto INT NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'Tarjeta Débito', 'Tarjeta Crédito', 'Cheque') NOT NULL,
    numero_recibo VARCHAR(50) UNIQUE,
    año_academico INT COMMENT 'Año al que aplica el pago',
    periodo_academico ENUM('I', 'II', 'III', 'IV', 'Anual') COMMENT 'Período al que aplica el pago',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_acudiente) REFERENCES acudientes(id_acudiente),
    FOREIGN KEY (id_concepto) REFERENCES conceptos_pago(id_concepto),
    INDEX idx_estudiante_fecha (id_estudiante, fecha_pago),
    INDEX idx_concepto (id_concepto),
    INDEX idx_fecha (fecha_pago),
    INDEX idx_recibo (numero_recibo)
) ENGINE=InnoDB COMMENT='Registro de todos los pagos realizados';

-- Tabla de facturas electrónicas
CREATE TABLE facturas (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE COMMENT 'Número consecutivo de factura',
    id_pago INT NOT NULL COMMENT 'Pago asociado a esta factura',
    fecha_emision DATE NOT NULL,
    cufe VARCHAR(100) COMMENT 'Código Único de Factura Electrónica (simulado)',
    nombre_archivo_pdf VARCHAR(255) COMMENT 'Nombre del archivo PDF generado',
    ruta_archivo VARCHAR(500) COMMENT 'Ruta completa donde se guardó el PDF',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE CASCADE,
    INDEX idx_numero_factura (numero_factura),
    INDEX idx_fecha_emision (fecha_emision),
    INDEX idx_pago (id_pago)
) ENGINE=InnoDB COMMENT='Registro de facturas electrónicas generadas';

-- ============================================
-- 5. MÓDULO DE GESTIÓN DE RECURSOS
-- ============================================

-- Categorías de recursos
CREATE TABLE categorias_recurso (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Categorías de recursos (Libro, Equipo de Laboratorio, etc.)';

-- Tabla de recursos del colegio
CREATE TABLE recursos (
    id_recurso INT AUTO_INCREMENT PRIMARY KEY,
    codigo_recurso VARCHAR(50) NOT NULL UNIQUE COMMENT 'Código de inventario único',
    nombre_recurso VARCHAR(150) NOT NULL,
    id_categoria INT NOT NULL,
    descripcion TEXT,
    fecha_adquisicion DATE,
    ubicacion_fisica VARCHAR(100) COMMENT 'Ubicación física (Biblioteca, Laboratorio 1, etc.)',
    estado_recurso ENUM('Disponible', 'Prestado', 'En Mantenimiento', 'Dado de Baja') DEFAULT 'Disponible',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categorias_recurso(id_categoria),
    INDEX idx_codigo (codigo_recurso),
    INDEX idx_estado (estado_recurso),
    INDEX idx_categoria (id_categoria),
    INDEX idx_nombre (nombre_recurso)
) ENGINE=InnoDB COMMENT='Inventario de recursos prestables del colegio';

-- Tabla de préstamos de recursos a ESTUDIANTES
CREATE TABLE prestamos_estudiante (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_recurso INT NOT NULL,
    id_estudiante INT NOT NULL COMMENT 'Estudiante que toma prestado el recurso',
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado_prestamo ENUM('Activo', 'Devuelto', 'Vencido') DEFAULT 'Activo',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso) ON DELETE CASCADE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    INDEX idx_recurso_estado (id_recurso, estado_prestamo),
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_estado (estado_prestamo),
    INDEX idx_fecha_devolucion (fecha_devolucion_esperada)
) ENGINE=InnoDB COMMENT='Registro de préstamos de recursos a estudiantes';

-- Tabla de préstamos de recursos a PROFESORES
CREATE TABLE prestamos_profesor (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_recurso INT NOT NULL,
    id_profesor INT NOT NULL COMMENT 'Profesor que toma prestado el recurso',
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado_prestamo ENUM('Activo', 'Devuelto', 'Vencido') DEFAULT 'Activo',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso) ON DELETE CASCADE,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor) ON DELETE CASCADE,
    INDEX idx_recurso_estado (id_recurso, estado_prestamo),
    INDEX idx_profesor (id_profesor),
    INDEX idx_estado (estado_prestamo),
    INDEX idx_fecha_devolucion (fecha_devolucion_esperada)
) ENGINE=InnoDB COMMENT='Registro de préstamos de recursos a profesores';

-- ============================================

-- ============================================================================
-- SECTION 2: DATA MANIPULATION LANGUAGE (DML) - SEED DATA
-- All records are realistic for Colombian educational system.
-- Includes >= 30 records for each primary table and full referential integrity.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ACADEMIC GRADES (grados)
-- ----------------------------------------------------------------------------
INSERT INTO grados (id_grado, nombre_grado, nivel_numerico) VALUES
(1, 'Preescolar', 0),
(2, 'Primero', 1),
(3, 'Segundo', 2),
(4, 'Tercero', 3),
(5, 'Cuarto', 4),
(6, 'Quinto', 5),
(7, 'Sexto', 6),
(8, 'Séptimo', 7),
(9, 'Octavo', 8),
(10, 'Noveno', 9),
(11, 'Décimo', 10),
(12, 'Once', 11);

-- ----------------------------------------------------------------------------
-- 2. RESOURCE CATEGORIES (categorias_recurso)
-- ----------------------------------------------------------------------------
INSERT INTO categorias_recurso (id_categoria, nombre_categoria, descripcion) VALUES
(1, 'Libro', 'Libros de texto, literatura y material bibliográfico'),
(2, 'Equipo de Laboratorio', 'Instrumental científico de física, química y biología'),
(3, 'Equipo Deportivo', 'Implementos para educación física y escuelas deportivas'),
(4, 'Dispositivo Tecnológico', 'Tablets, computadores portátiles y calculadoras gráficas'),
(5, 'Material Didáctico', 'Modelos anatómicos, globos terráqueos y kits de robótica');

-- ----------------------------------------------------------------------------
-- 3. PAYMENT CONCEPTS (conceptos_pago)
-- ----------------------------------------------------------------------------
INSERT INTO conceptos_pago (id_concepto, codigo_concepto, nombre_concepto, descripcion, monto_base, es_recurrente) VALUES
(1, 'MAT-2024', 'Matrícula Anual Ordinaria', 'Derecho de matrícula académica anual', 650000.00, FALSE),
(2, 'PEN-MEN', 'Pensión Mensual Escolar', 'Mensualidad académica regular (Febrero - Noviembre)', 380000.00, TRUE),
(3, 'UNI-COM', 'Uniforme Escolar Completo', 'Kit de uniforme de diario y educación física', 260000.00, FALSE),
(4, 'TRN-MEN', 'Transporte Escolar Mensual', 'Ruta escolar puerta a puerta mensual', 170000.00, TRUE),
(5, 'ALM-MEN', 'Servicio de Restaurante y Almuerzo', 'Alimentación balanceada mensual en cafetería', 145000.00, TRUE),
(6, 'EXT-DEP', 'Salida Pedagógica y Campamento', 'Actividad vivencial y campamento científico', 75000.00, FALSE),
(7, 'CER-ACA', 'Certificado y Constancia de Estudio', 'Expedición de constancia oficial de notas', 18000.00, FALSE);

-- ----------------------------------------------------------------------------
-- 4. GUARDIANS / ACUDIENTES (acudientes - 30 records)
-- ----------------------------------------------------------------------------
INSERT INTO acudientes (id_acudiente, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, parentesco, telefono_principal, telefono_secundario, correo_electronico, direccion, ocupacion, es_responsable_financiero) VALUES
(1, 'CC', '1017123001', 'Carlos', 'Eduardo', 'Ramírez', 'Ospina', 'Padre', '3104567801', '3001234501', 'carlos.ramirez@gmail.com', 'Calle 45 # 12-34, Bogotá', 'Ingeniero Civil', TRUE),
(2, 'CC', '1017123002', 'Martha', 'Lucía', 'Gómez', 'Restrepo', 'Madre', '3114567802', '3011234502', 'martha.gomez@hotmail.com', 'Carrera 7 # 115-20, Bogotá', 'Médica Pediatra', TRUE),
(3, 'CC', '1017123003', 'Jorge', 'Enrique', 'Martínez', 'Vargas', 'Padre', '3124567803', '3021234503', 'jorge.martinez@outlook.com', 'Avenida 19 # 104-50, Bogotá', 'Contador Público', TRUE),
(4, 'CC', '1017123004', 'Claudia', 'Patricia', 'Rodríguez', 'Peña', 'Madre', '3134567804', '3031234504', 'claudia.rodriguez@gmail.com', 'Calle 127 # 45-18, Bogotá', 'Arquitecta', TRUE),
(5, 'CC', '1017123005', 'Andrés', 'Felipe', 'López', 'Castro', 'Padre', '3144567805', '3041234505', 'andres.lopez@yahoo.com', 'Transversal 23 # 85-10, Bogotá', 'Administrador de Empresas', TRUE),
(6, 'CC', '1017123006', 'Sandra', 'Milena', 'Hernández', 'Silva', 'Madre', '3154567806', '3051234506', 'sandra.hernandez@gmail.com', 'Calle 80 # 68-40, Bogotá', 'Docente Universitaria', TRUE),
(7, 'CC', '1017123007', 'Mauricio', 'Javier', 'Díaz', 'Morales', 'Padre', '3164567807', '3061234507', 'mauricio.diaz@gmail.com', 'Carrera 58 # 134-22, Bogotá', 'Abogado Corporativo', TRUE),
(8, 'CC', '1017123008', 'Diana', 'Marcela', 'Torres', 'Navarro', 'Madre', '3174567808', '3071234508', 'diana.torres@outlook.com', 'Calle 134 # 9-45, Bogotá', 'Economista', TRUE),
(9, 'CC', '1017123009', 'Ricardo', 'Alfonso', 'Pérez', 'Salazar', 'Padre', '3184567809', '3081234509', 'ricardo.perez@gmail.com', 'Carrera 15 # 93-60, Bogotá', 'Consultor Financiero', TRUE),
(10, 'CC', '1017123010', 'Paola', 'Andrea', 'Castro', 'Mendoza', 'Madre', '3194567810', '3091234510', 'paola.castro@hotmail.com', 'Calle 100 # 19A-25, Bogotá', 'Odontóloga', TRUE),
(11, 'CC', '1017123011', 'Gustavo', 'Adolfo', 'Vargas', 'Reyes', 'Tutor Legal', '3204567811', '3101234511', 'gustavo.vargas@gmail.com', 'Calle 63 # 24-15, Bogotá', 'Empresario', TRUE),
(12, 'CC', '1017123012', 'Adriana', 'María', 'Rojas', 'Guerrero', 'Madre', '3214567812', '3111234512', 'adriana.rojas@gmail.com', 'Carrera 11 # 82-40, Bogotá', 'Psicóloga Clínica', TRUE),
(13, 'CC', '1017123013', 'Héctor', 'Fabio', 'Suárez', 'Cárdenas', 'Padre', '3224567813', '3121234513', 'hector.suarez@outlook.com', 'Calle 140 # 11-12, Bogotá', 'Ingeniero Químico', TRUE),
(14, 'CC', '1017123014', 'Liliana', 'Beatriz', 'Moreno', 'Acosta', 'Madre', '3234567814', '3131234514', 'liliana.moreno@gmail.com', 'Carrera 7A # 124-30, Bogotá', 'Diseñadora Gráfica', TRUE),
(15, 'CC', '1017123015', 'Fernando', 'José', 'Gutiérrez', 'Ortega', 'Padre', '3244567815', '3141234515', 'fernando.gutierrez@yahoo.com', 'Calle 116 # 18-50, Bogotá', 'Banquero', TRUE),
(16, 'CC', '1017123016', 'Gloria', 'Esperanza', 'Mejía', 'Pardo', 'Madre', '3254567816', '3151234516', 'gloria.mejia@gmail.com', 'Avenida Pepe Sierra # 15-20, Bogotá', 'Periodista', TRUE),
(17, 'CC', '1017123017', 'Óscar', 'Darío', 'Jiménez', 'Beltrán', 'Padre', '3264567817', '3161234517', 'oscar.jimenez@hotmail.com', 'Carrera 9 # 100-11, Bogotá', 'Analista de Sistemas', TRUE),
(18, 'CC', '1017123018', 'Monica', 'Cecilia', 'Herrera', 'Castaño', 'Madre', '3274567818', '3171234518', 'monica.herrera@gmail.com', 'Calle 72 # 5-80, Bogotá', 'Directora de Mercadeo', TRUE),
(19, 'CC', '1017123019', 'Javier', 'Ignacio', 'Medina', 'Rincón', 'Padre', '3284567819', '3181234519', 'javier.medina@outlook.com', 'Transversal 55 # 102-14, Bogotá', 'Comerciante', TRUE),
(20, 'CC', '1017123020', 'Beatriz', 'Elena', 'Aguilar', 'Paredes', 'Tutor Legal', '3294567820', '3191234520', 'beatriz.aguilar@gmail.com', 'Calle 53 # 16-32, Bogotá', 'Nutricionista', TRUE),
(21, 'CC', '1017123021', 'Wilson', 'Alexander', 'Sánchez', 'Cruz', 'Padre', '3109876521', '3009876521', 'wilson.sanchez@gmail.com', 'Carrera 13 # 63-45, Bogotá', 'Ingeniero Mecánico', TRUE),
(22, 'CC', '1017123022', 'Yolanda', 'Inés', 'Ortiz', 'Mora', 'Madre', '3119876522', '3019876522', 'yolanda.ortiz@hotmail.com', 'Calle 106 # 22-10, Bogotá', 'Bióloga', TRUE),
(23, 'CC', '1017123023', 'César', 'Augusto', 'Ríos', 'Valencia', 'Padre', '3129876523', '3029876523', 'cesar.rios@gmail.com', 'Carrera 19 # 88-34, Bogotá', 'Piloto Comercial', TRUE),
(24, 'CC', '1017123024', 'Marisol', 'del Carmen', 'Camacho', 'Duque', 'Madre', '3139876524', '3039876524', 'marisol.camacho@yahoo.com', 'Calle 147 # 14-25, Bogotá', 'Fisioterapeuta', TRUE),
(25, 'CC', '1017123025', 'Nelson', 'Enrique', 'Pineda', 'Soto', 'Padre', '3149876525', '3049876525', 'nelson.pineda@outlook.com', 'Carrera 50 # 128-40, Bogotá', 'Arquitecto de Software', TRUE),
(26, 'CC', '1017123026', 'Constanza', 'María', 'Franco', 'Montoya', 'Madre', '3159876526', '3059876526', 'constanza.franco@gmail.com', 'Calle 95 # 11-45, Bogotá', 'Traductora Oficial', TRUE),
(27, 'CC', '1017123027', 'Alvaro', 'Hernán', 'Bermúdez', 'Caicedo', 'Padre', '3169876527', '3069876527', 'alvaro.bermudez@gmail.com', 'Carrera 21 # 100-30, Bogotá', 'Gerente de Proyectos', TRUE),
(28, 'CC', '1017123028', 'Clara', 'Inés', 'Giraldo', 'Zapata', 'Madre', '3179876528', '3079876528', 'clara.giraldo@hotmail.com', 'Calle 122 # 15-60, Bogotá', 'Veterinaria', TRUE),
(29, 'CC', '1017123029', 'Germán', 'Antonio', 'Ochoa', 'Vélez', 'Padre', '3189876529', '3089876529', 'german.ochoa@outlook.com', 'Transversal 17 # 104-12, Bogotá', 'Auditor Financiero', TRUE),
(30, 'CC', '1017123030', 'Sonia', 'Patricia', 'Cifuentes', 'Londoño', 'Madre', '3199876530', '3099876530', 'sonia.cifuentes@gmail.com', 'Calle 152 # 9-80, Bogotá', 'Química Farmacéutica', TRUE);

-- ----------------------------------------------------------------------------
-- 5. STUDENTS / ESTUDIANTES (estudiantes - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO estudiantes (id_estudiante, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, fecha_nacimiento, genero, direccion, telefono, correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, estado) VALUES
(1, 'TI', '1085001001', 'Mateo', 'Alejandro', 'Ramírez', 'Gómez', '2012-03-15', 'M', 'Calle 45 # 12-34, Bogotá', '3104567801', 'mateo.ramirez@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2020-01-20', 'Activo'),
(2, 'TI', '1085001002', 'Valentina', 'Sofía', 'Martínez', 'Rodríguez', '2011-06-22', 'F', 'Avenida 19 # 104-50, Bogotá', '3124567803', 'valentina.martinez@lh.edu.co', 'A+', 'Polen', 'Rinitis alérgica', '2019-01-22', 'Activo'),
(3, 'TI', '1085001003', 'Santiago', 'Nicolás', 'López', 'Hernández', '2010-09-10', 'M', 'Transversal 23 # 85-10, Bogotá', '3144567805', 'santiago.lopez@lh.edu.co', 'B+', 'Penicilina', 'Ninguna', '2018-01-15', 'Activo'),
(4, 'TI', '1085001004', 'Isabella', 'Marie', 'Díaz', 'Torres', '2013-11-05', 'F', 'Carrera 58 # 134-22, Bogotá', '3164567807', 'isabella.diaz@lh.edu.co', 'O-', 'Maní', 'Asma leve controlada', '2021-01-18', 'Activo'),
(5, 'TI', '1085001005', 'Samuel', 'David', 'Pérez', 'Castro', '2008-04-18', 'M', 'Carrera 15 # 93-60, Bogotá', '3184567809', 'samuel.perez@lh.edu.co', 'AB+', 'Ninguna', 'Ninguna', '2016-01-20', 'Activo'),
(6, 'TI', '1085001006', 'Mariana', 'Lucía', 'Vargas', 'Rojas', '2007-08-30', 'F', 'Calle 63 # 24-15, Bogotá', '3204567811', 'mariana.vargas@lh.edu.co', 'O+', 'Ibuprofeno', 'Miopía', '2015-01-19', 'Activo'),
(7, 'TI', '1085001007', 'Sebastián', 'Andrés', 'Suárez', 'Moreno', '2014-02-14', 'M', 'Calle 140 # 11-12, Bogotá', '3224567813', 'sebastian.suarez@lh.edu.co', 'A-', 'Ninguna', 'Ninguna', '2022-01-24', 'Activo'),
(8, 'TI', '1085001008', 'Camila', 'Andrea', 'Gutiérrez', 'Mejía', '2009-12-03', 'F', 'Calle 116 # 18-50, Bogotá', '3244567815', 'camila.gutierrez@lh.edu.co', 'O+', 'Mariscos', 'Ninguna', '2017-01-17', 'Activo'),
(9, 'TI', '1085001009', 'Daniel', 'Esteban', 'Jiménez', 'Herrera', '2006-05-25', 'M', 'Carrera 9 # 100-11, Bogotá', '3264567817', 'daniel.jimenez@lh.edu.co', 'B-', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo'),
(10, 'TI', '1085001010', 'Luciana', 'Valeria', 'Medina', 'Aguilar', '2015-07-19', 'F', 'Transversal 55 # 102-14, Bogotá', '3284567819', 'luciana.medina@lh.edu.co', 'O+', 'Lactosa', 'Intolerancia a la lactosa', '2023-01-23', 'Activo'),
(11, 'TI', '1085001011', 'Gabriel', 'Felipe', 'Sánchez', 'Ortiz', '2011-10-12', 'M', 'Carrera 13 # 63-45, Bogotá', '3109876521', 'gabriel.sanchez@lh.edu.co', 'A+', 'Ninguna', 'Ninguna', '2019-01-22', 'Activo'),
(12, 'TI', '1085001012', 'Salomé', 'Victoria', 'Ríos', 'Camacho', '2013-01-28', 'F', 'Carrera 19 # 88-34, Bogotá', '3129876523', 'salome.rios@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2021-01-18', 'Activo'),
(13, 'TI', '1085001013', 'Alejandro', 'Manuel', 'Pineda', 'Franco', '2008-11-14', 'M', 'Carrera 50 # 128-40, Bogotá', '3149876525', 'alejandro.pineda@lh.edu.co', 'AB-', 'Acaros', 'Dermatitis atópica', '2016-01-20', 'Activo'),
(14, 'TI', '1085001014', 'Gabriela', 'Fernanda', 'Bermúdez', 'Giraldo', '2010-04-09', 'F', 'Carrera 21 # 100-30, Bogotá', '3169876527', 'gabriela.bermudez@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2018-01-15', 'Activo'),
(15, 'TI', '1085001015', 'Emiliano', 'José', 'Ochoa', 'Cifuentes', '2012-08-21', 'M', 'Transversal 17 # 104-12, Bogotá', '3189876529', 'emiliano.ochoa@lh.edu.co', 'A+', 'Ninguna', 'Ninguna', '2020-01-20', 'Activo'),
(16, 'TI', '1085001016', 'Antonella', 'Paz', 'Ramírez', 'Gómez', '2016-09-04', 'F', 'Calle 45 # 12-34, Bogotá', '3104567801', 'antonella.ramirez@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-01-22', 'Activo'),
(17, 'TI', '1085001017', 'Joaquín', 'Ignacio', 'Martínez', 'Rodríguez', '2014-12-11', 'M', 'Avenida 19 # 104-50, Bogotá', '3124567803', 'joaquin.martinez@lh.edu.co', 'A+', 'Ninguna', 'Ninguna', '2022-01-24', 'Activo'),
(18, 'TI', '1085001018', 'Martín', 'Eduardo', 'López', 'Hernández', '2013-05-16', 'M', 'Transversal 23 # 85-10, Bogotá', '3144567805', 'martin.lopez@lh.edu.co', 'B+', 'Ninguna', 'Ninguna', '2021-01-18', 'Activo'),
(19, 'TI', '1085001019', 'Victoria', 'Elena', 'Díaz', 'Torres', '2015-03-27', 'F', 'Carrera 58 # 134-22, Bogotá', '3164567807', 'victoria.diaz@lh.edu.co', 'O-', 'Ninguna', 'Ninguna', '2023-01-23', 'Activo'),
(20, 'TI', '1085001020', 'Lucas', 'Guillermo', 'Pérez', 'Castro', '2011-02-08', 'M', 'Carrera 15 # 93-60, Bogotá', '3184567809', 'lucas.perez@lh.edu.co', 'AB+', 'Ninguna', 'Ninguna', '2019-01-22', 'Activo'),
(21, 'TI', '1085001021', 'Emma', 'Carolina', 'Vargas', 'Rojas', '2014-06-30', 'F', 'Calle 63 # 24-15, Bogotá', '3204567811', 'emma.vargas@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2022-01-24', 'Activo'),
(22, 'TI', '1085001022', 'Tomás', 'Alonso', 'Suárez', 'Moreno', '2016-10-15', 'M', 'Calle 140 # 11-12, Bogotá', '3224567813', 'tomas.suarez@lh.edu.co', 'A-', 'Ninguna', 'Ninguna', '2024-01-22', 'Activo'),
(23, 'TI', '1085001023', 'Mia', 'Celeste', 'Gutiérrez', 'Mejía', '2012-04-03', 'F', 'Calle 116 # 18-50, Bogotá', '3244567815', 'mia.gutierrez@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2020-01-20', 'Activo'),
(24, 'TI', '1085001024', 'Benjamín', 'Arturo', 'Jiménez', 'Herrera', '2010-01-19', 'M', 'Carrera 9 # 100-11, Bogotá', '3264567817', 'benjamin.jimenez@lh.edu.co', 'B-', 'Ninguna', 'Ninguna', '2018-01-15', 'Activo'),
(25, 'TI', '1085001025', 'Julieta', 'Renata', 'Medina', 'Aguilar', '2017-07-25', 'F', 'Transversal 55 # 102-14, Bogotá', '3284567819', 'julieta.medina@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-01-22', 'Activo'),
(26, 'TI', '1085001026', 'Ignacio', 'Javier', 'Sánchez', 'Ortiz', '2015-11-09', 'M', 'Carrera 13 # 63-45, Bogotá', '3109876521', 'ignacio.sanchez@lh.edu.co', 'A+', 'Ninguna', 'Ninguna', '2023-01-23', 'Activo'),
(27, 'TI', '1085001027', 'Elena', 'Beatriz', 'Ríos', 'Camacho', '2016-03-14', 'F', 'Carrera 19 # 88-34, Bogotá', '3129876523', 'elena.rios@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-01-22', 'Activo'),
(28, 'TI', '1085001028', 'Facundo', 'Gael', 'Pineda', 'Franco', '2011-09-02', 'M', 'Carrera 50 # 128-40, Bogotá', '3149876525', 'facundo.pineda@lh.edu.co', 'AB-', 'Ninguna', 'Ninguna', '2019-01-22', 'Activo'),
(29, 'TI', '1085001029', 'Olivia', 'Guadalupe', 'Bermúdez', 'Giraldo', '2013-12-20', 'F', 'Carrera 21 # 100-30, Bogotá', '3169876527', 'olivia.bermudez@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2021-01-18', 'Activo'),
(30, 'TI', '1085001030', 'Simón', 'Vicente', 'Ochoa', 'Cifuentes', '2015-08-05', 'M', 'Transversal 17 # 104-12, Bogotá', '3189876529', 'simon.ochoa@lh.edu.co', 'A+', 'Ninguna', 'Ninguna', '2023-01-23', 'Activo'),
(31, 'CC', '1000103031', 'Felipe', 'Andrés', 'Castro', 'Gómez', '2006-02-11', 'M', 'Calle 100 # 19A-25, Bogotá', '3194567810', 'felipe.castro@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo'),
(32, 'CC', '1000103032', 'Sara', 'Isabel', 'Vargas', 'Reyes', '2006-04-15', 'F', 'Calle 63 # 24-15, Bogotá', '3204567811', 'sara.vargas@lh.edu.co', 'O+', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo'),
(33, 'CC', '1000103033', 'Nicolás', 'Esteban', 'Suárez', 'Cárdenas', '2006-07-22', 'M', 'Calle 140 # 11-12, Bogotá', '3224567813', 'nicolas.suarez@lh.edu.co', 'A-', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo'),
(34, 'CC', '1000103034', 'Valeria', 'Sofía', 'Moreno', 'Acosta', '2006-09-18', 'F', 'Carrera 7A # 124-30, Bogotá', '3234567814', 'valeria.moreno@lh.edu.co', 'B+', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo'),
(35, 'CC', '1000103035', 'Julián', 'David', 'Jiménez', 'Beltrán', '2006-11-30', 'M', 'Carrera 9 # 100-11, Bogotá', '3264567817', 'julian.jimenez@lh.edu.co', 'B-', 'Ninguna', 'Ninguna', '2014-01-20', 'Activo');

-- ----------------------------------------------------------------------------
-- 6. STUDENT - GUARDIAN RELATIONSHIPS (estudiante_acudiente - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, es_principal, es_contacto_emergencia) VALUES
(1, 1, TRUE, TRUE), (1, 2, FALSE, TRUE),
(2, 3, TRUE, TRUE), (2, 4, FALSE, TRUE),
(3, 5, TRUE, TRUE), (3, 6, FALSE, TRUE),
(4, 7, TRUE, TRUE), (4, 8, FALSE, TRUE),
(5, 9, TRUE, TRUE), (5, 10, FALSE, TRUE),
(6, 11, TRUE, TRUE), (6, 12, FALSE, TRUE),
(7, 13, TRUE, TRUE), (7, 14, FALSE, TRUE),
(8, 15, TRUE, TRUE), (8, 16, FALSE, TRUE),
(9, 17, TRUE, TRUE), (9, 18, FALSE, TRUE),
(10, 19, TRUE, TRUE), (10, 20, FALSE, TRUE),
(11, 21, TRUE, TRUE), (11, 22, FALSE, TRUE),
(12, 23, TRUE, TRUE), (12, 24, FALSE, TRUE),
(13, 25, TRUE, TRUE), (13, 26, FALSE, TRUE),
(14, 27, TRUE, TRUE), (14, 28, FALSE, TRUE),
(15, 29, TRUE, TRUE), (15, 30, FALSE, TRUE),
(16, 1, TRUE, TRUE), (17, 3, TRUE, TRUE),
(18, 5, TRUE, TRUE), (19, 7, TRUE, TRUE),
(20, 9, TRUE, TRUE), (31, 10, TRUE, TRUE);

-- ----------------------------------------------------------------------------
-- 7. ENROLLMENTS (matriculas - 35 records for academic year 2024)
-- ----------------------------------------------------------------------------
INSERT INTO matriculas (id_matricula, id_estudiante, id_grado, año_academico, estado_matricula, fecha_matricula, observaciones) VALUES
(1, 1, 7, 2024, 'Activa', '2024-01-15', 'Promovido de grado quinto'),
(2, 2, 8, 2024, 'Activa', '2024-01-15', 'Promovida con excelencia'),
(3, 3, 9, 2024, 'Activa', '2024-01-16', 'Promovido de grado octavo'),
(4, 4, 6, 2024, 'Activa', '2024-01-16', 'Ingreso a primaria superior'),
(5, 5, 11, 2024, 'Activa', '2024-01-17', 'Estudiante de grado décimo'),
(6, 6, 12, 2024, 'Activa', '2024-01-17', 'Grado once - Promoción 2024'),
(7, 7, 5, 2024, 'Activa', '2024-01-18', 'Promovido a cuarto'),
(8, 8, 10, 2024, 'Activa', '2024-01-18', 'Excelente rendimiento académico'),
(9, 9, 12, 2024, 'Activa', '2024-01-19', 'Grado once - Promoción 2024'),
(10, 10, 4, 2024, 'Activa', '2024-01-19', 'Promovida a tercero'),
(11, 11, 8, 2024, 'Activa', '2024-01-20', 'Promovido a séptimo'),
(12, 12, 6, 2024, 'Activa', '2024-01-20', 'Promovida a quinto'),
(13, 13, 11, 2024, 'Activa', '2024-01-21', 'Promovido a décimo'),
(14, 14, 9, 2024, 'Activa', '2024-01-21', 'Promovida a octavo'),
(15, 15, 7, 2024, 'Activa', '2024-01-22', 'Promovido a sexto'),
(16, 16, 2, 2024, 'Activa', '2024-01-22', 'Ingreso a grado primero'),
(17, 17, 4, 2024, 'Activa', '2024-01-23', 'Promovido a tercero'),
(18, 18, 5, 2024, 'Activa', '2024-01-23', 'Promovido a cuarto'),
(19, 19, 3, 2024, 'Activa', '2024-01-24', 'Promovida a segundo'),
(20, 20, 8, 2024, 'Activa', '2024-01-24', 'Promovido a séptimo'),
(21, 21, 5, 2024, 'Activa', '2024-01-25', 'Promovida a cuarto'),
(22, 22, 2, 2024, 'Activa', '2024-01-25', 'Ingreso a grado primero'),
(23, 23, 7, 2024, 'Activa', '2024-01-26', 'Promovida a sexto'),
(24, 24, 9, 2024, 'Activa', '2024-01-26', 'Promovido a octavo'),
(25, 25, 1, 2024, 'Activa', '2024-01-27', 'Ingreso a Preescolar'),
(26, 26, 3, 2024, 'Activa', '2024-01-27', 'Promovido a segundo'),
(27, 27, 2, 2024, 'Activa', '2024-01-28', 'Ingreso a grado primero'),
(28, 28, 8, 2024, 'Activa', '2024-01-28', 'Promovido a séptimo'),
(29, 29, 6, 2024, 'Activa', '2024-01-29', 'Promovida a quinto'),
(30, 30, 3, 2024, 'Activa', '2024-01-29', 'Promovido a segundo'),
(31, 31, 12, 2024, 'Activa', '2024-01-30', 'Grado once - Promoción 2024'),
(32, 32, 12, 2024, 'Activa', '2024-01-30', 'Grado once - Promoción 2024'),
(33, 33, 12, 2024, 'Activa', '2024-01-31', 'Grado once - Promoción 2024'),
(34, 34, 12, 2024, 'Activa', '2024-01-31', 'Grado once - Promoción 2024'),
(35, 35, 12, 2024, 'Activa', '2024-01-31', 'Grado once - Promoción 2024');

-- ----------------------------------------------------------------------------
-- 8. TEACHERS / PROFESORES (profesores - 30 records)
-- ----------------------------------------------------------------------------
INSERT INTO profesores (id_profesor, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, correo_electronico, telefono, especializacion, fecha_contratacion, estado) VALUES
(1, 'CC', '79845001', 'Alejandro', 'Darío', 'Morales', 'Paredes', 'alejandro.morales@lh.edu.co', '3102345601', 'Matemáticas y Cálculo', '2018-01-10', 'Activo'),
(2, 'CC', '79845002', 'Beatriz', 'Helena', 'Navarro', 'Salcedo', 'beatriz.navarro@lh.edu.co', '3112345602', 'Lengua Castellana y Literatura', '2017-02-15', 'Activo'),
(3, 'CC', '79845003', 'Carlos', 'Alberto', 'Guzmán', 'Ríos', 'carlos.guzman@lh.edu.co', '3122345603', 'Ciencias Naturales y Química', '2019-01-12', 'Activo'),
(4, 'CC', '79845004', 'Diana', 'Carolina', 'Velasco', 'Chávez', 'diana.velasco@lh.edu.co', '3132345604', 'Física Clásica y Moderna', '2016-07-20', 'Activo'),
(5, 'CC', '79845005', 'Eduardo', 'José', 'Montes', 'Beltrán', 'eduardo.montes@lh.edu.co', '3142345605', 'Ciencias Sociales e Historia', '2015-01-15', 'Activo'),
(6, 'CC', '79845006', 'Fabiola', 'Inés', 'Rincón', 'Pinto', 'fabiola.rincon@lh.edu.co', '3152345606', 'Inglés y Bilingüismo', '2020-01-14', 'Activo'),
(7, 'CC', '79845007', 'Germán', 'Alonso', 'Cadena', 'Soler', 'german.cadena@lh.edu.co', '3162345607', 'Educación Física y Deportes', '2018-03-01', 'Activo'),
(8, 'CC', '79845008', 'Helena', 'Sofía', 'Pabón', 'Nieto', 'helena.pabon@lh.edu.co', '3172345608', 'Artes Plásticas y Visuales', '2019-06-18', 'Activo'),
(9, 'CC', '79845009', 'Iván', 'Rodrigo', 'Castañeda', 'Mora', 'ivan.castaneda@lh.edu.co', '3182345609', 'Tecnología e Informática', '2017-08-22', 'Activo'),
(10, 'CC', '79845010', 'Johanna', 'Paola', 'Delgado', 'Sierra', 'johanna.delgado@lh.edu.co', '3192345610', 'Biología y Educación Ambiental', '2016-01-18', 'Activo'),
(11, 'CC', '79845011', 'Kevin', 'Andrés', 'Sarmiento', 'Duque', 'kevin.sarmiento@lh.edu.co', '3202345611', 'Filosofía y Ética', '2021-01-11', 'Activo'),
(12, 'CC', '79845012', 'Lorena', 'Marcela', 'Palacios', 'Barrios', 'lorena.palacios@lh.edu.co', '3212345612', 'Música y Expresión Sonora', '2019-02-01', 'Activo'),
(13, 'CC', '79845013', 'Mario', 'Fernando', 'Caicedo', 'Henao', 'mario.caicedo@lh.edu.co', '3222345613', 'Geometría y Estadística', '2015-09-01', 'Activo'),
(14, 'CC', '79845014', 'Nathalia', 'Andrea', 'Bohórquez', 'Parra', 'nathalia.bohorquez@lh.edu.co', '3232345614', 'Pedagogía Infantil Preescolar', '2018-01-20', 'Activo'),
(15, 'CC', '79845015', 'Orlando', 'Enrique', 'Zambrano', 'Campos', 'orlando.zambrano@lh.edu.co', '3242345615', 'Robótica y Automatización', '2020-07-15', 'Activo'),
(16, 'CC', '79845016', 'Patricia', 'Elena', 'Arango', 'Villegas', 'patricia.arango@lh.edu.co', '3252345616', 'Lengua Castellana Básica', '2017-01-16', 'Activo'),
(17, 'CC', '79845017', 'Quirino', 'Adolfo', 'Villarreal', 'Molina', 'quirino.villarreal@lh.edu.co', '3262345617', 'Química Orgánica', '2016-03-10', 'Activo'),
(18, 'CC', '79845018', 'Rosario', 'del Pilar', 'Ceballos', 'Guarín', 'rosario.ceballos@lh.edu.co', '3272345618', 'Ciencias Sociales Primaria', '2018-08-01', 'Activo'),
(19, 'CC', '79845019', 'Sergio', 'Esteban', 'Uribe', 'Echeverry', 'sergio.uribe@lh.edu.co', '3282345619', 'Inglés Avanzado TOEFL', '2019-01-25', 'Activo'),
(20, 'CC', '79845020', 'Teresa', 'Consuelo', 'Lara', 'Trujillo', 'teresa.lara@lh.edu.co', '3292345620', 'Matemáticas Primaria', '2015-02-10', 'Activo'),
(21, 'CC', '79845021', 'Ulises', 'Bernardo', 'Monroy', 'Téllez', 'ulises.monroy@lh.edu.co', '3103456721', 'Física Mecánica', '2021-02-01', 'Activo'),
(22, 'CC', '79845022', 'Viviana', 'Lucía', 'Carvajal', 'Páez', 'viviana.carvajal@lh.edu.co', '3113456722', 'Francés y Segunda Lengua', '2020-03-15', 'Activo'),
(23, 'CC', '79845023', 'Walter', 'Antonio', 'Bedoya', 'Quintero', 'walter.bedoya@lh.edu.co', '3123456723', 'Educación Física y Natación', '2017-06-01', 'Activo'),
(24, 'CC', '79845024', 'Ximena', 'Rocío', 'Gallego', 'Londoño', 'ximena.gallego@lh.edu.co', '3133456724', 'Teatro y Danza', '2019-04-10', 'Activo'),
(25, 'CC', '79845025', 'Yamil', 'Arturo', 'Salgar', 'Meza', 'yamil.salgar@lh.edu.co', '3143456725', 'Economía y Ciencias Políticas', '2016-11-01', 'Activo'),
(26, 'CC', '79845026', 'Zulma', 'Yolanda', 'Barreto', 'Novoa', 'zulma.barreto@lh.edu.co', '3153456726', 'Psicología y Orientación Escolar', '2018-09-15', 'Activo'),
(27, 'CC', '79845027', 'Armando', 'Luis', 'Peñaranda', 'Bustamante', 'armando.penaranda@lh.edu.co', '3163456727', 'Ecología y Sostenibilidad', '2021-07-20', 'Activo'),
(28, 'CC', '79845028', 'Brenda', 'Carolina', 'Flórez', 'Cardona', 'brenda.florez@lh.edu.co', '3173456728', 'Literatura Colombiana', '2022-01-17', 'Activo'),
(29, 'CC', '79845029', 'Camilo', 'Ernesto', 'Restrepo', 'Jaramillo', 'camilo.restrepo@lh.edu.co', '3183456729', 'Astronomía y Astrofísica', '2020-08-10', 'Activo'),
(30, 'CC', '79845030', 'Dora', 'Inés', 'Patiño', 'Muñoz', 'dora.patino@lh.edu.co', '3193456730', 'Comprensión Lectora y Escritura', '2017-10-05', 'Activo');

-- ----------------------------------------------------------------------------
-- 9. COURSES / MATERIAS (materias - 32 records)
-- ----------------------------------------------------------------------------
INSERT INTO materias (id_materia, codigo_materia, nombre_materia, descripcion, id_grado, horas_semanales, esta_activa) VALUES
(1, 'MAT-PRE', 'Iniciación al Pensamiento Matemático', 'Nociones espaciales y números preescolar', 1, 4, TRUE),
(2, 'LEN-PRE', 'Desarrollo del Lenguaje y Expresión', 'Vocabulario y motricidad fina preescolar', 1, 4, TRUE),
(3, 'ING-PRE', 'First English Steps', 'Inglés lúdico e interactivo para preescolar', 1, 3, TRUE),
(4, 'MAT-01', 'Matemáticas Primero', 'Operaciones básicas de suma y resta', 2, 5, TRUE),
(5, 'ESP-01', 'Español Primero', 'Lectoescritura inicial y comprensión', 2, 5, TRUE),
(6, 'ING-01', 'Inglés Básico 1', 'Introducción al vocabulario y escucha', 2, 4, TRUE),
(7, 'CN-01', 'Ciencias Naturales 1', 'Entorno vivo y cuidados corporales', 2, 3, TRUE),
(8, 'MAT-03', 'Matemáticas Tercero', 'Multiplicación, división y fracciones iniciales', 4, 5, TRUE),
(9, 'ESP-03', 'Lengua Castellana 3', 'Gramática, ortografía y redacción', 4, 5, TRUE),
(10, 'SOC-03', 'Ciencias Sociales 3', 'Geografía colombiana e historia básica', 4, 3, TRUE),
(11, 'MAT-05', 'Matemáticas Quinto', 'Fracciones, decimales y geometría básica', 6, 5, TRUE),
(12, 'ESP-05', 'Lengua Castellana 5', 'Tipologías textuales y narrativa', 6, 4, TRUE),
(13, 'CN-05', 'Ciencias Naturales 5', 'Ecosistemas, materia y energía', 6, 4, TRUE),
(14, 'ING-05', 'Inglés Primaria 5', 'Lectura guiada y conversación inicial', 6, 4, TRUE),
(15, 'MAT-06', 'Matemáticas Sexto', 'Números enteros, teoría de números y álgebra', 7, 5, TRUE),
(16, 'ESP-06', 'Lengua Castellana 6', 'Literatura universal y análisis de textos', 7, 4, TRUE),
(17, 'BIO-06', 'Biología Celular Sexto', 'Estructura celular y reinos biológicos', 7, 4, TRUE),
(18, 'SOC-06', 'Ciencias Sociales 6', 'Civilizaciones antiguas y geografía universal', 7, 4, TRUE),
(19, 'ING-06', 'Inglés Intermedio A2', 'Estructuras gramaticales intermedias', 7, 4, TRUE),
(20, 'MAT-08', 'Álgebra Octavo', 'Expresiones algebraicas y factorización', 9, 5, TRUE),
(21, 'ESP-08', 'Lengua Castellana 8', 'Literatura hispanoamericana y ensayo', 9, 4, TRUE),
(22, 'FIS-08', 'Introducción a la Física 8', 'Cinemática y leyes de movimiento', 9, 3, TRUE),
(23, 'QUI-08', 'Química General 8', 'Tabla periódica y enlaces químicos', 9, 3, TRUE),
(24, 'ING-08', 'Inglés Intermedio B1', 'Debate oral y redacción de ensayos', 9, 4, TRUE),
(25, 'MAT-10', 'Trigonometría y Geometría Analítica', 'Funciones trigonométricas y cónicas', 11, 5, TRUE),
(26, 'FIS-10', 'Física Clásica 10', 'Dinámica, trabajo, energía y fluidos', 11, 4, TRUE),
(27, 'QUI-10', 'Química Inorgánica 10', 'Estequiometría, gases y soluciones', 11, 4, TRUE),
(28, 'FIL-10', 'Filosofía Clásica y Medieval', 'Presocráticos, Platón, Aristóteles y Escolástica', 11, 3, TRUE),
(29, 'CAL-11', 'Cálculo Diferencial e Integral', 'Límites, derivadas, integrales y aplicaciones', 12, 5, TRUE),
(30, 'FIS-11', 'Física Moderna y Termodinámica', 'Electromagnetismo, óptica y física cuántica', 12, 4, TRUE),
(31, 'QUI-11', 'Química Orgánica y Bioquímica', 'Hidrocarburos, macromoléculas y metabolismo', 12, 4, TRUE),
(32, 'FIL-11', 'Filosofía Moderna y Contemporánea', 'Racionalismo, empirismo, existencialismo y ética', 12, 3, TRUE);

-- ----------------------------------------------------------------------------
-- 10. TEACHER ASSIGNMENTS (asignaciones_profesor - 32 records)
-- ----------------------------------------------------------------------------
INSERT INTO asignaciones_profesor (id_profesor, id_materia, año_academico, seccion) VALUES
(14, 1, 2024, 'A'), (14, 2, 2024, 'A'), (6, 3, 2024, 'A'), (20, 4, 2024, 'A'), (16, 5, 2024, 'A'),
(6, 6, 2024, 'A'), (10, 7, 2024, 'A'), (20, 8, 2024, 'A'), (16, 9, 2024, 'A'), (18, 10, 2024, 'A'),
(13, 11, 2024, 'A'), (2, 12, 2024, 'A'), (10, 13, 2024, 'A'), (6, 14, 2024, 'A'), (1, 15, 2024, 'A'),
(2, 16, 2024, 'A'), (10, 17, 2024, 'A'), (5, 18, 2024, 'A'), (19, 19, 2024, 'A'), (1, 20, 2024, 'A'),
(2, 21, 2024, 'A'), (4, 22, 2024, 'A'), (3, 23, 2024, 'A'), (19, 24, 2024, 'A'), (1, 25, 2024, 'A'),
(4, 26, 2024, 'A'), (3, 27, 2024, 'A'), (11, 28, 2024, 'A'), (1, 29, 2024, 'A'), (4, 30, 2024, 'A'),
(17, 31, 2024, 'A'), (11, 32, 2024, 'A');

-- ----------------------------------------------------------------------------
-- 11. COURSE ENROLLMENTS (inscripciones_materia - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO inscripciones_materia (id_estudiante, id_materia, año_academico, periodo_academico, seccion, fecha_inscripcion, estado) VALUES
(1, 15, 2024, 'I', 'A', '2024-02-01', 'Activa'), (1, 16, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(1, 17, 2024, 'I', 'A', '2024-02-01', 'Activa'), (1, 18, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(1, 19, 2024, 'I', 'A', '2024-02-01', 'Activa'), (2, 20, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(2, 21, 2024, 'I', 'A', '2024-02-01', 'Activa'), (2, 22, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(3, 20, 2024, 'I', 'A', '2024-02-01', 'Activa'), (3, 21, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(4, 11, 2024, 'I', 'A', '2024-02-01', 'Activa'), (4, 12, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(5, 25, 2024, 'I', 'A', '2024-02-01', 'Activa'), (5, 26, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(5, 27, 2024, 'I', 'A', '2024-02-01', 'Activa'), (6, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(6, 30, 2024, 'I', 'A', '2024-02-01', 'Activa'), (6, 31, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(6, 32, 2024, 'I', 'A', '2024-02-01', 'Activa'), (7, 8, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(8, 20, 2024, 'I', 'A', '2024-02-01', 'Activa'), (9, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(9, 30, 2024, 'I', 'A', '2024-02-01', 'Activa'), (10, 8, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(11, 20, 2024, 'I', 'A', '2024-02-01', 'Activa'), (12, 11, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(13, 25, 2024, 'I', 'A', '2024-02-01', 'Activa'), (14, 20, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(15, 15, 2024, 'I', 'A', '2024-02-01', 'Activa'), (16, 4, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(31, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'), (32, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(33, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'), (34, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(35, 29, 2024, 'I', 'A', '2024-02-01', 'Activa');

-- ----------------------------------------------------------------------------
-- 12. ACADEMIC RECORDS / GRADES (registros_academicos - 35 records, DECIMAL(2,1))
-- ----------------------------------------------------------------------------
INSERT INTO registros_academicos (id_registro, id_estudiante, id_materia, id_profesor, año_academico, periodo_academico, valor_nota, observaciones, fecha_registro) VALUES
(1, 1, 15, 1, 2024, 'I', 4.5, 'Excelente desempeño en operaciones con números enteros', '2024-03-25'),
(2, 1, 16, 2, 2024, 'I', 4.2, 'Buena redacción y comprensión de textos clásicos', '2024-03-25'),
(3, 1, 17, 10, 2024, 'I', 4.8, 'Destacada participación en prácticas de laboratorio', '2024-03-26'),
(4, 1, 18, 5, 2024, 'I', 3.9, 'Cumple satisfactoriamente con talleres y evaluaciones', '2024-03-26'),
(5, 1, 19, 19, 2024, 'I', 4.4, 'Fluidez oral y dominio de vocabulario', '2024-03-27'),
(6, 2, 20, 1, 2024, 'I', 4.9, 'Dominio sobresaliente de factorización algebraica', '2024-03-25'),
(7, 2, 21, 2, 2024, 'I', 4.7, 'Profundo análisis crítico literario en ensayos', '2024-03-25'),
(8, 2, 22, 4, 2024, 'I', 4.6, 'Habilidad destacada en resolución de problemas cinemáticos', '2024-03-26'),
(9, 3, 20, 1, 2024, 'I', 3.4, 'Requiere reforzar productos notables', '2024-03-25'),
(10, 3, 21, 2, 2024, 'I', 3.8, 'Buen desempeño general en lecturas asignadas', '2024-03-25'),
(11, 4, 11, 13, 2024, 'I', 4.1, 'Comprensión clara de operaciones con números decimales', '2024-03-27'),
(12, 4, 12, 2, 2024, 'I', 4.3, 'Creatividad en producción textual y ortografía', '2024-03-27'),
(13, 5, 25, 1, 2024, 'I', 4.0, 'Manejo adecuado de identidades trigonométricas', '2024-03-28'),
(14, 5, 26, 4, 2024, 'I', 3.7, 'Buen trabajo en dinámica y leyes de Newton', '2024-03-28'),
(15, 5, 27, 3, 2024, 'I', 4.2, 'Destacado en cálculos estequiométricos', '2024-03-28'),
(16, 6, 29, 1, 2024, 'I', 4.8, 'Excelente comprensión conceptual del límite y la derivada', '2024-03-29'),
(17, 6, 30, 4, 2024, 'I', 4.7, 'Sobresaliente en termodinámica y circuitos', '2024-03-29'),
(18, 6, 31, 17, 2024, 'I', 4.9, 'Dominio riguroso de nomenclatura de hidrocarburos', '2024-03-29'),
(19, 6, 32, 11, 2024, 'I', 4.6, 'Capacidad argumentativa destacada en debates éticos', '2024-03-29'),
(20, 7, 8, 20, 2024, 'I', 3.9, 'Proceso de multiplicación consolidado', '2024-03-26'),
(21, 8, 20, 1, 2024, 'I', 4.5, 'Desempeño superior en operaciones algebraicas', '2024-03-25'),
(22, 9, 29, 1, 2024, 'I', 4.2, 'Buen análisis de tasas de cambio relacionadas', '2024-03-29'),
(23, 9, 30, 4, 2024, 'I', 4.0, 'Presenta informes de laboratorio con rigurosidad', '2024-03-29'),
(24, 10, 8, 20, 2024, 'I', 4.3, 'Rápida agilidad en cálculo mental', '2024-03-26'),
(25, 11, 20, 1, 2024, 'I', 3.6, 'Necesita practicar descomposición en factores primos', '2024-03-25'),
(26, 12, 11, 13, 2024, 'I', 4.4, 'Excelente orden y resolución de problemas cotidianos', '2024-03-27'),
(27, 13, 25, 1, 2024, 'I', 3.8, 'Cumple con los talleres propuestos', '2024-03-28'),
(28, 14, 20, 1, 2024, 'I', 4.7, 'Participación activa y liderazgo en clase', '2024-03-25'),
(29, 15, 15, 1, 2024, 'I', 4.1, 'Apropiación clara de conceptos geométricos', '2024-03-25'),
(30, 16, 4, 20, 2024, 'I', 4.6, 'Gran entusiasmo y precisión en conteo y sumas', '2024-03-26'),
(31, 31, 29, 1, 2024, 'I', 3.5, 'Requiere tutoría en regla de la cadena', '2024-03-29'),
(32, 32, 29, 1, 2024, 'I', 4.4, 'Buen manejo de problemas de optimización', '2024-03-29'),
(33, 33, 29, 1, 2024, 'I', 4.1, 'Compromiso y entrega oportuna de talleres', '2024-03-29'),
(34, 34, 29, 1, 2024, 'I', 4.8, 'Alto nivel de rigor formal en demostraciones', '2024-03-29'),
(35, 35, 29, 1, 2024, 'I', 3.9, 'Buen razonamiento geométrico en derivadas', '2024-03-29');

-- ----------------------------------------------------------------------------
-- 13. ATTENDANCE / ASISTENCIAS (asistencias - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO asistencias (id_asistencia, id_estudiante, id_materia, fecha_asistencia, estado_asistencia, observaciones, registrado_por) VALUES
(1, 1, 15, '2024-03-04', 'Presente', 'Puntual y participativo', 1),
(2, 1, 16, '2024-03-04', 'Presente', 'Participó en lectura oral', 2),
(3, 1, 15, '2024-03-05', 'Tardanza', 'Llegó 10 min tarde por tráfico', 1),
(4, 2, 20, '2024-03-04', 'Presente', 'Atenta y propositiva', 1),
(5, 2, 21, '2024-03-04', 'Presente', 'Entregó trabajo a tiempo', 2),
(6, 2, 22, '2024-03-05', 'Presente', 'Excelente trabajo en equipo', 4),
(7, 3, 20, '2024-03-04', 'Ausente', 'Inasistencia sin justificar', 1),
(8, 3, 21, '2024-03-04', 'Excusa Médica', 'Incapacidad médica por virosis', 2),
(9, 4, 11, '2024-03-04', 'Presente', 'Buena disposición', 13),
(10, 4, 12, '2024-03-04', 'Presente', 'Participó activamente', 2),
(11, 5, 25, '2024-03-04', 'Presente', 'Presente en clase magistral', 1),
(12, 5, 26, '2024-03-04', 'Presente', 'Participó en laboratorio', 4),
(13, 6, 29, '2024-03-04', 'Presente', 'Líder en resolución de ejercicios', 1),
(14, 6, 30, '2024-03-04', 'Presente', 'Aportó en discusión de circuitos', 4),
(15, 6, 31, '2024-03-05', 'Presente', 'Excelente protocolo de seguridad', 17),
(16, 7, 8, '2024-03-04', 'Presente', 'Puntual', 20),
(17, 8, 20, '2024-03-04', 'Presente', 'Atenta a la explicación', 1),
(18, 9, 29, '2024-03-04', 'Presente', 'Participación destacada', 1),
(19, 10, 8, '2024-03-04', 'Presente', 'Puntual', 20),
(20, 11, 20, '2024-03-04', 'Tardanza', 'Ingresó 5 minutos después', 1),
(21, 12, 11, '2024-03-04', 'Presente', 'Presente en clase', 13),
(22, 13, 25, '2024-03-04', 'Presente', 'Presente en clase', 1),
(23, 14, 20, '2024-03-04', 'Presente', 'Excelente actitud', 1),
(24, 15, 15, '2024-03-04', 'Presente', 'Trabajo en clase completado', 1),
(25, 16, 4, '2024-03-04', 'Presente', 'Muy alegre y participativa', 20),
(26, 31, 29, '2024-03-04', 'Ausente', 'Cita médica no notificada', 1),
(27, 31, 29, '2024-03-05', 'Presente', 'Reingreso con compromiso de adelanto', 1),
(28, 32, 29, '2024-03-04', 'Presente', 'Puntual', 1),
(29, 33, 29, '2024-03-04', 'Presente', 'Puntual', 1),
(30, 34, 29, '2024-03-04', 'Presente', 'Puntual', 1),
(31, 35, 29, '2024-03-04', 'Presente', 'Puntual', 1),
(32, 1, 15, '2024-03-06', 'Presente', 'Puntual', 1),
(33, 2, 20, '2024-03-06', 'Presente', 'Puntual', 1),
(34, 3, 20, '2024-03-06', 'Presente', 'Reincorporado a clases', 1),
(35, 6, 29, '2024-03-06', 'Presente', 'Resolvió dudas de compañeros', 1);

-- ----------------------------------------------------------------------------
-- 14. EXTRACURRICULAR CLUBS (clubes - 30 records)
-- ----------------------------------------------------------------------------
INSERT INTO clubes (id_club, nombre_club, descripcion, id_profesor_supervisor, capacidad_maxima, horario_reunion, esta_activo) VALUES
(1, 'Club de Robótica y Programación', 'Diseño, ensamble de robots con Arduino y código Python', 15, 25, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(2, 'Club de Ciencias y Bioquímica', 'Experimentos de laboratorio, microscopía y biología aplicada', 10, 20, 'Miércoles 3:30 - 5:00 PM', TRUE),
(3, 'Club de Astronomía y Cosmología', 'Observación telescópica nocturna y divulgación aeroespacial', 29, 30, 'Viernes 4:00 - 6:00 PM', TRUE),
(4, 'Club de Debate y Oratoria', 'Técnicas de argumentación, persuasión y Modelo ONU', 11, 25, 'Lunes 3:30 - 5:00 PM', TRUE),
(5, 'Club de Artes Plásticas y Óleo', 'Pintura al óleo, acuarela, escultura y técnicas mixtas', 8, 20, 'Martes 3:30 - 5:00 PM', TRUE),
(6, 'Club de Fútbol Sala Infantil', 'Entrenamiento técnico, táctico y valores deportivos', 7, 30, 'Lunes y Miércoles 3:30 - 5:00 PM', TRUE),
(7, 'Club de Baloncesto Femenino y Masculino', 'Fundamentos de dribling, lanzamiento y juego en equipo', 23, 25, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(8, 'Club de Ajedrez Táctico', 'Estrategia, aperturas, finales y torneos suizos', 1, 25, 'Viernes 3:30 - 5:00 PM', TRUE),
(9, 'Club de Música y Banda Sinfónica', 'Interpretación instrumental, vientos, percusión y armonía', 12, 35, 'Miércoles y Viernes 3:30 - 5:30 PM', TRUE),
(10, 'Club de Teatro y Expresión Corporal', 'Improvisación dramática, montaje de obras y puesta en escena', 24, 25, 'Jueves 3:30 - 5:30 PM', TRUE),
(11, 'Club de Danza Folclórica y Contemporánea', 'Ritmos colombianos, danza moderna y expresión corporal', 24, 25, 'Martes 3:30 - 5:00 PM', TRUE),
(12, 'Club de Literatura y Creación Poética', 'Taller de escritura de cuentos, crónicas y poemas', 28, 20, 'Lunes 3:30 - 5:00 PM', TRUE),
(13, 'Club de Francés y Cultura Gala', 'Conversación cotidiana, cinefrancés y gastronomía básica', 22, 20, 'Miércoles 3:30 - 5:00 PM', TRUE),
(14, 'Club de Periodismo Escolar y Radio', 'Redacción de periódico mural, podcast y entrevistas', 2, 20, 'Jueves 3:30 - 5:00 PM', TRUE),
(15, 'Club de Ecología y Huerto Sostenible', 'Siembra de hortalizas orgánicas, compostaje y reciclaje', 27, 25, 'Viernes 3:00 - 4:30 PM', TRUE),
(16, 'Club de Matemáticas Olímpicas', 'Resolución de problemas de ingenio y olimpiadas nacionales', 1, 20, 'Lunes y Miércoles 3:30 - 5:00 PM', TRUE),
(17, 'Club de Natación y Estilos', 'Técnica de crol, espalda, mariposa y resistencia acuática', 23, 25, 'Lunes y Jueves 3:30 - 5:00 PM', TRUE),
(18, 'Club de Fotografía y Edición Digital', 'Composición visual, iluminación y retoque en Lightroom', 8, 20, 'Viernes 3:30 - 5:00 PM', TRUE),
(19, 'Club de Emprendimiento e Innovación', 'Generación de modelos de negocio, finanzas y pitch comercial', 25, 25, 'Martes 3:30 - 5:00 PM', TRUE),
(20, 'Club de Primeros Auxilios y Brigada', 'Atención básica de emergencias, RCP y prevención', 26, 25, 'Jueves 3:30 - 5:00 PM', TRUE),
(21, 'Club de Cine y Crítica Cinematográfica', 'Análisis de directores clásicos, guión y lenguaje fílmico', 11, 25, 'Viernes 4:00 - 6:00 PM', TRUE),
(22, 'Club de Gimnasia Rítmica', 'Flexibilidad, coordinación con cinta y pelota', 7, 20, 'Miércoles 3:30 - 5:00 PM', TRUE),
(23, 'Club de Voleibol Mixto', 'Recepción, saque flotante, bloqueo y remate', 23, 25, 'Martes y Viernes 3:30 - 5:00 PM', TRUE),
(24, 'Club de Diseño Gráfico e Ilustración', 'Herramientas digitales vectoriales y diseño editorial', 9, 20, 'Jueves 3:30 - 5:00 PM', TRUE),
(25, 'Club de Tenis de Mesa', 'Agilidad de reflejos, efectos de raqueta y competición', 7, 16, 'Lunes 3:30 - 5:00 PM', TRUE),
(26, 'Club de Modelado 3D e Impresión', 'Modelado en Blender y fabricación aditiva con filamento', 15, 18, 'Miércoles 3:30 - 5:00 PM', TRUE),
(27, 'Club de Filosofía en Diálogo', 'Café filosófico y debates éticos de la tecnología', 11, 20, 'Jueves 4:00 - 5:30 PM', TRUE),
(28, 'Club de Canto y Ensamble Coral', 'Técnica vocal, afinación y polifonía a cuatro voces', 12, 30, 'Lunes y Jueves 3:30 - 5:00 PM', TRUE),
(29, 'Club de Yoga y Mindfulness', 'Posturas, respiración consciente y reducción del estrés escolar', 26, 20, 'Martes 3:30 - 4:45 PM', TRUE),
(30, 'Club de Gastronomía y Nutrición Saludable', 'Cocina saludable, balance de macronutrientes y hábitos', 10, 20, 'Viernes 3:30 - 5:30 PM', TRUE);

-- ----------------------------------------------------------------------------
-- 15. CLUB MEMBERSHIPS (membresias_club - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO membresias_club (id_membresia, id_estudiante, id_club, fecha_ingreso, estado_membresia) VALUES
(1, 1, 1, '2024-02-05', 'Activa'), (2, 1, 8, '2024-02-05', 'Activa'), (3, 2, 2, '2024-02-05', 'Activa'),
(4, 2, 4, '2024-02-05', 'Activa'), (5, 3, 6, '2024-02-06', 'Activa'), (6, 4, 5, '2024-02-06', 'Activa'),
(7, 5, 1, '2024-02-06', 'Activa'), (8, 5, 3, '2024-02-06', 'Activa'), (9, 6, 4, '2024-02-07', 'Activa'),
(10, 6, 16, '2024-02-07', 'Activa'), (11, 7, 6, '2024-02-07', 'Activa'), (12, 8, 9, '2024-02-08', 'Activa'),
(13, 8, 10, '2024-02-08', 'Activa'), (14, 9, 3, '2024-02-08', 'Activa'), (15, 9, 16, '2024-02-08', 'Activa'),
(16, 10, 11, '2024-02-09', 'Activa'), (17, 11, 7, '2024-02-09', 'Activa'), (18, 12, 5, '2024-02-09', 'Activa'),
(19, 13, 1, '2024-02-10', 'Activa'), (20, 14, 12, '2024-02-10', 'Activa'), (21, 15, 8, '2024-02-10', 'Activa'),
(22, 17, 6, '2024-02-11', 'Activa'), (23, 18, 15, '2024-02-11', 'Activa'), (24, 20, 7, '2024-02-12', 'Activa'),
(25, 23, 9, '2024-02-12', 'Activa'), (26, 24, 1, '2024-02-13', 'Activa'), (27, 28, 8, '2024-02-13', 'Activa'),
(28, 29, 5, '2024-02-14', 'Activa'), (29, 31, 19, '2024-02-14', 'Activa'), (30, 32, 4, '2024-02-15', 'Activa'),
(31, 33, 6, '2024-02-15', 'Activa'), (32, 34, 16, '2024-02-16', 'Activa'), (33, 35, 1, '2024-02-16', 'Activa'),
(34, 2, 13, '2024-02-17', 'Activa'), (35, 6, 21, '2024-02-17', 'Activa');

-- ----------------------------------------------------------------------------
-- 16. SCHOOL EVENTS (eventos - 12 records)
-- ----------------------------------------------------------------------------
INSERT INTO eventos (id_evento, nombre_evento, descripcion, tipo_evento, fecha_evento, lugar, tiene_competencia, id_organizador, esta_activo) VALUES
(1, 'Feria Escolar de la Ciencia y Tecnología 2024', 'Exposición de proyectos innovadores de ciencias aplicadas y robótica', 'Científico', '2024-05-15', 'Coliseo Principal del Colegio', TRUE, 3, TRUE),
(2, 'Olimpiadas Matemáticas Little Horizons', 'Competencia de agilidad matemática y resolución de acertijos', 'Académico', '2024-04-20', 'Auditorio Central', TRUE, 1, TRUE),
(3, 'Día de la Colombianidad y Folclor', 'Muestras culturales de las diferentes regiones del país', 'Cultural', '2024-07-19', 'Plaza Cívica Escolar', FALSE, 24, TRUE),
(4, 'Torneo Intercolegiado de Fútbol Sala', 'Competencia deportiva con colegios invitados de la zona norte', 'Deportivo', '2024-08-10', 'Canchas Múltiples', TRUE, 7, TRUE),
(5, 'Spelling Bee Contest & Debate Cup', 'Concurso bilingüe de deletreo en inglés y debate formal', 'Académico', '2024-09-05', 'Auditorio de Bilingüismo', TRUE, 6, TRUE),
(6, 'Festival de Teatro Escolar y Monólogos', 'Puesta en escena de obras literarias clásicas y modernas', 'Cultural', '2024-09-22', 'Teatro Principal', TRUE, 10, TRUE),
(7, 'Feria de Emprendimiento Juvenil', 'Exposición de planes de negocio e iniciativas empresariales de estudiantes', 'Social', '2024-10-12', 'Plazoleta de Eventos', TRUE, 25, TRUE),
(8, 'Encuentro Coral y Concierto de Primavera', 'Recital de la banda sinfónica y coros escolares', 'Cultural', '2024-10-25', 'Capilla y Auditorio', FALSE, 12, TRUE),
(9, 'Noche de las Estrellas y Campamento Astronómico', 'Jornada de observación con telescopios y charlas cósmicas', 'Científico', '2024-11-08', 'Campo Deportivo y Terraza', FALSE, 29, TRUE),
(10, 'Jornada Ecológica y Siembra de Árboles', 'Actividad ambiental comunitaria en reserva forestal', 'Social', '2024-03-22', 'Parque Ecológico Distrital', FALSE, 27, TRUE),
(11, 'Torneo Relámpago de Ajedrez Suizo', 'Siete rondas de juego rápido a 10 minutos', 'Deportivo', '2024-06-08', 'Biblioteca Escolar', TRUE, 1, TRUE),
(12, 'Ceremonia de Graduación Promoción 2024', 'Solemne entrega de diplomas de bachiller académico', 'Social', '2024-11-29', 'Centro de Convenciones', FALSE, 11, TRUE);

-- ----------------------------------------------------------------------------
-- 17. EVENT PARTICIPANTS (participantes_evento - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO participantes_evento (id_participacion, id_evento, id_estudiante, rol_participacion, posicion_obtenida, observaciones) VALUES
(1, 1, 1, 'Competidor', '1er Lugar', 'Robot clasificador de residuos con visión artificial'),
(2, 1, 5, 'Competidor', '2do Lugar', 'Brazo robótico impreso en 3D para prótesis'),
(3, 1, 13, 'Competidor', '3er Lugar', 'Sistema de riego automatizado con sensores'),
(4, 1, 2, 'Competidor', 'Participante', 'Filtro purificador de agua con biocarbón'),
(5, 1, 6, 'Competidor', 'Participante', 'Bioplástico a base de almidón de yuca'),
(6, 2, 6, 'Competidor', '1er Lugar', 'Puntaje perfecto en fase final de cálculo'),
(7, 2, 2, 'Competidor', '2do Lugar', 'Excelente desempeño en trigonometría'),
(8, 2, 9, 'Competidor', '3er Lugar', 'Agilidad en resolución de ecuaciones'),
(9, 2, 34, 'Competidor', 'Participante', 'Mención de honor en geometría'),
(10, 2, 1, 'Competidor', 'Participante', 'Destacada participación en nivel medio'),
(11, 4, 3, 'Competidor', '1er Lugar', 'Capitán del equipo campeón de fútbol sala'),
(12, 4, 7, 'Competidor', '1er Lugar', 'Goleador del torneo intercolegiado'),
(13, 4, 17, 'Competidor', '2do Lugar', 'Guardameta con mejor promedio'),
(14, 4, 33, 'Competidor', 'Participante', 'Jugador destacado en mediocampo'),
(15, 5, 2, 'Competidor', '1er Lugar', 'Deletreo sin fallas en ronda de desempate'),
(16, 5, 6, 'Competidor', '2do Lugar', 'Argumentación elocuente en debate final'),
(17, 5, 32, 'Competidor', 'Participante', 'Excelente dicción y vocabulario'),
(18, 11, 1, 'Competidor', '1er Lugar', 'Campeón invicto con 7 puntos de 7'),
(19, 11, 15, 'Competidor', '2do Lugar', 'Subcampeón con 5.5 puntos'),
(20, 11, 28, 'Competidor', '3er Lugar', 'Gran solidez defensiva'),
(21, 3, 4, 'Participante', 'Participante', 'Baile del bambuco con traje típico'),
(22, 3, 10, 'Participante', 'Participante', 'Canto tradicional de la región andina'),
(23, 3, 16, 'Participante', 'Participante', 'Desfile infantil de comparsas'),
(24, 6, 8, 'Competidor', '1er Lugar', 'Monólogo dramático de Shakespeare'),
(25, 6, 14, 'Competidor', '2do Lugar', 'Puesta en escena de La Vorágine'),
(26, 7, 31, 'Competidor', '1er Lugar', 'Mejor modelo de negocio sustentable'),
(27, 7, 35, 'Competidor', '2do Lugar', 'Aplicación móvil de apoyo estudiantil'),
(28, 8, 8, 'Participante', 'Participante', 'Solista de violín en ensamble'),
(29, 8, 23, 'Participante', 'Participante', 'Intérprete de flauta traversa'),
(30, 9, 5, 'Participante', 'Participante', 'Operador de telescopio refractor'),
(31, 9, 9, 'Participante', 'Participante', 'Expositor sobre nebulosas y galaxias'),
(32, 10, 18, 'Participante', 'Participante', 'Siembra de 15 especies nativas'),
(33, 12, 6, 'Participante', 'Participante', 'Graduanda con honores y mejor ICFES'),
(34, 12, 9, 'Participante', 'Participante', 'Graduando con mención deportiva'),
(35, 12, 31, 'Participante', 'Participante', 'Graduando con mención en liderazgo');

-- ----------------------------------------------------------------------------
-- 18. PAYMENTS / PAGOS (pagos - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO pagos (id_pago, id_estudiante, id_acudiente, id_concepto, monto_pagado, fecha_pago, metodo_pago, numero_recibo, año_academico, periodo_academico, notas) VALUES
(1, 1, 1, 1, 650000.00, '2024-01-15', 'Transferencia', 'REC-2024-0001', 2024, 'Anual', 'Matrícula año lectivo 2024 cancelada'),
(2, 1, 1, 2, 380000.00, '2024-02-05', 'Transferencia', 'REC-2024-0002', 2024, 'I', 'Pensión Febrero 2024 cancelada'),
(3, 1, 1, 2, 380000.00, '2024-03-05', 'Transferencia', 'REC-2024-0003', 2024, 'I', 'Pensión Marzo 2024 cancelada'),
(4, 2, 3, 1, 650000.00, '2024-01-15', 'Tarjeta Débito', 'REC-2024-0004', 2024, 'Anual', 'Matrícula grado octavo cancelada'),
(5, 2, 3, 2, 380000.00, '2024-02-04', 'Tarjeta Débito', 'REC-2024-0005', 2024, 'I', 'Pensión Febrero 2024'),
(6, 2, 3, 2, 380000.00, '2024-03-04', 'Tarjeta Débito', 'REC-2024-0006', 2024, 'I', 'Pensión Marzo 2024'),
(7, 3, 5, 1, 650000.00, '2024-01-16', 'Efectivo', 'REC-2024-0007', 2024, 'Anual', 'Matrícula grado noveno cancelada'),
(8, 3, 5, 2, 380000.00, '2024-02-06', 'Efectivo', 'REC-2024-0008', 2024, 'I', 'Pensión Febrero 2024'),
(9, 4, 7, 1, 650000.00, '2024-01-16', 'Tarjeta Crédito', 'REC-2024-0009', 2024, 'Anual', 'Matrícula grado quinto cancelada'),
(10, 4, 7, 2, 380000.00, '2024-02-05', 'Tarjeta Crédito', 'REC-2024-0010', 2024, 'I', 'Pensión Febrero 2024'),
(11, 4, 7, 4, 170000.00, '2024-02-05', 'Tarjeta Crédito', 'REC-2024-0011', 2024, 'I', 'Transporte escolar Febrero 2024'),
(12, 5, 9, 1, 650000.00, '2024-01-17', 'Transferencia', 'REC-2024-0012', 2024, 'Anual', 'Matrícula grado décimo'),
(13, 5, 9, 2, 380000.00, '2024-02-07', 'Transferencia', 'REC-2024-0013', 2024, 'I', 'Pensión Febrero 2024'),
(14, 6, 11, 1, 650000.00, '2024-01-17', 'Transferencia', 'REC-2024-0014', 2024, 'Anual', 'Matrícula grado once'),
(15, 6, 11, 2, 380000.00, '2024-02-05', 'Transferencia', 'REC-2024-0015', 2024, 'I', 'Pensión Febrero 2024'),
(16, 7, 13, 1, 650000.00, '2024-01-18', 'Efectivo', 'REC-2024-0016', 2024, 'Anual', 'Matrícula grado cuarto'),
(17, 7, 13, 2, 380000.00, '2024-02-08', 'Efectivo', 'REC-2024-0017', 2024, 'I', 'Pensión Febrero 2024'),
(18, 8, 15, 1, 650000.00, '2024-01-18', 'Tarjeta Débito', 'REC-2024-0018', 2024, 'Anual', 'Matrícula grado décimo'),
(19, 8, 15, 2, 380000.00, '2024-02-06', 'Tarjeta Débito', 'REC-2024-0019', 2024, 'I', 'Pensión Febrero 2024'),
(20, 9, 17, 1, 650000.00, '2024-01-19', 'Transferencia', 'REC-2024-0020', 2024, 'Anual', 'Matrícula grado once'),
(21, 9, 17, 2, 380000.00, '2024-02-05', 'Transferencia', 'REC-2024-0021', 2024, 'I', 'Pensión Febrero 2024'),
(22, 10, 19, 1, 650000.00, '2024-01-19', 'Efectivo', 'REC-2024-0022', 2024, 'Anual', 'Matrícula grado tercero'),
(23, 11, 21, 1, 650000.00, '2024-01-20', 'Transferencia', 'REC-2024-0023', 2024, 'Anual', 'Matrícula grado séptimo'),
(24, 12, 23, 1, 650000.00, '2024-01-20', 'Tarjeta Débito', 'REC-2024-0024', 2024, 'Anual', 'Matrícula grado quinto'),
(25, 13, 25, 1, 650000.00, '2024-01-21', 'Transferencia', 'REC-2024-0025', 2024, 'Anual', 'Matrícula grado décimo'),
(26, 14, 27, 1, 650000.00, '2024-01-21', 'Tarjeta Crédito', 'REC-2024-0026', 2024, 'Anual', 'Matrícula grado octavo'),
(27, 15, 29, 1, 650000.00, '2024-01-22', 'Transferencia', 'REC-2024-0027', 2024, 'Anual', 'Matrícula grado sexto'),
(28, 16, 1, 1, 585000.00, '2024-01-22', 'Transferencia', 'REC-2024-0028', 2024, 'Anual', 'Matrícula grado primero con 10% descuento por hermano'),
(29, 17, 3, 1, 585000.00, '2024-01-23', 'Tarjeta Débito', 'REC-2024-0029', 2024, 'Anual', 'Matrícula grado tercero con 10% descuento por hermano'),
(30, 18, 5, 1, 585000.00, '2024-01-23', 'Efectivo', 'REC-2024-0030', 2024, 'Anual', 'Matrícula grado cuarto con 10% descuento por hermano'),
(31, 31, 10, 1, 650000.00, '2024-01-30', 'Transferencia', 'REC-2024-0031', 2024, 'Anual', 'Matrícula grado once'),
(32, 32, 11, 1, 650000.00, '2024-01-30', 'Transferencia', 'REC-2024-0032', 2024, 'Anual', 'Matrícula grado once'),
(33, 33, 13, 1, 650000.00, '2024-01-31', 'Efectivo', 'REC-2024-0033', 2024, 'Anual', 'Matrícula grado once'),
(34, 34, 14, 1, 650000.00, '2024-01-31', 'Tarjeta Débito', 'REC-2024-0034', 2024, 'Anual', 'Matrícula grado once'),
(35, 35, 17, 1, 650000.00, '2024-01-31', 'Transferencia', 'REC-2024-0035', 2024, 'Anual', 'Matrícula grado once');

-- ----------------------------------------------------------------------------
-- 19. ELECTRONIC INVOICES / FACTURAS (facturas - 30 records)
-- ----------------------------------------------------------------------------
INSERT INTO facturas (id_factura, numero_factura, id_pago, fecha_emision, cufe, nombre_archivo_pdf, ruta_archivo) VALUES
(1, 'FE-LH-000001', 1, '2024-01-15', 'a1b2c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_000001.pdf', 'invoices/factura_000001.pdf'),
(2, 'FE-LH-000002', 2, '2024-02-05', 'b2c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef1', 'factura_000002.pdf', 'invoices/factura_000002.pdf'),
(3, 'FE-LH-000003', 3, '2024-03-05', 'c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef2', 'factura_000003.pdf', 'invoices/factura_000003.pdf'),
(4, 'FE-LH-000004', 4, '2024-01-15', 'd4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef3', 'factura_000004.pdf', 'invoices/factura_000004.pdf'),
(5, 'FE-LH-000005', 5, '2024-02-04', 'e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef4', 'factura_000005.pdf', 'invoices/factura_000005.pdf'),
(6, 'FE-LH-000006', 6, '2024-03-04', 'f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef5', 'factura_000006.pdf', 'invoices/factura_000006.pdf'),
(7, 'FE-LH-000007', 7, '2024-01-16', '0718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef6', 'factura_000007.pdf', 'invoices/factura_000007.pdf'),
(8, 'FE-LH-000008', 8, '2024-02-06', '18293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef7', 'factura_000008.pdf', 'invoices/factura_000008.pdf'),
(9, 'FE-LH-000009', 9, '2024-01-16', '293a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef8', 'factura_000009.pdf', 'invoices/factura_000009.pdf'),
(10, 'FE-LH-000010', 10, '2024-02-05', '3a4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef9', 'factura_000010.pdf', 'invoices/factura_000010.pdf'),
(11, 'FE-LH-000011', 11, '2024-02-05', '4b5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdefa', 'factura_000011.pdf', 'invoices/factura_000011.pdf'),
(12, 'FE-LH-000012', 12, '2024-01-17', '5c6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdefb', 'factura_000012.pdf', 'invoices/factura_000012.pdf'),
(13, 'FE-LH-000013', 13, '2024-02-07', '6d7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdefc', 'factura_000013.pdf', 'invoices/factura_000013.pdf'),
(14, 'FE-LH-000014', 14, '2024-01-17', '7e8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdefd', 'factura_000014.pdf', 'invoices/factura_000014.pdf'),
(15, 'FE-LH-000015', 15, '2024-02-05', '8f90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdefe', 'factura_000015.pdf', 'invoices/factura_000015.pdf'),
(16, 'FE-LH-000016', 16, '2024-01-18', '90123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdeff', 'factura_000016.pdf', 'invoices/factura_000016.pdf'),
(17, 'FE-LH-000017', 17, '2024-02-08', '0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef00', 'factura_000017.pdf', 'invoices/factura_000017.pdf'),
(18, 'FE-LH-000018', 18, '2024-01-18', '123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef01', 'factura_000018.pdf', 'invoices/factura_000018.pdf'),
(19, 'FE-LH-000019', 19, '2024-02-06', '23456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef02', 'factura_000019.pdf', 'invoices/factura_000019.pdf'),
(20, 'FE-LH-000020', 20, '2024-01-19', '3456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef03', 'factura_000020.pdf', 'invoices/factura_000020.pdf'),
(21, 'FE-LH-000021', 21, '2024-02-05', '456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef04', 'factura_000021.pdf', 'invoices/factura_000021.pdf'),
(22, 'FE-LH-000022', 22, '2024-01-19', '56789abcdef0123456789abcdef0123456789abcdef0123456789abcdef05', 'factura_000022.pdf', 'invoices/factura_000022.pdf'),
(23, 'FE-LH-000023', 23, '2024-01-20', '6789abcdef0123456789abcdef0123456789abcdef0123456789abcdef06', 'factura_000023.pdf', 'invoices/factura_000023.pdf'),
(24, 'FE-LH-000024', 24, '2024-01-20', '789abcdef0123456789abcdef0123456789abcdef0123456789abcdef07', 'factura_000024.pdf', 'invoices/factura_000024.pdf'),
(25, 'FE-LH-000025', 25, '2024-01-21', '89abcdef0123456789abcdef0123456789abcdef0123456789abcdef08', 'factura_000025.pdf', 'invoices/factura_000025.pdf'),
(26, 'FE-LH-000026', 26, '2024-01-21', '9abcdef0123456789abcdef0123456789abcdef0123456789abcdef09', 'factura_000026.pdf', 'invoices/factura_000026.pdf'),
(27, 'FE-LH-000027', 27, '2024-01-22', 'abcdef0123456789abcdef0123456789abcdef0123456789abcdef0a', 'factura_000027.pdf', 'invoices/factura_000027.pdf'),
(28, 'FE-LH-000028', 28, '2024-01-22', 'bcdef0123456789abcdef0123456789abcdef0123456789abcdef0b', 'factura_000028.pdf', 'invoices/factura_000028.pdf'),
(29, 'FE-LH-000029', 29, '2024-01-23', 'cdef0123456789abcdef0123456789abcdef0123456789abcdef0c', 'factura_000029.pdf', 'invoices/factura_000029.pdf'),
(30, 'FE-LH-000030', 30, '2024-01-23', 'def0123456789abcdef0123456789abcdef0123456789abcdef0d', 'factura_000030.pdf', 'invoices/factura_000030.pdf');

-- ----------------------------------------------------------------------------
-- 20. SCHOOL RESOURCES (recursos - 35 records)
-- ----------------------------------------------------------------------------
INSERT INTO recursos (id_recurso, codigo_recurso, nombre_recurso, id_categoria, descripcion, fecha_adquisicion, ubicacion_fisica, estado_recurso) VALUES
(1, 'LIB-001', 'Don Quijote de la Mancha - Edición RAE', 1, 'Obra cumbre de Miguel de Cervantes Saavedra', '2021-02-10', 'Biblioteca - Estante A1', 'Disponible'),
(2, 'LIB-002', 'Cien Años de Soledad - Gabriel García Márquez', 1, 'Novela representativa del realismo mágico', '2020-05-15', 'Biblioteca - Estante A2', 'Prestado'),
(3, 'LIB-003', 'Cálculo de una Variable - James Stewart 8va Ed.', 1, 'Texto guía universitario de cálculo diferencial e integral', '2022-01-20', 'Biblioteca - Estante B1', 'Disponible'),
(4, 'LIB-004', 'Física Universitaria Vol. 1 - Sears & Zemansky', 1, 'Fundamentos de mecánica y ondas mecánicas', '2021-08-14', 'Biblioteca - Estante B2', 'Disponible'),
(5, 'LIB-005', 'Química General - Raymond Chang 12va Ed.', 1, 'Texto de referencia para química media', '2021-09-10', 'Biblioteca - Estante B3', 'Prestado'),
(6, 'LIB-006', 'Biología Celular y Molecular - Karp 7ma Ed.', 1, 'Estructura microscópica y fisiología celular', '2022-03-05', 'Biblioteca - Estante B4', 'Disponible'),
(7, 'LIB-007', 'Historia de Colombia y sus Crisis - Bushnell', 1, 'Análisis histórico de los siglos XIX y XX', '2020-11-18', 'Biblioteca - Estante C1', 'Disponible'),
(8, 'LIB-008', 'Álgebra de Baldor - Nueva Edición Ilustrada', 1, 'Texto clásico de ejercicios algebraicos', '2019-01-15', 'Biblioteca - Estante B1', 'Disponible'),
(9, 'LIB-009', 'Diccionario Oxford Advanced Learner''s', 1, 'Diccionario especializado de inglés británico', '2021-04-12', 'Biblioteca - Estante D1', 'Disponible'),
(10, 'LIB-010', 'El Mundo de Sofía - Jostein Gaarder', 1, 'Novela introductoria a la historia de la filosofía', '2020-07-22', 'Biblioteca - Estante C2', 'Prestado'),
(11, 'LAB-001', 'Microscopio Óptico Binocular Olympus CX23', 2, 'Aumentos de 40x a 1000x con iluminación LED', '2022-06-15', 'Laboratorio de Biología - Gaveta 1', 'Disponible'),
(12, 'LAB-002', 'Balanza Analítica Digital Ohaus Explorer 0.1mg', 2, 'Pesaje de alta precisión para reactivos químicos', '2021-10-10', 'Laboratorio de Química - Mesa 3', 'Disponible'),
(13, 'LAB-003', 'Centrífuga de Mesa Eppendorf 5418', 2, 'Centrífuga clínica para separación de muestras', '2022-02-18', 'Laboratorio de Biología - Gaveta 2', 'Prestado'),
(14, 'LAB-004', 'Sensor de Fuerza y Movimiento Vernier GoDirect', 2, 'Sensor Bluetooth para cinemática experimental', '2023-01-25', 'Laboratorio de Física - Armario 1', 'Disponible'),
(15, 'LAB-005', 'Kit de Óptica Geométrica y Prismas Pasco', 2, 'Banco óptico, láseres de diodo y lentes acromáticas', '2021-05-30', 'Laboratorio de Física - Armario 2', 'Disponible'),
(16, 'LAB-006', 'pHmetro Digital de Mesa Hanna Instruments', 2, 'Medidor electroquímico de acidez con electrodo de vidrio', '2022-08-11', 'Laboratorio de Química - Mesa 1', 'Disponible'),
(17, 'LAB-007', 'Termociclador para PCR MiniAmp Applied Biosystems', 2, 'Amplificación de ADN para proyectos científicos', '2023-04-10', 'Laboratorio de Biología - Gaveta 3', 'Disponible'),
(18, 'LAB-008', 'Espectrofotómetro Visible Genesys 30', 2, 'Medición de absorbancia en rango de 325 a 1100 nm', '2022-09-01', 'Laboratorio de Química - Mesa 2', 'En Mantenimiento'),
(19, 'TEC-001', 'Apple iPad 9th Gen 64GB - LH01', 4, 'Tablet institucional para apoyo interactivo en aula', '2023-01-15', 'Sala de Innovación - Carro 1', 'Disponible'),
(20, 'TEC-002', 'Apple iPad 9th Gen 64GB - LH02', 4, 'Tablet institucional para apoyo interactivo en aula', '2023-01-15', 'Sala de Innovación - Carro 1', 'Prestado'),
(21, 'TEC-003', 'Laptop Dell Latitude 3420 Intel Core i5', 4, 'Portátil para uso docente y proyectos de investigación', '2022-07-20', 'Sistemas - Armario A', 'Disponible'),
(22, 'TEC-004', 'Laptop Dell Latitude 3420 Intel Core i5', 4, 'Portátil para uso docente y proyectos de investigación', '2022-07-20', 'Sistemas - Armario A', 'Prestado'),
(23, 'TEC-005', 'Calculadora Gráfica Texas Instruments TI-Nspire CX', 4, 'Calculadora avanzada con visualización de funciones 3D', '2022-03-10', 'Biblioteca - Casillero Tech', 'Disponible'),
(24, 'TEC-006', 'Calculadora Científica Casio FX-991LAX ClassWiz', 4, 'Calculadora no programable para exámenes estándar', '2021-02-15', 'Biblioteca - Casillero Tech', 'Disponible'),
(25, 'TEC-007', 'Proyector Láser Epson PowerLite L200W', 4, 'Video proyector portátil 4200 lúmenes WXGA', '2022-11-05', 'Coordinación Académica', 'Disponible'),
(26, 'DEP-001', 'Balón de Fútbol N° 5 Golty Origen Profesional', 3, 'Balón oficial de cuero termosellado para césped', '2023-02-10', 'Bodega de Deportes - Malla 1', 'Disponible'),
(27, 'DEP-002', 'Balón de Baloncesto N° 7 Molten GG7X', 3, 'Balón reglamentario de cuero compuesto', '2023-02-10', 'Bodega de Deportes - Malla 2', 'Disponible'),
(28, 'DEP-003', 'Balón de Voleibol Mikasa V200W', 3, 'Balón oficial de competencia FIVB', '2023-02-10', 'Bodega de Deportes - Malla 3', 'Prestado'),
(29, 'DEP-004', 'Set de 20 Conos de Entrenamiento y Agilidad', 3, 'Conos flexibles con ranuras para barras de salto', '2022-08-20', 'Bodega de Deportes - Estante 1', 'Disponible'),
(30, 'DEP-005', 'Cronómetro Digital Casio HS-80TW con memoria', 3, 'Cronómetro deportivo para toma de tiempos en atletismo', '2022-04-15', 'Bodega de Deportes - Gaveta 1', 'Disponible'),
(31, 'DID-001', 'Esqueleto Humano Articulado Escala Real 170cm', 5, 'Modelo anatómico con marcas musculares pintadas', '2021-09-18', 'Laboratorio de Biología - Rincón Anatómico', 'Disponible'),
(32, 'DID-002', 'Torso Humano Unisex con Órganos Desmontables', 5, 'Modelo de 27 piezas anatómicas numeradas', '2021-09-18', 'Laboratorio de Biología - Rincón Anatómico', 'Disponible'),
(33, 'DID-003', 'Globo Terráqueo Físico y Político Iluminado 32cm', 5, 'Globo con relieve topográfico y cartografía actualizada', '2020-03-12', 'Biblioteca - Sala Infantil', 'Disponible'),
(34, 'DID-004', 'Kit de Robótica Educativa LEGO SPIKE Prime', 5, 'Set de construcción con motores, sensores y software', '2023-03-20', 'Sala de Innovación - Gaveta LEGO', 'Disponible'),
(35, 'DID-005', 'Kit de Modelos Moleculares para Química Orgánica', 5, 'Esferas plásticas y enlaces covalentes de carbono', '2022-05-14', 'Laboratorio de Química - Armario Didáctico', 'Disponible');

-- ----------------------------------------------------------------------------
-- 21. STUDENT LOANS (prestamos_estudiante - 15 records)
-- ----------------------------------------------------------------------------
INSERT INTO prestamos_estudiante (id_prestamo, id_recurso, id_estudiante, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado_prestamo, observaciones) VALUES
(1, 2, 1, '2024-03-01', '2024-03-15', '2024-03-14', 'Devuelto', 'Libro devuelto en perfecto estado'),
(2, 5, 2, '2024-03-05', '2024-03-19', '2024-03-18', 'Devuelto', 'Sin anotaciones ni tachaduras'),
(3, 10, 6, '2024-03-10', '2024-03-24', NULL, 'Activo', 'Préstamo vigente para lectura de filosofía'),
(4, 20, 5, '2024-03-12', '2024-03-13', '2024-03-13', 'Devuelto', 'iPad entregado con batería completa'),
(5, 28, 3, '2024-03-08', '2024-03-08', '2024-03-08', 'Devuelto', 'Balón usado en recreo deportivo'),
(6, 2, 8, '2024-03-15', '2024-03-29', NULL, 'Activo', 'Préstamo para plan lector'),
(7, 24, 9, '2024-02-15', '2024-02-28', '2024-02-27', 'Devuelto', 'Calculadora devuelta con estuche'),
(8, 1, 14, '2024-03-02', '2024-03-16', '2024-03-15', 'Devuelto', 'Lectura de literatura clásica'),
(9, 3, 31, '2024-02-20', '2024-03-05', '2024-03-04', 'Devuelto', 'Estudio para parcial de derivadas'),
(10, 20, 34, '2024-03-18', '2024-03-25', NULL, 'Activo', 'Uso en taller de diseño'),
(11, 4, 32, '2024-02-10', '2024-02-24', '2024-02-23', 'Devuelto', 'Física universitaria tomo 1'),
(12, 5, 33, '2024-03-14', '2024-03-28', NULL, 'Activo', 'Preparación de olimpiada de química'),
(13, 8, 11, '2024-01-25', '2024-02-08', '2024-02-20', 'Devuelto', 'Devuelto con retraso justificado'),
(14, 23, 35, '2024-03-01', '2024-03-10', '2024-03-09', 'Devuelto', 'Calculadora gráfica TI-Nspire'),
(15, 28, 7, '2024-03-15', '2024-03-15', NULL, 'Vencido', 'Balón de voleibol pendiente de retorno a bodega');

-- ----------------------------------------------------------------------------
-- 22. TEACHER LOANS (prestamos_profesor - 15 records)
-- ----------------------------------------------------------------------------
INSERT INTO prestamos_profesor (id_prestamo, id_recurso, id_profesor, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado_prestamo, observaciones) VALUES
(1, 13, 10, '2024-03-01', '2024-03-30', NULL, 'Activo', 'Centrífuga en uso para proyecto de microbiología'),
(2, 22, 1, '2024-02-01', '2024-06-30', NULL, 'Activo', 'Portátil docente asignado para cálculo y notas'),
(3, 25, 6, '2024-03-05', '2024-03-05', '2024-03-05', 'Devuelto', 'Video proyector para clase magistral de inglés'),
(4, 11, 3, '2024-02-15', '2024-02-28', '2024-02-28', 'Devuelto', 'Microscopio para sesión de laboratorio'),
(5, 14, 4, '2024-03-10', '2024-03-24', '2024-03-23', 'Devuelto', 'Sensor Vernier para práctica de caída libre'),
(6, 15, 4, '2024-02-20', '2024-03-06', '2024-03-05', 'Devuelto', 'Kit de prismas para descomposición de luz'),
(7, 16, 17, '2024-03-04', '2024-03-18', '2024-03-17', 'Devuelto', 'pHmetro para titulación ácido-base'),
(8, 25, 11, '2024-03-12', '2024-03-12', '2024-03-12', 'Devuelto', 'Proyector para cineforo filosófico'),
(9, 31, 10, '2024-02-12', '2024-02-26', '2024-02-25', 'Devuelto', 'Modelo anatómico para clase de sistema óseo'),
(10, 32, 10, '2024-03-01', '2024-03-15', '2024-03-15', 'Devuelto', 'Torso humano para clase de digestión'),
(11, 34, 15, '2024-02-05', '2024-06-30', NULL, 'Activo', 'Kit LEGO para el Club de Robótica'),
(12, 35, 3, '2024-03-08', '2024-03-22', '2024-03-21', 'Devuelto', 'Modelos moleculares para enlaces de carbono'),
(13, 27, 23, '2024-03-11', '2024-03-11', '2024-03-11', 'Devuelto', 'Balón de baloncesto para partido de exhibición'),
(14, 30, 7, '2024-03-04', '2024-03-08', '2024-03-08', 'Devuelto', 'Cronómetros para prueba de resistencia Cooper'),
(15, 12, 3, '2024-03-15', '2024-03-29', NULL, 'Activo', 'Balanza para práctica de síntesis química');

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
SELECT 'estudiantes' AS Tabla, COUNT(*) AS Total FROM estudiantes
UNION ALL
SELECT 'acudientes', COUNT(*) FROM acudientes
UNION ALL
SELECT 'profesores', COUNT(*) FROM profesores
UNION ALL
SELECT 'materias', COUNT(*) FROM materias
UNION ALL
SELECT 'registros_academicos', COUNT(*) FROM registros_academicos
UNION ALL
SELECT 'pagos', COUNT(*) FROM pagos
UNION ALL
SELECT 'clubes', COUNT(*) FROM clubes
UNION ALL
SELECT 'recursos', COUNT(*) FROM recursos;
