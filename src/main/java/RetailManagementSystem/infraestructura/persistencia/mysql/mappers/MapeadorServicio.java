package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorServicio {

    public Servicio mapearServicioCompleto(ResultSet rs, Impuesto impuesto, Descuento descuento) throws SQLException {
        String codigo = rs.getString("codigo_servicio");
        String nombre = rs.getString("nombre");
        BigDecimal precioBase = rs.getBigDecimal("precio_base");
        boolean activo = rs.getBoolean("activo");

        return Servicio.reconstruirDesdeBD(codigo, nombre, precioBase, impuesto, descuento, activo);
    }

}//===================================================================================================================//

