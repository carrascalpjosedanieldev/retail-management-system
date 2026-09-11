package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorRol {

    public Rol mapearRol(ResultSet rs) throws SQLException {
        int idRol = rs.getInt("id_rol");
        String nombre = rs.getString("nombre");
        boolean activo = rs.getBoolean("activo");

        return Rol.reconstruirDesdeBD(idRol, nombre, activo);
    }

}//===================================================================================================================//

