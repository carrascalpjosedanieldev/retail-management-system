package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.UsuarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.VinculadorTransaccion;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorPermisos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorRol;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RepositorioUsuarioMySQL implements RepositorioUsuario {

    //ATRIBUTOS:

    private final MapeadorUsuario mapeadorUsuario;

    private final MapeadorRol mapeadorRol;

    private final MapeadorPermisos mapeadorPermisos;

    //CONSTRUCTOR:

    public RepositorioUsuarioMySQL(
            MapeadorUsuario mapeadorUsuario, MapeadorRol mapeadorRol, MapeadorPermisos mapeadorPermisos
    ) {
        this.mapeadorUsuario = mapeadorUsuario;
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

    private static final String SQL_INSERTAR_USUARIO =
            "INSERT INTO usuarios (nombre, apellido, email, password_hash, intentos_fallidos, " +
            "bloqueado_hasta, activo, debe_cambiar_contrasena) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public Usuario insertarUsuarioNuevo(Usuario usuario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try(PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_USUARIO, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getHash());
            pstmt.setInt(5, usuario.getIntentosFallidos());

            LocalDateTime bloqueadoHasta = usuario.getBloqueadoHasta();
            if (bloqueadoHasta != null){
                pstmt.setTimestamp(6, Timestamp.valueOf(bloqueadoHasta));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }

            pstmt.setBoolean(7, usuario.isActivo());
            pstmt.setBoolean(8, usuario.isDebeCambiarContrasena());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La Inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    Long idReal = gk.getLong(1);
                    return Usuario.reconstruirDesdeBD(
                            idReal, usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                            usuario.getIntentosFallidos(), usuario.getBloqueadoHasta(), usuario.getHash(),
                            usuario.isActivo(), usuario.isDebeCambiarContrasena());
                } else {
                    throw new IdAutogeneradoNoRecibidoException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new EmailDuplicadoException("El correo electrónico ya se encuentra registrado en el sistema.");

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al crear el usuario", e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_USUARIO_POR_EMAIL =
            "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.password_hash, u.intentos_fallidos, " +
            "u.bloqueado_hasta, u.activo, u.debe_cambiar_contrasena, " +
            "r.id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
            "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.id_modulo, p.activo AS permiso_activo, " +
            "m.nombre AS nombre_modulo " +
            "FROM usuarios u " +
            "LEFT JOIN usuario_rol urol ON u.id_usuario = urol.id_usuario " +
            "LEFT JOIN roles r ON urol.id_rol = r.id_rol " +
            "LEFT JOIN rol_permiso rolp ON r.id_rol = rolp.id_rol " +
            "LEFT JOIN permisos p ON rolp.id_permiso = p.id_permiso " +
            "LEFT JOIN modulos m ON p.id_modulo = m.id_modulo " +
            "WHERE u.email = ?";

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_USUARIO_POR_EMAIL)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                return recuperarUsuarioCompleto(rs);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el Usuario por email" + e.getMessage(), e);
        }
    }

    private Optional<Usuario> recuperarUsuarioCompleto(ResultSet rs) throws SQLException{

        Usuario usuario = null;
        Map<Integer, Rol> rolesMap = new HashMap<>();

        while (rs.next()) {

            if (usuario == null) {
                usuario = this.mapeadorUsuario.mapearUsuario(rs);
            }

            int idRol = rs.getInt("id_rol");
            if (!rs.wasNull()){

                Rol rol = rolesMap.get(idRol);
                if (rol == null) {
                    rol = this.mapeadorRol.mapearRol(rs);
                    rolesMap.put(idRol, rol);
                }

                rs.getInt("id_permiso");
                if (!rs.wasNull()){

                    Permiso permiso = this.mapeadorPermisos.mapearPermiso(rs);
                    rol.recuperarPermisoDeBD(permiso);

                }

            }

        }

        if (usuario != null){
            for (Rol rol:rolesMap.values()){
                usuario.recuperarRolDeBD(rol);
            }
        }
        return Optional.ofNullable(usuario);

    }


    private static final String SQL_OBTENER_USUARIO_POR_ID =
            "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.password_hash, u.intentos_fallidos, " +
                    "u.bloqueado_hasta, u.activo, u.debe_cambiar_contrasena, " +
                    "r.id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
                    "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.id_modulo, p.activo AS permiso_activo, " +
                    "m.nombre AS nombre_modulo " +
                    "FROM usuarios u " +
                    "LEFT JOIN usuario_rol urol ON u.id_usuario = urol.id_usuario " +
                    "LEFT JOIN roles r ON urol.id_rol = r.id_rol " +
                    "LEFT JOIN rol_permiso rolp ON r.id_rol = rolp.id_rol " +
                    "LEFT JOIN permisos p ON rolp.id_permiso = p.id_permiso " +
                    "LEFT JOIN modulos m ON p.id_modulo = m.id_modulo " +
                    "WHERE u.id_usuario = ?";

    @Override
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_USUARIO_POR_ID)) {

            pstmt.setLong(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {

                return recuperarUsuarioCompleto(rs)
                        .orElseThrow(() -> new UsuarioNoEncontradoException("El Usuario de ID -" + idUsuario + "- NO Existe."));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el Usuario por ID", e);
        }
    }


    private static final String SQL_OBTENER_TODOS_LOS_USUARIOS =
            "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.password_hash, u.intentos_fallidos, " +
            "u.bloqueado_hasta, u.activo, u.debe_cambiar_contrasena " +
            "FROM usuarios u " +
            "ORDER BY u.activo DESC, u.id_usuario ASC ";

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_USUARIOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()){

                Usuario usuario = this.mapeadorUsuario.mapearUsuario(rs);
                listaUsuarios.add(usuario);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Listar los Usuarios.", e);
        }
        return listaUsuarios;
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_DATOS_LOGIN =
            "UPDATE usuarios SET intentos_fallidos = ?, bloqueado_hasta = ? WHERE id_usuario = ?";

    @Override
    public void actualizarDatosLoginUsuario(Usuario usuario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DATOS_LOGIN)) {

            pstmt.setInt(1, usuario.getIntentosFallidos());

            if (usuario.getBloqueadoHasta() != null) {
                pstmt.setTimestamp(2, Timestamp.valueOf(usuario.getBloqueadoHasta()));
            } else {
                pstmt.setNull(2, Types.TIMESTAMP);
            }

            pstmt.setLong(3, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" +
                        usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al Actualizar la Seguridad del Usuario", e);
        }
    }


    private static final String SQL_ACTUALIZAR_DATOS_USUARIO =
            "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, activo = ? WHERE id_usuario = ?";

    @Override
    public void actualizarDatosUsuario(Usuario usuario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DATOS_USUARIO)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setBoolean(4, usuario.isActivo());
            pstmt.setLong(5, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" +
                        usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar la seguridad del usuario", e);
        }
    }


    private static final String SQL_ACTUALIZAR_SEGURIDAD =
            "UPDATE usuarios SET password_hash = ?, debe_cambiar_contrasena = ?, " +
            "intentos_fallidos = ?, bloqueado_hasta = ? WHERE id_usuario = ?";

    @Override
    public void actualizarSeguridad(Usuario usuario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_SEGURIDAD)) {

            pstmt.setString(1, usuario.getHash());
            pstmt.setBoolean(2, usuario.isDebeCambiarContrasena());
            pstmt.setInt(3, usuario.getIntentosFallidos());

            if (usuario.getBloqueadoHasta() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(usuario.getBloqueadoHasta()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setLong(5, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" + usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar la Seguridad del Usuario", e);
        }
    }


    @Override
    public void actualizarRolesUsuario(Usuario usuario) {
        if (usuario == null){
            throw new IllegalArgumentException("NO puedes Actualizar un Usuario Nulo");
        }
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try {

            borrarRelacionesUsuarioRolViejas(conn, usuario.getIdUsuario());

            if (!usuario.getRoles().isEmpty()) {
                insertarPermisosActualizados(conn, usuario);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de Base de Datos al Actualizar los Roles del Usuario", e);
        }
    }

    private static final String SQL_DELETE_ROLES =
            "DELETE FROM usuario_rol WHERE id_usuario = ?";

    private void borrarRelacionesUsuarioRolViejas(Connection conn, Long idUsuario) throws SQLException{
        try (PreparedStatement psDelete = conn.prepareStatement(SQL_DELETE_ROLES)) {
            psDelete.setLong(1, idUsuario);
            psDelete.executeUpdate();
        }
    }

    private static final String SQL_INSERT_DE_ROLES_ACTUALIZADOS =
            "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";

    private void insertarPermisosActualizados(Connection conn, Usuario usuario) throws SQLException{
        try (PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT_DE_ROLES_ACTUALIZADOS)) {

            psInsert.setLong(1, usuario.getIdUsuario());
            for (Rol rol: usuario.getRoles()){
                psInsert.setInt(2, rol.getIdRol());
                psInsert.addBatch();
            }

            psInsert.executeBatch();

        }
    }

}//===================================================================================================================//

