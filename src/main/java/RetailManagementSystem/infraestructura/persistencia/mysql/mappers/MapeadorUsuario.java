package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MapeadorUsuario {

    public Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Long idUsuario = rs.getLong("id_usuario");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String email = rs.getString("email");
        int intentosFallidos = rs.getInt("intentos_fallidos");
        Timestamp timestampBloqueado = rs.getTimestamp("bloqueado_hasta");
        LocalDateTime fechaBloqueo = timestampBloqueado != null ? timestampBloqueado.toLocalDateTime() : null;
        String hash = rs.getString("password_hash");
        boolean activo = rs.getBoolean("activo");
        boolean debeCambiarContrasena = rs.getBoolean("debe_cambiar_contrasena");

        return Usuario.reconstruirDesdeBD(
                idUsuario, nombre, apellido, email, intentosFallidos, fechaBloqueo,hash, activo, debeCambiarContrasena
        );
    }

}//===================================================================================================================//

