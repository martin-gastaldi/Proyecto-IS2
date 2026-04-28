package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
//import java.sql.Date; en duda si las fechas usar String o Date


@Table ("cursado")
public class Cursado extends Model{
    
    public Integer getDniEstudiante(){
        return getInteger("dniEstudiante");
    }

    public void setDniEstudiante(Integer dniEstudiante){
        set("dniEstudiante", dniEstudiante);
    }

    public Integer getIdMateria(){
        return getInteger("id_materia");
    }

    public void setIdMateria(Integer id_materia){
        set("id_materia", id_materia);
    }

    public String getFechaInscripcion(){
        return getString("fechaInscripcion");
    }

    public void setFechaInscripcion(String fechaInscripcion){
        set("fechaInscripcion", fechaInscripcion);
    }

    public String getEstado(){
        return getString("estado");
    }

    public void setEstado(String estado){
        set("estado", estado);
    }

    public Double getNotaFinal(){
        return getDouble("notaFinal");
    }

    public void setNotaFinal(Double notaFinal){
        set("notaFinal", notaFinal);
    }
}
