package com.is1.proyecto.services;

import com.is1.proyecto.models.Estudiante;
import com.is1.proyecto.models.Materia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Correlatividades {

    private final Connection conn;

    public Correlatividades(Connection conn) {
        this.conn = conn;
    }

    public List<Materia> materiasHabilitadas(Estudiante e) {
        List<Materia> lista = new ArrayList<>();

        String sql = "SELECT m.* " +
                     "FROM inscripcion ins " +
                     "JOIN carrera ca ON ca.id_carrera = ins.id_carrera " +
                     "JOIN plan_estudio p ON p.id_carrera = ca.id_carrera " +
                     "JOIN plan_materia pm ON pm.id_plan = p.id_plan " +
                     "JOIN materia m ON m.id_materia = pm.id_materia " +
                     "WHERE p.vigente = 1 AND ins.dniEstudiante = ? " +
                     "AND NOT EXISTS ( " +
                        "SELECT 1 FROM cursado cu " +
                        "WHERE cu.id_materia = m.id_materia " +
                        "AND cu.dniEstudiante = ins.dniEstudiante " +
                        "AND cu.estado IN ('PROMOCIONADA','APROBADA')" +
                     ") " +
                     "AND NOT EXISTS ( " +
                        "SELECT 1 FROM correlatividad co " +
                        "JOIN materia m_corr ON co.id_correlativa = m_corr.id_materia " +
                        "JOIN cursado cu ON cu.id_materia = m_corr.id_materia " +
                        "WHERE co.id_materia = m.id_materia " +
                        "AND cu.dniEstudiante = ins.dniEstudiante " +
                        "AND (cu.estado = 'LIBRE' OR (cu.estado = 'REGULAR' AND co.condicion = 'APROBADA'))" +
                     ");";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getDni());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
Materia m = new Materia();
m.set("id_materia", rs.getInt("id_materia"));
m.set("nombreMateria", rs.getString("nombre_materia"));
m.set("anio", rs.getInt("anio"));
m.set("cuatrimestre", rs.getInt("cuatrimestre"));
m.set("carga_horaria", rs.getInt("carga_horaria"));
m.set("id_carrera", rs.getInt("id_carrera"));
lista.add(m);

                
                lista.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public Boolean puedeCursar(Estudiante e, Materia m) {
        List<Materia> materiasC = materiasHabilitadas(e);
        for (Materia mc : materiasC) {
            if (Objects.equals(mc.getIdMateria(), m.getIdMateria())) {
                return true;
            }
        }
        return false;
    }
}
