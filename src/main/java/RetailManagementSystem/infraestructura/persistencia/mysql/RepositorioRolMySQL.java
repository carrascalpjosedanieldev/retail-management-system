package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.excepciones.RolDuplicadoException;
import RetailManagementSystem.dominio.excepciones.RolNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioRol;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioRolMySQL implements RepositorioRol {

    //CREATE:

    @Override
    public void insertarRol(Rol rolNuevo) {
        if (rolNuevo == null){
            throw new IllegalArgumentException("NO puedes Registrar un Rol Nulo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion()) {

            try {
                conn.setAutoCommit(false);

                int idRolGenerado = this.insertarRegistroRol(conn, rolNuevo);

                if (!rolNuevo.getPermisos().isEmpty()) {
                    this.insertarPermisosRol(conn, rolNuevo, idRolGenerado);
                }

                conn.commit();

            } catch (SQLException originalException) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    originalException.addSuppressed(rollbackEx);
                }
                throw new RuntimeException("Error en la Transacción de Inserción", originalException);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) { }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new RolDuplicadoException("El Rol ya se encuentra Registrado en el Sistema.");
            }
            throw new PersistenciaException("Error Crítico de Infraestructura al Obtener Conexión", e);
        }
    }

    private static final String SQL_INSERTAR_REGISTRO_ROL =
            "INSERT INTO roles (nombre, activo) VALUES (?, ?)";

    private int insertarRegistroRol(Connection conn, Rol rolNuevo) throws SQLException{
        try (PreparedStatement pstmt = conn.prepareStatement(
                SQL_INSERTAR_REGISTRO_ROL, Statement.RETURN_GENERATED_KEYS
        )) {

            pstmt.setString(1, rolNuevo.getNombre());
            pstmt.setBoolean(2, rolNuevo.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("La Inserción Falló: Ninguna fila fue afectada en la Base de Datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    return gk.getInt(1);
                } else {
                    throw new SQLException("NO se pudo obtener el ID autogenerado para el Rol.");
                }
            }

        }
    }

    private static final String SQL_INSERTAR_PERMISOS_ROL =
            "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (?, ?)";

    private void insertarPermisosRol(Connection conn, Rol rolNuevo, int idRolGenerado) throws SQLException{
        try (PreparedStatement psHijo = conn.prepareStatement(SQL_INSERTAR_PERMISOS_ROL)) {

            psHijo.setInt(1, idRolGenerado);
            for (Permiso permiso: rolNuevo.getPermisos()){

                psHijo.setInt(2, permiso.getIdPermiso());
                psHijo.addBatch();

            }
            psHijo.executeBatch();

        }
    }

    //READ:

    private static final String SQL_OBTENER_ROL =
            "SELECT r.id_rol, r.nombre, r.activo, " +
                    "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS activo_permiso, " +
                    "m.nombre AS nombre_modulo " +
                    "FROM roles r " +
                    "LEFT JOIN rol_permiso rp ON r.id_rol = rp.id_rol " +
                    "LEFT JOIN permisos p ON rp.id_permiso = p.id_permiso " +
                    "LEFT JOIN modulos m ON p.id_modulo = m.id_modulo " +
                    "WHERE r.id_rol = ? ";

    @Override
    public Rol obtenerRol(int idRol) {
        if (idRol <=0){
            throw new IllegalArgumentException("El ID -" + idRol + "- NO es valido.");
        }

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_ROL)) {

            pstmt.setInt(1, idRol);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()){
                    throw new RolNoEncontradoException("El Rol de ID -" + idRol + "- NO Existe.");

                }

                Rol rol = Rol.reconstruirDesdeBD(
                        rs.getInt("id_rol"),
                        rs.getString("nombre"),
                        rs.getBoolean("activo")
                );

                do {

                    int idPermiso = rs.getInt("id_permiso");
                    if (!rs.wasNull()) {
                        Permiso permiso = Permiso.reconstruirDesdeBD(
                                idPermiso,
                                rs.getString("nombre_permiso"),
                                rs.getString("descripcion"),
                                rs.getString("nombre_modulo"),
                                rs.getBoolean("activo_permiso")
                        );
                        rol.recuperarPermisoDeBD(permiso);
                    }

                } while (rs.next());

                return rol;

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Buscar el Rol por ID", e);
        }
    }


    @Override
    public List<Rol> obtenerRolesActivos() {
        return this.obtenerRolesPorEstado(true);
    }


    @Override
    public List<Rol> obtenerRolesInactivos() {
        return this.obtenerRolesPorEstado(false);
    }


    private final static String SQL_OBTENER_ROLES_SEGUN_ESTADO =
            "SELECT r.id_rol, r.nombre, r.activo, " +
                    "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS activo_permiso, " +
                    "m.nombre AS nombre_modulo " +
                    "FROM roles r " +
                    "LEFT JOIN rol_permiso rp ON r.id_rol = rp.id_rol " +
                    "LEFT JOIN permisos p ON rp.id_permiso = p.id_permiso " +
                    "LEFT JOIN modulos m ON p.id_modulo = m.id_modulo " +
                    "WHERE r.activo = ? ORDER BY r.id_rol, p.id_permiso ";

    private List<Rol> obtenerRolesPorEstado(boolean activo) {

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_ROLES_SEGUN_ESTADO)) {

            pstmt.setBoolean(1, activo);

            try (ResultSet rs = pstmt.executeQuery()) {

                Map<Integer, Rol> rolesMap = new LinkedHashMap<>();

                while (rs.next()){

                    int idRol = rs.getInt("id_rol");
                    Rol rolActual = rolesMap.get(idRol);
                    if (rolActual == null) {
                        rolActual = Rol.reconstruirDesdeBD(
                                idRol,
                                rs.getString("nombre"),
                                rs.getBoolean("activo")
                        );
                        rolesMap.put(idRol, rolActual);
                    }

                    int idPermiso = rs.getInt("id_permiso");
                    if (!rs.wasNull()) {
                        Permiso permiso = Permiso.reconstruirDesdeBD(
                                idPermiso,
                                rs.getString("nombre_permiso"),
                                rs.getString("descripcion"),
                                rs.getString("nombre_modulo"),
                                rs.getBoolean("activo_permiso")
                        );
                        rolActual.recuperarPermisoDeBD(permiso);
                    }

                }
                return new ArrayList<>(rolesMap.values());

            }

        } catch (SQLException e) {
            String estadoStr = activo ? "Activos" : "Inactivos";
            throw new PersistenciaException("Error al listar los Roles " + estadoStr, e);
        }
    }

    //UPDATE:

    private static final String SQL_ACTUALIZAR_DATOS_ROL =
            "UPDATE roles SET nombre = ?, activo = ? WHERE id_rol = ?";

    @Override
    public void actualizarDatosRol(Rol rol) {
        if (rol == null){
            throw new IllegalArgumentException("NO puedes Actualizar un Rol Nulo");
        }

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DATOS_ROL)) {

            pstmt.setString(1, rol.getNombre());
            pstmt.setBoolean(2, rol.isActivo());
            pstmt.setInt(3, rol.getIdRol());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RolNoEncontradoException("NO se pudo Actualizar: El Rol con ID -" + rol.getIdRol() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de Base de Datos al Actualizar el Rol", e);
        }
    }


    @Override
    public void actualizarPermisosRol(Rol rol) {
        if (rol == null){
            throw new IllegalArgumentException("NO puedes Actualizar un Rol Nulo");
        }

        try (Connection conn = AdministradorConexion.obtenerConexion()) {

            try {
                conn.setAutoCommit(false);

                this.borrarRelacionesRolPermisoViejas(conn, rol.getIdRol());

                if (!rol.getPermisos().isEmpty()) {
                    this.insertarPermisosActualizados(conn, rol);
                }

                conn.commit();

            } catch (SQLException originalException) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    originalException.addSuppressed(rollbackEx);
                }
                throw new PersistenciaException("Error en la Transacción de Actualización de Permisos", originalException);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) { }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de Base de Datos al Actualizar los Permisos del Rol", e);
        }
    }

    private static final String SQL_DELETE_DE_PERMISOS = "DELETE FROM rol_permiso WHERE id_rol = ?";

    private void borrarRelacionesRolPermisoViejas(Connection conn, int idRol) throws SQLException {
        try (PreparedStatement psDelete = conn.prepareStatement(SQL_DELETE_DE_PERMISOS)) {
            psDelete.setInt(1, idRol);
            psDelete.executeUpdate();
        }
    }

    private static final String SQL_INSERT_DE_PERMISOS_ACTUALIZADOS =
            "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (?, ?)";

    private void insertarPermisosActualizados(Connection conn, Rol rol) throws SQLException {
        try (PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT_DE_PERMISOS_ACTUALIZADOS)) {

            psInsert.setInt(1, rol.getIdRol());
            for (Permiso permiso: rol.getPermisos()){
                psInsert.setInt(2, permiso.getIdPermiso());
                psInsert.addBatch();
            }

            psInsert.executeBatch();

        }
    }

}//===================================================================================================================//

