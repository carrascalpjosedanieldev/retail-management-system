package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.puertos.RepositorioImpuestos;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioImpuestosMySQL implements RepositorioImpuestos {

    //CREATE:

    private static final String SQL_INSERTAR_IMPUESTO =
            "INSERT INTO impuestos (nombre, porcentaje, activo) VALUES (?, ?, ?)";

    @Override
    public Impuesto insertarImpuesto(Impuesto impuesto) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_IMPUESTO, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, impuesto.getNombre());
            pstmt.setBigDecimal(2, impuesto.getPorcentaje());
            pstmt.setBoolean(3, impuesto.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La Inserción falló: Ninguna fila fue afectada en la Base de Datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return Impuesto.reconstruirDesdeBD(
                            idReal,
                            impuesto.getNombre(),
                            impuesto.getPorcentaje(),
                            impuesto.isActivo()
                    );
                } else {
                    throw new IdAutogeneradoNoRecibidoException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe un Impuesto registrado con el Nombre: " + impuesto.getNombre());
            }
            throw new PersistenciaException("Error crítico de persistencia al guardar el Impuesto: " + e.getMessage(), e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_IMPUESTO =
            "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos WHERE id_impuesto = ?";

    @Override
    public Impuesto obtenerImpuesto(int idImpuesto) {
        if (idImpuesto <= 0) {
            throw new IllegalStateException("El ID a buscar debe ser un número positivo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_IMPUESTO)) {

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
            throw new PersistenciaException("Error de base de datos al obtener el Impuesto", e);
        }
    }


    private static final String SQL_OBTENER_IMPUESTOS_ACTIVOS =
            "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos WHERE activo = true";

    @Override
    public List<Impuesto> obtenerImpuestosActivos() {
        List<Impuesto> impuestos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_IMPUESTOS_ACTIVOS);
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
            throw new PersistenciaException("Error al listar los Impuestos Activos", e);
        }
        return impuestos;
    }


    private static final String SQL_OBTENER_TODOS_LOS_IMPUESTOS =
            "SELECT id_impuesto, nombre, porcentaje, activo FROM impuestos ORDER BY activo DESC, id_impuesto ASC";

    @Override
    public List<Impuesto> obtenerTodosLosImpuestos() {
        List<Impuesto> impuestos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_IMPUESTOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Impuesto impuesto = Impuesto.reconstruirDesdeBD(rs.getInt("id_impuesto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("porcentaje"),
                        rs.getBoolean("activo")
                );
                impuestos.add(impuesto);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los Impuestos Inactivos", e);
        }
        return impuestos;
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_IMPUESTO =
            "UPDATE impuestos SET nombre = ?, porcentaje = ?, activo = ? WHERE id_impuesto = ?";

    @Override
    public void actualizarImpuesto(Impuesto impuesto) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_IMPUESTO)) {

            pstmt.setString(1, impuesto.getNombre());
            pstmt.setBigDecimal(2, impuesto.getPorcentaje());
            pstmt.setBoolean(3, impuesto.isActivo());
            pstmt.setInt(4, impuesto.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ImpuestoNoEncontradoException("No se pudo actualizar: El Impuesto con ID -" +
                        impuesto.getId() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar el Impuesto", e);
        }
    }


}//===================================================================================================================//

