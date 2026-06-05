package com.is1.proyecto.dao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.is1.proyecto.models.Correlatividad;
import com.is1.proyecto.models.Materia;

public class MateriaDao {

    public List<Map<String,Object>> obtenerMaterias() {

        List<Map<String,Object>> materiasList =
                new ArrayList<>();

        List<Materia> materias =
                Materia.findAll();

        for (Materia materia : materias) {

            Map<String,Object> data =
                    new HashMap<>();

            data.put(
                "id_materia",
                materia.getInteger("id_materia")
            );

            data.put(
                "nombreMateria",
                materia.getString("nombreMateria")
            );

            data.put(
                "anio",
                materia.getInteger("anio")
            );

            data.put(
                "cuatrimestre",
                materia.getInteger("cuatrimestre")
            );

            data.put(
                "carga_horaria",
                materia.getInteger("carga_horaria")
            );

            data.put(
                "id_carrera",
                materia.getInteger("id_carrera")
            );

            materiasList.add(data);
        }

        return materiasList;
    }

    public void crearMateria(
            String nombreMateria,
            Integer anio,
            Integer cuatrimestre,
            Integer cargaHoraria,
            Integer idCarrera) {

        Materia materia = new Materia();

        materia.set(
            "nombreMateria",
            nombreMateria
        );

        materia.set(
            "anio",
            anio
        );

        materia.set(
            "cuatrimestre",
            cuatrimestre
        );

        materia.set(
            "carga_horaria",
            cargaHoraria
        );

        materia.set(
            "id_carrera",
            idCarrera
        );

        materia.saveIt();
    }

    public Materia buscarPorId(
            Integer idMateria) {

        return Materia.findById(idMateria);
    }

    public void actualizarMateria(
            Integer idMateria,
            String nombreMateria,
            Integer anio,
            Integer cuatrimestre,
            Integer cargaHoraria,
            Integer idCarrera) {

        Materia materia =
                Materia.findById(idMateria);

        if (materia == null) {
            return;
        }

        materia.set(
            "nombreMateria",
            nombreMateria
        );

        materia.set(
            "anio",
            anio
        );

        materia.set(
            "cuatrimestre",
            cuatrimestre
        );

        materia.set(
            "carga_horaria",
            cargaHoraria
        );

        materia.set(
            "id_carrera",
            idCarrera
        );

        materia.saveIt();
    }

    public void eliminarMateria(
            Integer idMateria) {

        Correlatividad.delete(
                "id_materia = ? OR id_correlativa = ?",
                idMateria,
                idMateria
        );

        Materia materia =
                Materia.findById(idMateria);

        if (materia != null) {
            materia.delete();
        }
    }

    public List<Map<String,Object>>
    obtenerCorrelativas(Integer idMateria){

        List<Map<String,Object>> lista =
                new ArrayList<>();

        List<Correlatividad> correlativas =
                Correlatividad.where(
                        "id_materia = ?",
                        idMateria
                );

        for(Correlatividad c : correlativas){

            Materia correlativa =
                    Materia.findById(
                            c.getInteger(
                                    "id_correlativa"
                            )
                    );

            if(correlativa == null){
                continue;
            }

            Map<String,Object> data =
                    new HashMap<>();

            data.put(
                    "id_correlativa",
                    correlativa.getIdMateria()
            );

            data.put(
                    "nombreCorrelativa",
                    correlativa.getNombreMateria()
            );

            data.put(
                    "condicion",
                    c.getString("condicion")
            );
         
            lista.add(data);
        }

        return lista;
    }

    public void agregarCorrelativa(
        Integer idMateria,
        Integer idCorrelativa,
        String condicion){

        Correlatividad existente =
                Correlatividad.findFirst(
                        "id_materia = ? AND id_correlativa = ?",
                        idMateria,
                        idCorrelativa
                );

        if(existente != null){
            return;
        }

        Correlatividad correlatividad =
                new Correlatividad();

        correlatividad.set(
                "id_materia",
                idMateria
        );

        correlatividad.set(
                "id_correlativa",
                idCorrelativa
        );

        correlatividad.set(
                "condicion",
                condicion
        );

        correlatividad.saveIt();
    }
    
    public void eliminarCorrelativa(
            Integer idMateria,
            Integer idCorrelativa){

        Correlatividad.delete(
                "id_materia = ? AND id_correlativa = ?",
                idMateria,
                idCorrelativa
        );
    }

    public List<Map<String,Object>>
    obtenerMateriasSelector(){

        List<Map<String,Object>> lista =
                new ArrayList<>();

        List<Materia> materias =
                Materia.findAll();

        for(Materia m : materias){

            Map<String,Object> data =
                    new HashMap<>();

            data.put(
                    "id_materia",
                    m.getIdMateria()
            );

            data.put(
                    "nombreMateria",
                    m.getNombreMateria()
            );

            lista.add(data);
        }

        return lista;
    }
}