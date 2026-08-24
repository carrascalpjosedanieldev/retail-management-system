package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.PermisoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioPermiso;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPermisoMySQL implements RepositorioPermiso {

    //READ:

    private static final String SQL_OBTENER_PERMISO =
            "SELECT p.id_permiso, p.id_modulo, p.nombre, p.descripcion, p.activo, m.nombre AS nombre_modulo " +
            "FROM permisos p " +
            "INNER JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "WHERE p.id_permiso = ? ";


    @Override
    public Permiso obtenerPermiso(int idPermiso) {
        if (idPermiso <= 0){
            throw new IllegalArgumentException("El ID del Permiso debe ser un Numero Positivo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PERMISO)){

            pstmt.setInt(1, idPermiso);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()){
                    return this.extraerPermisoDeResultSet(rs);
                }

                throw new PermisoNoEncontradoException("NO existe un Permiso de ID:  " + idPermiso);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al obtener el Permiso", e);
        }
    }

    private Permiso extraerPermisoDeResultSet(ResultSet rs) throws SQLException {
        return Permiso.reconstruirDesdeBD(
                rs.getInt("id_permiso"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("nombre_modulo"),
                rs.getBoolean("activo")
        );
    }


    @Override
    public List<Permiso> obtenerPermisosActivos() {
        return obtenerPermisosPorEstado(true);
    }


    @Override
    public List<Permiso> obtenerPermisosInactivos() {
        return obtenerPermisosPorEstado(false);
    }


    private static final String SQL_OBTENER_PERMISOS_POR_ESTADO =
            "SELECT p.id_permiso, p.nombre, p.descripcion, p.activo, m.nombre AS nombre_modulo " +
            "FROM permisos p " +
            "INNER JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "WHERE p.activo = ?";


    private List<Permiso> obtenerPermisosPorEstado(boolean estado) {
        List<Permiso> permisos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PERMISOS_POR_ESTADO)) {

            pstmt.setBoolean(1, estado);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Permiso permiso = this.extraerPermisoDeResultSet(rs);
                    permisos.add(permiso);

                }

            }

        } catch (SQLException e) {
            String estadoStr = estado ? "Activos" : "Inactivos";
            throw new PersistenciaException("Error al listar los Permisos " + estadoStr, e);
        }
        return permisos;
    }


    private static final String SQL_OBTENER_NOMBRES_PERMISOS =
            "SELECT nombre FROM permisos ORDER BY id_permiso ASC";

    @Override
    public List<String> obtenerNombresTodosLosPermisos() {
        List<String> permisos = new ArrayList<>();

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_NOMBRES_PERMISOS)) {

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    String nombrePermiso = rs.getString("nombre");
                    permisos.add(nombrePermiso.toUpperCase());

                }

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los Nombres de los Permisos.", e);
        }
        return permisos;
    }


    //UPDATE:

    private static final String SQL_CAMBIAR_ESTADO =
            "UPDATE permisos SET activo = ? WHERE id_permiso = ?";

    @Override
    public void cambiarEstado(int idPermiso, boolean activo) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CAMBIAR_ESTADO)) {

            pstmt.setBoolean(1, activo);
            pstmt.setInt(2, idPermiso);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new PermisoNoEncontradoException("NO se pudo Actualizar: El Permiso con ID -" + idPermiso + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar el Permiso", e);
        }
    }


}//===================================================================================================================//

