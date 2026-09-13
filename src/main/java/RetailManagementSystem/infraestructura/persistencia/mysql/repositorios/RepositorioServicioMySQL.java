package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ServicioNoDisponibleException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioServicio;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ServicioNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.VinculadorTransaccion;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorDescuentos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorImpuestos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioServicioMySQL implements RepositorioServicio {

    //ATRIBUTOS:

    private final MapeadorServicio mapeadorServicio;

    private final MapeadorImpuestos mapeadorImpuestos;

    private final MapeadorDescuentos mapeadorDescuentos;

    //CONSTRUCTOR:

    public RepositorioServicioMySQL(
            MapeadorServicio mapeadorServicio, MapeadorImpuestos mapeadorImpuestos,
            MapeadorDescuentos mapeadorDescuentos
    ) {
        this.mapeadorServicio = mapeadorServicio;
        this.mapeadorImpuestos = mapeadorImpuestos;
        this.mapeadorDescuentos = mapeadorDescuentos;
    }

    //MÉTODOS:

    private void validarConexion(Connection conn){
        if (conn == null) {
            throw new IllegalStateException("NO hay una Transacción Activa para este Hilo");
        }
    }

    //CREATE:

    private static final String SQL_INSERTAR_SERVICIO =
            "INSERT INTO servicios (codigo_servicio, nombre, precio_base, id_impuesto, id_descuento) " +
            "VALUES (?, ?, ?, ?, ?)";

    @Override
    public void insertarServicio(Servicio servicio){
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_SERVICIO)){

            pstmt.setString(1, servicio.getCodigo());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getImpuesto().getId());
            pstmt.setInt(5, servicio.getDescuento().getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                throw new ImpuestoNoEncontradoException("No se puede guardar el servicio: El impuesto especificado no existe.");
            }
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Violación de integridad: Ya existe un servicio con este código o nombre.");
            }
            throw new PersistenciaException("Error crítico de persistencia al guardar el servicio: " + e.getMessage(), e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_SERVICIO =
            "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.activo, " +
            "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS impuesto_activo, " +
            "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
            "des.activo AS descuento_activo " +
            "FROM servicios s " +
            "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
            "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
            "WHERE s.codigo_servicio = ?";

    @Override
    public Servicio obtenerServicio(String codigoServicio) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_SERVICIO)) {

            pstmt.setString(1, codigoServicio);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()){
                    return mapearServicioDesdeResultSet(rs);
                }
                throw new ServicioNoEncontradoException("Error de negocio: El Servicio con código -" + codigoServicio + "- no existe");

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }
    }

    private Servicio mapearServicioDesdeResultSet(ResultSet rs) throws SQLException {
        Impuesto impuesto = this.mapeadorImpuestos.mapearImpuesto(rs);

        Descuento descuento = this.mapeadorDescuentos.mapearDescuento(rs);

        return this.mapeadorServicio.mapearServicioCompleto(rs, impuesto, descuento);
    }


    private static final String SQL_OBTENER_TODOS_LOS_SERVICIOS =
            "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.activo, " +
            "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS impuesto_activo, " +
            "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
            "des.activo AS descuento_activo " +
            "FROM servicios s " +
            "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
            "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
            "ORDER BY s.activo DESC, s.codigo_servicio ASC ";

    @Override
    public List<Servicio> obtenerTodosLosServicios() {
        List<Servicio> servicios = new ArrayList<>();
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODOS_LOS_SERVICIOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()){
                Servicio servicio = mapearServicioDesdeResultSet(rs);
                servicios.add(servicio);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error Crítico de Infraestructura al Intentar obtener los Servicios.", e);
        }
        return servicios;
    }


    private static final String SQL_OBTENER_SERVICIO_POR_CODIGO =
            "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.activo, " +
            "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS impuesto_activo, " +
            "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
            "des.activo AS descuento_activo " +
            "FROM servicios s " +
            "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
            "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
            "WHERE s.codigo_servicio = ?";

    @Override
    public Servicio obtenerServicioActivoSoloPorCodigo(String codigoServicio) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_SERVICIO_POR_CODIGO)) {

            pstmt.setString(1, codigoServicio);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()){
                    boolean activo = rs.getBoolean("activo");
                    if (!activo) {
                        throw new ServicioNoDisponibleException("El Servicio con Código -" + codigoServicio +
                                "- NO esta Disponible");
                    }
                    return mapearServicioDesdeResultSet(rs);
                }

                throw new ServicioNoEncontradoException("Error de negocio: El Servicio con código -" + codigoServicio + "- no existe");

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }
    }


    private static final String SQL_EXISTE_SERVICIO =
            "SELECT 1 FROM servicios WHERE codigo_servicio = ?";

    @Override
    public boolean existeServicio(String codigoServicio) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTE_SERVICIO)) {

            pstmt.setString(1, codigoServicio);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de infraestructura al intentar verificar el Servicio", e);
        }
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_SERVICIO =
            "UPDATE servicios SET id_impuesto = ?, nombre = ?, precio_base = ?, id_descuento = ?, activo = ? " +
            "WHERE codigo_servicio = ?";

    @Override
    public void actualizarServicio(Servicio servicio) {
        Connection conn = VinculadorTransaccion.getConnection();
        validarConexion(conn);
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_SERVICIO)){

            pstmt.setInt(1, servicio.getImpuesto().getId());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getDescuento().getId());
            pstmt.setBoolean(5, servicio.isActivo());
            pstmt.setString(6, servicio.getCodigo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas==0){
                throw new ServicioNoEncontradoException("NO se pudo actualizar. El Servicio con el Código -" + servicio.getCodigo() + "- NO existe");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de infraestructura al intentar modificar el Servicio", e);
        }

    }


}//===================================================================================================================//

