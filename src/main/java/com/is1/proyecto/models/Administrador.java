package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("administrador")
public class Administrador extends Model {

    public Integer getDni(){
        return getInteger("dni");
    }

    public void setDni(Integer dni){
        set("dni", dni);
    }
}
