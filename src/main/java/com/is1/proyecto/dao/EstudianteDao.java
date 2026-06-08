package com.is1.proyecto.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.mindrot.jbcrypt.BCrypt;

import com.is1.proyecto.models.Correlatividad;
import com.is1.proyecto.models.Cursado;
import com.is1.proyecto.models.Estudiante;
import com.is1.proyecto.models.Inscripcion;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.PlanMateria;
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

   public List<Map<String,Object>>
    obtenerMateriasCursadas(Integer dni) {

        List<Map<String,Object>> materias =
                new ArrayList<>();

        List<Cursado> cursados =
                Cursado.where(
                    "dniEstudiante = ?",
                    dni
                );

        for (Cursado c : cursados) {

            Materia materia =
                    Materia.findById(
                        c.getIdMateria()
                    );

            if (materia == null) {

                System.out.println(
                    "Materia inexistente: "
                    + c.getIdMateria()
                );

                continue;
            }

            Map<String,Object> data =
                    new HashMap<>();

            data.put(
                "id_materia",
                materia.getIdMateria()
            );

            data.put(
                "nombreMateria",
                materia.getNombreMateria()
            );

            data.put(
                "estado",
                c.getEstado()
            );

            data.put(
                "notaFinal",
                c.getNotaFinal()
            );

            materias.add(data);
        }

        return materias;
    }

    public List<Map<String,Object>>
    obtenerMateriasDisponibles(Integer dni) {

        List<Map<String,Object>> materias =
                new ArrayList<>();

        Inscripcion inscripcion =
                Inscripcion.findFirst(
                    "dniEstudiante = ?",
                    dni
                );

        if (inscripcion == null) {
            return materias;
        }

        Integer idPlan =
                inscripcion.getIdPlan();

        List<PlanMateria> planMaterias =
                PlanMateria.where(
                    "id_plan = ?",
                    idPlan
                );

        for (PlanMateria pm : planMaterias) {

            Integer idMateria =
                    pm.getIdMateria();

            /*
            * Ya cursada
            */
            Cursado cursado =
                    Cursado.findFirst(
                        "dniEstudiante = ? AND id_materia = ?",
                        dni,
                        idMateria
                    );

            if (cursado != null) {
                continue;
            }

            /*
            * Tiene correlativas?
            */
            Correlatividad correlativa =
                    Correlatividad.findFirst(
                        "id_materia = ?",
                        idMateria
                    );

            if (correlativa != null) {
                continue;
            }

            Materia materia =
                    Materia.findById(
                        idMateria
                    );

            if (materia == null) {
                continue;
            }

            Map<String,Object> data =
                    new HashMap<>();

            data.put(
                "id_materia",
                materia.getIdMateria()
            );

            data.put(
                "nombreMateria",
                materia.getNombreMateria()
            );

            data.put(
                "anio",
                materia.getAnio()
            );

            data.put(
                "cuatrimestre",
                materia.getCuatrimestre()
            );

            materias.add(data);
        }

        return materias;
    }

        private boolean cumpleCorrelativas(
            Integer dni,
            Integer idMateria) {

        List<Correlatividad> correlativas =
                Correlatividad.where(
                    "id_materia = ?",
                    idMateria
                );

        for (Correlatividad correlativa
                : correlativas) {

            Cursado cursado =
                    Cursado.findFirst(
                        "dniEstudiante = ? AND id_materia = ?",
                        dni,
                        correlativa.getIdCorrelativa()
                    );

            if (cursado == null) {
                return false;
            }

            String estado =
                    cursado.getEstado();

            String condicion =
                    correlativa.getCondicion();

            if ("REGULAR".equals(condicion)) {

                if (
                    !estado.equals("REGULAR") &&
                    !estado.equals("PROMOCIONADA") &&
                    !estado.equals("APROBADA")
                ) {
                    return false;
                }
            }

            if ("APROBADA".equals(condicion)) {

                if (
                    !estado.equals("PROMOCIONADA") &&
                    !estado.equals("APROBADA")
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    public void inscribirMateria(
            Integer dni,
            Integer idMateria) {

        Cursado existente =
                Cursado.findFirst(
                    "dniEstudiante = ? AND id_materia = ?",
                    dni,
                    idMateria
                );

        if (existente != null) {
            return;
        }

        Cursado cursado =
                new Cursado();

        cursado.setDniEstudiante(dni);

        cursado.setIdMateria(idMateria);

        cursado.setFechaInscripcion(
            java.time.LocalDate.now()
                .toString()
        );

        cursado.setEstado("LIBRE");

        cursado.saveIt();
    }


}