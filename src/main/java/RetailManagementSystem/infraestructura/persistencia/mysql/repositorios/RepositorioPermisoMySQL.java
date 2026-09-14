package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.PermisoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPermiso;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.VinculadorTransaccion;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorPermisos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPermisoMySQL implements RepositorioPermiso {

    //ATRIBUTOS:

    private final MapeadorPermisos mapeadorPermisos;

    //CONSTRUCTOR:

    public RepositorioPermisoMySQL(MapeadorPermisos mapeadorPermisos) {
        this.mapeadorPermisos = mapeadorPermisos;
    }

    //MÉTODOS:

    private void validarConexion(Connection conn){
        if (conn == null) {
            throw new IllegalStateException("NO hay una Transacción Activa para este Hilo");
        }
    }

    //READ:

    private static final String SQL_OBTENER_PERMISO_POR_ID =
            "SELECT " +
            "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo, " +
            "m.nombre AS nombre_modulo " +
            "FROM permisos p " +
            "INNER JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "WHERE p.id_permiso = ?";

    @Override
    public Permiso obtenerPermisoPorId(int idPermiso) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PERMISO_POR_ID)) {

            pstmt.setInt(1, idPermiso);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()) {
                    return this.mapeadorPermisos.mapearPermiso(rs);
                }
                throw new PermisoNoEncontradoException("NO existe un Permiso con el ID: " + idPermiso);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al obtener el Permiso", e);
        }
    }

    private static final String SQL_OBTENER_PERMISOS_POR_ESTADO =
            "SELECT " +
            "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo, " +
            "m.nombre AS nombre_modulo " +
            "FROM permisos p " +
            "INNER JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "WHERE p.activo = TRUE";

    @Override
    public List<Permiso> obtenerPermisosActivos() {
        List<Permiso> permisos = new ArrayList<>();
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PERMISOS_POR_ESTADO);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Permiso permiso = this.mapeadorPermisos.mapearPermiso(rs);
                permisos.add(permiso);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los Permisos Activos", e);
        }
        return permisos;
    }


    private static final String SQL_OBTENER_TODOS_LOS_PERMISOS =
            "SELECT " +
            "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo, " +
            "m.nombre AS nombre_modulo " +
            "FROM permisos p " +
            "INNER JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "ORDER BY p.activo DESC ";

    @Override
    public List<Permiso> obtenerTodosLosPermisos() {
        List<Permiso> listaTodosLosPermisos = new ArrayList<>();
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_PERMISOS);
             ResultSet rs = pstmt.executeQuery()){

            while (rs.next()){

                Permiso permiso = this.mapeadorPermisos.mapearPermiso(rs);
                listaTodosLosPermisos.add(permiso);

            }

        } catch (SQLException e){
            throw new PersistenciaException("Error al listar todos los Permisos", e);
        }
        return listaTodosLosPermisos;
    }

    private static final String SQL_OBTENER_NOMBRES_PERMISOS =
            "SELECT nombre FROM permisos ORDER BY id_permiso ASC";

    @Override
    public List<String> obtenerNombresTodosLosPermisos() {
        List<String> permisos = new ArrayList<>();
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_NOMBRES_PERMISOS)) {

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
            "UPDATE permisos SET descripcion = ?, activo = ? WHERE id_permiso = ?";

    @Override
    public void actualizarPermiso(Permiso permiso) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_CAMBIAR_ESTADO)) {

            pstmt.setString(1, permiso.getDescripcion());
            pstmt.setBoolean(2, permiso.isActivo());
            pstmt.setInt(3, permiso.getIdPermiso());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new PermisoNoEncontradoException("NO se pudo Actualizar: El Permiso con ID -" +
                        permiso.getIdPermiso() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al Actualizar el Permiso", e);
        }
    }

}//===================================================================================================================//

