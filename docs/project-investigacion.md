# Investigacion de las ISSUES 15, 17 y 20

# **Riesgo de abandono (15)**
La detección de estudiantes en riesgo de abandono se basa en sistemas de alerta temprana que combinan indicadores académicos (rendimiento, materias aprobadas, regularidad) con modelos predictivos de datos y aprendizaje automático. Hoy en Argentina se están aplicando enfoques que integran encuestas, sistemas de información universitaria y algoritmos de predicción para intervenir antes de que el abandono ocurra.

### **Factores de riesgo a tener en cuenta**
Rendimiento academico: notas bajas, recursar materias, desaprobar de manera frecuente.

Cantidad de materias aprobadas: retraso segun el plan de estudios, baja de carga academica por cuatrimestre.

Regularidad en la cursada: incripciones discontinuas, abandono de cursadas, baja asistencia.

Contexto socioeconomico: dificultades economicas, necesidad de trabajar, falta de apoyo familiar.

Motivacion y compromiso: baja participacion en actividades, desinteres en la carrera.

### **Estrategias de deteccion**
Sistemas de alerta temprana: avisos o indicadores estructurados que permitan identificar estudiantes vulnerables.

Modelos de aprendizaje automático: algoritmos entrenados con datos del SIU(es un sistema informático desarrollado en Argentina por el Sistema de Información Universitaria) que predicen riesgo de abandono con métricas de precisión superiores al 80%.

Recomendadores académicos: software que sugiere materias a cursar según la cursada individual, reduciendo el abandono.

Encuestas y tutorías: programas de acompañamiento que vinculan datos académicos con percepciones y necesidades de los estudiantes.

### **Riesgos y desafios**
Privacidad de datos: el uso de modelos predictivos requiere proteger la información personal de los estudiantes.

Falsos positivos: algunos estudiantes pueden ser clasificados en riesgo sin realmente abandonar.

Intervención institucional: detectar no basta; se necesitan programas de apoyo (becas, tutorías, acompañamiento psicológico); acuerdos durante la cursada con profesor y alumno.

# **Dependencia tecnologica**

### **Contesxto del problema**
Cuando un sistema depende de una herramienta o framework que ya no evoluciona.

Framework obsoleto: si ya no recibe actualizaciones, se acumulan vulnerabilidades y se dificulta el mantenimiento.

Gestion de riesgos: es clave identificar este tipo de dependencia para planificar opciones de migraciones a otras herramientas o entornos de trabajo.

### **Impacto**
Riesgo de seguridad: ataques posibles por exploits conocidos.

Obsolencia tecnologica: el sistema queda "atado" a una tecnologia muerta, lo que limita la evoluciondel proyecto.

Costos ocultos: mas tiempo de desarrollo, necesidad de parches manuales, migraciones forzadas.

### **Estrategias de mitigacion**
Evaluar alternativas: buscar frameworks activos con comunidad y soporte.

Plan de migración: definir fases para pasar a una tecnología más moderna.

Monitoreo de seguridad: aplicar escáneres de vulnerabilidades y parches manuales.

Documentación interna: registrar cómo funciona el framework para reducir dependencia del conocimiento externo.

## **Ejemplo practico**

### **Escenario**
El sistema académico está desarrollado en Java con Mustache, SQLite y Maven. Supongamos que el framework de plantillas Mustache deja de recibir actualizaciones durante 3 años.

### **Situación**
El sistema depende de Mustache para renderizar vistas (formularios de inscripción, paneles de administrador, listado de materias).

La comunidad deja de mantenerlo y no hay nuevas versiones.

Surgen vulnerabilidades en el motor de plantillas (ejemplo: inyección de código en vistas).

### **Riesgo técnico**
Vulnerabilidades sin parches: un atacante podría manipular las plantillas y acceder a datos sensibles de alumnos o profesores.

Compatibilidad futura: nuevas versiones de Java o Maven podrían no ser compatibles con Mustache.

Dificultad de mantenimiento: menos documentación y soporte, lo que complica resolver errores.

### **Impacto en el sistema académico**
Seguridad: riesgo de fuga de información de alumnos y docentes.

Obsolescencia: el sistema queda atado a una tecnología muerta.

Costos ocultos: el equipo debe invertir tiempo en parches manuales o migrar a otro motor de plantillas.

### **Estrategias de mitigación**
Migrar a un framework activo: por ejemplo, Thymeleaf o Spring Boot MVC para las vistas.

Plan de transición: definir fases para reemplazar Mustache sin interrumpir el servicio.

Monitoreo de seguridad: aplicar escáneres de vulnerabilidades en el código y las dependencias.

Documentación interna: registrar cómo se integraba Mustache para facilitar la migración.

### **Ejemplo aplicado**
El administrador intenta cargar el listado de materias en la vista Mustache.

Un atacante explota una vulnerabilidad conocida en Mustache (sin parche).

El sistema expone datos de alumnos y profesores.

Como no hay soporte oficial, el equipo debe migrar a Thymeleaf para mantener seguridad y compatibilidad.

# **Integracion con sistemas externos**
La integración con sistemas externos es un aspecto crítico en la gestión académica digital. Actualmente, el sistema no se comunica con plataformas de e-learning como Moodle o Canvas, lo que genera duplicación de datos y procesos manuales. Esta falta de interoperabilidad limita la eficiencia administrativa y la experiencia de los estudiantes y docentes.

El riesgo técnico principal es la duplicación de información: los alumnos y profesores deben registrarse en múltiples plataformas, y las inscripciones o notas deben cargarse manualmente en cada sistema. Esto aumenta la probabilidad de errores humanos y de inconsistencias en los datos. Además, la ausencia de integración impide contar con un flujo académico digital unificado, donde las acciones realizadas en el sistema académico se reflejen automáticamente en el entorno virtual de aprendizaje.

El impacto de esta situación se traduce en una gestión académica menos eficiente. Los administradores invierten más tiempo en tareas repetitivas, los estudiantes enfrentan una experiencia fragmentada al tener que usar varias plataformas sin conexión entre sí, y los docentes deben duplicar esfuerzos para mantener actualizada la información en cada entorno. Todo esto reduce la productividad y la calidad del servicio educativo.

Las estrategias de integración más relevantes incluyen el uso de APIs de Moodle y Canvas para sincronizar usuarios, materias y notas; la implementación de Single Sign-On (SSO) para que alumnos y docentes utilicen una sola cuenta en todos los sistemas; procesos ETL de extracción, transformación y carga para mantener alineadas las bases de datos; y arquitecturas basadas en microservicios que faciliten la comunicación modular entre sistemas externos.

En el caso del proyecto, el patrón Singleton juega un rol importante en esta integración. Al centralizar la conexión a la base de datos SQLite, se asegura que todos los módulos del sistema (alumnos, profesores, materias, administradores) accedan a la misma instancia de conexión. Esto facilita la implementación de un módulo de integración que, mediante APIs externas, sincronice datos con Moodle o Canvas de manera consistente y segura. Por ejemplo, cuando un alumno se inscribe en una materia en el sistema, el Singleton gestiona la conexión y asegura que la inscripción se registre en la base de datos local y, al mismo tiempo, se envíe a Moodle mediante su API REST. De igual forma, las notas cargadas en Moodle podrían sincronizarse automáticamente con el sistema académico, manteniendo la coherencia de la información.

En conclusión, la falta de integración con plataformas de e-learning representa un riesgo de ineficiencia y duplicación de datos. La investigación debe demostrar que implementar mecanismos de integración tecnológica, apoyados en el patrón Singleton para garantizar consistencia en el acceso a la base de datos, es fundamental para lograr un sistema académico moderno, seguro y centrado en el estudiante.