package com.is1.proyecto.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.is1.proyecto.models.Carrera;

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