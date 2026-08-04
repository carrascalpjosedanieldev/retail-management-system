package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.UsuarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class RepositorioUsuarioMySQL implements RepositorioUsuario {

    //CREATE:

    @Override
    public Usuario insertarUsuarioNuevo(Usuario usuario) {
        String sql =
                "INSERT INTO usuarios (nombre, apellido, email, password_hash, intentos_fallidos, " +
                "bloqueado_hasta, activo, debe_cambiar_contrasena) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = AdministradorConexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                throw new RuntimeException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Usuario.reconstruirDesdeBD(
                            idReal, usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                            usuario.getIntentosFallidos(), usuario.getBloqueadoHasta(), usuario.getHash(),
                            usuario.isActivo(), usuario.isDebeCambiarContrasena());
                } else {
                    throw new RuntimeException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new EmailDuplicadoException("El correo electrónico ya se encuentra registrado en el sistema.");
            }
            throw new RuntimeException("Error de base de datos al crear el usuario", e);
        }
    }


    //READ:

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        String sql =
                "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.password_hash, u.intentos_fallidos, " +
                        "u.bloqueado_hasta, u.activo, u.debe_cambiar_contrasena, " +
                        "r.id_rol AS rol_id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
                        "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo " +
                        "FROM usuarios u " +
                        "LEFT JOIN usuario_rol urol ON u.id_usuario = urol.id_usuario " +
                        "LEFT JOIN roles r ON urol.id_rol = r.id_rol " +
                        "LEFT JOIN rol_permiso rolp ON r.id_rol = rolp.id_rol " +
                        "LEFT JOIN permisos p ON rolp.id_permiso = p.id_permiso " +
                        "WHERE u.email = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                Usuario usuario = null;
                Map<Integer, Rol> rolesMap = new HashMap<>();

                while (rs.next()) {

                    if (usuario == null) {
                        Timestamp timestampBloqueado = rs.getTimestamp("bloqueado_hasta");
                        LocalDateTime fechaBloqueo = (timestampBloqueado != null) ? timestampBloqueado.toLocalDateTime() : null;
                        usuario = Usuario.reconstruirDesdeBD(
                                rs.getInt("id_usuario"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("email"),
                                rs.getInt("intentos_fallidos"),
                                fechaBloqueo,
                                rs.getString("password_hash"),
                                rs.getBoolean("activo"),
                                rs.getBoolean("debe_cambiar_contrasena")
                        );
                    }

                    Integer idRol = (Integer) rs.getObject("rol_id_rol");
                    if (idRol != null){

                        Rol rol = rolesMap.get(idRol);
                        if (rol == null) {
                            rol = Rol.reconstruirDesdeBD(
                                    idRol,
                                    rs.getString("nombre_rol"),
                                    new HashSet<>(),
                                    rs.getBoolean("rol_activo")
                            );
                            rolesMap.put(idRol, rol);
                        }

                        Integer idPermiso = (Integer) rs.getObject("id_permiso");
                        if (idPermiso != null){

                            Permiso permiso = Permiso.reconstruirDesdeBD(
                                    idPermiso,
                                    rs.getString("nombre_permiso"),
                                    rs.getString("descripcion"),
                                    rs.getBoolean("permiso_activo")
                            );
                            rol.anadirPermiso(permiso);

                        }

                    }

                }

                if (usuario != null){
                    for (Rol rol:rolesMap.values()){
                        usuario.anadirRol(rol);
                    }
                }
                return Optional.ofNullable(usuario);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el Usuario por email", e);
        }
    }

    @Override
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql =
                "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.password_hash, u.intentos_fallidos, " +
                        "u.bloqueado_hasta, u.activo, u.debe_cambiar_contrasena, " +
                        "r.id_rol AS rol_id_rol, r.nombre AS nombre_rol, r.activo AS rol_activo, " +
                        "p.id_permiso, p.nombre AS nombre_permiso, p.descripcion, p.activo AS permiso_activo " +
                        "FROM usuarios u " +
                        "LEFT JOIN usuario_rol urol ON u.id_usuario = urol.id_usuario " +
                        "LEFT JOIN roles r ON urol.id_rol = r.id_rol " +
                        "LEFT JOIN rol_permiso rolp ON r.id_rol = rolp.id_rol " +
                        "LEFT JOIN permisos p ON rolp.id_permiso = p.id_permiso " +
                        "WHERE u.id_usuario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {

                Usuario usuario = null;
                Map<Integer, Rol> rolesMap = new HashMap<>();

                while (rs.next()) {

                    if (usuario == null) {
                        Timestamp timestampBloqueado = rs.getTimestamp("bloqueado_hasta");
                        LocalDateTime fechaBloqueo = (timestampBloqueado != null) ? timestampBloqueado.toLocalDateTime() : null;
                        usuario = Usuario.reconstruirDesdeBD(
                                rs.getInt("id_usuario"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("email"),
                                rs.getInt("intentos_fallidos"),
                                fechaBloqueo,
                                rs.getString("password_hash"),
                                rs.getBoolean("activo"),
                                rs.getBoolean("debe_cambiar_contrasena")
                        );
                    }

                    Integer idRol = (Integer) rs.getObject("rol_id_rol");
                    if (idRol != null){

                        Rol rol = rolesMap.get(idRol);
                        if (rol == null) {
                            rol = Rol.reconstruirDesdeBD(
                                    idRol,
                                    rs.getString("nombre_rol"),
                                    new HashSet<>(),
                                    rs.getBoolean("rol_activo")
                            );
                            rolesMap.put(idRol, rol);
                        }

                        Integer idPermiso = (Integer) rs.getObject("id_permiso");
                        if (idPermiso != null){

                            Permiso permiso = Permiso.reconstruirDesdeBD(
                                    idPermiso,
                                    rs.getString("nombre_permiso"),
                                    rs.getString("descripcion"),
                                    rs.getBoolean("permiso_activo")
                            );
                            rol.anadirPermiso(permiso);

                        }

                    }

                }

                if (usuario != null){
                    for (Rol rol:rolesMap.values()){
                        usuario.anadirRol(rol);
                    }
                    return usuario;
                }

                throw new UsuarioNoEncontradoException("El Usuario de ID -" + idUsuario + "- NO Existe.");

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el Usuario por email", e);
        }
    }

    //UPDATE:

    @Override
    public void actualizarDatosLoginUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = ?, bloqueado_hasta = ? WHERE id_usuario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuario.getIntentosFallidos());

            if (usuario.getBloqueadoHasta() != null) {
                pstmt.setTimestamp(2, Timestamp.valueOf(usuario.getBloqueadoHasta()));
            } else {
                pstmt.setNull(2, Types.TIMESTAMP);
            }

            pstmt.setInt(3, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" + usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al Actualizar la Seguridad del Usuario", e);
        }
    }

    @Override
    public void actualizarDatosUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, activo = ? WHERE id_usuario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setBoolean(4, usuario.isActivo());
            pstmt.setInt(5, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" + usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la seguridad del usuario", e);
        }
    }

    @Override
    public void actualizarSeguridad(Usuario usuario) {
        String sql = "UPDATE usuarios SET password_hash = ?, debe_cambiar_contrasena = ?, " +
                "intentos_fallidos = ?, bloqueado_hasta = ? WHERE id = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getHash());
            pstmt.setBoolean(2, usuario.isDebeCambiarContrasena());
            pstmt.setInt(3, usuario.getIntentosFallidos());

            if (usuario.getBloqueadoHasta() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(usuario.getBloqueadoHasta()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setInt(5, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new UsuarioNoEncontradoException("NO se pudo Actualizar: El Usuario con ID -" + usuario.getIdUsuario() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la seguridad del usuario", e);
        }
    }

}//===================================================================================================================//

