# **Proyecto Integrador IS2 - Administración Académica Universitaria**

Sistema web orientado a la **gestión académica universitaria**, diseñado para centralizar la información de estudiantes, docentes, materias y planes de estudio, mejorando la eficiencia, consistencia y accesibilidad de los datos.

---

## **TECNOLOGÍAS UTILIZADAS**

- Java.
- Spark Java.
- SQLite.
- ActiveJDBC.
- Mustache.
- Apache Maven.

---

## **CÓMO EJECUTAR EL PROYECTO**

Clonar el repositorio:

```bash
git clone https://github.com/martin-gastaldi/Proyecto-IS2.git
```

Ubicarse en la carpeta:

```bash
cd Proyecto-IS2
```

Compilar el proyecto:

```bash
mvn clean package -Ptest
```

Correr los tests:

```bash
mvn test -Ptest
```

Ejecutar el servidor:

```bash
java -jar "target/proye-is-1.0-SNAPSHOT.jar"
```

Acceder desde el navegador:

http://localhost:8080/

---

## **USUARIOS DEL SISTEMA**

- ***Administrador***

  - Gestión completa del sistema (estudiantes, docentes, materias, planes).

- ***Estudiantes***

  - Consulta de información académica.
  - Inscripción a materias.

- ***Docentes***

  - Carga de notas.
  - Consulta de alumnos.

---

## **FUNCIONALIDADES PRINCIPALES**

- Gestión de estudiantes, docentes y materias.
- Administración de planes de estudio.
- Validación de correlatividades.
- Inscripción a materias.
- Carga y consulta de notas.

---

## **DOCUMENTACIÓN COMPLETA**

Para ver la especificación, planificación y análisis de riesgos del proyecto dirigirse a:

[Documentación del Proyecto](docs/project-documentation.md)

---

## **CONTEXTO ACADÉMICO**

Proyecto Integrador de la materia Ingeniería de Software II (Cód. 3387 - Año 2026).

***Integrantes***

- AGÜERO, Daniel Ignacio.
- GASTALDI, Martín.
- GUZMAN, Ayelen.
- PEREYRA, Jorge Pedro Ezequiel.
- VEGA, Matías Thomas.