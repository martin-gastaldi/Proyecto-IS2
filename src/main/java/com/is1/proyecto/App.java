package com.is1.proyecto;

import org.javalite.activejdbc.Base;

import com.is1.proyecto.config.DBConfigSingleton;
import com.is1.proyecto.controller.AdminController;
import com.is1.proyecto.controller.AuthController;
import com.is1.proyecto.controller.DocenteController;
import com.is1.proyecto.controller.MateriaController;
import com.is1.proyecto.controller.EstudianteController;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
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

        // ESTUDIANTE - perfil y edición
        get("/estudiante/perfil",
            EstudianteController::profile,
            engine);

        get("/estudiante/perfil/editar",
            EstudianteController::editProfileView,
            engine);

        post("/estudiante/perfil/editar",
            EstudianteController::updateProfile,
            engine);

        // ADMIN
        get("/admin/panel",
            AdminController::dashboard,
            engine);

        get("/admin/carreras",
            AdminController::manageCarreras,
            engine);

        get("/admin/carreras/new",
            AdminController::createCarreraView,
            engine);

        get("/admin/carreras/edit/:id",
            AdminController::editCarreraView,
            engine);

        post("/admin/carreras/create",
            AdminController::createCarrera,
            engine);

        post("/admin/carreras/edit",
            AdminController::editCarrera,
            engine);

        post("/admin/carreras/delete",
            AdminController::deleteCarrera,
            engine);

        get("/admin/planes",
            AdminController::managePlanes,
            engine);

        get("/admin/planes/new",
            AdminController::createPlanView,
            engine);

        get("/admin/planes/edit/:id",
            AdminController::editPlanView,
            engine);

        post("/admin/planes/create",
            AdminController::createPlan,
            engine);

        post("/admin/planes/edit",
            AdminController::editPlan,
            engine);

        post("/admin/planes/delete",
            AdminController::deletePlan,
            engine);

        get("/admin/create",
            AdminController::createView,
            engine);

        post("/admin/create",
            AdminController::createAdmin,
            engine);

        get(
            "/admin/materias",
            MateriaController::listar,
            engine
        );

        get(
            "/admin/materias/new",
            MateriaController::formNueva,
            engine
        );

        post(
            "/admin/materias/create",
            MateriaController::crear
        );

        get(
            "/admin/estudiantes/new",
            AdminController::createEstudianteView,
            engine
        );

        post(
            "/admin/estudiantes/create",
            AdminController::createEstudiante
        );

        post(
            "/admin/estudiantes/inscripcion",
            AdminController::agregarInscripcion
        );

        // EDITAR

        get(
            "/admin/materias/edit/:id",
            MateriaController::editView,
            engine
        );

        post(
            "/admin/materias/edit",
            MateriaController::editar
        );



        // ELIMINAR

        post(
            "/admin/materias/delete/:id",
            MateriaController::eliminar
        );

        // CORRELATIVAS

        get(
            "/admin/materias/correlativas/:id",
            MateriaController::correlativas,
            engine
        );

        post(
            "/admin/materias/correlativas/add",
            MateriaController::agregarCorrelativa
        );

        post(
            "/admin/materias/correlativas/delete",
            MateriaController::eliminarCorrelativa
        );
    }
}