package com.is1.proyecto.services;

// importar modelos de la base de datos.
import com.is1.proyecto.models.Estudiante;
import com.is1.proyecto.models.Materia;

// para la base de datos.
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

// clases genericas.
import java.util.List;
import java.util.ArrayList;

public class Correlatividades {

	public Correlatividades(){}

	/*
		Recivo un estudiante.
		Busco todas las materias, las filtro para que sean las de su carrera
		devuelvo su lista de materias validas a cursar
	*/
	public List<Materia> materiasHabilitadas (Estudiante e){
		List<Materia> lista = new ArrayList<>();
		//creo una peticion para tener la lista
		String peticion =   "SELECT m.* " +
							"FROM inscripcion ins " +
								"JOIN carrera ca ON ca.id_carrera = ins.id_carrera" +
								"JOIN plan_estudio p ON p.id_carrera = ca.id_carrera" +
								"JOIN plan_materia pm ON pm.id_plan = p.id_plan" +
								"JOIN materia m ON m.id_materia = pm.id_materia" +
							"WHERE p.vigente = 1 AND ins.dniEstudiante = ?" +
  		//filtro las materias materias que no estan aprobadas
  							"AND NOT EXISTS (" +
      							"SELECT 1 FROM cursado cu" +
      							"WHERE cu.id_materia = m.id_materia" +
        							"AND cu.dniEstudiante = ins.dniEstudiante" +
        							"AND cu.estado IN ('PROMOCIONADA','APROBADA')" +
  							")" +
 		//filtro por correlativas incumplidas
  							"AND NOT EXISTS (" +
      							"SELECT 1 FROM correlatividad co" +
      								"JOIN materia m_corr ON co.id_correlativa = m_corr.id_materia" +
      								"JOIN cursado cu ON cu.id_materia = m_corr.id_materia" +
      							"WHERE co.id_materia = m.id_materia" +
        							"AND cu.dniEstudiante = ins.dniEstudiante" +
        							"AND (" +
										"cu.estado = 'LIBRE'" +
            							"OR (cu.estado = 'REGULAR' AND co.condicion = 'APROBADA')" +
        							")" +
  							");";
   		try (PreparedStatement ps = conn.preparedStatement(peticion)) {

            ps.setInt(1, e.getDni());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Materia m = new Materia(
                    m.getInteger("id_materia"),
                    m.getString("nombreMateria"),
                    m.getInteger("anio"),
                    m.getInteger("cuatrimestre"),
					m.getInteger("carga_horaria"),
					m.getInteger("id_carrera")
            	);
            	lista.add(m);
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return lista;
	}

	public Boolean puedeCursar (Estudiante e, Materia m) {
		List<Materia> materiasC = materiasHabilitadas(e); 
		for (Object mc : materiasC){
			if (mc.getIdMateria() == m.getIdMateria())
				return true;
		}
		return false;
	}
}