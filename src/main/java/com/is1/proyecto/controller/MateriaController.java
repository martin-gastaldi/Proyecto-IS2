package com.is1.proyecto.controller;

import java.util.HashMap;
import java.util.Map;

import com.is1.proyecto.dao.MateriaDao;
import com.is1.proyecto.models.Materia;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class MateriaController {

    private static final MateriaDao materiaDao =
            new MateriaDao();

    public static ModelAndView listar(
            Request req,
            Response res) {

        Map<String,Object> model =
                new HashMap<>();

        model.put(
            "materias",
            materiaDao.obtenerMaterias()
        );

        return new ModelAndView(
            model,
            "materias.mustache"
        );
    }

    public static ModelAndView formNueva(
            Request req,
            Response res) {

        return new ModelAndView(
            new HashMap<>(),
            "materia_new.mustache"
        );
    }

    public static Object crear(
            Request req,
            Response res) {

        materiaDao.crearMateria(
            req.queryParams("nombreMateria"),
            Integer.valueOf(req.queryParams("anio")),
            Integer.valueOf(req.queryParams("cuatrimestre")),
            Integer.valueOf(req.queryParams("carga_horaria")),
            Integer.valueOf(req.queryParams("id_carrera"))
        );

        res.redirect("materias");

        return null;
    }

    /*
     * ========= EDITAR =========
     */

    public static ModelAndView editView(
            Request req,
            Response res) {

        Integer idMateria =
                Integer.valueOf(req.params(":id"));

        Materia materia =
                materiaDao.buscarPorId(idMateria);

        Map<String,Object> materiaMap =
                new HashMap<>();

        materiaMap.put(
            "id_materia",
            materia.getInteger("id_materia")
        );

        materiaMap.put(
            "nombreMateria",
            materia.getString("nombreMateria")
        );

        materiaMap.put(
            "anio",
            materia.getInteger("anio")
        );

        materiaMap.put(
            "cuatrimestre",
            materia.getInteger("cuatrimestre")
        );

        materiaMap.put(
            "carga_horaria",
            materia.getInteger("carga_horaria")
        );

        materiaMap.put(
            "id_carrera",
            materia.getInteger("id_carrera")
        );

        Map<String,Object> model =
                new HashMap<>();

        model.put(
            "materia",
            materiaMap
        );

        return new ModelAndView(
            model,
            "materia_edit.mustache"
        );
    }

    public static Object editar(
            Request req,
            Response res) {
                System.out.println("id_materia=" + req.queryParams("id_materia"));
                System.out.println("nombreMateria=" + req.queryParams("nombreMateria"));
                System.out.println("anio=" + req.queryParams("anio"));
                System.out.println("cuatrimestre=" + req.queryParams("cuatrimestre"));
                System.out.println("carga_horaria=" + req.queryParams("carga_horaria"));
                System.out.println("id_carrera=" + req.queryParams("id_carrera"));

        materiaDao.actualizarMateria(
            Integer.valueOf(
                req.queryParams("id_materia")
            ),
            req.queryParams("nombreMateria"),
            Integer.valueOf(req.queryParams("anio")),
            Integer.valueOf(req.queryParams("cuatrimestre")),
            Integer.valueOf(req.queryParams("carga_horaria")),
            Integer.valueOf(req.queryParams("id_carrera"))
        );

        res.redirect("/admin/materias");

        return null;
    }

    /*
     * ========= ELIMINAR =========
     */

    public static Object eliminar(
            Request req,
            Response res) {

        materiaDao.eliminarMateria(
            Integer.valueOf(
                req.params(":id")
            )
        );

        res.redirect("/materias");

        return null;
    }

    /*
     * ========= CORRELATIVIDADES =========
     */

    public static ModelAndView correlativas(
            Request req,
            Response res) {

        Integer idMateria =
                Integer.valueOf(
                    req.params(":id")
                );

        Map<String,Object> model =
                new HashMap<>();

         model.put(
            "idMateria",
            idMateria
        );
                
        model.put(
            "materia",
            materiaDao.buscarPorId(idMateria)
        );
        

        model.put(
            "correlativas",
            materiaDao.obtenerCorrelativas(
                idMateria
            )
        );

        model.put(
            "materiasDisponibles",
            materiaDao.obtenerMaterias()
        );

        return new ModelAndView(
            model,
            "correlativas.mustache"
        );
    }

    public static Object agregarCorrelativa(
            Request req,
            Response res) {


        System.out.println(
            "id_materia = " +
            req.queryParams("id_materia")
        );

        System.out.println(
            "id_correlativa = " +
            req.queryParams("id_correlativa")
        );

        System.out.println(
            "condicion = " +
            req.queryParams("condicion")
        );        

        materiaDao.agregarCorrelativa(
            Integer.valueOf(
                req.queryParams("id_materia")
            ),
            Integer.valueOf(
                req.queryParams("id_correlativa")
            ),
            req.queryParams("condicion")
        );

        res.redirect(
            "/admin/materias/correlativas/"
            + req.queryParams("id_materia")
        );

        return null;
    }

    public static Object eliminarCorrelativa(
            Request req,
            Response res) {

        Integer idMateria =
                Integer.valueOf(
                    req.queryParams("id_materia")
                );

        Integer idCorrelativa =
                Integer.valueOf(
                    req.queryParams("id_correlativa")
                );

        materiaDao.eliminarCorrelativa(
            idMateria,
            idCorrelativa
        );

        res.redirect(
            "/admin/materias/correlativas/"
            + idMateria
        );

        return null;
    }
}