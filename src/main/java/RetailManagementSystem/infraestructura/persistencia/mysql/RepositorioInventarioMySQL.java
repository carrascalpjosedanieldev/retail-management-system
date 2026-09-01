package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.puertos.RepositorioInventario;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.InventarioNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioInventarioMySQL implements RepositorioInventario {

    //CREATE:

    private static final String SQL_INSERTAR_INVENTARIO =
            "INSERT INTO inventarios (nombre, capacidad_maxima) VALUES (?, ?)";

    @Override
    public Inventario insertarInventario(Inventario borrador) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_INVENTARIO, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, borrador.getNombre());
            pstmt.setInt(2, borrador.getCapacidadMaxima());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Inventario.reconstruirDesdeBD(
                            idReal,
                            borrador.getNombre(),
                            borrador.getCapacidadMaxima(),
                            0
                    );
                } else {
                    throw new IdAutogeneradoNoRecibidoException("La inserción fue exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de persistencia al guardar el inventario: " + e.getMessage(), e);
        }
    }

    //READ:

    private static final String SQL_OBTENER_INVENTARIO =
            "SELECT i.id_inventario, i.nombre, i.capacidad_maxima, COALESCE(SUM(p.stock), 0) AS capacidad_ocupada " +
            "FROM inventarios i " +
            "LEFT JOIN productos p ON i.id_inventario = p.id_inventario " +
            "WHERE i.id_inventario = ? " +
            "GROUP BY i.id_inventario";

    @Override
    public Inventario obtenerInventario(int idInventario) {
        if (idInventario <= 0) {
            throw new IllegalArgumentException("El ID a buscar debe ser un Número Positivo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_INVENTARIO)){

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
            throw new PersistenciaException("Error de base de datos al obtener el inventario", e);
        }
    }


    private static final String SQL_OBTENER_TODOS_LOS_INVENTARIOS =
            "SELECT i.id_inventario, i.nombre, i.capacidad_maxima, COALESCE(SUM(p.stock), 0) AS capacidad_ocupada " +
            "FROM inventarios i " +
            "LEFT JOIN productos p ON i.id_inventario = p.id_inventario " +
            "GROUP BY i.id_inventario";

    @Override
    public List<Inventario> obtenerTodosInventariosConCapacidadOcupada() {
        List<Inventario> inventarios = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_INVENTARIOS);
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
            throw new PersistenciaException("Error al listar inventarios", e);
        }
        return inventarios;
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_INVENTARIO =
            "UPDATE inventarios SET nombre = ?, capacidad_maxima = ? WHERE id_inventario = ?";

    @Override
    public void actualizarInventario(Inventario inventario) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_INVENTARIO)) {

            pstmt.setString(1, inventario.getNombre());
            pstmt.setInt(2, inventario.getCapacidadMaxima());
            pstmt.setInt(3, inventario.getIdInventario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new InventarioNoEncontradoException("No se pudo actualizar: El inventario con ID " + inventario.getIdInventario() + " no existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar el inventario", e);
        }
    }


}//===================================================================================================================//

