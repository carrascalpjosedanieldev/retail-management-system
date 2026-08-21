package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.entidades.ventas.ReporteRecaudo;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.puertos.RepositorioFacturas;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class RepositorioFacturasMySQL implements RepositorioFacturas {

    //CREATE:

    private static final String SQL_OBTENER_SECUENCIA_FACTURA =
            "SELECT prefijo, siguiente_valor FROM secuencias_factura WHERE prefijo = 'FAC-' FOR UPDATE";

    private static final String SQL_ACTUALIZAR_SECUENCIA_FACTURA =
            "UPDATE secuencias_factura SET siguiente_valor = siguiente_valor + 1 WHERE prefijo = 'FAC-'";

    private String generarSiguienteNumeroFactura(Connection con) throws SQLException {
        try (PreparedStatement psLook = con.prepareStatement(SQL_OBTENER_SECUENCIA_FACTURA);
             ResultSet rs = psLook.executeQuery()) {

            if (rs.next()) {
                String prefijo = rs.getString("prefijo");
                int siguienteValor = rs.getInt("siguiente_valor");

                try (PreparedStatement psUpdate = con.prepareStatement(SQL_ACTUALIZAR_SECUENCIA_FACTURA)) {
                    psUpdate.executeUpdate();
                }

                return String.format("%s%05d", prefijo, siguienteValor);
            } else {
                throw new SQLException("Error: NO se encontró la Secuencia de Facturación 'FAC-'");
            }
        }
    }

    private static final String SQL_INSERTAR_CABECERA_FACTURA =
            "INSERT INTO facturas (numero_factura, fecha, subtotal, total_impuestos, total_general) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private int insertarCabeceraFactura(Connection conn, Factura factura) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR_CABECERA_FACTURA, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, factura.getNumeroFactura());
            ps.setObject(2, factura.getFechaHoraEmision());
            ps.setBigDecimal(3, factura.getSubTotal());
            ps.setBigDecimal(4, factura.getTotalImpuestos());
            ps.setBigDecimal(5, factura.getTotalGeneral());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    return generatedKeys.getInt(1);

                } else {
                    throw new IdAutogeneradoNoRecibidoException("Error crítico: No se pudo obtener el ID autogenerado de la factura.");
                }
            }
        }
    }

    private void agregarLineaDetalleAlBatch(PreparedStatement ps, int idFactura, ItemVendido item) throws SQLException {
        ps.setInt(1, idFactura);
        ps.setString(2, item.getTipoItem().name());
        ps.setString(3, item.getCodigo());
        ps.setString(4, item.getNombre());
        ps.setInt(5, item.getCantidad());
        ps.setBigDecimal(6, item.getPrecioUnitario());
        ps.setBigDecimal(7, item.getSubtotalNeto());
        ps.setBigDecimal(8, item.getPorcentajeImpuesto());
        ps.setBigDecimal(9, item.getMontoImpuesto());
        ps.setBigDecimal(10, item.getTotalLinea());

        ps.addBatch();
    }

    private static final String SQL_INSERTAR_DETALLE_FACTURA =
            "INSERT INTO detalle_facturas " +
                    "(id_factura, tipo_item, codigo_referencia, nombre_item, cantidad, " +
                    "precio_unitario, subtotal_neto, porcentaje_impuesto, monto_impuesto, total_linea) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR_STOCK =
            "UPDATE productos SET stock = stock - ? WHERE codigo_producto = ? AND stock >= ?";

    @Override
    public Factura insertarFactura(List<ItemVendido> items) {

        Connection conn = null;
        try {
            conn = AdministradorConexion.obtenerConexion();
            conn.setAutoCommit(false);

            items.sort(Comparator.comparing(ItemVendido::getCodigo));

            String numeroGenerado = generarSiguienteNumeroFactura(conn);

            Factura factura = Factura.crearNueva(items, numeroGenerado, LocalDateTime.now());

            int idFacturaBD = insertarCabeceraFactura(conn, factura);

            try (PreparedStatement psDetalle = conn.prepareStatement(SQL_INSERTAR_DETALLE_FACTURA);
                 PreparedStatement psStock = conn.prepareStatement(SQL_ACTUALIZAR_STOCK)) {

                for (ItemVendido item : items) {
                    if (item.getTipoItem() == TipoItem.PRODUCTO){

                        psStock.setInt(1, item.getCantidad());
                        psStock.setString(2, item.getCodigo());
                        psStock.setInt(3, item.getCantidad());

                        int filasAfectadas = psStock.executeUpdate();
                        if (filasAfectadas == 0) {
                            throw new StockInsuficienteException("NO hay Stock suficiente para: " + item.getCodigo());
                        }
                    }
                    agregarLineaDetalleAlBatch(psDetalle, idFacturaBD, item);
                }
                psDetalle.executeBatch();
            }

            conn.commit();

            return Factura.reconstruirDesdeBD(
                    items,
                    idFacturaBD,
                    factura.getNumeroFactura(),
                    factura.getFechaHoraEmision(),
                    factura.getTotalGeneral(),
                    factura.getTotalImpuestos(),
                    factura.getSubTotal()
            );

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PersistenciaException("Venta cancelada: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //READ:

    private static final String SQL_OBTENER_REPORTE_RECAUDO =
            "SELECT " +
            "COUNT(id_factura) AS cantidad, " +
            "COALESCE(SUM(subtotal), 0) AS suma_subtotal, " +
            "COALESCE(SUM(total_impuestos), 0) AS suma_impuestos, " +
            "COALESCE(SUM(total_general), 0) AS suma_general " +
            "FROM facturas " +
            "WHERE DATE(fecha) BETWEEN ? AND ?";

    @Override
    public ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_OBTENER_REPORTE_RECAUDO)) {

            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    int cantidad = rs.getInt("cantidad");
                    BigDecimal sumaSubtotal = rs.getBigDecimal("suma_subtotal");
                    BigDecimal sumaImpuestos = rs.getBigDecimal("suma_impuestos");
                    BigDecimal sumaGeneral = rs.getBigDecimal("suma_general");

                    return ReporteRecaudo.reconstruirDesdeBD(
                            fechaInicio, fechaFin, cantidad, sumaSubtotal, sumaImpuestos, sumaGeneral
                    );
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al generar el reporte de recaudo", e);
        }

        return ReporteRecaudo.reconstruirDesdeBD(
                fechaInicio, fechaFin, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
    }


    private static final String SQL_OBTENER_ULTIMA_VENTA =
            "SELECT total_general FROM facturas WHERE DATE(fecha) = ? ORDER BY fecha DESC LIMIT 1";

    @Override
    public BigDecimal obtenerTotalUltimaVenta(LocalDate fecha) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_OBTENER_ULTIMA_VENTA)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_general");
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar la última venta del día", e);
        }

        return BigDecimal.ZERO;
    }


}//===================================================================================================================//

