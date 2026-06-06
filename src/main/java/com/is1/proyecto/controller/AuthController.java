package com.is1.proyecto.controller;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
public class AuthController {

    private static final ObjectMapper objectMapper =
            new ObjectMapper();

    public static ModelAndView loginView(Request req,
                                         Response res) {

        Map<String, Object> model = new HashMap<>();

        String error = req.queryParams("error");
        String success = req.queryParams("message");

        model.put("errorMessage", error);
        model.put("successMessage", success);

        return new ModelAndView(model,
                "login.mustache");
    }

    public static ModelAndView dashboard(Request req,
                                         Response res) {

        Map<String, Object> model = new HashMap<>();

        String username =
                req.session().attribute("currentUserUsername");

        Boolean loggedIn =
                req.session().attribute("loggedIn");

        if (username == null ||
            loggedIn == null ||
            !loggedIn) {

            res.redirect("/?error=Debes iniciar sesión");
            return null;
        }

        model.put("username", username);

        return new ModelAndView(model,
                "dashboard.mustache");
    }

    public static ModelAndView login(Request req,
                                     Response res) {

        Map<String, Object> model = new HashMap<>();

        String username = req.queryParams("username");
        String password = req.queryParams("password");

        User user = User.findFirst("name = ?", username);

        if (user == null) {

            model.put("errorMessage",
                    "Usuario incorrecto");

            return new ModelAndView(model,
                    "login.mustache");
        }

        String hashedPassword =
                user.getString("password");

        if (!BCrypt.checkpw(password, hashedPassword)) {

            model.put("errorMessage",
                    "Contraseña incorrecta");

            return new ModelAndView(model,
                    "login.mustache");
        }

        req.session(true)
                .attribute("loggedIn", true);

        req.session()
                .attribute("currentUserUsername",
                        username);

        req.session()
                .attribute("dni",
                        user.getInteger("dni"));

        boolean isAdmin = user.esAdministrador();
        boolean isDocente = user.esDocente();

        req.session().attribute("isDocente", isDocente);
        req.session().attribute("isAdmin", isAdmin);

        if (isAdmin) {

         res.redirect("/admin/panel");

        } else if (isDocente) {

         res.redirect("/docente/dashboard");

        } else {

         res.redirect("/dashboard");
        }

        return null;
    }

    public static Object logout(Request req,
                                Response res) {

        req.session().invalidate();
        res.redirect("/");

        return null;
    }

    public static ModelAndView userCreateView(Request req,
                                              Response res) {

        Map<String, Object> model = new HashMap<>();

        model.put("successMessage",
                req.queryParams("message"));

        model.put("errorMessage",
                req.queryParams("error"));

        return new ModelAndView(model,
                "user_form.mustache");
    }

    public static ModelAndView createUser(Request req,
                                          Response res) {

        String username = req.queryParams("name");
        String password = req.queryParams("password");
        String dniStr = req.queryParams("dni");
        String realName = req.queryParams("realName");
        String surname = req.queryParams("surname");
        String correo = req.queryParams("correo");

        if (username == null ||
                password == null ||
                dniStr == null) {

                res.redirect("/user/create?error=Campos obligatorios");
                return null;
        }

        Integer dni = Integer.valueOf(dniStr);

        if (User.findFirst("name = ?", username) != null) {

                res.redirect("/user/create?error=Usuario existente");
                return null;
        }

        if (Persona.findFirst("dni = ?", dni) == null) {

                Persona persona = new Persona();
                persona.set("dni", dni);
                persona.set("realName", realName);
                persona.set("surname", surname);
                persona.set("telefono", "SIN_TELEFONO");
                persona.set("correo", correo);
                persona.saveIt();
        }

        String hashed =
                BCrypt.hashpw(password,
                        BCrypt.gensalt());

        User user = new User();

        user.set("name", username);
        user.set("password", hashed);
        user.set("dni", dni);

        user.saveIt();

        res.redirect("/");
        return null;
        }

    public static Object addUserApi(Request req,
                                    Response res)
            throws Exception {

        res.type("application/json");

        String name = req.queryParams("name");
        String password = req.queryParams("password");

        User user = new User();

        user.set("name", name);
        user.set("password", password);

        user.saveIt();

        return objectMapper.writeValueAsString(
                Map.of(
                    "message",
                    "Usuario creado"
                )
        );
    }
}