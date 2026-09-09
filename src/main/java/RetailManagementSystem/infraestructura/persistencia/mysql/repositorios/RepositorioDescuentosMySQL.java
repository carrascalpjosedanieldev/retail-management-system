package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.AdministradorConexion;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorDescuentos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDescuentosMySQL implements RepositorioDescuentos {

    //ATRIBUTOS:

    private final MapeadorDescuentos mapeadorDescuentos;

    //CONSTRUCTOR:

    public RepositorioDescuentosMySQL(MapeadorDescuentos mapeadorDescuentos) {
        this.mapeadorDescuentos = mapeadorDescuentos;
    }

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
                    throw new IdAutogeneradoNoRecibidoException("La Inserción fue Exitosa, pero NO se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Ya existe un Descuento registrado con el Nombre: " + descuento.getNombre());
        } catch (SQLException e) {

            throw new PersistenciaException("Error crítico de persistencia al guardar el Descuento: " + e.getMessage(), e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_DESCUENTO =
            "SELECT " +
            "id_descuento, nombre AS nombre_descuento, porcentaje AS porcentaje_descuento, activo AS descuento_activo " +
            "FROM descuentos WHERE id_descuento = ?";

    @Override
    public Descuento obtenerDescuento(int idDescuento) {
        if (idDescuento<=0) {
            throw new IllegalArgumentException("El ID a Buscar debe ser un Número Positivo.");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_DESCUENTO)){

            pstmt.setInt(1, idDescuento);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return this.mapeadorDescuentos.mapearDescuento(rs);
                }
                throw new DescuentoNoEncontradoException("NO existe un Descuento con el ID: " + idDescuento);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al obtener el Descuento", e);
        }
    }


    private static final String SQL_OBTENER_DESCUENTOS_ACTIVOS =
            "SELECT " +
            "id_descuento, nombre AS nombre_descuento, porcentaje AS porcentaje_descuento, activo AS descuento_activo " +
            "FROM descuentos WHERE activo = true";

    @Override
    public List<Descuento> obtenerDescuentosActivos() {
        List<Descuento> descuentos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_DESCUENTOS_ACTIVOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Descuento descuento = this.mapeadorDescuentos.mapearDescuento(rs);
                descuentos.add(descuento);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los Descuentos Activos", e);
        }
        return descuentos;
    }


    private static final String SQL_OBTENER_TODOS_LOS_DESCUENTOS =
            "SELECT " +
            "id_descuento, nombre AS nombre_descuento, porcentaje AS porcentaje_descuento, activo AS descuento_activo " +
            "FROM descuentos ORDER BY activo DESC, id_descuento ASC";

    @Override
    public List<Descuento> obtenerTodosLosDescuentos() {
        List<Descuento> descuentos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_DESCUENTOS);

             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Descuento descuento = this.mapeadorDescuentos.mapearDescuento(rs);
                descuentos.add(descuento);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar Todos los Descuentos", e);
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
                throw new DescuentoNoEncontradoException("NO se pudo Actualizar: El Descuento con ID -" + descuento.getId() + "- NO Existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar el Descuento", e);
        }
    }


}//===================================================================================================================//

