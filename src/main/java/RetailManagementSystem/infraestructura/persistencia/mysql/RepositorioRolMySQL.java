package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.excepciones.RolNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioRol;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.sql.*;
import java.util.List;

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
            }

        } catch (SQLException e) {
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
    public List<Rol> obtenerRoles() {
        return List.of();
    }

    //UPDATE:

    @Override
    public void actualizarRol(Rol rol) {

    }
}//===================================================================================================================//

