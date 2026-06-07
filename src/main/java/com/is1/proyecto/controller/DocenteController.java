package com.is1.proyecto.controller;
import com.is1.proyecto.models.User;

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

    private static Integer obtenerDniDocenteLogueado(Request req) {

        String username =
                req.session()
                .attribute(
                        "currentUserUsername"
                );

        User user =
                User.findFirst(
                    "name = ?",
                    username
                );

        if(user == null){
            return null;
        }

        return user.getInteger("dni");
    }

    public static ModelAndView notasView(Request req,Response res){

        Integer dniDocente =
                obtenerDniDocenteLogueado(req);

        Map<String,Object> model =
                new HashMap<>();

        Integer idMateriaSeleccionada =
                null;

        String idMateriaParam =
                req.queryParams("id_materia");

        List<Map<String,Object>> materias =
                docenteDao.obtenerMateriasDocente(
                    dniDocente
                );

        if(idMateriaParam != null){

            idMateriaSeleccionada =
                    Integer.valueOf(
                        idMateriaParam
                    );

            for(Map<String,Object> materia : materias){

                Integer id =
                        (Integer) materia.get("id_materia");

                materia.put(
                    "selected",
                    id.equals(idMateriaSeleccionada)
                );
            }

            List<Map<String,Object>> alumnos =
                    docenteDao.obtenerAlumnosMateria(
                        idMateriaSeleccionada
                    );

            for(Map<String,Object> alumno : alumnos){

                String estado =
                        (String) alumno.get("estado");

                alumno.put(
                    "esLibre",
                    "LIBRE".equals(estado)
                );

                alumno.put(
                    "esRegular",
                    "REGULAR".equals(estado)
                );

                alumno.put(
                    "esPromocionada",
                    "PROMOCIONADA".equals(estado)
                );

                alumno.put(
                    "esAprobada",
                    "APROBADA".equals(estado)
                );
            }

            model.put(
                "alumnos",
                alumnos
            );
        }

        model.put(
            "materias",
            materias
        );

        model.put(
            "idMateriaSeleccionada",
            idMateriaSeleccionada
        );

        return new ModelAndView(
            model,
            "docente/notas.mustache"
        );
    }

    public static ModelAndView alumnosMateriaNotas(
            Request req,
            Response res){

        Integer idMateria =
                Integer.valueOf(
                    req.queryParams("id_materia")
                );

        Integer dniDocente =
                obtenerDniDocenteLogueado(req);

        Map<String,Object> model =
                new HashMap<>();

        List<Map<String,Object>> materias =
                docenteDao.obtenerMateriasDocente(
                    dniDocente
                );

        for(Map<String,Object> materia : materias){

            Integer id =
                    (Integer) materia.get("id_materia");

            materia.put(
                "selected",
                id.equals(idMateria)
            );
        }

        List<Map<String,Object>> alumnos =
                docenteDao.obtenerAlumnosMateria(
                    idMateria
                );

        for(Map<String,Object> alumno : alumnos){

            String estado =
                    (String) alumno.get("estado");

            alumno.put(
                "esLibre",
                "LIBRE".equals(estado)
            );

            alumno.put(
                "esRegular",
                "REGULAR".equals(estado)
            );

            alumno.put(
                "esPromocionada",
                "PROMOCIONADA".equals(estado)
            );

            alumno.put(
                "esAprobada",
                "APROBADA".equals(estado)
            );
        }

        model.put(
            "materias",
            materias
        );

        model.put(
            "idMateriaSeleccionada",
            idMateria
        );

        model.put(
            "alumnos",
            alumnos
        );

        return new ModelAndView(
            model,
            "docente/notas.mustache"
        );
    }


    public static Object guardarNota(
        Request req,
        Response res){
        System.out.println("dniEstudiante=" + req.queryParams("dniEstudiante"));
        System.out.println("id_materia=" + req.queryParams("id_materia"));
        System.out.println("notaFinal=" + req.queryParams("notaFinal"));
        System.out.println("estado=" + req.queryParams("estado"));

    docenteDao.actualizarNota(
        Integer.valueOf(
            req.queryParams(
                "dniEstudiante"
            )
        ),
        Integer.valueOf(
            req.queryParams(
                "id_materia"
            )
        ),
        Double.valueOf(
            req.queryParams(
                "notaFinal"
            )
        ),
        req.queryParams(
            "estado"
        )
    );

    res.redirect(
        "/docente/notas?id_materia="
        + req.queryParams(
            "id_materia"
        )
    );

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
        model.put("materiasCount", materias.size());

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

    public static ModelAndView profile(Request req,
                                       Response res) {

        Integer dni = req.session().attribute("dni");

        Map<String, Object> model = new HashMap<>();
        
        model.put("dni", dni);
        model.put("docente", docenteDao.obtenerDatosDocente(dni));
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "docente/profile.mustache"
        );
    }

    public static ModelAndView editProfileView(Request req,
                                               Response res) {

        Integer dni = req.session().attribute("dni");

        Map<String, Object> model = new HashMap<>();

        model.put("dni", dni);
        model.put("docente", docenteDao.obtenerDatosDocente(dni));

        return new ModelAndView(
            model,
            "docente/edit_profile.mustache"
        );
    }

    public static ModelAndView updateProfile(Request req,
                                             Response res) {

        try {
            docenteDao.editarDocente(req);

            res.redirect("/docente/perfil?message=Datos actualizados correctamente");

        } catch (Exception e) {
            res.redirect("/docente/perfil?error=" + e.getMessage());
        }

        return null;
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