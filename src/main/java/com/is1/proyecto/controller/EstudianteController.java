package com.is1.proyecto.controller;

import java.util.HashMap;
import java.util.Map;

import com.is1.proyecto.dao.EstudianteDao;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class EstudianteController {

    private static EstudianteDao estudianteDao = new EstudianteDao();

    public static ModelAndView profile(Request req, Response res) {

        Boolean loggedIn = req.session().attribute("loggedIn");
        Boolean isDocente = req.session().attribute("isDocente");
        Boolean isAdmin = req.session().attribute("isAdmin");

        if (loggedIn == null || !loggedIn || Boolean.TRUE.equals(isDocente) || Boolean.TRUE.equals(isAdmin)) {
            res.redirect("/?error=Acceso no autorizado");
            return null;
        }

        Integer dni = req.session().attribute("dni");

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("estudiante", estudianteDao.obtenerDatosEstudiante(dni));
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(model, "estudiante/profile.mustache");
    }

    public static ModelAndView editProfileView(Request req, Response res) {

        Boolean loggedIn = req.session().attribute("loggedIn");
        Boolean isDocente = req.session().attribute("isDocente");
        Boolean isAdmin = req.session().attribute("isAdmin");

        if (loggedIn == null || !loggedIn || Boolean.TRUE.equals(isDocente) || Boolean.TRUE.equals(isAdmin)) {
            res.redirect("/?error=Acceso no autorizado");
            return null;
        }

        Integer dni = req.session().attribute("dni");

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("estudiante", estudianteDao.obtenerDatosEstudiante(dni));

        return new ModelAndView(model, "estudiante/edit_profile.mustache");
    }

    public static ModelAndView updateProfile(Request req, Response res) {

        Boolean loggedIn = req.session().attribute("loggedIn");
        Boolean isDocente = req.session().attribute("isDocente");
        Boolean isAdmin = req.session().attribute("isAdmin");

        if (loggedIn == null || !loggedIn || Boolean.TRUE.equals(isDocente) || Boolean.TRUE.equals(isAdmin)) {
            res.redirect("/?error=Acceso no autorizado");
            return null;
        }

        try {
            estudianteDao.editarEstudiante(req);

            res.redirect("/estudiante/perfil?message=Datos actualizados correctamente");

        } catch (Exception e) {
            res.redirect("/estudiante/perfil?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView materias(
            Request req,
            Response res) {

        Boolean loggedIn =
                req.session().attribute("loggedIn");

        Boolean isDocente =
                req.session().attribute("isDocente");

        Boolean isAdmin =
                req.session().attribute("isAdmin");

        if (
            loggedIn == null ||
            !loggedIn ||
            Boolean.TRUE.equals(isDocente) ||
            Boolean.TRUE.equals(isAdmin)
        ) {
            res.redirect("/?error=Acceso no autorizado");
            return null;
        }

        Integer dni =
                req.session().attribute("dni");

        Map<String,Object> model =
                new HashMap<>();

        model.put(
            "materiasCursadas",
            estudianteDao.obtenerMateriasCursadas(dni)
        );

        model.put(
            "materiasDisponibles",
            estudianteDao.obtenerMateriasDisponibles(dni)
        );

        return new ModelAndView(
            model,
            "estudiante/materias.mustache"
        );
    }

    public static Object inscribirMateria(
            Request req,
            Response res) {

        Integer dni =
                req.session().attribute("dni");

        Integer idMateria =
                Integer.valueOf(
                    req.queryParams("id_materia")
                );

        estudianteDao.inscribirMateria(
            dni,
            idMateria
        );

        res.redirect("/estudiante/materias");

        return null;
    }
}