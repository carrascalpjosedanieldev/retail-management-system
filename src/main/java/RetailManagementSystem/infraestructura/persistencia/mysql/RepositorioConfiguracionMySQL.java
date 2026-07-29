package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.puertos.RepositorioConfiguracion;

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
                throw new RuntimeException("El Valor para la Configuración de Clave -" + clave + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Obtener la Configuración del Sistema", e);
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
                throw new RuntimeException("La Descripción para la Configuración de Clave -" + clave + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Obtener la Configuración del Sistema", e);
        }
    }

    @Override
    public void actualizarValorConfiguracion(String clave, String valor) {
        String sql = "UPDATE configuraciones_sistema SET valor = ? WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (valor != null){
                pstmt.setString(1, valor);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }

            pstmt.setString(2, clave);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException("NO se pudo Actualizar: La Clave NO le Pertenece a ninguna Configuración.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Actualizar la Configuración del Sistema", e);
        }
    }

    @Override
    public void actualizarDescripcionConfiguracion(String clave, String descripcion) {
        String sql = "UPDATE configuraciones_sistema SET descripcion = ? WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (descripcion != null){
                pstmt.setString(1, descripcion);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }

            pstmt.setString(2, clave);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException("NO se pudo Actualizar: La Clave NO le Pertenece a ninguna Configuración.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Actualizar la Configuración del Sistema", e);
        }
    }

    @Override
    public void actualizarValorYDescripcionConfiguracion(String clave, String valor, String descripcion) {
        String sql = "UPDATE configuraciones_sistema SET valor = ? , descripcion = ? WHERE clave = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (valor != null){
                pstmt.setString(1, valor);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }

            if (descripcion != null){
                pstmt.setString(2, descripcion);
            } else {
                pstmt.setNull(2, Types.VARCHAR);
            }

            pstmt.setString(3, clave);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException("NO se pudo Actualizar: La Clave NO le Pertenece a ninguna Configuración.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Actualizar la Configuración del Sistema", e);
        }

    }
}
