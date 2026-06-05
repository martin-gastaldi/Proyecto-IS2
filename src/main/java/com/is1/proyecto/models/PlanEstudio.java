package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table ("plan_estudio")
@IdName("id_plan")
public class PlanEstudio extends Model {

    public Integer getIdPlanEstudio(){
        return getInteger("id_plan");
    }

    public Integer getAnio(){
        return getInteger("anio");
    }

    public void setAnio(Integer anio){
        set("anio", anio);
    }

    public boolean getVigente(){
        return getBoolean("vigente");
    }

    public void setVigente(boolean vigente){
        set("vigente", vigente);
    }

    public String getDescripcion(){
        return getString("descripcion");
    }

    public void setDescripcion(String descripcion){
        set("descripcion", descripcion);
    }

    public Integer getIdCarrera(){
        return getInteger("id_carrera");
    }
    
    public void setIdCarrera(Integer id_carrera){
        set("id_carrera", id_carrera);
    }
}