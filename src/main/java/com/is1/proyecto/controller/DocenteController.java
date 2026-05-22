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

    public static ModelAndView manageDocentes(Request req, Response res) {

        Map<String, Object> model = new HashMap<>();

        model.put("docentes", docenteDao.obtenerDocentes());
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "docente/admin_docente_list.mustache"
        );
    }

    public static ModelAndView deleteDocente(Request req, Response res) {

        try {
            String dniStr = req.queryParams("dni");

            if (dniStr == null || dniStr.isBlank()) {
                throw new IllegalArgumentException("DNI requerido");
            }

            Integer dni = Integer.valueOf(dniStr);

            docenteDao.eliminarDocente(dni);

            res.redirect("/admin/docentes?message=Docente eliminado");

        } catch (Exception e) {
            res.redirect("/admin/docentes?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView viewDocente(Request req, Response res) {

        Integer dni = Integer.valueOf(req.params(":dni"));

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("docente", docenteDao.obtenerDatosDocente(dni));
        model.put("materias", docenteDao.obtenerMateriasDocente(dni));

        return new ModelAndView(
            model,
            "docente/admin_view_docente.mustache"
        );
    }

    public static ModelAndView editDocenteView(Request req, Response res) {

        Integer dni = Integer.valueOf(req.params(":dni"));

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("docente", docenteDao.obtenerDatosDocente(dni));

        return new ModelAndView(
            model,
            "docente/admin_edit_docente.mustache"
        );
    }

    public static ModelAndView asignarMateriaView(Request req, Response res) {

        Integer dni = Integer.valueOf(req.params(":dni"));

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("docente", docenteDao.obtenerDatosDocente(dni));
        model.put("materias", docenteDao.obtenerTodasMaterias());
        model.put("materiasAsignadas", docenteDao.obtenerMateriasDocente(dni));

        return new ModelAndView(
            model,
            "docente/admin_asignar_materia.mustache"
        );
    }

    public static ModelAndView editDocente(Request req, Response res) {

        try {
            docenteDao.editarDocente(req);

            res.redirect("/admin/docentes?message=Docente actualizado");

        } catch (Exception e) {
            res.redirect("/admin/docentes?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView asignarMateria(Request req, Response res) {

        try {
            Integer dni = Integer.valueOf(req.queryParams("dni"));
            Integer idMateria = Integer.valueOf(req.queryParams("id_materia"));
            String cargo = req.queryParams("cargo");
            String dedicacion = req.queryParams("dedicacion");
            String fechaInicio = req.queryParams("fechaInicio");
            String fechaFin = req.queryParams("fechaFin");

            docenteDao.asignarMateria(
                dni,
                idMateria,
                cargo,
                dedicacion,
                fechaInicio,
                fechaFin
            );

            res.redirect("/admin/docentes?message=Materia asignada");

        } catch (Exception e) {
            res.redirect("/admin/docentes?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView desasignarMateria(Request req, Response res) {

        try {
            String dniStr = req.queryParams("dni");
            String idMateriaStr = req.queryParams("id_materia");

            if (dniStr == null || dniStr.isBlank() ||
                idMateriaStr == null || idMateriaStr.isBlank()) {
                res.redirect("/admin/docentes?error=Faltan datos para desasignar materia");
                return null;
            }

            Integer dni = Integer.valueOf(dniStr);
            Integer idMateria = Integer.valueOf(idMateriaStr);

            docenteDao.desasignarMateria(dni, idMateria);

            res.redirect("/admin/docentes?message=Materia desasignada");

        } catch (Exception e) {
            res.redirect("/admin/docentes?error=" + e.getMessage());
        }

        return null;
    }

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
                "/admin/docentes?message=Docente cargado exitosamente"
            );

        } catch (Exception e) {

            res.redirect(
                "/get_docente?error=" +
                e.getMessage()
            );
        }

        return null;
    }
}