package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.puertos.RepositorioConfiguracion;

import java.sql.*;

public class RepositorioConfiguracionMySQL implements RepositorioConfiguracion {

    @Override
    public String obtenerValorConfiguracion(String clave) {
        String sql = "SELECT valor FROM configuraciones_sistema WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, clave);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valor");
                }
                throw new RuntimeException("El valor para la configuración de clave '" + clave + "' no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al leer la configuración del sistema", e);
        }
    }

    @Override
    public String obtenerDescripcionConfiguracion(String clave) {
        String sql = "SELECT descripcion FROM configuraciones_sistema WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, clave);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("descripcion");
                }
                throw new RuntimeException("La descripcion para la configuración de clave '" + clave + "' no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al leer la configuración del sistema", e);
        }
    }

    @Override
    public void actualizarValorConfiguracion(String clave, String valor, String descripcion) {
        String sql = "UPDATE configuraciones_sistema SET valor = ?, descripcion = ? WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (valor != null){
                pstmt.setString(1, valor);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }

            if (descripcion != null) {
                pstmt.setString(2, descripcion);
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setString(3, clave);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException("No se pudo actualizar: La clave no le pertenece a ninguna configuracion.");
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error al leer la configuración del sistema", e);
        }

    }

}
