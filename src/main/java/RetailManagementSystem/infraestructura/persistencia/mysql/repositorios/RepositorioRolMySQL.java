package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.excepciones.conflictos.RolDuplicadoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.RolNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioRol;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.VinculadorTransaccion;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorPermisos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorRol;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioRolMySQL implements RepositorioRol {

    //ATRIBUTOS:

    private final MapeadorRol mapeadorRol;

    private final MapeadorPermisos mapeadorPermisos;

    //CONSTRUCTOR:

    public RepositorioRolMySQL(MapeadorRol mapeadorRol, MapeadorPermisos mapeadorPermisos) {
        this.mapeadorRol = mapeadorRol;
        this.mapeadorPermisos = mapeadorPermisos;
    }

    //MÉTODOS:

    private void validarConexion(Connection conn){
        if (conn == null) {
            throw new IllegalStateException("NO hay una Transacción Activa para este Hilo");
        }
    }

    //CREATE:

    @Override
    public Rol insertarRol(Rol rolNuevo) {
        if (rolNuevo == null){
            throw new IllegalArgumentException("NO puedes Registrar un Rol Nulo.");
        }
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try {

            int idRolGenerado = insertarRegistroRol(conn, rolNuevo);

            if (!rolNuevo.getPermisos().isEmpty()) {
                insertarPermisosRol(conn, rolNuevo, idRolGenerado);
            }

            Rol rol =  Rol.reconstruirDesdeBD(
                    idRolGenerado,
                    rolNuevo.getNombre(),
                    rolNuevo.isActivo()
            );

            for (Permiso permiso:rolNuevo.getPermisos()){
                rol.recuperarPermisoDeBD(permiso);
            }

            return rol;

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
            "SELECT r.id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
                    "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo, " +
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
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_ROL)) {

            pstmt.setInt(1, idRol);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()){
                    throw new RolNoEncontradoException("El Rol de ID -" + idRol + "- NO Existe.");

                }

                return recuperarRolCompleto(rs);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Buscar el Rol por ID", e);
        }
    }

    private Rol recuperarRolCompleto(ResultSet rs) throws SQLException {
        Rol rol = this.mapeadorRol.mapearRol(rs);

        do {
            rs.getInt("id_permiso");
            if (!rs.wasNull()) {
                Permiso permiso = this.mapeadorPermisos.mapearPermiso(rs);
                rol.recuperarPermisoDeBD(permiso);
            }
        } while (rs.next());

        return rol;
    }


    private final static String SQL_OBTENER_ROLES_SEGUN_ESTADO =
            "SELECT r.id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
                    "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo, " +
                    "m.nombre AS nombre_modulo " +
                    "FROM roles r " +
                    "LEFT JOIN rol_permiso rp ON r.id_rol = rp.id_rol " +
                    "LEFT JOIN permisos p ON rp.id_permiso = p.id_permiso " +
                    "LEFT JOIN modulos m ON p.id_modulo = m.id_modulo " +
                    "ORDER BY r.id_rol, p.id_permiso ";

    @Override
    public List<Rol> obtenerTodosLosRoles() {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_ROLES_SEGUN_ESTADO);
             ResultSet rs = pstmt.executeQuery()) {

            return listarRolesDelResulSet(rs);

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los Todos los Roles.", e);
        }
    }

    private List<Rol> listarRolesDelResulSet(ResultSet rs) throws SQLException {
        Map<Integer, Rol> rolesMap = new LinkedHashMap<>();

        while (rs.next()){

            int idRol = rs.getInt("id_rol");
            Rol rolActual = rolesMap.get(idRol);
            if (rolActual == null) {
                rolActual = this.mapeadorRol.mapearRol(rs);
                rolesMap.put(idRol, rolActual);
            }

            rs.getInt("id_permiso");
            if (!rs.wasNull()) {
                Permiso permiso = this.mapeadorPermisos.mapearPermiso(rs);
                rolActual.recuperarPermisoDeBD(permiso);
            }

        }
        return new ArrayList<>(rolesMap.values());
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_DATOS_ROL =
            "UPDATE roles SET nombre = ?, activo = ? WHERE id_rol = ?";

    @Override
    public void actualizarDatosRol(Rol rol) {
        if (rol == null){
            throw new IllegalArgumentException("NO puedes Actualizar un Rol Nulo");
        }
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DATOS_ROL)) {

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
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try {

            borrarRelacionesRolPermisoViejas(conn, rol.getIdRol());

            if (!rol.getPermisos().isEmpty()) {
                insertarPermisosActualizados(conn, rol);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Actualizar los Permisos del Rol.", e);
        }
    }

    private static final String SQL_DELETE_DE_PERMISOS =
            "DELETE FROM rol_permiso WHERE id_rol = ?";

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

