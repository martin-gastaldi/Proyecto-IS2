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