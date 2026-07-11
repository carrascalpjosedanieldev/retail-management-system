package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.excepciones.DescuentoNoEncontradoExeption;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDescuentosMySQL implements RepositorioDescuentos {

    @Override
    public Descuento insertarDescuento(Descuento descuento) {
        String sql = "INSERT INTO descuentos (nombre, porcentaje, activo) VALUES (?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, descuento.getNombre());
            pstmt.setBigDecimal(2, descuento.getPorcentaje());
            pstmt.setBoolean(3, descuento.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt("id_descuento");
                    return Descuento.reconstruirDesdeBD(idReal, descuento.getNombre(), descuento.getPorcentaje(), descuento.isActivo());
                } else {
                    throw new RuntimeException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe un Descuento registrado con el nombre: " + descuento.getNombre());
            }
            throw new RuntimeException("Error crítico de persistencia al guardar el Descuento: " + e.getMessage(), e);
        }
    }


    @Override
    public Descuento obtenerDescuento(int idDescuento) {
        if (idDescuento<=0) {
            throw new IllegalStateException("El ID a buscar debe ser un número positivo.");
        }
        String sql = "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos WHERE id_descuento = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, idDescuento);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    int idReal = rs.getInt("id_descuento");
                    String nombre = rs.getString("nombre");
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
                    boolean activo = rs.getBoolean("activo");

                    return Descuento.reconstruirDesdeBD(idReal, nombre, porcentaje, activo);
                }

                throw new DescuentoNoEncontradoExeption("No existe un Descuento con el ID: " + idDescuento);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener el Descuento", e);
        }
    }

    @Override
    public void actualizarDescuento(Descuento descuento) {
        String sql = "UPDATE descuentos SET nombre = ?, porcentaje = ?, activo = ? WHERE id_descuento = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, descuento.getNombre());
            pstmt.setBigDecimal(2, descuento.getPorcentaje());
            pstmt.setBoolean(3, descuento.isActivo());
            pstmt.setInt(4, descuento.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DescuentoNoEncontradoExeption("No se pudo actualizar: El Descuento con ID -" + descuento.getId() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al actualizar el Descuento", e);
        }
    }

    @Override
    public List<Descuento> obtenerDescuentosActivos() {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos WHERE activo = true";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);

             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Descuento descuento = Descuento.reconstruirDesdeBD(
                        rs.getInt("id_descuento"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("porcentaje"),
                        rs.getBoolean("activo")
                );
                descuentos.add(descuento);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los Descuentos Activos", e);
        }
        return descuentos;
    }

    @Override
    public List<Descuento> obtenerDescuentosInactivos() {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos WHERE activo = false";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);

             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Descuento descuento = Descuento.reconstruirDesdeBD(
                        rs.getInt("id_descuento"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("porcentaje"),
                        rs.getBoolean("activo")
                );
                descuentos.add(descuento);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los Descuentos Inactivos", e);
        }
        return descuentos;
    }

}
