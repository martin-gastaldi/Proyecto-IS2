**Ingeniería de Software II (Cód. 3387)**  
**Año 2026**

# **Proyecto Integrador: Especificación, Gestión y Planificación**

## **1. DESCRIPCIÓN DEL PROYECTO**

### **- Problema que se quiere resolver**
En la actualidad, muchas universidades aún cuentan con **sistemas poco eficientes para realizar su gestión académica**. En algunos casos, poseen su información distribuida entre muchos sistemas fragmentados y poco seguros, o incluso siguen haciendo uso de procesos manuales lentos, problemáticos y poco escalables.  

Esta situación genera, entre otras cosas:

- Dificultad para acceder a información actualizada en tiempo real.
- Errores en la carga de datos.
- Inconsistencia en la información.

El proyecto busca solucionar estas problemáticas y propone desarrollar un **sistema centralizado, el cual permita mejorar la administración académica y el acceso a la información de una manera sencilla, ágil y segura**.

# Proyecto de Gestión Académica - Universidad

##  Usuarios del Sistema
- **Administrador / Personal de oficina**
  - Gestiona estudiantes, docentes, materias y planes de estudio.
  - Tiene permisos de edición y control total sobre la información.

- **Estudiantes**
  - Consultan su información personal, notas y avance en la carrera.
  - Se inscriben en materias si cumplen correlatividades.

- **Docentes**
  - Cargan notas de los estudiantes.
  - Consultan listados de alumnos en sus materias.
  - Se les asigna cargo (jefe de cátedra, jefe de trabajos prácticos, ayudante).

---

##  Funcionalidades principales
1. **Gestión de estudiantes**
   - Alta, baja, modificación y consulta de datos personales.
   - Inscripción a materias validando correlatividades.
   - Visualización del estado en la carrera y avance en el plan.

2. **Gestión de docentes**
   - Alta, baja y modificación de datos.
   - Asignación de materias y cargos.
   - Acceso a listados de alumnos y carga de notas.

3. **Gestión de materias**
   - Alta, baja y modificación de materias.
   - Definición de correlatividades.
   - Asociación con planes de estudio.
   - Acceso de docentes para cargar notas.

4. **Gestión de planes de estudio**
   - Creación, edición y consulta de planes por carrera.
   - Definición de materias, carga horaria, año/cuatrimestre y correlatividades.

5. **Gestión de correlatividades**
   - Validación automática de inscripciones según materias aprobadas.
   - Mostrar correlativas al consultar estudiantes.
---

## **CÓMO EJECUTAR EL SERVIDOR**

Ubicarse en la carpeta:

```bash
cd Proyecto-IS2\
```

Luego, ejecutar el comando:

```bash
mvn clean package
```

Seguido de:
```bash
java -jar "target\proye-is-1.0-SNAPSHOT.jar"
```
El servidor puede accederse desde:

```
http://localhost:8080/
```
