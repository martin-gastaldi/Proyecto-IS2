package com.is1.proyecto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.is1.proyecto.config.DBConfigSingleton;
import com.is1.proyecto.models.Docente;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

/**
 * La clase AppTest contiene tests unitarios básicos del sistema.
 *
 * Su objetivo es validar el correcto funcionamiento de los modelos ActiveJDBC.
 */
public class AppTest {
    /**
     * TEST: getters y setters del modelo User.
     */
    @Test
    void testUser_gettersAndSetters () {
        User user = new User ();

        user.setName ("testUser");
        user.setPassword ("1234");

        assertEquals ("testUser", user.getName ());
        assertEquals ("1234", user.getPassword ());
    }

    /**
     * TEST: getters y setters del modelo Persona.
     */
    @Test
    void testPersona_gettersAndSetters () {
        Persona persona = new Persona ();

        persona.setDni (123);
        persona.setRealName ("Juan");
        persona.setSurname ("Perez");

        assertEquals (123, persona.getDni ());
        assertEquals ("Juan", persona.getRealName ());
        assertEquals ("Perez", persona.getSurname ());
    }

    /**
     * TEST: getters y setters del modelo Docente.
     */
    @Test
    void testDocente_gettersAndSetters () {
        Docente docente = new Docente ();

        docente.setDni (123);
        docente.setDepartament ("CS");
        docente.setCorreo ("test@test.com");

        assertEquals (123, docente.getDni ());
        assertEquals ("CS", docente.getDepartament ());
        assertEquals ("test@test.com", docente.getCorreo ());
    }

    /**
     * TEST: getters y setters del modelo Materia.
     */
    @Test
    void testMateria_gettersAndSetters () {
        Materia materia = new Materia ();

        materia.setEncargado (123);
        materia.setNombreMateria ("IS1");
        materia.setIdCarrera (1);

        assertEquals (123, materia.getEncargado ());
        assertEquals ("IS1", materia.getNombreMateria ());
        assertEquals (1, materia.getIdCarrera ());
    }

    /**
     * TEST: el singleton devuelve siempre la misma instancia.
     */
    @Test
    void testDBConfigSingleton_sameInstance () {
        DBConfigSingleton a = DBConfigSingleton.getInstance ();
        DBConfigSingleton b = DBConfigSingleton.getInstance ();

        assertSame (a, b);
    }

    /**
     * TEST: la configuración de base de datos no es nula.
     */
    @Test
    void testDBConfigSingleton_propertiesNotNull () {
        DBConfigSingleton config = DBConfigSingleton.getInstance ();

        assertEquals ("org.sqlite.JDBC", config.getDriver ());
        assertTrue (config.getDbUrl ().contains ("sqlite"));
    }

    /**
     * TEST: apertura y cierre de conexión sin excepciones.
     */
    @Test
    void testDBConfigSingleton_openCloseConnection () {
        DBConfigSingleton config = DBConfigSingleton.getInstance ();

        assertDoesNotThrow (() -> {config.openConnection (); config.closeConnection ();});
    }
}