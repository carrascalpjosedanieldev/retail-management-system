package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeadorPermisos {

    public Permiso mapearPermiso(ResultSet rs) throws SQLException {
        int idPermiso = rs.getInt("id_permiso");
        String nombre = rs.getString("nombre_permiso");
        String descripcion = rs.getString("descripcion");
        String modulo = rs.getString("nombre_modulo");
        boolean activo = rs.getBoolean("permiso_activo");

        return Permiso.reconstruirDesdeBD(idPermiso, nombre, descripcion, modulo, activo);
    }

}//===================================================================================================================//

