package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorImpuestos {

    public Impuesto mapearImpuesto(ResultSet rs) throws SQLException {
        int idImpuesto = rs.getInt("id_impuesto");
        String nombre = rs.getString("nombre_impuesto");
        BigDecimal porcentaje = rs.getBigDecimal("porcentaje_impuesto");
        boolean activo = rs.getBoolean("impuesto_activo");

        return Impuesto.reconstruirDesdeBD(idImpuesto, nombre, porcentaje, activo);
    }

}//===================================================================================================================//

