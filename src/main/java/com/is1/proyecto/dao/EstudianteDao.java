package com.is1.proyecto.dao;

import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.mindrot.jbcrypt.BCrypt;

import com.is1.proyecto.models.Estudiante;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

import spark.Request;

public class EstudianteDao {

    public Map<String, Object> obtenerDatosEstudiante(Integer dni) {

        Map<String, Object> data = new HashMap<>();

        Persona persona = Persona.findFirst("dni = ?", dni);

        Estudiante estudiante = Estudiante.findFirst("dni = ?", dni);

        if (persona != null) {
            data.put("nombre", persona.getString("realName"));
            data.put("apellido", persona.getString("surname"));
            data.put("correo", persona.getString("correo"));
            data.put("telefono", persona.getString("telefono"));
        }

        if (estudiante != null) {
            data.put("legajo", estudiante.getInteger("legajo"));
            data.put("fecha_ingreso", estudiante.getString("fecha_ingreso"));
        }

        return data;
    }

    public void editarEstudiante(Request req) {

        String dniStr = req.queryParams("dni");

        if (dniStr == null || dniStr.isBlank()) {
            throw new IllegalArgumentException("DNI requerido");
        }

        Integer dni = Integer.valueOf(dniStr);

        String realName = req.queryParams("realName");
        String surname = req.queryParams("surname");
        String correo = req.queryParams("correo");
        String telefono = req.queryParams("telefono");

        if (
            realName == null || realName.isBlank() ||
            surname == null || surname.isBlank() ||
            correo == null || correo.isBlank() ||
            telefono == null || telefono.isBlank()
        ) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        boolean personaExists = Base.count("persona", "dni = ?", dni) > 0;

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

        String password = req.queryParams("password");
        String passwordConfirm = req.queryParams("password_confirm");

        if (password != null && !password.isBlank()) {

            if (!password.equals(passwordConfirm)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            User user = User.findFirst("dni = ?", dni);

            if (user == null) {
                throw new IllegalArgumentException("No existe usuario asociado.");
            }

            user.set("password", BCrypt.hashpw(password, BCrypt.gensalt()));

            user.saveIt();
        }
    }
}