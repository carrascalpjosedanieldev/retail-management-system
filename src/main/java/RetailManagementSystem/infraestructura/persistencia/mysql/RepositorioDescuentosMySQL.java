package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.puertos.RepositorioDescuentos;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDescuentosMySQL implements RepositorioDescuentos {

    //CREATE:

    private static final String SQL_INSERTAR_DESCUENTO =
            "INSERT INTO descuentos (nombre, porcentaje, activo) VALUES (?, ?, ?)";

    @Override
    public Descuento insertarDescuento(Descuento descuento) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DESCUENTO, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, descuento.getNombre());
            pstmt.setBigDecimal(2, descuento.getPorcentaje());
            pstmt.setBoolean(3, descuento.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Descuento.reconstruirDesdeBD(
                            idReal,
                            descuento.getNombre(),
                            descuento.getPorcentaje(),
                            descuento.isActivo()
                    );
                } else {
                    throw new IdAutogeneradoNoRecibidoException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe un Descuento registrado con el Nombre: " + descuento.getNombre());
            }
            throw new PersistenciaException("Error crítico de persistencia al guardar el Descuento: " + e.getMessage(), e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_DESCUENTO =
            "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos WHERE id_descuento = ?";

    @Override
    public Descuento obtenerDescuento(int idDescuento) {
        if (idDescuento<=0) {
            throw new IllegalStateException("El ID a buscar debe ser un número positivo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_DESCUENTO)){

            pstmt.setInt(1, idDescuento);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    int idReal = rs.getInt("id_descuento");
                    String nombre = rs.getString("nombre");
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
                    boolean activo = rs.getBoolean("activo");

                    return Descuento.reconstruirDesdeBD(idReal, nombre, porcentaje, activo);
                }

                throw new DescuentoNoEncontradoException("NO existe un Descuento con el ID: " + idDescuento);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al obtener el Descuento", e);
        }
    }


    private static final String SQL_OBTENER_DESCUENTOS_ACTIVOS =
            "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos WHERE activo = true";

    @Override
    public List<Descuento> obtenerDescuentosActivos() {
        List<Descuento> descuentos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_DESCUENTOS_ACTIVOS);
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
            throw new PersistenciaException("Error al listar los Descuentos Activos", e);
        }
        return descuentos;
    }


    private static final String SQL_OBTENER_TODOS_LOS_DESCUENTOS =
            "SELECT id_descuento, nombre, porcentaje, activo FROM descuentos ORDER BY activo DESC, id_descuento ASC";

    @Override
    public List<Descuento> obtenerTodosLosDescuentos() {
        List<Descuento> descuentos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_DESCUENTOS);

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
            throw new PersistenciaException("Error al listar los Descuentos Inactivos", e);
        }
        return descuentos;
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_DESCUENTOS =
            "UPDATE descuentos SET nombre = ?, porcentaje = ?, activo = ? WHERE id_descuento = ?";

    @Override
    public void actualizarDescuento(Descuento descuento) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_DESCUENTOS)) {

            pstmt.setString(1, descuento.getNombre());
            pstmt.setBigDecimal(2, descuento.getPorcentaje());
            pstmt.setBoolean(3, descuento.isActivo());
            pstmt.setInt(4, descuento.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new DescuentoNoEncontradoException("No se pudo actualizar: El Descuento con ID -" + descuento.getId() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar el Descuento", e);
        }
    }


}//===================================================================================================================//

