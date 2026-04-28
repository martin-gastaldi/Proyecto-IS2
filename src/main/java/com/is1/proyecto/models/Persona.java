package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("persona") //relaciona esta clase con la bdd
public class Persona extends Model {

    public Integer getDni() {
        return getInteger("dni");  // Obtiene el valor de la columna 'dni'
    }

    public void setDni(Integer dni) {
        set("dni", dni); // establece el valor de dni para la columna dni
    }

    public String getRealName() {
        return getString("realName");  // Obtiene el valor de la columna 'realName'
    }

    public void setRealName(String realName) {
        set("realName", realName); // establece el valor de realName para la columna realName
    }

    public String getSurname(){
        return getString("surname"); //retorna el valor de la columna apellido
    }

    public void setSurname(String surname){
        set ("surname", surname); //le asigna apellido a la columna apellido
    }
 
    public String getCorreo(){
        return getString("correo"); //retorna el valor de la columna correo
    }

    public void setCorreo(String correo){
        set("correo", correo); //le asigna correo a la columna correo
    }

    public String getTelefono(){
        return getString("telefono");
    }

    public void setTelefono(String telefono){
        set("telefono", telefono);
    }

}
