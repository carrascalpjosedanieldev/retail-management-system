package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorProductoBase {

    public DatosProductoBase mapearProductoBase(ResultSet rs, Impuesto impuesto, Descuento descuento) throws SQLException {
        String codigo = rs.getString("codigo_producto");
        String nombre = rs.getString("nombre");
        BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
        BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
        int stock = rs.getInt("stock");
        boolean activo = rs.getBoolean("activo");

        return new DatosProductoBase(
                codigo,
                nombre,
                valorCompra,
                porcentajeGanancia,
                stock,
                activo,
                impuesto,
                descuento
        );
    }

}//===================================================================================================================//

