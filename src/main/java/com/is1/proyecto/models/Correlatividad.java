package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("correlatividad")
public class Correlatividad extends Model{

    public Integer getIdMateria(){
        return getInteger("id_materia");
    }

    public void setIdMateria(Integer id_materia){
        set("id_materia", id_materia);
    }

    public Integer getIdCorrelativa(){
        return getInteger("id_correlativa");
    }

    public void setIdCorrelativa(Integer id_correlativa){
        set("id_correlativa", id_correlativa);
    }

    public String getCondicion(){
        return getString("condicion");
    }

    public void setCondicion(String condicion){
        set("condicion", condicion);
    }
}
