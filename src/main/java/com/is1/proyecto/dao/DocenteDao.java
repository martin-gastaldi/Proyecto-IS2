package com.is1.proyecto.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.mindrot.jbcrypt.BCrypt;

import com.is1.proyecto.models.Cursado;
import com.is1.proyecto.models.Dictado;
import com.is1.proyecto.models.Docente;
import com.is1.proyecto.models.Estudiante;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

import spark.Request;

public class DocenteDao {

    public void crearDocente(Request req) {

    String dniStr = req.queryParams("dni");
    String realName = req.queryParams("realName");
    String surname = req.queryParams("surname");
    String departament = req.queryParams("departament");
    String nombreMateria = req.queryParams("nombreMateria");
    String correo = req.queryParams("correo");
    String telefono = req.queryParams("telefono");
    String username = req.queryParams("username");
    String password = req.queryParams("password");

    if (
        dniStr == null || dniStr.isBlank() ||
        realName == null || realName.isBlank() ||
        surname == null || surname.isBlank() ||
        departament == null || departament.isBlank() ||
        nombreMateria == null || nombreMateria.isBlank()
    ) {

        throw new IllegalArgumentException(
            "Todos los campos son obligatorios."
        );
    }

    Integer dni = Integer.valueOf(dniStr);

    boolean personaExists = Base.count(
            "persona",
            "dni = ?",
            dni
    ) > 0;

    if (personaExists) {
        Base.exec(
            "UPDATE persona SET realName = ?, surname = ?, telefono = ?, correo = ? WHERE dni = ?",
            realName,
            surname,
            telefono,
            correo,
            dni
        );
    } else {
        Base.exec(
            "INSERT INTO persona (dni, realName, surname, telefono, correo) VALUES (?, ?, ?, ?, ?)",
            dni,
            realName,
            surname,
            telefono,
            correo
        );
    }

    boolean docenteExists = Base.count(
            "docente",
            "dni = ?",
            dni
    ) > 0;

    if (docenteExists) {
        Base.exec(
            "UPDATE docente SET departament = ? WHERE dni = ?",
            departament,
            dni
        );
    } else {
        Base.exec(
            "INSERT INTO docente (dni, departament) VALUES (?, ?)",
            dni,
            departament
        );
    }

    Materia materia = Materia.findFirst(
            "nombreMateria = ?",
            nombreMateria
    );

    if (materia == null) {

        materia = new Materia();

       materia.set("nombreMateria", nombreMateria);

        materia.set("anio", 1);

        materia.set("cuatrimestre", 1);

        materia.set("carga_horaria", 64);

        String idCarreraStr =
                req.queryParams("id_carrera");

        if (idCarreraStr != null
                && !idCarreraStr.isEmpty()) {

            materia.set(
                "id_carrera",
                Integer.valueOf(idCarreraStr)
            );
        }
        System.out.println(
            "Materia ID: " +
            materia.getInteger("id_materia")
        );

        materia.saveIt();
            System.out.println(
            "Materia creada: " +
            materia.toString()
        );
    }

    Integer idMateria = materia.getInteger("id_materia");

    System.out.println("ID materia: " + idMateria);

    Dictado dictado = Dictado.findFirst(
            "dniDocente = ? AND id_materia = ?",
            dni,
            idMateria
    );

    if (dictado == null) {

        dictado = new Dictado();

        dictado.set("dniDocente", dni);

        dictado.set("id_materia", idMateria);

        dictado.set("cargo", "TITULAR");

        dictado.set("dedicacion", "SIMPLE");

        dictado.set("fechaInicio", "2024-01-01");

        dictado.saveIt();
    }
    if (
        username != null && !username.isBlank() &&
        password != null && !password.isBlank()
    ) {

        User user = User.findFirst(
                "dni = ?",
                dni
        );

        if (user == null) {
            user = new User();
            user.set("dni", dni);
        }

        user.set("name", username);
        user.set(
            "password",
            BCrypt.hashpw(
                password,
                BCrypt.gensalt()
            )
        );

        user.saveIt();
    }

    }

    public List<Map<String, Object>> obtenerDocentes() {

        List<Map<String, Object>> docentesList =
                new ArrayList<>();

        List<Docente> docentes = Docente.findAll();

        for (Docente docente : docentes) {

            Map<String, Object> data =
                    new HashMap<>();

            Integer dni = docente.getInteger("dni");

            Persona persona = Persona.findFirst(
                    "dni = ?",
                    dni
            );

            Dictado dictado = Dictado.findFirst(
                    "dniDocente = ?",
                    dni
            );

            Materia materia = null;

            if (dictado != null) {

                materia = Materia.findById(
                        dictado.getInteger("id_materia")
                );
            }

            data.put("dni", dni);

            if (persona != null) {

                data.put(
                        "realName",
                        persona.getString("realName")
                );

                data.put(
                        "surname",
                        persona.getString("surname")
                );

                data.put(
                        "correo",
                        persona.getString("correo")
                );
            }

            data.put(
                    "departament",
                    docente.getString("departament")
            );

            if (materia != null) {

                data.put(
                        "idMateria",
                        materia.getInteger("id_materia")
                );

                data.put(
                        "nombreMateria",
                        materia.getString("nombreMateria")
                );

                data.put(
                        "anio",
                        materia.getInteger("anio")
                );

                data.put(
                        "cuatrimestre",
                        materia.getInteger("cuatrimestre")
                );
            }

            docentesList.add(data);
        }

        return docentesList;
    }
    public Map<String, Object> obtenerDatosDocente(Integer dni) {

        Map<String, Object> data = new HashMap<>();

        Persona persona = Persona.findFirst(
                "dni = ?", dni
        );

        Docente docente = Docente.findFirst(
                "dni = ?", dni
        );

        if (persona != null) {
            data.put(
                "nombre",
                persona.getString("realName")
            );

            data.put(
                "apellido",
                persona.getString("surname")
            );

            data.put(
                "correo",
                persona.getString("correo")
            );

            data.put(
                "telefono",
                persona.getString("telefono")
            );
        }

        if (docente != null) {
            data.put(
                "departamento",
                docente.getString("departament")
            );

            data.put(
                "cuil",
                docente.getString("cuil")
            );
        }

        return data;
    }

    public List<Map<String, Object>>
        obtenerMateriasDocente(Integer dni) {

            List<Map<String, Object>> materias =
                    new ArrayList<>();

            List<Dictado> dictados =
                    Dictado.where("dniDocente = ?", dni);

            for (Dictado d : dictados) {

                Materia m = Materia.findById(
                        d.getInteger("id_materia")
                );

                if (m == null) {
                    continue;
                }

                Map<String, Object> data =
                        new HashMap<>();

                data.put(
                    "id",
                    m.getInteger("id_materia")
                );

                data.put(
                    "id_materia",
                    m.getInteger("id_materia")
                );

                data.put(
                    "dni",
                    dni
                );

                data.put(
                    "cargo",
                    d.getString("cargo")
                );

                data.put(
                    "dedicacion",
                    d.getString("dedicacion")
                );

                data.put(
                    "fechaInicio",
                    d.getString("fechaInicio")
                );

                data.put(
                    "fechaFin",
                    d.getString("fechaFin")
                );

                data.put(
                    "nombreMateria",
                    m.getString("nombreMateria")
                );

                data.put(
                    "anio",
                    m.getInteger("anio")
                );

                data.put(
                    "cuatrimestre",
                    m.getInteger("cuatrimestre")
                );

                materias.add(data);
            }

            return materias;
        }

    public List<Map<String, Object>> obtenerAlumnosMateria(Integer idMateria) {

        List<Map<String, Object>> alumnos =
                new ArrayList<>();

        List<Cursado> cursados =
                Cursado.where("id_materia = ?", idMateria);

        for (Cursado c : cursados) {

            Integer dni = c.getInteger("dniEstudiante");

            Estudiante estudiante =
                    Estudiante.findFirst(
                        "dni = ?", dni
                    );

            Persona persona =
                    Persona.findFirst(
                        "dni = ?", dni
                    );

            Map<String, Object> data = new HashMap<>();

            data.put(
                "legajo",
                estudiante.getInteger("legajo")
            );

            data.put(
                "nombre",
                persona.getString("realName")
            );

            data.put(
                "apellido",
                persona.getString("surname")
            );

            data.put(
                "estado",
                c.getString("estado")
            );

            data.put(
                "notaFinal",
                c.get("notaFinal")
            );

            alumnos.add(data);
        }

        return alumnos;
    }

    public void eliminarDocente(Integer dni) {

        Base.exec(
            "DELETE FROM dictado WHERE dniDocente = ?",
            dni
        );

        Base.exec(
            "DELETE FROM docente WHERE dni = ?",
            dni
        );

        Base.exec(
            "DELETE FROM users WHERE dni = ?",
            dni
        );
    }

    public void editarDocente(Request req) {

        String dniStr = req.queryParams("dni");

        if (dniStr == null || dniStr.isBlank()) {
            throw new IllegalArgumentException("DNI requerido");
        }

        Integer dni = Integer.valueOf(dniStr);

        String realName = req.queryParams("realName");
        String surname = req.queryParams("surname");
        String correo = req.queryParams("correo");
        String telefono = req.queryParams("telefono");
        String departament = req.queryParams("departament");
        String cuil = req.queryParams("cuil");

        if (
                realName == null || realName.isBlank() ||
                surname == null || surname.isBlank() ||
                correo == null || correo.isBlank() ||
                telefono == null || telefono.isBlank() ||
                departament == null || departament.isBlank() ||
                cuil == null || cuil.isBlank()
        ) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        boolean personaExists = Base.count(
                "persona",
                "dni = ?",
                dni
        ) > 0;

        if (personaExists) {
            Base.exec(
                "UPDATE persona SET realName = ?, surname = ?, correo = ?, telefono = ? WHERE dni = ?",
                realName,
                surname,
                correo,
                telefono,
                dni
            );
        } else {
            Base.exec(
                "INSERT INTO persona (dni, realName, surname, correo, telefono) VALUES (?, ?, ?, ?, ?)",
                dni,
                realName,
                surname,
                correo,
                telefono
            );
        }

        boolean docenteExists = Base.count(
                "docente",
                "dni = ?",
                dni
        ) > 0;

        if (docenteExists) {
            Base.exec(
                "UPDATE docente SET departament = ?, cuil = ? WHERE dni = ?",
                req.queryParams("departament"),
                cuil,
                dni
            );
        } else {
            Base.exec(
                "INSERT INTO docente (dni, departament, cuil) VALUES (?, ?, ?)",
                dni,
                req.queryParams("departament"),
                cuil
            );
        }
    }

    public void asignarMateria(Integer dni,
                                Integer idMateria,
                                String cargo,
                                String dedicacion,
                                String fechaInicio,
                                String fechaFin) {

        Dictado dictado = Dictado.findFirst(
                "dniDocente = ? AND id_materia = ?",
                dni,
                idMateria
        );

        if (dictado == null) {
            dictado = new Dictado();
            dictado.set("dniDocente", dni);
            dictado.set("id_materia", idMateria);
        }

        dictado.set("cargo", cargo != null && !cargo.isBlank() ? cargo : "TITULAR");
        dictado.set("dedicacion", dedicacion != null && !dedicacion.isBlank() ? dedicacion : "SIMPLE");
        dictado.set("fechaInicio", fechaInicio != null && !fechaInicio.isBlank() ? fechaInicio : "2024-01-01");

        if (fechaFin != null && !fechaFin.isBlank()) {
            dictado.set("fechaFin", fechaFin);
        } else {
            dictado.set("fechaFin", null);
        }

        dictado.saveIt();
    }

    public void desasignarMateria(Integer dni, Integer idMateria) {

        Dictado dictado = Dictado.findFirst(
                "dniDocente = ? AND id_materia = ?",
                dni,
                idMateria
        );

        if (dictado != null) {
            Base.exec(
                "DELETE FROM dictado WHERE dniDocente = ? AND id_materia = ?",
                dni,
                idMateria
            );
        }
    }

    public List<Map<String, Object>> obtenerTodasMaterias() {

        List<Map<String, Object>> materiasList = new ArrayList<>();

        List<Materia> materias = Materia.findAll();

        for (Materia m : materias) {
            Map<String, Object> data = new HashMap<>();

            data.put("id_materia", m.getInteger("id_materia"));
            data.put("nombreMateria", m.getString("nombreMateria"));
            data.put("anio", m.getInteger("anio"));
            data.put("cuatrimestre", m.getInteger("cuatrimestre"));

            materiasList.add(data);
        }

        return materiasList;
    }
}