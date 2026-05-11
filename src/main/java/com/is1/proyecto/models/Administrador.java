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

    /**
     * @post Verifica si una persona con un DNI específico es administrador.
     */
    public static boolean esAdministrador (Integer dni) {
        return Administrador.findFirst ("dni = ?", dni) != null;
    }

    /**
     * @post Obtiene el administrador asociado a un usuario dado, si es que existe.
     */
    public static Administrador obtenerPorUsuario (User usuario) {
        Integer dni = usuario.getDni ();
        if (dni != null) {
            return Administrador.findFirst ("dni = ?", dni);
        }
        return null;
    }
}