package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.UsuarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioUsuario;

import java.sql.*;
import java.time.LocalDateTime;

public class RepositorioUsuarioMySQL implements RepositorioUsuario {

    //CREATE:

    @Override
    public Usuario insertarUsuarioNuevo(Usuario usuario) {
        String sql =
                "INSERT INTO usuarios (nombre, apellido, email, password_hash, intentos_fallidos, " +
                "bloqueado_hasta, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                            usuario.isActivo());
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
    public Usuario obtenerUsuarioPorEmail(String email) {
        String sql =
                "SELECT id_usuario, nombre, apellido, email, password_hash, intentos_fallidos, bloqueado_hasta, activo " +
                "FROM usuarios " +
                "WHERE email = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    return Usuario.reconstruirDesdeBD(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getInt("intentos_fallidos"),
                            rs.getTimestamp("bloqueado_hasta").toLocalDateTime(),
                            rs.getString("password_hash"),
                            rs.getBoolean("activo")
                    );
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el Usuario por email", e);
        }
        return null;
    }

    @Override
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql =
                "SELECT id_usuario, nombre, apellido, email, password_hash, intentos_fallidos, bloqueado_hasta, activo " +
                        "FROM usuarios " +
                        "WHERE id_usuario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    return Usuario.reconstruirDesdeBD(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getInt("intentos_fallidos"),
                            rs.getTimestamp("bloqueado_hasta").toLocalDateTime(),
                            rs.getString("password_hash"),
                            rs.getBoolean("activo")
                    );
                }

                throw new UsuarioNoEncontradoException("El Usuario de ID -" + idUsuario + "- NO existe.");

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

}//===================================================================================================================//

