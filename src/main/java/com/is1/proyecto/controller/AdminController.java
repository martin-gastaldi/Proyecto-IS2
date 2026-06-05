package com.is1.proyecto.controller;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.is1.proyecto.dao.AdminDao;
import com.is1.proyecto.models.Administrador;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AdminController {

    private static AdminDao adminDao = new AdminDao();

    public static ModelAndView dashboard(Request req,
                                         Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();

        model.put(
            "adminUsername",
            req.session().attribute("currentUserUsername")
        );

        return new ModelAndView(
            model,
            "admin_dashboard.mustache"
        );
    }

    public static ModelAndView manageCarreras(Request req,
                                              Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();

        model.put("carreras", adminDao.obtenerCarreras());
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_carrera_list.mustache"
        );
    }

    public static ModelAndView createCarreraView(Request req,
                                                 Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();

        model.put("formAction", "/admin/carreras/create");
        model.put("isEdit", false);
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_edit_carrera.mustache"
        );
    }

    public static ModelAndView editCarreraView(Request req,
                                               Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Integer id = Integer.valueOf(req.params(":id"));

        Map<String, Object> model = new HashMap<>();

        model.put("formAction", "/admin/carreras/edit");
        model.put("isEdit", true);
        model.put("carrera", adminDao.obtenerCarrera(id));
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_edit_carrera.mustache"
        );
    }

    public static ModelAndView createCarrera(Request req,
                                             Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String validationError = validarCamposCarrera(req);
            if (validationError != null) {
                String encoded = "";
                try {
                    encoded = URLEncoder.encode(validationError, "UTF-8");
                } catch (Exception ex) {
                    encoded = validationError.replace(" ", "%20");
                }
                res.redirect("/admin/carreras/new?error=" + encoded);
                return null;
            }

            adminDao.crearCarrera(req);
            res.redirect("/admin/carreras?message=Carrera creada");
        } catch (Exception e) {
            res.redirect("/admin/carreras/new?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView editCarrera(Request req,
                                           Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String validationError = validarCamposCarrera(req);
            if (validationError != null) {
                String id = req.queryParams("id_carrera");
                String encoded = "";
                try {
                    encoded = URLEncoder.encode(validationError, "UTF-8");
                } catch (Exception ex) {
                    encoded = validationError.replace(" ", "%20");
                }
                res.redirect("/admin/carreras/edit/" + id + "?error=" + encoded);
                return null;
            }

            adminDao.editarCarrera(req);
            res.redirect("/admin/carreras?message=Carrera actualizada");
        } catch (Exception e) {
            String id = req.queryParams("id_carrera");
            res.redirect("/admin/carreras/edit/" + id + "?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView deleteCarrera(Request req,
                                             Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String idStr = req.queryParams("id_carrera");

            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("ID de carrera requerido");
            }

            Integer id = Integer.valueOf(idStr);

            adminDao.eliminarCarrera(id);
            res.redirect("/admin/carreras?message=Carrera eliminada");
        } catch (Exception e) {
            res.redirect("/admin/carreras?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView managePlanes(Request req,
                                            Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();

        String carreraParam = req.queryParams("carrera");
        Integer selectedCarrera = null;

        if (carreraParam != null && !carreraParam.isBlank()) {
            try {
                selectedCarrera = Integer.valueOf(carreraParam);
            } catch (NumberFormatException ignored) {
                selectedCarrera = null;
            }
        }

        model.put("planes", adminDao.obtenerPlanes(selectedCarrera));
        model.put("carreras", adminDao.obtenerCarreras(selectedCarrera));
        model.put("selectedCarrera", selectedCarrera);
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_plan_list.mustache"
        );
    }

    public static ModelAndView createPlanView(Request req,
                                              Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();

        model.put("formAction", "/admin/planes/create");
        model.put("isEdit", false);
        model.put("carreras", adminDao.obtenerCarreras(null));
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_edit_plan.mustache"
        );
    }

    public static ModelAndView editPlanView(Request req,
                                            Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        Integer id = Integer.valueOf(req.params(":id"));
        Map<String, Object> plan = adminDao.obtenerPlan(id);

        Map<String, Object> model = new HashMap<>();

        model.put("formAction", "/admin/planes/edit");
        model.put("isEdit", true);
        model.put("plan", plan);
        model.put("carreras", adminDao.obtenerCarreras((Integer) plan.get("id_carrera")));
        model.put("successMessage", req.queryParams("message"));
        model.put("errorMessage", req.queryParams("error"));

        return new ModelAndView(
            model,
            "admin/admin_edit_plan.mustache"
        );
    }

    public static ModelAndView createPlan(Request req,
                                          Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String validationError = validarCamposPlan(req);
            if (validationError != null) {
                String encoded = "";
                try {
                    encoded = URLEncoder.encode(validationError, "UTF-8");
                } catch (Exception ex) {
                    encoded = validationError.replace(" ", "%20");
                }
                res.redirect("/admin/planes/new?error=" + encoded);
                return null;
            }

            adminDao.crearPlan(req);
            res.redirect("/admin/planes?message=Plan creado");
        } catch (Exception e) {
            res.redirect("/admin/planes/new?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView editPlan(Request req,
                                        Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String validationError = validarCamposPlan(req);
            if (validationError != null) {
                String id = req.queryParams("id_plan");
                String encoded = "";
                try {
                    encoded = URLEncoder.encode(validationError, "UTF-8");
                } catch (Exception ex) {
                    encoded = validationError.replace(" ", "%20");
                }
                res.redirect("/admin/planes/edit/" + id + "?error=" + encoded);
                return null;
            }

            adminDao.editarPlan(req);
            res.redirect("/admin/planes?message=Plan actualizado");
        } catch (Exception e) {
            String id = req.queryParams("id_plan");
            res.redirect("/admin/planes/edit/" + id + "?error=" + e.getMessage());
        }

        return null;
    }

    public static ModelAndView deletePlan(Request req,
                                          Response res) {

        if (!validarAdmin(req, res)) {
            return null;
        }

        try {
            String idStr = req.queryParams("id_plan");

            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("ID de plan requerido");
            }

            Integer id = Integer.valueOf(idStr);

            adminDao.eliminarPlan(id);
            res.redirect("/admin/planes?message=Plan eliminado");
        } catch (Exception e) {
            res.redirect("/admin/planes?error=" + e.getMessage());
        }

        return null;
    }

    private static String validarCamposPlan(Request req) {
        String anioStr = req.queryParams("anio");
        String descripcion = req.queryParams("descripcion");
        String idCarreraStr = req.queryParams("id_carrera");

        if (idCarreraStr == null || idCarreraStr.isBlank()) {
            return "Carrera es requerida";
        }

        if (anioStr == null || anioStr.isBlank()) {
            return "Año es requerido";
        }

        if (descripcion == null || descripcion.isBlank()) {
            return "Descripción es requerida";
        }

        try {
            int anio = Integer.parseInt(anioStr);
            if (anio < 1) {
                return "Año inválido";
            }
        } catch (NumberFormatException e) {
            return "Año inválido";
        }

        try {
            Integer.valueOf(idCarreraStr);
        } catch (NumberFormatException e) {
            return "Carrera inválida";
        }

        return null;
    }

    private static boolean validarAdmin(Request req,
                                        Response res) {

        Boolean isAdmin = req.session().attribute("isAdmin");

        if (isAdmin == null || !isAdmin) {
            res.redirect("/?error=Acceso denegado");
            return false;
        }

        return true;
    }

    private static String validarCamposCarrera(Request req) {
        String nombre = req.queryParams("nombreCarrera");
        String facultad = req.queryParams("facultad");
        String duracionStr = req.queryParams("duracion");
        String titulo = req.queryParams("titulo");

        if (nombre == null || nombre.isBlank()) {
            return "Nombre de la carrera es requerido";
        }

        if (facultad == null || facultad.isBlank()) {
            return "Facultad es requerida";
        }

        if (titulo == null || titulo.isBlank()) {
            return "Título es requerido";
        }

        if (duracionStr == null || duracionStr.isBlank()) {
            return "Duración es requerida";
        }

        try {
            int dur = Integer.parseInt(duracionStr);
            if (dur < 1) {
                return "Duración debe ser al menos 1 año";
            }
        } catch (NumberFormatException e) {
            return "Duración inválida";
        }

        return null;
    }

    public static ModelAndView createView(Request req,
                                          Response res) {

        Map<String, Object> model = new HashMap<>();

        model.put(
            "successMessage",
            req.queryParams("message")
        );

        model.put(
            "errorMessage",
            req.queryParams("error")
        );

        return new ModelAndView(
            model,
            "admin_create_form.mustache"
        );
    }

    public static ModelAndView createAdmin(Request req,
                                       Response res) {

    try {

        String username =
                req.queryParams("username");

        String password =
                req.queryParams("password");

        String dniStr =
                req.queryParams("dni");

        String realName =
                req.queryParams("realName");

        String surname =
                req.queryParams("surname");

        String telefono =
                req.queryParams("telefono");

        String correo =
                req.queryParams("correo");

        Integer dni =
                Integer.valueOf(dniStr);

        Persona persona =
                Persona.findFirst(
                        "dni = ?",
                        dni
                );

        if (persona == null) {

            persona = new Persona();

            persona.set("dni", dni);

            persona.set(
                    "realName",
                    realName
            );

            persona.set(
                    "surname",
                    surname
            );

            persona.set(
                    "telefono",
                    telefono
            );

            persona.set(
                    "correo",
                    correo
            );

            persona.saveIt();
        }

        User user = User.findFirst(
                "name = ?",
                username
        );

        if (user == null) {

            user = new User();

            user.set("name", username);

            user.set(
                "password",
                BCrypt.hashpw(
                    password,
                    BCrypt.gensalt()
                )
            );

            user.set("dni", dni);

            user.saveIt();
        }

        Administrador admin =
                Administrador.findFirst(
                        "dni = ?",
                        dni
                );

        if (admin == null) {

            admin = new Administrador();

            admin.set("dni", dni);

            admin.saveIt();
        }

        res.redirect(
            "/admin/create?message=Administrador creado"
        );

    } catch (Exception e) {

        e.printStackTrace();

        res.redirect(
            "/admin/create?error=" +
            e.getMessage()
        );
    }

    return null;
}
}