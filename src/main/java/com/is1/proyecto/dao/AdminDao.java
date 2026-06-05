package com.is1.proyecto.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.is1.proyecto.models.Carrera;
import com.is1.proyecto.models.PlanEstudio;

import spark.Request;

public class AdminDao {

    public List<Map<String, Object>> obtenerCarreras() {

        List<Map<String, Object>> carreras = new ArrayList<>();

        List<Carrera> lista = Carrera.findAll();

        for (Carrera carrera : lista) {

            Map<String, Object> data = new HashMap<>();

            data.put("id_carrera", carrera.getIdCarrera());
            data.put("nombreCarrera", carrera.getNombreCarrera());
            data.put("facultad", carrera.getFacultad());
            data.put("duracion", carrera.getDuracion());
            data.put("titulo", carrera.getTitulo());

            carreras.add(data);
        }

        return carreras;
    }

    public List<Map<String, Object>> obtenerCarreras(Integer selectedCarrera) {

        List<Map<String, Object>> carreras = new ArrayList<>();

        List<Carrera> lista = Carrera.findAll();

        for (Carrera carrera : lista) {

            Map<String, Object> data = new HashMap<>();

            data.put("id_carrera", carrera.getIdCarrera());
            data.put("nombreCarrera", carrera.getNombreCarrera());
            data.put("facultad", carrera.getFacultad());
            data.put("duracion", carrera.getDuracion());
            data.put("titulo", carrera.getTitulo());
            data.put("selected", selectedCarrera != null && carrera.getIdCarrera().equals(selectedCarrera) ? "selected" : "");

            carreras.add(data);
        }

        return carreras;
    }

    public Map<String, Object> obtenerCarrera(Integer id) {

        Carrera carrera = Carrera.findFirst("id_carrera = ?", id);

        if (carrera == null) {
            throw new IllegalArgumentException("Carrera no encontrada");
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id_carrera", carrera.getIdCarrera());
        data.put("nombreCarrera", carrera.getNombreCarrera());
        data.put("facultad", carrera.getFacultad());
        data.put("duracion", carrera.getDuracion());
        data.put("titulo", carrera.getTitulo());

        return data;
    }

    public List<Map<String, Object>> obtenerPlanes(Integer idCarrera) {

        List<Map<String, Object>> planes = new ArrayList<>();
        List<PlanEstudio> lista;

        if (idCarrera == null) {
            lista = PlanEstudio.findAll();
        } else {
            lista = PlanEstudio.where("id_carrera = ?", idCarrera);
        }

        for (PlanEstudio plan : lista) {

            Map<String, Object> data = new HashMap<>();
            Carrera carrera = Carrera.findFirst("id_carrera = ?", plan.getIdCarrera());

            data.put("id_plan", plan.getIdPlanEstudio());
            data.put("anio", plan.getAnio());
            data.put("vigente", plan.getVigente() ? "Sí" : "No");
            data.put("descripcion", plan.getDescripcion());
            data.put("id_carrera", plan.getIdCarrera());
            data.put("nombreCarrera", carrera != null ? carrera.getNombreCarrera() : "Sin carrera");

            planes.add(data);
        }

        return planes;
    }

    public Map<String, Object> obtenerPlan(Integer id) {

        PlanEstudio plan = PlanEstudio.findFirst("id_plan = ?", id);

        if (plan == null) {
            throw new IllegalArgumentException("Plan de estudio no encontrado");
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id_plan", plan.getIdPlanEstudio());
        data.put("anio", plan.getAnio());
        data.put("vigente", plan.getVigente());
        data.put("descripcion", plan.getDescripcion());
        data.put("id_carrera", plan.getIdCarrera());

        return data;
    }

    public void crearPlan(Request req) {

        String carreraId = req.queryParams("id_carrera");
        String anioStr = req.queryParams("anio");
        String descripcion = req.queryParams("descripcion");
        boolean vigente = req.queryParams("vigente") != null;

        if (carreraId == null || carreraId.isBlank()) {
            throw new IllegalArgumentException("Carrera es obligatoria");
        }

        PlanEstudio plan = new PlanEstudio();

        plan.set("id_carrera", Integer.valueOf(carreraId));
        plan.set("anio", Integer.valueOf(anioStr));
        plan.set("descripcion", descripcion);
        plan.set("vigente", vigente);

        plan.saveIt();
    }

    public void editarPlan(Request req) {

        String idStr = req.queryParams("id_plan");
        String carreraId = req.queryParams("id_carrera");
        String anioStr = req.queryParams("anio");
        String descripcion = req.queryParams("descripcion");
        boolean vigente = req.queryParams("vigente") != null;

        if (idStr == null || idStr.isBlank()) {
            throw new IllegalArgumentException("ID de plan requerido");
        }

        PlanEstudio plan = PlanEstudio.findFirst("id_plan = ?", Integer.valueOf(idStr));

        if (plan == null) {
            throw new IllegalArgumentException("Plan de estudio no encontrado");
        }

        plan.set("id_carrera", Integer.valueOf(carreraId));
        plan.set("anio", Integer.valueOf(anioStr));
        plan.set("descripcion", descripcion);
        plan.set("vigente", vigente);

        plan.saveIt();
    }

    public void eliminarPlan(Integer id) {

        PlanEstudio plan = PlanEstudio.findFirst("id_plan = ?", id);

        if (plan == null) {
            throw new IllegalArgumentException("Plan de estudio no encontrado");
        }

        plan.delete();
    }

    public void crearCarrera(Request req) {

        String nombreCarrera = req.queryParams("nombreCarrera");
        String facultad = req.queryParams("facultad");
        String duracionStr = req.queryParams("duracion");
        String titulo = req.queryParams("titulo");

        if (nombreCarrera == null || nombreCarrera.isBlank()) {
            throw new IllegalArgumentException("Nombre de carrera es obligatorio");
        }

        Carrera carrera = new Carrera();

        carrera.set("nombreCarrera", nombreCarrera);
        carrera.set(
            "facultad",
            facultad != null && !facultad.isBlank() ? facultad : null
        );

        if (duracionStr != null && !duracionStr.isBlank()) {
            try {
                carrera.set(
                    "duracion",
                    Integer.valueOf(duracionStr)
                );
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Duración inválida");
            }
        }

        carrera.set(
            "titulo",
            titulo != null && !titulo.isBlank() ? titulo : null
        );

        carrera.saveIt();
    }

    public void editarCarrera(Request req) {

        String idStr = req.queryParams("id_carrera");
        String nombreCarrera = req.queryParams("nombreCarrera");
        String facultad = req.queryParams("facultad");
        String duracionStr = req.queryParams("duracion");
        String titulo = req.queryParams("titulo");

        if (idStr == null || idStr.isBlank()) {
            throw new IllegalArgumentException("ID de carrera requerido");
        }

        Integer id = Integer.valueOf(idStr);

        Carrera carrera = Carrera.findFirst("id_carrera = ?", id);

        if (carrera == null) {
            throw new IllegalArgumentException("Carrera no encontrada");
        }

        if (nombreCarrera == null || nombreCarrera.isBlank()) {
            throw new IllegalArgumentException("Nombre de carrera es obligatorio");
        }

        carrera.set("nombreCarrera", nombreCarrera);
        carrera.set(
            "facultad",
            facultad != null && !facultad.isBlank() ? facultad : null
        );

        if (duracionStr != null && !duracionStr.isBlank()) {
            try {
                carrera.set(
                    "duracion",
                    Integer.valueOf(duracionStr)
                );
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Duración inválida");
            }
        } else {
            carrera.set("duracion", null);
        }

        carrera.set(
            "titulo",
            titulo != null && !titulo.isBlank() ? titulo : null
        );

        carrera.saveIt();
    }

    public void eliminarCarrera(Integer id) {

        Carrera carrera = Carrera.findFirst("id_carrera = ?", id);

        if (carrera == null) {
            throw new IllegalArgumentException("Carrera no encontrada");
        }

        carrera.delete();
    }
}