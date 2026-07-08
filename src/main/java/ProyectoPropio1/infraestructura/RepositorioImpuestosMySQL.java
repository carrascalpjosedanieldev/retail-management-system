package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.excepciones.ImpuestoNoEncontradoException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioImpuestosMySQL implements RepositorioImpuestos {

    @Override
    public Impuesto insertarImpuesto(Impuesto borrador) {
        String sql = "INSERT INTO impuestos (nombre, porcentaje, activo) VALUES (?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, borrador.getNombre());
            pstmt.setBigDecimal(2, borrador.getPorcentaje());
            pstmt.setBoolean(3, borrador.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Impuesto.reconstruirDesdeBD(idReal, borrador.getNombre(), borrador.getPorcentaje(), borrador.isActivo());
                } else {
                    throw new RuntimeException("La inserción fue exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe un Impuesto registrado con el nombre: " + borrador.getNombre());
            }
            throw new RuntimeException("Error crítico de persistencia al guardar el Impuesto: " + e.getMessage(), e);
        }
    }


    @Override
    public void eliminarImpuesto(int idImpuesto) {
        String sql = "DELETE FROM impuestos WHERE id_impuesto = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idImpuesto);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ImpuestoNoEncontradoException("El impuesto con ID " + idImpuesto + " no existe o ya fue eliminado antes.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalStateException("Acción denegada: No se puede eliminar este Impuesto porque tiene Productos o Servicios asociados.");
            }
            throw new RuntimeException("Error crítico de infraestructura al intentar eliminar el impuesto", e);
        }

    }


    @Override
    public Impuesto obtenerImpuesto(int idImpuesto) {
        if (idImpuesto<=0) {
            throw new IllegalStateException("El ID a buscar debe ser un número positivo.");
        }
        String sql = "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos WHERE id_impuesto = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, idImpuesto);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    int idReal = rs.getInt("id_impuesto");
                    String nombre = rs.getString("nombre");
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
                    boolean activo = rs.getBoolean("activo");

                    return Impuesto.reconstruirDesdeBD(idReal, nombre, porcentaje, activo);
                }

                throw new ImpuestoNoEncontradoException("No existe un Impuesto con el ID: " + idImpuesto);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener el inventario", e);
        }
    }


    @Override
    public void actualizarImpuesto(Impuesto impuesto) {
        String sql = "UPDATE impuestos SET nombre = ?, porcentaje = ?, activo = ? WHERE id_impuesto = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, impuesto.getNombre());
            pstmt.setBigDecimal(2, impuesto.getPorcentaje());
            pstmt.setBoolean(3, impuesto.isActivo());
            pstmt.setInt(4, impuesto.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ImpuestoNoEncontradoException("No se pudo actualizar: El Impuesto con ID -" + impuesto.getId() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al actualizar el Impuesto", e);
        }
    }


    @Override
    public List<Impuesto> obtenerImpuestosActivos() {
        List<Impuesto> impuestos = new ArrayList<>();
        String sql = "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos WHERE activo = true";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);

             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                        rs.getInt("id_impuesto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("porcentaje"),
                        rs.getBoolean("activo")
                );
                impuestos.add(impuesto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Impuestos", e);
        }
        return impuestos;
    }


    @Override
    public List<Impuesto> obtenerImpuestosInactivos() {
        List<Impuesto> impuestos = new ArrayList<>();
        String sql = "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos WHERE activo = false";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);

             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                        rs.getInt("id_impuesto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("porcentaje"),
                        rs.getBoolean("activo")
                );
                impuestos.add(impuesto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Impuestos", e);
        }
        return impuestos;
    }


}
