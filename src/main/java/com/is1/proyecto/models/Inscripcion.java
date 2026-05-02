package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
//import java.sql.Date; en duda si las fechas usar String o Date

@Table ("inscripcion")
public class Inscripcion extends Model{
    
    public Integer getDniEstudiante(){
        return getInteger("dniEstudiante");
    }

    public void setDniEstudiante(Integer dniEstudiante){
        set("dniEstudiante", dniEstudiante);
    }

    public Integer getIdCarrera(){
        return getInteger("id_carrera");
    }

    public void setIdCarrera(Integer id_carrera){
        set("id_carrera", id_carrera);
    }

    public String getFechaIngreso(){
        return getString("fecha_ingreso");
    }

    public void setFechaIngreso(String fecha_ingreso){
        set("fecha_ingreso", fecha_ingreso);
    }

    public String getSituacion(){
        return getString("situacion");
    }

    public void setSituacion(String situacion){
        set("situacion", situacion);
    }
}
