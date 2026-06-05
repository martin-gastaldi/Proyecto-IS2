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
import com.is1.proyecto.models.Carrera;
import com.is1.proyecto.models.Dictado;
import com.is1.proyecto.models.Docente;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

/**
 * Tests de integración completos.
 */
public class AppIntegrationTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static Thread serverThread;

    private static final String DB_DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:./target/test.db";

    @BeforeAll
    static void setup() throws Exception {

        // Configurar DB de testing
        System.setProperty("db.driver", DB_DRIVER);
        System.setProperty("db.url", DB_URL);

        initDatabase();

        // Levantar servidor
        serverThread = new Thread(() -> App.main(new String[]{}));
        serverThread.setDaemon(true);
        serverThread.start();

        waitForServer();
    }

    static void waitForServer() throws Exception {

        for (int i = 0; i < 10; i++) {

            try {

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/"))
                    .GET()
                    .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());

                return;

            } catch (Exception e) {

                Thread.sleep(1000);
            }
        }

        throw new RuntimeException("Spark no levantó a tiempo.");
    }

    static void initDatabase() {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try (InputStream is = AppIntegrationTest.class
                .getClassLoader()
                .getResourceAsStream("scheme.sql")) {

            if (is == null) {
                throw new RuntimeException("scheme.sql no encontrado.");
            }

            String schema = new String(is.readAllBytes());

            for (String stmt : schema.split(";")) {

                if (!stmt.trim().isEmpty()) {
                    Base.exec(stmt);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException("Error inicializando DB.", e);

        } finally {

            Base.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            // Orden importante por FK
            Base.exec("DELETE FROM dictado");
            Base.exec("DELETE FROM administrador");
            Base.exec("DELETE FROM docente");
            Base.exec("DELETE FROM materia");
            Base.exec("DELETE FROM users");
            Base.exec("DELETE FROM persona");
            Base.exec("DELETE FROM carrera");

        } finally {

            Base.close();
        }
    }

    private HttpResponse<String> post(String path, String body) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080" + path))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080" + path))
            .GET()
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void crearDocenteBase() {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec(
                "INSERT INTO carrera " +
                "(id_carrera, nombreCarrera, facultad, duracion, titulo) " +
                "VALUES " +
                "(1, 'Ingenieria', 'FCEFyN', 5, 'Ingeniero')"
            );

            Base.exec(
                "INSERT INTO persona " +
                "(dni, realName, surname, telefono, correo) " +
                "VALUES " +
                "(123, 'Juan', 'Perez', '358', 'juan@test.com')"
            );

            Base.exec(
                "INSERT INTO docente " +
                "(dni, departament, cuil) " +
                "VALUES " +
                "(123, 'CS', '20-123')"
            );

            Base.exec(
                "INSERT INTO materia " +
                "(id_materia, nombreMateria, anio, cuatrimestre, carga_horaria, id_carrera) " +
                "VALUES " +
                "(1, 'IS1', 1, 1, 64, 1)"
            );

            Base.exec(
                "INSERT INTO dictado " +
                "(dniDocente, id_materia, cargo, dedicacion, fechaInicio) " +
                "VALUES " +
                "(123, 1, 'TITULAR', 'SIMPLE', '2024-01-01')"
            );

        } finally {

            Base.close();
        }
    }

    private String loginDocente() throws Exception {

        crearDocenteBase();

        post(
            "/user/new",
            "name=docenteTest" +
            "&password=1234" +
            "&dni=123" +
            "&realName=Juan" +
            "&surname=Perez" +
            "&correo=juan@test.com"
        );

        HttpResponse<String> loginRes = post(
            "/login",
            "username=docenteTest&password=1234"
        );

        return loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");
    }

    private String loginAdmin() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Base.exec(
                "INSERT INTO persona " +
                "(dni, realName, surname, telefono, correo) " +
                "VALUES (123, 'Admin', 'User', '000', 'admin@test.com')"
            );

            Base.exec(
                "INSERT INTO administrador (dni) VALUES (123)"
            );
        } finally {
            Base.close();
        }

        post(
            "/user/new",
            "name=adminTest" +
            "&password=1234" +
            "&dni=123" +
            "&realName=Admin" +
            "&surname=User" +
            "&correo=admin@test.com"
        );

        HttpResponse<String> loginRes = post(
            "/login",
            "username=adminTest&password=1234"
        );

        String cookie = loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");

        return cookie.split(";", 2)[0];
    }

    private HttpResponse<String> get(String path,
                                     String cookie) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080" + path))
            .header("Cookie", cookie)
            .GET()
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path,
                                      String body,
                                      String cookie) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080" + path))
            .header("Cookie", cookie)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // =========================================================
    // USERS
    // =========================================================

    @Test
    void testCreateUser_OK() throws Exception {

        HttpResponse<String> res = post(
            "/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        assertEquals(302, res.statusCode());

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            User user = User.findFirst("name = ?", "testuser");

            assertNotNull(user);

        } finally {

            Base.close();
        }
    }

    @Test
    void testCreateUser_passwordIsHashed() throws Exception {

        post(
            "/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            User user = User.findFirst("name = ?", "testuser");

            assertNotNull(user);

            String storedPassword = user.getString("password");

            assertNotEquals("1234", storedPassword);

            assertTrue(storedPassword.startsWith("$2"));

        } finally {

            Base.close();
        }
    }

    @Test
    void testLogin_OK() throws Exception {

        post(
            "/user/new",
            "name=testuser&password=1234&dni=123&realName=Juan&surname=Perez&correo=test@test.com"
        );

        HttpResponse<String> res = post(
            "/login",
            "username=testuser&password=1234"
        );

        assertEquals(302, res.statusCode());

        assertFalse(res.body().contains("Usuario o contraseña incorrectos"));
    }

    @Test
    void testLogin_FAIL_wrongCredentials() throws Exception {

        HttpResponse<String> res = post(
            "/login",
            "username=wrong&password=wrong"
        );

        assertEquals(200, res.statusCode());

        assertEquals(200, res.statusCode());
    }

    // =========================================================
    // DOCENTES
    // =========================================================

    @Test
    void testCreateDocente_OK() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec(" INSERT INTO carrera (id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES (1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')");

        } finally {

            Base.close();
        }

        HttpResponse<String> res = post(
            "/get_docente",
            "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com&telefono=358000000"
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("Docente cargado exitosamente"));

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Persona persona = Persona.findFirst("dni = ?", 123);
            Docente docente = Docente.findFirst("dni = ?", 123);
            Materia materia = Materia.findFirst("nombreMateria = ?", "IS1");
            Dictado dictado = Dictado.findFirst("dniDocente = ?", 123);

            assertNotNull(persona);
            assertNotNull(docente);
            assertNotNull(materia);
            assertNotNull(dictado);

        } finally {

            Base.close();
        }
    }

    @Test
    void testCreateDocente_validationError() throws Exception {

        HttpResponse<String> res = post(
            "/get_docente",
            "dni=&realName=&surname=&nombreMateria=&id_carrera=&departament="
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(
            location.contains("Todos los campos son obligatorios.")
        );
    }

    @Test
    void testCreateDocente_duplicateDni() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec("INSERT INTO carrera (id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES (1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas') ");

        } finally {

            Base.close();
        }

        post(
            "/get_docente",
            "dni=123&realName=Juan&surname=Perez&nombreMateria=IS1&id_carrera=1&departament=CS&correo=test@test.com&telefono=358000000"
        );

        post(
            "/get_docente",
            "dni=123&realName=Juan&surname=Perez&nombreMateria=IS2&id_carrera=1&departament=CS&correo=test@test.com&telefono=358000000"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            long docentes = Docente.find("dni = ?", 123).size();

            assertEquals(1, docentes);

        } finally {

            Base.close();
        }
    }

    @Test
    void testManageDocentes_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res =
            get("/admin/docentes");

        assertEquals(200, res.statusCode());

        String body = res.body();

        assertTrue(body.contains("Juan"));
        assertTrue(body.contains("Perez"));
    }

    @Test
    void testManageDocentes_empty() throws Exception {

        HttpResponse<String> res =
            get("/admin/docentes");

        assertEquals(200, res.statusCode());
    }

    @Test
    void testManageCarreras_OK() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Base.exec(
                "INSERT INTO carrera " +
                "(id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES " +
                "(1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')"
            );
        } finally {
            Base.close();
        }

        String cookie = loginAdmin();

        HttpResponse<String> res = get("/admin/carreras", cookie);

        assertEquals(200, res.statusCode());
        assertTrue(res.body().contains("Ingenieria en Sistemas"));
    }

    @Test
    void testCreateCarrera_OK() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=4&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Carrera carrera = Carrera.findFirst(
                "nombreCarrera = ?",
                "Licenciatura"
            );

            assertNotNull(carrera);
            assertEquals("FCEFyN", carrera.getString("facultad"));
            assertEquals(4, carrera.getInteger("duracion"));
        } finally {
            Base.close();
        }
    }

    @Test
    void testDeleteCarrera_OK() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Base.exec(
                "INSERT INTO carrera " +
                "(id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES " +
                "(1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')"
            );
        } finally {
            Base.close();
        }

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/delete",
            "id_carrera=1",
            cookie
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Carrera carrera = Carrera.findFirst("id_carrera = ?", 1);
            assertEquals(null, carrera);
        } finally {
            Base.close();
        }
    }

    @Test
    void testCreateCarrera_missingNombre() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=&facultad=FCEFyN&duracion=4&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
        assertTrue(location.contains("Nombre"));
    }

    @Test
    void testCreateCarrera_missingFacultad() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=&duracion=4&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
        assertTrue(location.contains("Facultad"));
    }

    @Test
    void testCreateCarrera_missingDuracion() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
        assertTrue(location.contains("Duraci"));
    }

    @Test
    void testCreateCarrera_missingTitulo() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=4&titulo=",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
        assertTrue(location.contains("T"));
    }

    @Test
    void testCreateCarrera_duracionZero() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=0&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testCreateCarrera_duracionNegativa() throws Exception {

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/create",
            "nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=-1&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testEditCarrera_missingFields() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Base.exec(
                "INSERT INTO carrera " +
                "(id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES " +
                "(1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')"
            );
        } finally {
            Base.close();
        }

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/edit",
            "id_carrera=1&nombreCarrera=Licenciatura&facultad=&duracion=4&titulo=",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
        assertTrue(location.contains("edit"));
    }

    @Test
    void testEditCarrera_invalidDuracion() throws Exception {

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {
            Base.exec(
                "INSERT INTO carrera " +
                "(id_carrera, nombreCarrera, facultad, duracion, titulo) VALUES " +
                "(1, 'Ingenieria en Sistemas', 'FCEFyN', 5, 'Ing. en Sistemas')"
            );
        } finally {
            Base.close();
        }

        String cookie = loginAdmin();

        HttpResponse<String> res = post(
            "/admin/carreras/edit",
            "id_carrera=1&nombreCarrera=Licenciatura&facultad=FCEFyN&duracion=abc&titulo=Licenciado",
            cookie
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testViewDocente_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res =
            get("/admin/docentes/view/123");

        assertEquals(200, res.statusCode());

        String body = res.body();

        assertTrue(body.contains("Juan"));
        assertTrue(body.contains("Perez"));
    }

    @Test
    void testEditDocenteView_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res =
            get("/admin/docentes/edit/123");

        assertEquals(200, res.statusCode());

        assertTrue(res.body().contains("Juan"));
    }

    @Test
    void testAsignarMateriaView_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res =
            get("/admin/docentes/asignar/123");

        assertEquals(200, res.statusCode());

        assertTrue(res.body().contains("IS1"));
    }

    @Test
    void testEditDocente_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res = post(
            "/admin/docentes/edit",
            "dni=123" +
            "&realName=Carlos" +
            "&surname=Lopez" +
            "&correo=carlos@test.com" +
            "&telefono=111" +
            "&departament=Matematica" +
            "&cuil=20-999"
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Persona persona =
                Persona.findFirst("dni = ?", 123);

            Docente docente =
                Docente.findFirst("dni = ?", 123);

            assertEquals(
                "Carlos",
                persona.getString("realName")
            );

            assertEquals(
                "Matematica",
                docente.getString("departament")
            );

        } finally {

            Base.close();
        }
    }

    @Test
    void testEditDocente_validationError() throws Exception {

        HttpResponse<String> res = post(
            "/admin/docentes/edit",
            "dni=&realName="
        );

        assertEquals(302, res.statusCode());

        String location =
            res.headers()
                .firstValue("Location")
                .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testDeleteDocente_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res = post(
            "/admin/docentes/delete",
            "dni=123"
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Docente docente =
                Docente.findFirst("dni = ?", 123);

            assertEquals(null, docente);

        } finally {

            Base.close();
        }
    }

    @Test
    void testDeleteDocente_missingDni() throws Exception {

        HttpResponse<String> res = post(
            "/admin/docentes/delete",
            ""
        );

        assertEquals(302, res.statusCode());

        String location =
            res.headers()
                .firstValue("Location")
                .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testAsignarMateria_OK() throws Exception {

        crearDocenteBase();

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec(
                "INSERT INTO materia " +
                "(id_materia, nombreMateria, anio, cuatrimestre, carga_horaria) " +
                "VALUES " +
                "(2, 'BD', 2, 1, 64)"
            );

        } finally {

            Base.close();
        }

        HttpResponse<String> res = post(
            "/admin/docentes/asignar",
            "dni=123" +
            "&id_materia=2" +
            "&cargo=AYUDANTE" +
            "&dedicacion=SIMPLE" +
            "&fechaInicio=2025-01-01"
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Dictado d =
                Dictado.findFirst(
                    "dniDocente = ? AND id_materia = ?",
                    123,
                    2
                );

            assertNotNull(d);

        } finally {

            Base.close();
        }
    }

    @Test
    void testAsignarMateria_invalidData() throws Exception {

        HttpResponse<String> res = post(
            "/admin/docentes/asignar",
            "dni=a"
        );

        assertEquals(302, res.statusCode());

        String location =
            res.headers()
                .firstValue("Location")
                .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testDesasignarMateria_OK() throws Exception {

        crearDocenteBase();

        HttpResponse<String> res = post(
            "/admin/docentes/desasignar",
            "dni=123&id_materia=1"
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Dictado d =
                Dictado.findFirst(
                    "dniDocente = ? AND id_materia = ?",
                    123,
                    1
                );

            assertEquals(null, d);

        } finally {

            Base.close();
        }
    }

    @Test
    void testDesasignarMateria_missingData() throws Exception {

        HttpResponse<String> res = post(
            "/admin/docentes/desasignar",
            ""
        );

        assertEquals(302, res.statusCode());

        String location =
            res.headers()
                .firstValue("Location")
                .orElse("");

        assertTrue(location.contains("error"));
    }

    @Test
    void testDocenteMaterias_OK() throws Exception {

        crearDocenteBase();

        post(
            "/user/new",
            "name=docente2&password=1234&dni=123&realName=Juan&surname=Perez&correo=juan@test.com"
        );

        HttpResponse<String> loginRes = post(
            "/login",
            "username=docente2&password=1234"
        );

        String cookie = loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/materias"))
            .header("Cookie", cookie)
            .GET()
            .build();

        HttpResponse<String> res =
            client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

        assertEquals(200, res.statusCode());

        assertTrue(res.body().contains("IS1"));
    }

    @Test
    void testAlumnosMateria_OK() throws Exception {

        crearDocenteBase();

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec(
                "INSERT INTO persona " +
                "(dni, realName, surname, telefono, correo) " +
                "VALUES " +
                "(500, 'Ana', 'Gomez', '123', 'ana@test.com')"
            );

            Base.exec(
                "INSERT INTO estudiante " +
                "(dni, legajo, fecha_ingreso) " +
                "VALUES " +
                "(500, 1000, '2024-01-01')"
            );

            Base.exec(
                "INSERT INTO cursado " +
                "(dniEstudiante, id_materia, fechaInscripcion, estado, notaFinal) " +
                "VALUES " +
                "(500, 1, '2024-01-01', 'REGULAR', 8)"
            );

        } finally {

            Base.close();
        }

        HttpResponse<String> res =
            get("/docente/alumnos/1");

        assertEquals(200, res.statusCode());

        assertTrue(res.body().contains("Ana"));
    }

    @Test
    void testProfile_OK() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil"))
            .header("Cookie", cookie)
            .GET()
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, res.statusCode());

        String body = res.body();

        assertTrue(body.contains("Juan"));
        assertTrue(body.contains("Perez"));
        assertTrue(body.contains("20-123"));
    }

    @Test
    void testEditProfileView_OK() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil/editar"))
            .header("Cookie", cookie)
            .GET()
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, res.statusCode());

        assertTrue(res.body().contains("Editar mis datos"));
        assertTrue(res.body().contains("Juan"));
    }

    @Test
    void testUpdateProfile_OK() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil/editar"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString(
                "dni=123" +
                "&realName=Carlos" +
                "&surname=Lopez" +
                "&correo=carlos@test.com" +
                "&telefono=999" +
                "&departament=Matematica" +
                "&cuil=20-999"
            ))
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Persona persona =
                Persona.findFirst("dni = ?", 123);

            Docente docente =
                Docente.findFirst("dni = ?", 123);

            assertEquals(
                "Carlos",
                persona.getString("realName")
            );

            assertEquals(
                "Matematica",
                docente.getString("departament")
            );

        } finally {

            Base.close();
        }
    }

    @Test
    void testUpdateProfile_changePassword_OK() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil/editar"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString(
                "dni=123" +
                "&realName=Juan" +
                "&surname=Perez" +
                "&correo=juan@test.com" +
                "&telefono=358" +
                "&departament=CS" +
                "&cuil=20-123" +
                "&password=nueva123" +
                "&password_confirm=nueva123"
            ))
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            User user =
                User.findFirst("dni = ?", 123);

            assertNotNull(user);

            String hashed =
                user.getString("password");

            assertNotEquals("nueva123", hashed);

            assertTrue(
                hashed.startsWith("$2")
            );

        } finally {

            Base.close();
        }
    }

    @Test
    void testUpdateProfile_changePassword_FAIL() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil/editar"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString(
                "dni=123" +
                "&realName=Juan" +
                "&surname=Perez" +
                "&correo=juan@test.com" +
                "&telefono=358" +
                "&departament=CS" +
                "&cuil=20-123" +
                "&password=abc" +
                "&password_confirm=xyz"
            ))
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(
            location.contains("Las contraseñas no coinciden")
        );
    }

    @Test
    void testUpdateProfile_validationError() throws Exception {

        String cookie = loginDocente();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/docente/perfil/editar"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString(
                "dni=123&realName="
            ))
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("error"));
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @Test
    void testDashboard_redirectIfNotLogged() throws Exception {

        HttpResponse<String> res = get("/dashboard");

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("/login")
            || location.contains("/?error="));
    }

    @Test
    void testLogout() throws Exception {

        HttpResponse<String> res = get("/logout");

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("/"));
    }

    // =========================================================
    // ADMIN
    // =========================================================

    @Test
    void testAdminLogin_redirectsToPanel() throws Exception {

        post(
            "/user/new",
            "name=adminuser&password=1234&dni=123456&realName=Admin&surname=User&correo=admin@test.com"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec("INSERT INTO administrador (dni) VALUES (123456) ");

        } finally {

            Base.close();
        }

        HttpResponse<String> res = post(
            "/login",
            "username=adminuser&password=1234"
        );

        assertEquals(302, res.statusCode());

        String location = res.headers()
            .firstValue("Location")
            .orElse("");

        assertTrue(location.contains("/admin/panel"));
    }

    @Test
    void testAdminPanel_access() throws Exception {

        post(
            "/user/new",
            "name=adminuser2&password=1234&dni=123457&realName=Admin2&surname=User2&correo=admin2@test.com"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec("INSERT INTO administrador (dni) VALUES (123457) ");

        } finally {

            Base.close();
        }

        HttpResponse<String> loginRes = post(
            "/login",
            "username=adminuser2&password=1234"
        );

        String cookie = loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/admin/panel"))
            .header("Cookie", cookie)
            .GET()
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, res.statusCode());
    }

    @Test
    void testAdminCreate_OK() throws Exception {

        post(
            "/user/new",
            "name=existingadmin&password=1234&dni=111111&realName=Existing&surname=Admin&correo=existing@admin.com"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Base.exec("INSERT INTO administrador (dni) VALUES (111111) ");

        } finally {

            Base.close();
        }

        HttpResponse<String> loginRes = post(
            "/login",
            "username=existingadmin&password=1234"
        );

        String cookie = loginRes.headers()
            .firstValue("Set-Cookie")
            .orElse("");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/admin/create"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString(
                "username=newadmin" +
                "&password=567890" +
                "&passwordConfirm=567890" +
                "&dni=222222" +
                "&realName=New" +
                "&surname=Admin" +
                "&telefono=123456789" +
                "&correo=new@admin.com"
            ))
            .build();

        HttpResponse<String> res = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(302, res.statusCode());

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            User user = User.findFirst("name = ?", "newadmin");
            Administrador admin = Administrador.findFirst("dni = ?", 222222);

            assertNotNull(user);
            assertNotNull(admin);

        } finally {

            Base.close();
        }

    }

    @Test
    void testCreateMateria() throws Exception {

        post(
            "/admin/materias/new",
            "nombreMateria=IS2" +
            "&anio=2" +
            "&cuatrimestre=1" +
            "&carga_horaria=96" +
            "&id_carrera=1"
        );

        Base.open(DB_DRIVER, DB_URL, "", "");

        try {

            Materia materia =
                Materia.findFirst(
                    "nombreMateria = ?",
                    "IS2"
                );

            assertNotNull(materia);

        } finally {

            Base.close();
        }
    }
}