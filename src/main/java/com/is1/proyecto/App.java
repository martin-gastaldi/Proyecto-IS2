package com.is1.proyecto;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

import org.javalite.activejdbc.Base;

import com.is1.proyecto.config.DBConfigSingleton;
import com.is1.proyecto.controller.AdminController;
import com.is1.proyecto.controller.AuthController;
import com.is1.proyecto.controller.DocenteController;

import spark.template.mustache.MustacheTemplateEngine;

public class App {

    public static void main(String[] args) {

        port(8080);

        DBConfigSingleton dbConfig = DBConfigSingleton.getInstance();

        before((req, res) -> {

            try {

                if (!Base.hasConnection()) {

                    Base.open(
                        dbConfig.getDriver(),
                        dbConfig.getDbUrl(),
                        dbConfig.getUser(),
                        dbConfig.getPass()
                    );
                }

            } catch (Exception e) {

                halt(500,
                    "Error conectando a la base de datos"
                );
            }
        });

        after((req, res) -> {

            if (Base.hasConnection()) {
                Base.close();
            }
        });

        MustacheTemplateEngine engine =
                new MustacheTemplateEngine();

        // AUTH
        get("/", AuthController::loginView, engine);
        post("/login", AuthController::login, engine);
        get("/logout", AuthController::logout);

        get("/user/create",
            AuthController::userCreateView,
            engine);

        post("/user/new",
            AuthController::createUser,
            engine);

        post("/add_users",
            AuthController::addUserApi);

        get("/dashboard",
            AuthController::dashboard,
            engine);

        // DOCENTE
        get("/get_docente",
            DocenteController::getDocenteView,
            engine);

        post("/get_docente",
            DocenteController::createDocente,
            engine);

        // ADMIN - GESTIONAR DOCENTES
        get("/admin/docentes",
            DocenteController::manageDocentes,
            engine);

        get("/admin/docentes/view/:dni",
            DocenteController::viewDocente,
            engine);

        get("/admin/docentes/edit/:dni",
            DocenteController::editDocenteView,
            engine);

        get("/admin/docentes/asignar/:dni",
            DocenteController::asignarMateriaView,
            engine);

        post("/admin/docentes/delete",
            DocenteController::deleteDocente,
            engine);

        post("/admin/docentes/edit",
            DocenteController::editDocente,
            engine);

        post("/admin/docentes/asignar",
            DocenteController::asignarMateria,
            engine);

        post("/admin/docentes/desasignar",
            DocenteController::desasignarMateria,
            engine);

        get("/docente/dashboard",
            DocenteController::dashboard,
            engine);

        get("/docente/perfil",
            DocenteController::profile,
            engine);

        get("/docente/perfil/editar",
            DocenteController::editProfileView,
            engine);

        post("/docente/perfil/editar",
            DocenteController::updateProfile,
            engine);

        get("/docente/materias",
            DocenteController::materias,
            engine);

        get("/docente/alumnos/:id",
            DocenteController::alumnosMateria,
            engine);

        // ADMIN
        get("/admin/panel",
            AdminController::dashboard,
            engine);

        get("/admin/create",
            AdminController::createView,
            engine);

        post("/admin/create",
            AdminController::createAdmin,
            engine);
    }
}