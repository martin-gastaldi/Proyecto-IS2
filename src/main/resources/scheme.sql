
 -- Elimina la tabla 'users' si ya existe para asegurar un inicio limpio
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS materia;
DROP TABLE IF EXISTS docente;
DROP TABLE IF EXISTS persona;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS correlatividad;
DROP TABLE IF EXISTS dictado;
DROP TABLE IF EXISTS cursado;
DROP TABLE IF EXISTS inscripcion;
DROP TABLE IF EXISTS plan_materia;
DROP TABLE IF EXISTS plan_estudio;
DROP TABLE IF EXISTS carrera;

 -- Crea la tabla 'users' con los campos originales, adaptados para SQLite
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- Clave primaria autoincremental para SQLite
    name TEXT NOT NULL UNIQUE,          -- Nombre de usuario (TEXT es el tipo de cadena recomendado para SQLite), con restricción UNIQUE
    password TEXT NOT NULL,           -- Contraseña hasheada (TEXT es el tipo de cadena recomendado para SQLite)
    dni INTEGER UNIQUE,  -- relacion 1..1 con persona
    FOREIGN KEY (dni) REFERENCES persona(dni)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE TABLE persona (
    dni INTEGER PRIMARY KEY,
    realName TEXT NOT NULL,
    surname TEXT NOT NULL,
    telefono TEXT NOT NULL,
    correo TEXT UNIQUE
);
CREATE TABLE docente (
    dni INTEGER PRIMARY KEY,
    departament TEXT NOT NULL,
    cuil TEXT,
    FOREIGN KEY (dni) REFERENCES persona(dni)
     ON DELETE CASCADE
     ON UPDATE CASCADE
);
CREATE TABLE estudiante (
    dni INTEGER PRIMARY KEY,
    legajo INTEGER UNIQUE,
    fecha_ingreso TEXT,
    FOREIGN KEY (dni) REFERENCES persona(dni)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE TABLE administrador (
    dni INTEGER PRIMARY KEY,
    FOREIGN KEY (dni) REFERENCES persona(dni)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE TABLE materia (
    id_materia INTEGER PRIMARY KEY AUTOINCREMENT,
    nombreMateria TEXT NOT NULL,
    anio INTEGER,
    cuatrimestre INTEGER,
    carga_horaria INTEGER,
    id_carrera INTEGER, 
     FOREIGN KEY (id_carrera) REFERENCES carrera(id_carrera)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE TABLE carrera (
    id_carrera INTEGER PRIMARY KEY AUTOINCREMENT,
    nombreCarrera TEXT NOT NULL,
    facultad TEXT,
    duracion INTEGER,
    titulo TEXT
);
CREATE TABLE plan_estudio (
    id_plan INTEGER PRIMARY KEY AUTOINCREMENT,
    anio INTEGER,
    vigente BOOLEAN,
    descripcion TEXT,
    id_carrera INTEGER, -- 1..N
    FOREIGN KEY (id_carrera) REFERENCES carrera(id_carrera)
        ON DELETE CASCADE
);
--Relacion N..N
CREATE TABLE plan_materia (
    id_plan INTEGER,
    id_materia INTEGER,
    PRIMARY KEY (id_plan, id_materia),
    FOREIGN KEY (id_plan) REFERENCES plan_estudio(id_plan)
        ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
        ON DELETE CASCADE
);
--Relacion N..N
CREATE TABLE inscripcion (
    dniEstudiante INTEGER,
    id_plan INTEGER,
    fecha_ingreso TEXT,
    situacion TEXT CHECK (situacion IN ('INGRESANTE','ACTIVA','FINALIZADA','ABANDONADA')),
    PRIMARY KEY (dniEstudiante, id_plan),
    FOREIGN KEY (dniEstudiante) REFERENCES estudiante(dni)
        ON DELETE CASCADE,
    FOREIGN KEY (id_plan) REFERENCES plan_estudio(id_plan)
        ON DELETE CASCADE
);
--Relacion N..N
CREATE TABLE cursado (
    dniEstudiante INTEGER,
    id_materia INTEGER,
    fechaInscripcion TEXT,
    estado TEXT CHECK (estado IN ('LIBRE','REGULAR','PROMOCIONADA','APROBADA')),
    notaFinal REAL,
    PRIMARY KEY (dniEstudiante, id_materia),
    FOREIGN KEY (dniEstudiante) REFERENCES estudiante(dni)
        ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
        ON DELETE CASCADE
);
--Relacion N..N
CREATE TABLE dictado (
    dniDocente INTEGER,
    id_materia INTEGER,
    cargo TEXT CHECK (cargo IN ('TITULAR','JEFE_PRACTICA','AYUDANTE')),
    dedicacion TEXT CHECK (dedicacion IN ('SIMPLE','SEMI_EXCLUSIVA','EXCLUSIVA')),
    fechaInicio TEXT,
    fechaFin TEXT,
    PRIMARY KEY (dniDocente, id_materia, fechaInicio),
    FOREIGN KEY (dniDocente) REFERENCES docente(dni)
        ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
        ON DELETE CASCADE
);
--Relacion Materia a Materia.
CREATE TABLE correlatividad (
    id_materia INTEGER,
    id_correlativa INTEGER,
    condicion TEXT CHECK (condicion IN ('REGULAR','APROBADA')),
    PRIMARY KEY (id_materia, id_correlativa),
    FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
        ON DELETE CASCADE,
    FOREIGN KEY (id_correlativa) REFERENCES materia(id_materia)
        ON DELETE CASCADE
);

-- Insertar administrador por defecto (con contraseña "admin123" hasheada).
INSERT INTO persona (dni, realName, surname, telefono, correo) 
VALUES (12345678, 'Admin', 'Sistema', '1234567890', 'admin@sistema.com');

INSERT INTO administrador (dni) 
VALUES (12345678);

INSERT INTO users (name, password, dni) 
VALUES ('admin', '$2a$10$PqHRcarvc/bJIizSJ9hwHudS1zsjk0bSnIP6bv7GfzyTjxtUyYu.G', 12345678);