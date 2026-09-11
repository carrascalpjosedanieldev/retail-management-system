package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorInventario {

    public Inventario mapearInventario(ResultSet rs) throws SQLException {
        int idInventario = rs.getInt("id_inventario");
        String nombre = rs.getString("nombre");
        int capacidadMaxima = rs.getInt("capacidad_maxima");
        int capacidadOcupada = rs.getInt("capacidad_ocupada");

        return Inventario.reconstruirDesdeBD(idInventario, nombre, capacidadMaxima, capacidadOcupada);
    }

}//===================================================================================================================//

