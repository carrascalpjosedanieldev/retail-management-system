package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dominio.enums.Talla;
import ProyectoPropio1.dominio.puertos.RepositorioProducto;
import ProyectoPropio1.excepciones.InventarioNoEncontradoException;
import ProyectoPropio1.excepciones.ProductoNoEncontradoException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioProductoMySQL implements RepositorioProducto {

    @Override
    public void insertarProducto(Producto producto, int idInventario) {

        String sqlPadre = "INSERT INTO productos (codigo_producto, id_inventario, id_impuesto, id_descuento, nombre, " +
                "valor_compra, porcentaje_ganancia, stock, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement pstmt = conn.prepareStatement(sqlPadre)) {
                    pstmt.setString(1, producto.getCodigo());
                    pstmt.setInt(2, idInventario);
                    pstmt.setInt(3, producto.getIdImpuesto());
                    pstmt.setInt(4, producto.getIdDescuento());
                    pstmt.setString(5, producto.getNombre());
                    pstmt.setBigDecimal(6, producto.getValorCompra());
                    pstmt.setBigDecimal(7, producto.getPorcentajeGanancia());
                    pstmt.setInt(8, producto.getStock());
                    pstmt.setBoolean(9, producto.isActivo());
                    pstmt.executeUpdate();
                }

                if (producto instanceof ProductoRopa) {
                    this.insertarEspecificoRopa(conn, (ProductoRopa) producto);
                } else if (producto instanceof ProductoPerecedero) {
                    this.insertarEspecificoPerecedero(conn, (ProductoPerecedero) producto);
                } else {
                    throw new IllegalArgumentException("Tipo de producto no soportado para persistencia.");
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                if (e.getErrorCode() == 1452) {
                    throw new InventarioNoEncontradoException("No se puede guardar el producto: El inventario destino no existe en la base de datos.");
                }
                throw new RuntimeException("Error en la transacción de inserción", e);

            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored){}
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al obtener conexión", e);
        }
    }

    private void insertarEspecificoRopa(Connection conn, ProductoRopa ropa) throws SQLException {
        String sql = "INSERT INTO producto_ropa (codigo_producto, talla) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ropa.getCodigo());
            pstmt.setString(2, ropa.getTalla().toString());
            pstmt.executeUpdate();
        }
    }

    private void insertarEspecificoPerecedero(Connection conn, ProductoPerecedero perecedero) throws SQLException {
        String sql = "INSERT INTO producto_perecedero (codigo_producto, fecha_vencimiento, id_politica) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, perecedero.getCodigo());
            pstmt.setDate(2, java.sql.Date.valueOf(perecedero.getFechaVencimiento()));
            pstmt.setInt(3, perecedero.getPoliticaVencimiento().getIdPolitica());
            pstmt.executeUpdate();
        }
    }



    @Override
    public void desactivarProductoDeInventario(String codigoProducto, int idInventario) {
        String sql = "UPDATE productos SET activo = false WHERE codigo_producto = ? AND id_inventario = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.setInt(2, idInventario);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ProductoNoEncontradoException("No se pudo eliminar: El producto con código -" + codigoProducto + "- no existe en el inventario de ID -" + idInventario + "-.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al intentar Desactivar el Producto: " + codigoProducto, e);
        }
    }



    @Override
    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto) {
        String sql =
                "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, p.stock, " +
                        "p.activo, " +
                        "r.talla, per.fecha_vencimiento, per.id_politica, " +
                        "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, " +
                        "i.activo AS impuesto_activo, " +
                        "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                        "des.activo AS descuento_activo, " +
                        "pove.id_politica, pove.nombre_politica, pove.dias_umbral, pove.porcentaje_descuento AS porcentaje_politica," +
                        "pove.activa AS politica_activa " +
                        "FROM productos p " +
                        "INNER JOIN impuestos i ON p.id_impuesto = i.id_impuesto " +
                        "INNER JOIN descuentos des ON p.id_descuento = des.id_descuento " +
                        "LEFT JOIN producto_ropa r ON p.codigo_producto = r.codigo_producto " +
                        "LEFT JOIN producto_perecedero per ON p.codigo_producto = per.codigo_producto " +
                        "LEFT JOIN politicas_vencimiento pove ON per.id_politica = pove.id_politica " +
                        "WHERE p.id_inventario = ? AND p.codigo_producto = ? ";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventario);
            pstmt.setString(2, codigoProducto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String codigo = rs.getString("codigo_producto");
                    String nombre = rs.getString("nombre");
                    BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
                    BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
                    int stock = rs.getInt("stock");
                    boolean activoProd = rs.getBoolean("activo");

                    int idImpuesto = rs.getInt("id_impuesto");
                    String nombreImp = rs.getString("nombre_impuesto");
                    BigDecimal porcentajeImp = rs.getBigDecimal("porcentaje_impuesto");
                    boolean activoImp = rs.getBoolean("impuesto_activo");
                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImp, porcentajeImp, activoImp);

                    int idDescuento = rs.getInt("id_descuento");
                    String nombreDesc = rs.getString("nombre_descuento");
                    BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                    boolean activoDesc = rs.getBoolean("descuento_activo");
                    Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                    String tallaString = rs.getString("talla");
                    if (tallaString != null) {
                        Talla talla = Talla.valueOf(tallaString);
                        return ProductoRopa.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia, stock,
                                impuesto, descuento, activoProd, talla);
                    }

                    Date fechaSql = rs.getDate("fecha_vencimiento");

                    int idPolitica = rs.getInt("id_politica");
                    String nombrePolitica = rs.getString("nombre_politica");
                    int diasUmbral = rs.getInt("dias_umbral");
                    BigDecimal porcentajePolitica = rs.getBigDecimal("porcentaje_politica");
                    boolean activaPolitica = rs.getBoolean("politica_activa");
                    PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(idPolitica, nombrePolitica,
                            diasUmbral, porcentajePolitica, activaPolitica);

                    if (fechaSql != null) {
                        LocalDate fechaVencimiento = fechaSql.toLocalDate();
                        return ProductoPerecedero.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia, stock,
                                impuesto, descuento, activoProd, fechaVencimiento, politicaVencimiento);
                    }

                    throw new IllegalStateException("Error de integridad: El producto existe pero no tiene un tipo definido.");
                }

                throw new ProductoNoEncontradoException("Error de negocio: El producto con código '" + codigoProducto +
                                "' no existe en el inventario con ID " + idInventario);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el producto: " + codigoProducto, e);
        }
    }



    @Override
    public void actualizarProducto(Producto producto, int idInventario) {
        String sql = "UPDATE productos SET nombre = ?, valor_compra = ?, porcentaje_ganancia = ?, stock = ?, " +
                "id_impuesto = ?, id_descuento = ? , activo = ? " +
                "WHERE id_inventario = ? AND codigo_producto = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, producto.getNombre());
            pstmt.setBigDecimal(2, producto.getValorCompra());
            pstmt.setBigDecimal(3, producto.getPorcentajeGanancia());
            pstmt.setInt(4, producto.getStock());
            pstmt.setInt(5, producto.getIdImpuesto());
            pstmt.setInt(6, producto.getIdDescuento());
            pstmt.setBoolean(7, producto.isActivo());
            pstmt.setInt(8, idInventario);
            pstmt.setString(9, producto.getCodigo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ProductoNoEncontradoException("No se pudo actualizar: El producto no existe en este inventario.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                throw new IllegalArgumentException("No se puede actualizar el producto: El Impuesto o el Inventario destino especificado no existen.");
            }
            throw new RuntimeException("Error al actualizar el producto: " + producto.getCodigo(), e);
        }
    }



    @Override
    public void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino) {
        String sql = "UPDATE productos SET id_inventario = ? WHERE id_inventario = ? AND codigo_producto = ?";
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventarioDestino);
            pstmt.setInt(2, idInventarioOrigen);
            pstmt.setString(3, codigoProducto);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ProductoNoEncontradoException("No se pudo mover: El producto no existe en el inventario de origen.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en la base de datos al mover el producto: " + codigoProducto, e);
        }
    }



    @Override
    public List<Producto> obtenerProductosPorInventario(int idInventario) {
        List<Producto> productos = new ArrayList<>();
        String sql =
                "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, p.stock, p.activo, " +
                        "r.talla, per.fecha_vencimiento, per.id_politica, " +
                        "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS impuesto_activo, " +
                        "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                        "des.activo AS descuento_activo, " +
                        "pove.id_politica, pove.nombre_politica, pove.dias_umbral, pove.porcentaje_descuento AS porcentaje_politica, " +
                        "pove.activa AS politica_activa " +
                        "FROM productos p " +
                        "INNER JOIN impuestos i ON p.id_impuesto = i.id_impuesto " +
                        "INNER JOIN descuentos des ON p.id_descuento = des.id_descuento " +
                        "LEFT JOIN producto_ropa r ON p.codigo_producto = r.codigo_producto " +
                        "LEFT JOIN producto_perecedero per ON p.codigo_producto = per.codigo_producto " +
                        "LEFT JOIN politicas_vencimiento pove ON per.id_politica = pove.id_politica " +
                        "WHERE p.id_inventario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String codigo = rs.getString("codigo_producto");
                    String nombre = rs.getString("nombre");
                    BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
                    BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
                    int stock = rs.getInt("stock");
                    boolean activoProd = rs.getBoolean("activo");

                    int idImpuesto = rs.getInt("id_impuesto");
                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, rs.getString("nombre_impuesto"),
                            rs.getBigDecimal("porcentaje_impuesto"), rs.getBoolean("impuesto_activo"));

                    int idDescuento = rs.getInt("id_descuento");
                    String nombreDesc = rs.getString("nombre_descuento");
                    BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                    boolean activoDesc = rs.getBoolean("descuento_activo");
                    Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                    String tallaString = rs.getString("talla");
                    if (tallaString != null) {
                        Talla talla = Talla.valueOf(tallaString);
                        productos.add(ProductoRopa.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia,
                                stock, impuesto, descuento, activoProd, talla));
                        continue;
                    }

                    Date fechaSql = rs.getDate("fecha_vencimiento");

                    int idPolitica = rs.getInt("id_politica");
                    String nombrePolitica = rs.getString("nombre_politica");
                    int diasUmbral = rs.getInt("dias_umbral");
                    BigDecimal porcentajePolitica = rs.getBigDecimal("porcentaje_politica");
                    boolean activaPolitica = rs.getBoolean("politica_activa");
                    PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(idPolitica, nombrePolitica,
                            diasUmbral, porcentajePolitica, activaPolitica);

                    if (fechaSql != null) {
                        LocalDate fechaVencimiento = fechaSql.toLocalDate();
                        productos.add(ProductoPerecedero.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia,
                                stock, impuesto, descuento, activoProd, fechaVencimiento, politicaVencimiento));
                        continue;
                    }

                    throw new IllegalStateException("Error de integridad: El producto " + codigo + " NO tiene un tipo definido.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico al listar los productos del inventario: " + idInventario, e);
        }
        return productos;
    }


    @Override
    public List<Producto> obtenerProductosRopaPorInventario(int idInventario) {
        List<Producto> productosRopa = new ArrayList<>();
        String sql =
                "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, " +
                        "p.stock, p.activo, r.talla, " +
                        "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, " +
                        "i.activo AS impuesto_activo, " +
                        "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                        "des.activo AS descuento_activo " +
                        "FROM productos p " +
                        "INNER JOIN impuestos i ON p.id_impuesto = i.id_impuesto " +
                        "INNER JOIN descuentos des ON p.id_descuento = des.id_descuento " +
                        "INNER JOIN producto_ropa r ON p.codigo_producto = r.codigo_producto " +
                        "WHERE p.id_inventario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String codigo = rs.getString("codigo_producto");
                    String nombre = rs.getString("nombre");
                    BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
                    BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
                    int stock = rs.getInt("stock");
                    boolean activoProd = rs.getBoolean("activo");

                    int idImpuesto = rs.getInt("id_impuesto");
                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, rs.getString("nombre_impuesto"),
                            rs.getBigDecimal("porcentaje_impuesto"), rs.getBoolean("impuesto_activo"));

                    int idDescuento = rs.getInt("id_descuento");
                    String nombreDesc = rs.getString("nombre_descuento");
                    BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                    boolean activoDesc = rs.getBoolean("descuento_activo");
                    Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                    String tallaString = rs.getString("talla");
                    if (tallaString != null) {
                        Talla talla = Talla.valueOf(tallaString);
                        productosRopa.add(ProductoRopa.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia,
                                stock, impuesto, descuento, activoProd, talla));
                        continue;
                    }

                    throw new IllegalStateException("Error de integridad: El producto " + codigo + " NO tiene un tipo definido.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico al listar los productos del inventario: " + idInventario, e);
        }
        return productosRopa;
    }

    @Override
    public List<Producto> obtenerProductosPerecederoPorInventario(int idInventario) {
        List<Producto> productosPerecederos = new ArrayList<>();
        String sql =
                "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, " +
                        "p.stock, p.activo, per.fecha_vencimiento, per.id_politica, " +
                        "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, " +
                        "i.activo AS impuesto_activo, " +
                        "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                        "des.activo AS descuento_activo, " +
                        "pove.id_politica, pove.nombre_politica, pove.dias_umbral, " +
                        "pove.porcentaje_descuento AS porcentaje_politica, pove.activa AS politica_activa " +
                        "FROM productos p " +
                        "INNER JOIN impuestos i ON p.id_impuesto = i.id_impuesto " +
                        "INNER JOIN descuentos des ON p.id_descuento = des.id_descuento " +
                        "INNER JOIN producto_perecedero per ON p.codigo_producto = per.codigo_producto " +
                        "INNER JOIN politicas_vencimiento pove ON per.id_politica = pove.id_politica " +
                        "WHERE p.id_inventario = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String codigo = rs.getString("codigo_producto");
                    String nombre = rs.getString("nombre");
                    BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
                    BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
                    int stock = rs.getInt("stock");
                    boolean activoProd = rs.getBoolean("activo");

                    int idImpuesto = rs.getInt("id_impuesto");
                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, rs.getString("nombre_impuesto"),
                            rs.getBigDecimal("porcentaje_impuesto"), rs.getBoolean("impuesto_activo"));

                    int idDescuento = rs.getInt("id_descuento");
                    String nombreDesc = rs.getString("nombre_descuento");
                    BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                    boolean activoDesc = rs.getBoolean("descuento_activo");
                    Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                    Date fechaSql = rs.getDate("fecha_vencimiento");

                    int idPolitica = rs.getInt("id_politica");
                    String nombrePolitica = rs.getString("nombre_politica");
                    int diasUmbral = rs.getInt("dias_umbral");
                    BigDecimal porcentajePolitica = rs.getBigDecimal("porcentaje_politica");
                    boolean activaPolitica = rs.getBoolean("politica_activa");
                    PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(idPolitica, nombrePolitica,
                            diasUmbral, porcentajePolitica, activaPolitica);

                    if (fechaSql != null) {
                        LocalDate fechaVencimiento = fechaSql.toLocalDate();
                        productosPerecederos.add(ProductoPerecedero.reconstruirDesdeBD(codigo, nombre, valorCompra, porcentajeGanancia,
                                stock, impuesto, descuento, activoProd, fechaVencimiento, politicaVencimiento));
                        continue;
                    }

                    throw new IllegalStateException("Error de integridad: El producto " + codigo + " NO tiene un tipo definido.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico al listar los productos del inventario: " + idInventario, e);
        }
        return productosPerecederos;
    }


}

