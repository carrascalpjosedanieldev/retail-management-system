package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Inventario;
import ProyectoPropio1.dominio.puertos.RepositorioInventario;
import ProyectoPropio1.excepciones.InventarioNoEncontradoException;
import ProyectoPropio1.excepciones.InventarioNoVacioException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioInventarioMySQL implements RepositorioInventario {

    @Override
    public Inventario insertarInventario(Inventario borrador) {
        String sql = "INSERT INTO inventarios (nombre, capacidad_maxima) VALUES (?, ?)";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, borrador.getNombre());
            pstmt.setInt(2, borrador.getCapacidadMaxima());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Inventario.reconstruirDesdeBD(idReal, borrador.getNombre(), borrador.getCapacidadMaxima(), 0);
                } else {
                    throw new RuntimeException("La inserción fue exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de persistencia al guardar el inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarInventario(int idInventario) {
        String sql = "DELETE FROM inventarios WHERE id_inventario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmtDelete = conn.prepareStatement(sql)) {

            pstmtDelete.setInt(1, idInventario);

            int filasAfectadas = pstmtDelete.executeUpdate();

            if (filasAfectadas == 0) {
                throw new InventarioNoEncontradoException("El inventario con ID " + idInventario + " no existe.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new InventarioNoVacioException("No se puede eliminar: El inventario tiene productos asociados.");
            }
            throw new RuntimeException("Error crítico al intentar eliminar el inventario.", e);
        }
    }

    @Override
    public Inventario obtenerInventario(int idInventario) {
        if (idInventario <= 0) {
            throw new IllegalArgumentException("El ID a buscar debe ser un número positivo.");
        }
        String sql = "SELECT i.id_inventario, i.nombre, i.capacidad_maxima, COALESCE(SUM(p.stock), 0) AS capacidad_ocupada " +
                "FROM inventarios i " +
                "LEFT JOIN productos p ON i.id_inventario = p.id_inventario " +
                "WHERE i.id_inventario = ? " +
                "GROUP BY i.id_inventario";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    int idReal = rs.getInt("id_inventario");
                    String nombre = rs.getString("nombre");
                    int capacidadMaxima = rs.getInt("capacidad_maxima");
                    int capacidadOcupada = rs.getInt("capacidad_ocupada");

                    return Inventario.reconstruirDesdeBD(idReal, nombre, capacidadMaxima, capacidadOcupada);
                }

                throw new InventarioNoEncontradoException("No existe un Inventario con el ID: " + idInventario);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener el inventario", e);
        }
    }

    @Override
    public void actualizarInventario(Inventario inventario) {
        String sql = "UPDATE inventarios SET nombre = ?, capacidad_maxima = ? WHERE id_inventario = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inventario.getNombre());
            pstmt.setInt(2, inventario.getCapacidadMaxima());
            pstmt.setInt(3, inventario.getIdInventario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new InventarioNoEncontradoException("No se pudo actualizar: El inventario con ID " + inventario.getIdInventario() + " no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al actualizar el inventario", e);
        }
    }

    @Override
    public List<Inventario> obtenerTodosInventariosConCapacidadOcupada() {
        List<Inventario> inventarios = new ArrayList<>();
        String sql = "SELECT i.id_inventario, i.nombre, i.capacidad_maxima, COALESCE(SUM(p.stock), 0) AS capacidad_ocupada " +
                "FROM inventarios i " +
                "LEFT JOIN productos p ON i.id_inventario = p.id_inventario " +
                "GROUP BY i.id_inventario";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Inventario inv = Inventario.reconstruirDesdeBD(
                        rs.getInt("id_inventario"),
                        rs.getString("nombre"),
                        rs.getInt("capacidad_maxima"),
                        rs.getInt("capacidad_ocupada")
                );
                inventarios.add(inv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar inventarios", e);
        }
        return inventarios;
    }

}

