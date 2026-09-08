package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorImpuestos {

    public Impuesto mapearImpuesto(ResultSet rs) throws SQLException {
        int idReal = rs.getInt("id_impuesto");
        String nombre = rs.getString("nombre");
        BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
        boolean activo = rs.getBoolean("activo");

        return Impuesto.reconstruirDesdeBD(idReal, nombre, porcentaje, activo);
    }

}//===================================================================================================================//

