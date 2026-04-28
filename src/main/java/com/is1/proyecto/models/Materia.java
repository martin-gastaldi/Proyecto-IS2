package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("materia") //relaciona esta clase con la bdd
public class Materia extends Model {
    /* 
    public Integer getEncargado(){
        return getInteger("encargado"); //retorna el valor de la columna encargado
    }
    public void setEncargado(Integer encargado){
        set("encargado", encargado); //le asigna encargado a la columna encargado
    }
    */
    public Integer getIdMateria(){
        return getInteger("id_materia");
    }

    public String getNombreMateria(){
        return getString("nombreMateria"); //retorna el valor de la columna nombreMateria
    }

    public void setNombreMateria(String nombreMateria){
        set("nombreMateria", nombreMateria); //le asigna nombreMateria a la columna nombreMateria
    }

    public Integer getAnio(){
        return getInteger("anio");
    }

    public void setAnio(Integer anio){
        set("anio", anio);
    }

    public Integer getCuatrimestre(){
        return getInteger("cuatrimestre");
    }

    public void setCuatrimestre(Integer cuatrimestre){
        set("cuatrimestre", cuatrimestre);
    }

    public Integer getCargaHoraria(){
        return getInteger("carga_horaria");
    }

    public void setCargaHoraria(Integer carga_horaria){
        set("carga_horaria", carga_horaria);
    }

    /*
    public Integer getIdCarrera(){
        return getInteger("id_carrera"); //retorna el valor de la columna id_carrera
    }
    public void setIdCarrera(Integer id_carrera){
        set("id_carrera", id_carrera); //le asigna idCarrera a la columna id_carrera
    }
    */
}
