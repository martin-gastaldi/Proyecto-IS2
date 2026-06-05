package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table ("carrera")
@IdName("id_carrera")
public class Carrera extends Model {

    public Integer getIdCarrera(){
        return getInteger("id_carrera");
    }

    public String getNombreCarrera(){
        return getString("nombreCarrera");
    }

    public void setNombreCarrera(String nombreCarrera){
        set("nombreCarrera", nombreCarrera);
    }

    public String getFacultad(){
        return getString("facultad");
    }

    public void setFacultad(String facultad){
        set("facultad", facultad);
    }

    public Integer getDuracion(){
        return getInteger("duracion");
    }

    public void setDuracion(Integer duracion){
        set("duracion", duracion);
    }

    public String getTitulo(){
        return getString("titulo");
    }

    public void setTitulo(String titulo){
        set("titulo", titulo);
    }
  
}
