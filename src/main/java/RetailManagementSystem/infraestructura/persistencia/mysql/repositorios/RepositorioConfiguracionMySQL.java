package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.AdministradorConexion;

import java.sql.*;

public class RepositorioConfiguracionMySQL implements RepositorioConfiguracion {

    //READ:

    private static final String SQL_OBTENER_VALOR_CONFIGURACION =
            "SELECT valor FROM configuraciones_sistema WHERE clave = ?";

    @Override
    public String obtenerValorConfiguracion(String clave) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_VALOR_CONFIGURACION)) {

            pstmt.setString(1, clave);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valor");
                }
                throw new RuntimeException("El Valor para la Configuración de Clave -" + clave + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Obtener la Configuración del Sistema", e);
        }
    }


    private static final String SQL_OBTENER_DESCRIPCION_CONFIGURACION =
            "SELECT descripcion FROM configuraciones_sistema WHERE clave = ?";

    @Override
    public String obtenerDescripcionConfiguracion(String clave) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_DESCRIPCION_CONFIGURACION)) {

            pstmt.setString(1, clave);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("descripcion");
                }
                throw new RuntimeException("La Descripción para la Configuración de Clave -" + clave + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Obtener la Configuración del Sistema", e);
        }
    }

    //UPDATE:

    private static final String SQL_ACTUALIZAR_VALOR_CONFIGURACION =
            "UPDATE configuraciones_sistema SET valor = ? WHERE clave = ?";

    @Override
    public void actualizarValorConfiguracion(String clave, String valor) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_VALOR_CONFIGURACION)) {

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
            throw new PersistenciaException("Error al Actualizar la Configuración del Sistema", e);
        }
    }


    private static final String SQL_ACTUALIZAR_DESCRIPCION_CONFIGURACION =
            "UPDATE configuraciones_sistema SET descripcion = ? WHERE clave = ?";

    @Override
    public void actualizarDescripcionConfiguracion(String clave, String descripcion) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DESCRIPCION_CONFIGURACION)) {

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
            throw new PersistenciaException("Error al Actualizar la Configuración del Sistema", e);
        }
    }


    private static final String SQL_ACTUALIZAR_VALOR_Y_DESCRIPCION =
            "UPDATE configuraciones_sistema SET valor = ? , descripcion = ? WHERE clave = ?";

    @Override
    public void actualizarValorYDescripcionConfiguracion(String clave, String valor, String descripcion) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_VALOR_Y_DESCRIPCION)) {

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
            throw new PersistenciaException("Error al Actualizar la Configuración del Sistema", e);
        }
    }


}//===================================================================================================================//

