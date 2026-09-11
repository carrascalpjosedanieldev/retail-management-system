package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorPoliticasVencimiento {

    public PoliticaVencimiento mapearPoliticaVencimiento(ResultSet rs) throws SQLException {
        int idPolitica = rs.getInt("id_politica");
        String nombre = rs.getString("nombre_politica");
        int diasUmbral = rs.getInt("dias_umbral");
        BigDecimal porcentaje = rs.getBigDecimal("porcentaje_politica");
        boolean activa = rs.getBoolean("politica_activa");

        return PoliticaVencimiento.reconstruirDesdeBD(idPolitica, nombre, diasUmbral, porcentaje, activa);
    }

}//===================================================================================================================//

