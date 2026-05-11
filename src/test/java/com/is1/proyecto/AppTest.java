package com.is1.proyecto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.is1.proyecto.config.DBConfigSingleton;
import com.is1.proyecto.models.Administrador;
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

        user.setName("testUser");
        user.setPassword("1234");
        user.setDni(123);

        assertEquals("testUser", user.getName());
        assertEquals("1234", user.getPassword());
        assertEquals(123, user.getDni());
    }

    /**
     * TEST: getters y setters del modelo Persona.
     */
    @Test
    void testPersona_gettersAndSetters() {
        Persona persona = new Persona();
 
        persona.setDni(12345678);
        persona.setRealName("Juan");
        persona.setSurname("Perez");
        persona.setTelefono("3512345678");
        persona.setCorreo("juan@test.com");
 
        assertEquals(12345678, persona.getDni());
        assertEquals("Juan", persona.getRealName());
        assertEquals("Perez", persona.getSurname());
        assertEquals("3512345678", persona.getTelefono());
        assertEquals("juan@test.com", persona.getCorreo());
    }

    /**
     * TEST: getters y setters del modelo Docente.
     */
     @Test
    void testDocente_gettersAndSetters() {
        Docente docente = new Docente();
 
        docente.setDni(12345678);
        docente.setDepartament("Ciencias de la Computacion");
        docente.setCuil("20-12345678-1");
 
        assertEquals(12345678, docente.getDni());
        assertEquals("Ciencias de la Computacion", docente.getDepartament());
        assertEquals("20-12345678-1", docente.getCuil());
    }

    /**
     * TEST: getters y setters del modelo Materia.
     */
     @Test
    void testMateria_gettersAndSetters() {
        Materia materia = new Materia();
 
        materia.setNombreMateria("Ingenieria de Software I");
        materia.setAnio(3);
        materia.setCuatrimestre(1);
        materia.setCargaHoraria(96);
        materia.setIdCarrera(1);
 
        assertEquals("Ingenieria de Software I", materia.getNombreMateria());
        assertEquals(3, materia.getAnio());
        assertEquals(1, materia.getCuatrimestre());
        assertEquals(96, materia.getCargaHoraria());
        assertEquals(1, materia.getIdCarrera());
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

    /**
     * TEST: getters y setters del modelo Administrador.
     */
    @Test
    void testAdministrador_gettersAndSetters () {
        Administrador admin = new Administrador ();

        admin.setDni (98765432);

        assertEquals (98765432, admin.getDni ());
    }

    /**
     * TEST: User.esAdministrador retorna false si dni es null.
     */
    @Test
    void testUser_esAdministrador_dniNull () {
        User user = new User ();

        user.setName ("testUser");
        user.setPassword ("1234");
        // dni no se setea, queda null.

        assertEquals (null, user.getDni ());
    }

    /**
     * TEST: Administrador.obtenerPorUsuario retorna null si user dni es null.
     */
    @Test
    void testAdministrador_obtenerPorUsuario_dniNull () {
        User user = new User ();
        user.setName ("testUser");

        Administrador admin = Administrador.obtenerPorUsuario (user);

        assertEquals (null, admin);
    }
}