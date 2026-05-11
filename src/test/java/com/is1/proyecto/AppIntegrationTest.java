package com.is1.proyecto;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.javalite.activejdbc.Base;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.is1.proyecto.models.Administrador;
import com.is1.proyecto.models.Docente;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

/**
 * La clase AppIntegrationTest realiza pruebas de integración completas en la aplicación.
 * 
 * Con ese fin, levanta el servidor real (Spark) y usa una base de datos de testing
 * (SQLite) para validar flujos completos ante requests HTTP reales.
 */
public class AppIntegrationTest {
    // -------------------------------------
    // CONFIGURACIONES Y MÉTODOS AUXILIARES.
    // -------------------------------------
    
    // Cliente HTTP reutilizable para todos los tests.
    private static final HttpClient client = HttpClient.newHttpClient ();

    // Hilo donde corre el servidor Spark.
    private static Thread serverThread;

    /**
     * Configuración inicial antes de correr cualquier test.
     * 
     * @post Inicializa la base de datos de testing y levanta el servidor Spark en un hilo separado.
     */
    @BeforeAll
    static void setup () throws Exception {
        // Inicializa la base de datos de testing (SQLite + schema.sql).
        initDatabase ();

        // Levanta la aplicación real (Spark) en un hilo separado.
        serverThread = new Thread (() -> App.main (new String [] {}));
        serverThread.setDaemon (true); // Muere cuando termina el test suite.
        serverThread.start ();

        // Espera a que el servidor esté listo antes de correr tests.
        waitForServer ();
    }

    /**
     * @post Intenta conectarse al servidor varias veces hasta que responda.
     */
    static void waitForServer () throws Exception {
        for (int i = 0; i < 10; i ++) {
            try {
                HttpRequest request = HttpRequest.newBuilder ()
                    .uri (URI.create ("http://localhost:8080/"))
                    .GET ()
                    .build ();

                client.send (request, HttpResponse.BodyHandlers.ofString ());
                return; // Servidor listo.
            } catch (Exception e) {
                Thread.sleep (1000); // Espera y reintenta.
            }
        }

        throw new RuntimeException ("Spark no levantó a tiempo.");
    }

    /**
     * @post Crea la base de datos de testing y ejecuta el schema.sql.
     */
    static void initDatabase () {
        // Abre conexión a SQLite de test (archivo en /target).
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try (InputStream is = AppIntegrationTest.class
                .getClassLoader ()
                .getResourceAsStream ("scheme.sql")) {

            // Si no encuentra el archivo, falla el test.
            if (is == null) {
                throw new RuntimeException ("scheme.sql no encontrado en src/main/resources.");
            }

            // Lee todo el SQL como string.
            String schema = new String (is.readAllBytes ());

            // Ejecuta cada sentencia SQL separada por ";".
            for (String stmt : schema.split (";")) {
                if (!stmt.trim ().isEmpty ()) {
                    Base.exec (stmt);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException ("Error inicializando DB de test.", e);
        } finally {
            Base.close (); // Cierra conexión.
        }
    }

    /**
     * @post Limpia la base de datos para que cada test sea independiente.
     */
    @BeforeEach
    void cleanDatabase () {
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            // Borra datos de todas las tablas.
            Base.exec ("DELETE FROM users");
            Base.exec ("DELETE FROM docente");
            Base.exec ("DELETE FROM persona");
            Base.exec ("DELETE FROM materia");
            Base.exec ("DELETE FROM administrador");
            Base.exec ("DELETE FROM carrera");
        } finally {
            Base.close ();
        }
    }

    /**
     * @post Realiza un POST HTTP al servidor.
     */
    private HttpResponse <String> post (String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080" + path))
            .header ("Content-Type", "application/x-www-form-urlencoded")
            .POST (HttpRequest.BodyPublishers.ofString (body))
            .build ();

        return client.send (request, HttpResponse.BodyHandlers.ofString ());
    }

    /**
     * @post Realiza un GET HTTP al servidor.
     */
    private HttpResponse <String> get (String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080" + path))
            .GET ()
            .build ();

        return client.send (request, HttpResponse.BodyHandlers.ofString ());
    }

    // ---------------------
    // TESTS DE INTEGRACIÓN.
    // ---------------------

    /**
     * TEST: crear usuario correctamente.
     */
    @Test
    void testCreateUser_OK () throws Exception {
        HttpResponse <String> res = post(
            "/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");
        
        assertTrue(location.contains("Usuario creado correctamente"));

        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            User user = User.findFirst ("name = ?", "testuser");

            assertNotNull (user);
        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: crear usuario con datos vacíos.
     */
    @Test
    void testCreateUser_ERROR_emptyFields () throws Exception {
        HttpResponse <String> res = post (
                "/user/new",
                "name=&password=&dni=&realName=&surname=&correo="
        );

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue(location.contains("Todos los campos son obligatorios."));
    }

    /**
     * TEST: crear usuario sin enviar parámetros (equivalente a null).
     */
    @Test
    void testCreateUser_missingFields () throws Exception {
        HttpResponse <String> res = post(
                "/user/new",
                ""
        );

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue(location.contains("Todos los campos son obligatorios."));
    }

    /**
     * TEST: no se pueden crear usuarios duplicados.
     */
    @Test
    void testCreateUser_DUPLICATE () throws Exception {
        HttpResponse <String> res1 = post (
                "/user/new",
                "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        assertEquals (302, res1.statusCode ());

        HttpResponse <String> res2 = post (
                "/user/new",
                "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        assertEquals (302, res2.statusCode ());

        String location = res2.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue(location.contains("El usuario ya existe."));
    }

    /**
     * TEST: la contraseña se guarda hasheada (BCrypt).
     */
    @Test
    void testCreateUser_passwordIsHashed () throws Exception {
        post ("/user/new", "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com");

        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            User user = User.findFirst ("name = ?", "testuser");

            assertNotNull (user);

            String storedPassword = user.getString ("password");

            assertNotEquals ("1234", storedPassword);

            assertTrue (storedPassword.startsWith ("$2a$"));
        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: login exitoso.
     */
    @Test
    void testLogin_OK () throws Exception {
        post("/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        HttpResponse<String> res = post(
            "/login",
            "username=testuser&password=1234"
        );

        assertEquals(200, res.statusCode());
        assertFalse(res.body().contains("Usuario o contraseña incorrectos"));
    }

    /**
     * TEST: login con credenciales incorrectas.
     */
    @Test
    void testLogin_FAIL_wrongCredentials () throws Exception {
        HttpResponse <String> res = post (
                "/login",
                "username=wrong&password=wrong&dni=wrong"
        );

        assertEquals (401, res.statusCode ());

        assertTrue (res.body ().contains ("Usuario o contraseña incorrectos."));
    }

    /**
     * TEST: login con contraseña incorrecta.
     */
    @Test
    void testLogin_WRONG_PASSWORD () throws Exception {
        post ("/user/new", "name=testuser&password=1234&dni=123");

        HttpResponse <String> res = post (
                "/login",
                "username=testuser&password=wrongpass&dni=123"
        );

        assertEquals (401, res.statusCode ());
        
        assertTrue (res.body ().contains ("Usuario o contraseña incorrectos."));
    }

    /**
     * TEST: login con campos vacíos.
     */
    @Test
    void testLogin_EMPTY_FIELDS () throws Exception {
        HttpResponse <String> res = post (
                "/login",
                "username=&password=&dni="
        );

        assertEquals (400, res.statusCode ());

        assertTrue (res.body ().contains ("El nombre de usuario y la contraseña son requeridos."));
    }

    /**
     * TEST: carga de página de login.
     */
    @Test
    void testLoginPage_load () throws Exception {
        HttpResponse <String> res = get ("/");

        assertEquals (200, res.statusCode ());
        assertTrue (res.body ().toLowerCase ().contains ("login"));
    }

    /**
     * TEST: acceso al dashboard con sesión válida.
     */
    @Test
    void testDashboard_withSession_OK () throws Exception {
        post("/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        HttpResponse<String> loginRes = post(
            "/login",
            "username=testuser&password=1234"
        );

        String cookie = loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/dashboard"))
            .header("Cookie", cookie)
            .GET()
            .build();

        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, res.statusCode());
        assertTrue(res.body().contains("testuser"));
    }

    /**
     * TEST: creación correcta de docente + persona + materia.
     */
    @Test
    void testCreateDocente_OK () throws Exception {
        // Insertar carrera necesaria.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO carrera (nombreCarrera, facultad, duracion, titulo) VALUES ('Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')");
        } finally {
            Base.close ();
        }

        HttpResponse <String> res = post (
                "/get_docente",
                "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com"
        );

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("Docente cargado exitosamente"));

        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            Docente docente = Docente.findFirst ("dni = ?", 123);
            Persona persona = Persona.findFirst ("dni = ?", 123);
            Materia materia = Materia.findFirst ("nombreMateria = ?", "IS1");

            assertNotNull (docente);
            assertNotNull (persona);
            assertNotNull (materia);
        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: validación de campos vacíos en docente.
     */
    @Test
    void testCreateDocente_validationError () throws Exception {
        HttpResponse <String> res = post (
                "/get_docente",
                "dni=&realName=&surname=&nombreMateria=&id_carrera=&departament=&correo="
        );

        assertEquals (302, res.statusCode());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("Todos los campos son obligatorios."));
    }

    /**
     * TEST: duplicado por correo en docente.
     */
    @Test
    void testCreateDocente_duplicateEmail () throws Exception {
        // Insertar carreras necesarias.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO carrera (id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES (1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')");
            Base.exec ("INSERT INTO carrera (id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES (2, 'Ingenieria Civil', 'FCEFyN', 5, 'Ing. Civil')");
        } finally {
            Base.close ();
        }

        post ("/get_docente",
            "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com"
        );

        HttpResponse <String> res = post ("/get_docente",
            "dni=456&realName=Otro&surname=Docente&nombreMateria=IS2&id_carrera=2&departament=CS&correo=test@test.com"
        );

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("error"));
    }

    /**
     * TEST: listado de docentes contiene datos reales.
     */
    @Test
    void testPostDocente_containsData () throws Exception {
        // Insertar carrera necesaria.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO carrera (nombreCarrera, facultad, duracion, titulo) VALUES ('Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')");
        } finally {
            Base.close ();
        }

        post ("/get_docente",
            "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com"
        );

        HttpResponse <String> res = get ("/post_docente");

        assertEquals (200, res.statusCode ());

        String body = res.body ();

        assertTrue (body.contains ("123"));
        assertTrue (body.contains ("test@test.com"));

        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            Persona persona = Persona.findFirst ("dni = ?", 123);
            Materia materia = Materia.findFirst ("nombreMateria = ?", "IS1");

            assertNotNull (persona);
            assertNotNull (materia);

            assertEquals ("Juan", persona.getRealName ());
            assertEquals ("Perez", persona.getSurname ());
            assertEquals ("IS1", materia.getNombreMateria ());
            assertEquals (1, materia.getIdCarrera ());

        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: no se duplican registros de docente con mismo DNI.
     */
    @Test
    void testCreateDocente_duplicateDni () throws Exception {
        // Insertar carrera necesaria.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO carrera (nombreCarrera, facultad, duracion, titulo) VALUES ('Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')");
        } finally {
            Base.close ();
        }

        post ("/get_docente",
                "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com"
        );

        post ("/get_docente",
                "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test2@test.com"
        );

        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );

        try {
            long count = Docente.find ("dni = ?", 123).size ();

            assertEquals (1, count);
        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: acceso protegido sin login al dashboard.
     */
    @Test
    void testDashboard_redirectIfNotLogged () throws Exception {
        HttpResponse <String> res = get ("/dashboard");

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/login"));
        assertTrue (location.contains ("Debes iniciar sesión para acceder a esta página."));
    }

    /**
     * TEST: logout invalida la sesión y redirige a login.
     */
    @Test
    void testLogout () throws Exception {
        HttpResponse <String> res = get ("/logout");

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/"));
    }

    /**
     * TEST: después del logout no se puede acceder al dashboard.
     */
    @Test
    void testLogout_blocksDashboardAccess () throws Exception {
        post ("/user/new", "name=testuser&password=1234");
        post ("/login", "username=testuser&password=1234");

        get ("/logout");

        HttpResponse <String> res = get ("/dashboard");

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/login"));
        assertTrue (location.contains ("Debes iniciar sesión para acceder a esta página."));
    }

    /**
     * TEST: login de administrador redirige al panel de admin.
     */
    @Test
    void testAdminLogin_redirectsToPanel () throws Exception {
        // Crear usuario admin.
        post ("/user/new", "name=adminuser&password=1234&dni=123456&realName=Admin&surname=User&correo=admin@test.com");

        // Insertar en administrador.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO administrador (dni) VALUES (123456)");
        } finally {
            Base.close ();
        }

        HttpResponse <String> res = post ("/login", "username=adminuser&password=1234");

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/admin/panel"));
    }

    /**
     * TEST: acceso al panel de administrador con sesión válida.
     */
    @Test
    void testAdminPanel_access () throws Exception {
        // Crear usuario admin.
        post("/user/new", "name=adminuser2&password=1234&dni=123457&realName=Admin2&surname=User2&correo=admin2@test.com");

        // Insertar en administrador.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO administrador (dni) VALUES (123457)");
        } finally {
            Base.close ();
        }

        // Login
        HttpResponse <String> loginRes = post ("/login", "username=adminuser2&password=1234");

        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Acceder a /admin/panel.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/panel"))
            .header ("Cookie", cookie)
            .GET ()
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (200, res.statusCode ());
    }

    /**
     * TEST: acceso a la página de creación de administrador con sesión válida.
     */
    @Test
    void testAdminCreate_access () throws Exception {
        // Crear usuario admin.
        post ("/user/new", "name=adminuser3&password=1234&dni=123458&realName=Admin3&surname=User3&correo=admin3@test.com");

        // Insertar en administrador.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO administrador (dni) VALUES (123458)");
        } finally {
            Base.close ();
        }

        // Login.
        HttpResponse <String> loginRes = post ("/login", "username=adminuser3&password=1234");

        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Acceder a /admin/create.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/create"))
            .header ("Cookie", cookie)
            .GET ()
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (200, res.statusCode ());
        assertTrue (res.body ().contains ("Crear Administrador"));
    }

    /**
     * TEST: creación exitosa de administrador desde un admin.
     */
    @Test
    void testAdminCreate_OK () throws Exception {
        // Crear admin existente.
        post ("/user/new", "name=existingadmin&password=1234&dni=111111&realName=Existing&surname=Admin&correo=existing@admin.com");
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO administrador (dni) VALUES (111111)");
        } finally {
            Base.close ();
        }

        // Login como admin.
        HttpResponse <String> loginRes = post ("/login", "username=existingadmin&password=1234");
        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Crear nuevo admin.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/create"))
            .header ("Content-Type", "application/x-www-form-urlencoded")
            .header ("Cookie", cookie)
            .POST (HttpRequest.BodyPublishers.ofString ("username=newadmin&password=567890&passwordConfirm=567890&dni=222222&realName=New&surname=Admin&telefono=123456789&correo=new@admin.com"))
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("Administrador creado exitosamente"));

        // Verificar en DB.
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            User user = User.findFirst ("name = ?", "newadmin");
            Administrador admin = Administrador.findFirst ("dni = ?", 222222);

            assertNotNull (user);
            assertNotNull (admin);
        } finally {
            Base.close ();
        }
    }

    /**
     * TEST: validación de errores en creación de administrador.
     */
    @Test
    void testAdminCreate_validationError () throws Exception {
        // Crear admin existente.
        post ("/user/new", "name=existingadmin2&password=1234&dni=333333&realName=Existing2&surname=Admin2&correo=existing2@admin.com");
        Base.open (
            System.getProperty ("db.driver", "org.sqlite.JDBC"),
            System.getProperty ("db.url", "jdbc:sqlite:./target/test.db"),
            "",
            ""
        );
        try {
            Base.exec ("INSERT INTO administrador (dni) VALUES (333333)");
        } finally {
            Base.close ();
        }

        // Login como admin.
        HttpResponse <String> loginRes = post ("/login", "username=existingadmin2&password=1234");
        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Intentar crear con campos vacíos.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/create"))
            .header ("Content-Type", "application/x-www-form-urlencoded")
            .header ("Cookie", cookie)
            .POST (HttpRequest.BodyPublishers.ofString ("username=&password=&passwordConfirm=&dni=&realName=&surname=&telefono=&correo="))
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("Todos los campos son obligatorios"));
    }

    /**
     * TEST: no se puede acceder a /admin/panel sin ser administrador.
     */
    @Test
    void testAdminPanel_noAccessWithoutAdmin () throws Exception {
        // Crear usuario normal.
        post ("/user/new", "name=normaluser&password=1234&dni=444444&realName=Normal&surname=User&correo=normal@user.com");

        // Login como usuario normal.
        HttpResponse <String> loginRes = post ("/login", "username=normaluser&password=1234");
        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Intentar acceder a /admin/panel.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/panel"))
            .header ("Cookie", cookie)
            .GET ()
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/login"));
    }

    /**
     * TEST: no se puede acceder a /admin/create sin ser administrador.
     */
    @Test
    void testAdminCreate_noAccessWithoutAdmin () throws Exception {
        // Crear usuario normal.
        post ("/user/new", "name=normaluser2&password=1234&dni=555555&realName=Normal2&surname=User2&correo=normal2@user.com");

        // Login como usuario normal.
        HttpResponse <String> loginRes = post ("/login", "username=normaluser2&password=1234");
        String cookie = loginRes.headers ()
            .firstValue ("Set-Cookie")
            .orElse ("");

        // Intentar acceder a /admin/create.
        HttpRequest request = HttpRequest.newBuilder ()
            .uri (URI.create ("http://localhost:8080/admin/create"))
            .header ("Cookie", cookie)
            .GET ()
            .build ();

        HttpResponse <String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

        assertEquals (302, res.statusCode ());

        String location = res.headers ()
            .firstValue ("Location")
            .orElse ("");

        assertTrue (location.contains ("/login"));
    }
}