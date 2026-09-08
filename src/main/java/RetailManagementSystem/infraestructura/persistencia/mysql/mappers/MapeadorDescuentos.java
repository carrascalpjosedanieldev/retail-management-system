package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorDescuentos {

    public Descuento mapearDescuento(ResultSet rs) throws SQLException{
        int idReal = rs.getInt("id_descuento");
        String nombre = rs.getString("nombre");
        BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
        boolean activo = rs.getBoolean("activo");

        return Descuento.reconstruirDesdeBD(idReal, nombre, porcentaje, activo);
    }

}//===================================================================================================================//

