package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("estudiante")
public class Estudiante extends Model{

    public Integer getDni(){
        return getInteger("dni");
    }

    public void setDni(Integer dni){
        set("dni", dni);
    }

    public Integer getLegajo(){
        return getInteger("legajo");
    }

    public void setLegajo(Integer legajo){
        set("legajo", legajo);
    }

    public String getFechaIngreso(){
       return  getString("fecha_ingreso");
    }

    public void setFechaIngreso(String fecha_ingreso){
        set("fecha_ingreso", fecha_ingreso);
    }

}
