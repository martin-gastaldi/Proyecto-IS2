package com.is1.proyecto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.is1.proyecto.dao.DocenteDao;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class DocenteController {

    private static DocenteDao docenteDao =
            new DocenteDao();

    public static ModelAndView dashboard(Request req,
                                         Response res) {

        Integer dni = req.session().attribute("dni");

        Map<String, Object> docente =
                docenteDao.obtenerDatosDocente(dni);

        List<Map<String, Object>> materias =
                docenteDao.obtenerMateriasDocente(dni);

        Map<String, Object> model = new HashMap<>();

        model.put("docente", docente);
        model.put("materias", materias);

        return new ModelAndView(
                model,
                "docente/dashboard.mustache"
        );
    }

    public static ModelAndView materias(Request req,
                                        Response res) {

        Integer dni = req.session().attribute("dni");

        List<Map<String, Object>> materias =
                docenteDao.obtenerMateriasDocente(dni);

        Map<String, Object> model = new HashMap<>();

        model.put("materias", materias);

        return new ModelAndView(
            model,
            "docente/materias.mustache"
        );
    }

    public static ModelAndView alumnosMateria(Request req,
                                              Response res) {

        Integer idMateria =
                Integer.valueOf(req.params(":id"));

        List<Map<String, Object>> alumnos =
                docenteDao.obtenerAlumnosMateria(idMateria);

        Map<String, Object> model = new HashMap<>();

        model.put("alumnos", alumnos);

        return new ModelAndView(
            model,
            "docente/alumnos.mustache"
        );
    }

    public static ModelAndView getDocenteView(Request req,
                                              Response res) {

        Map<String, Object> model = new HashMap<>();

        model.put("successMessage",
                req.queryParams("message"));

        model.put("errorMessage",
                req.queryParams("error"));

        return new ModelAndView(
            model,
            "docente/get_docente.mustache"
        );
    }

    public static ModelAndView createDocente(Request req,
                                            Response res) {

        try {

            docenteDao.crearDocente(req);

            res.redirect(
                "/post_docente?message=Docente cargado exitosamente"
            );

        } catch (Exception e) {

            res.redirect(
                "/get_docente?error=" +
                e.getMessage()
            );
        }

        return null;
    }

    public static ModelAndView listDocentes(Request req,
                                            Response res) {

        Map<String, Object> model = new HashMap<>();

        model.put(
            "docentes",
            docenteDao.obtenerDocentes()
        );

        return new ModelAndView(
            model,
            "docente/post_docente.mustache"
        );
    }
}