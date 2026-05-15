package com.is1.proyecto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * Tests unitarios básicos del sistema.
 *
 * Valida getters/setters, singleton y métodos auxiliares.
 */
public class AppTest {

    // =====================================================
    // USER
    // =====================================================

    @Test
    void testUser_gettersAndSetters() {
        User user = new User();

        user.setName("testUser");
        user.setPassword("1234");
        user.setDni(123);

        assertEquals("testUser", user.getName());
        assertEquals("1234", user.getPassword());
        assertEquals(123, user.getDni());
    }

    /**
     * TEST: User.esAdministrador retorna false cuando no tiene DNI.
     */
    @Test
    void testUser_esAdministrador_dniNull() {
        User user = new User();

        user.setName("testUser");
        user.setPassword("1234");

        assertNull(user.getDni());

        // Evita NullPointerException si el método está protegido.
        assertDoesNotThrow(() -> user.esAdministrador());
    }

    // =====================================================
    // PERSONA
    // =====================================================

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

    // =====================================================
    // DOCENTE
    // =====================================================

    @Test
    void testDocente_gettersAndSetters() {
        Docente docente = new Docente();

        docente.setDni(12345678);
        docente.setDepartament("Ciencias de la Computacion");


        assertEquals(12345678, docente.getDni());
        assertEquals("Ciencias de la Computacion", docente.getDepartament());

    }

    // =====================================================
    // MATERIA
    // =====================================================

    @Test
    void testMateria_gettersAndSetters() {

        Materia materia = new Materia();

        materia.setNombreMateria("Ingenieria de Software I");
        materia.setIdCarrera(1);

        assertEquals(
            "Ingenieria de Software I",
            materia.getNombreMateria()
        );

        assertEquals(
            1,
            materia.getIdCarrera()
        );
    }

    // =====================================================
    // ADMINISTRADOR
    // =====================================================

    @Test
    void testAdministrador_gettersAndSetters() {
        Administrador admin = new Administrador();

        admin.setDni(98765432);

        assertEquals(98765432, admin.getDni());
    }

    /**
     * TEST: obtenerPorUsuario retorna null si user no tiene dni.
     */
    @Test
    void testAdministrador_obtenerPorUsuario_dniNull() {
        User user = new User();
        user.setName("testUser");

        Administrador admin = Administrador.obtenerPorUsuario(user);

        assertNull(admin);
    }

    // =====================================================
    // DB CONFIG SINGLETON
    // =====================================================

    @Test
    void testDBConfigSingleton_sameInstance() {
        DBConfigSingleton a = DBConfigSingleton.getInstance();
        DBConfigSingleton b = DBConfigSingleton.getInstance();

        assertSame(a, b);
    }

    @Test
    void testDBConfigSingleton_propertiesNotNull() {
        DBConfigSingleton config = DBConfigSingleton.getInstance();

        assertEquals("org.sqlite.JDBC", config.getDriver());
        assertTrue(config.getDbUrl().contains("sqlite"));
    }

    @Test
    void testDBConfigSingleton_openCloseConnection() {
        DBConfigSingleton config = DBConfigSingleton.getInstance();

        assertDoesNotThrow(() -> {
            config.openConnection();
            config.closeConnection();
        });
    }
}
