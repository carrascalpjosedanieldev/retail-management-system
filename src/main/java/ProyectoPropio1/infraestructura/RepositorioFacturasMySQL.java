package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemVendido;
import ProyectoPropio1.dominio.enums.TipoItem;
import ProyectoPropio1.dominio.puertos.RepositorioFacturas;
import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class RepositorioFacturasMySQL implements RepositorioFacturas {

    private String generarSiguienteNumeroFactura(Connection con) throws SQLException {
        String sqlBloqueo = "SELECT prefijo, siguiente_valor FROM secuencias_factura WHERE prefijo = 'FAC-' FOR UPDATE";
        String sqlUpdate = "UPDATE secuencias_factura SET siguiente_valor = siguiente_valor + 1 WHERE prefijo = 'FAC-'";

        try (PreparedStatement psLook = con.prepareStatement(sqlBloqueo);
             ResultSet rs = psLook.executeQuery()) {

            if (rs.next()) {
                String prefijo = rs.getString("prefijo");
                int siguienteValor = rs.getInt("siguiente_valor");

                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.executeUpdate();
                }

                return String.format("%s%05d", prefijo, siguienteValor);
            } else {
                throw new SQLException("Error: No se encontró la secuencia de facturación 'FAC-'");
            }
        }
    }

    private int insertarCabeceraFactura(Connection conn, Factura factura) throws SQLException {
        String sql = "INSERT INTO facturas (numero_factura, fecha, subtotal, total_impuestos, total_general) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                    throw new SQLException("Error crítico: No se pudo obtener el ID autogenerado de la factura.");
                }
            }
        }
    }

    private void insertarLineaDetalle(Connection conn, int idFactura, ItemVendido item) throws SQLException {

        String sql = "INSERT INTO detalle_facturas " +
                "(id_factura, tipo_item, codigo_referencia, nombre_item, cantidad, " +
                "precio_unitario, subtotal_neto, porcentaje_impuesto, monto_impuesto, total_linea) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

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

            ps.executeUpdate();
        }
    }

    @Override
    public Factura insertarFactura(List<ItemVendido> items) {

        Connection conn = null;
        try {
            conn = AdministradorConexion.obtenerConexion();
            conn.setAutoCommit(false);

            String numeroGenerado = generarSiguienteNumeroFactura(conn);

            Factura factura = Factura.crearNueva(items, numeroGenerado, LocalDateTime.now());

            int idFacturaBD = insertarCabeceraFactura(conn, factura);

            for (ItemVendido item : items) {
                if (item.getTipoItem() == TipoItem.PRODUCTO){
                    String sqlUpdateStock = "UPDATE productos SET stock = stock - ? WHERE codigo_producto = ? AND stock >= ?";

                    try (PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock)) {
                        psStock.setInt(1, item.getCantidad());
                        psStock.setString(2, item.getCodigo());
                        psStock.setInt(3, item.getCantidad());

                        int filasAfectadas = psStock.executeUpdate();

                        if (filasAfectadas == 0) {
                            throw new StockInsuficienteException("No hay Stock suficiente para el Producto " + item.getCodigo() + ". Requerido: " + item.getCantidad());
                        }
                    }
                }
                insertarLineaDetalle(conn, idFacturaBD, item);
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
            throw new RuntimeException("Venta cancelada: " + e.getMessage(), e);
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

}

