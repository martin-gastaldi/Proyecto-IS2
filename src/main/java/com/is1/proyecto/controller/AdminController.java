package com.is1.proyecto.controller;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.is1.proyecto.models.Administrador;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.User;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AdminController {

    public static ModelAndView dashboard(Request req,
                                         Response res) {

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