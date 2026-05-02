package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
//import java.sql.Date; en duda si las fechas usar String o Date

@Table ("dictado")
public class Dictado extends Model{
    
    public Integer getDniDocente(){
        return getInteger("dniDocente");
    }

    public void setDniDocente(Integer dniDocente){
        set("dniDocente", dniDocente);
    }

    public Integer getIdMateria(){
        return getInteger("id_materia");
    }

    public void setIdMateria(Integer id_materia){
        set("id_materia", id_materia);
    }

    public String getCargo(){
        return getString("cargo");
    }

    public void setCargo(String cargo){
        set("cargo", cargo);
    }

    public String getDedicacion(){
        return getString("dedicacion");
    }

    public void setDedicacion(String dedicacion){
        set("dedicacion", dedicacion);
    }

    public String getFechaInicio(){
        return getString("fechaInicio");
    }

    public void setFechaInicio(String fechaInicio){
        set("fechaInicio", fechaInicio);
    }

    public String getFechaFin(){
        return getString("fechaFin");
    }

    public void setFechaFin(String fechaFin){
        set("fechaFin", fechaFin);
    }
}
