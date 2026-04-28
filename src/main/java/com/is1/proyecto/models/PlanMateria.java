package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("plan_materia")
public class PlanMateria extends Model{

    public Integer getIdPlan(){
        return getInteger("id_plan");
    } 

    public void setIdPlan(Integer id_plan){
        set("id_plan", id_plan);
    }
    public Integer getIdMateria(){
        return getInteger("id_materia");
    }

    public void setIdMateria(Integer id_materia){
        set("id_materia", id_materia);
    }

}
