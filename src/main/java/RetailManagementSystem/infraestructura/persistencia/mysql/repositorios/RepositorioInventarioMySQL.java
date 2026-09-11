package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.InventarioNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.VinculadorTransaccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioInventarioMySQL implements RepositorioInventario {

    //MÉTODOS:

    private void validarConexion(Connection conn){
        if (conn == null) {
            throw new IllegalStateException("NO hay una Transacción Activa para este Hilo");
        }
    }

    //CREATE:

    private static final String SQL_INSERTAR_INVENTARIO =
            "INSERT INTO inventarios (nombre, capacidad_maxima) VALUES (?, ?)";

    @Override
    public Inventario insertarInventario(Inventario borrador) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_INVENTARIO, Statement.RETURN_GENERATED_KEYS)) {

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
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_INVENTARIO)){

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
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_INVENTARIOS);
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

    private static final String SQL_VERIFICAR_CAPACIDAD_INVENTARIO =
            "SELECT " +
            "COALESCE(SUM(p.stock), 0) + ? > i.capacidad_maxima AS excedido " +
            "FROM inventarios i " +
            "LEFT JOIN productos p " +
            "ON i.id_inventario = p.id_inventario " +
            "WHERE i.id_inventario = ? " +
            "GROUP BY i.capacidad_maxima " +
            "FOR UPDATE ";

    @Override
    public void validarCapacidadInventario(int idInventario, int stockASumar){
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_VERIFICAR_CAPACIDAD_INVENTARIO)) {
            pstmt.setInt(1, stockASumar);
            pstmt.setInt(2, idInventario);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()){
                    boolean excedido = rs.getBoolean("excedido");

                    if (excedido){
                        throw new CapacidadInventarioExcedidaException("La Cantidad " + stockASumar + " Excede la capacidad del Inventario");
                    } else {
                        return;
                    }

                }

                throw new InventarioNoEncontradoException("NO Existe un Inventario con el ID: " + idInventario);

            }

        } catch (SQLException e){
            throw new PersistenciaException("Error inesperado en la transacción de base de datos", e);
        }
    }


    private static final String SQL_ACTUALIZAR_INVENTARIO =
            "UPDATE inventarios SET nombre = ?, capacidad_maxima = ? WHERE id_inventario = ?";

    @Override
    public void actualizarInventario(Inventario inventario) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_INVENTARIO)) {

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

