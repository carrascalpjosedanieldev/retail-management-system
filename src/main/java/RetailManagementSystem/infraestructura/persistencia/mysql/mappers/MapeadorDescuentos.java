package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorDescuentos {

    public Descuento mapearDescuento(ResultSet rs) throws SQLException{
        int idDescuento = rs.getInt("id_descuento");
        String nombre = rs.getString("nombre_descuento");
        BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
        boolean activo = rs.getBoolean("descuento_activo");

        return Descuento.reconstruirDesdeBD(idDescuento, nombre, porcentaje, activo);
    }

}//===================================================================================================================//

