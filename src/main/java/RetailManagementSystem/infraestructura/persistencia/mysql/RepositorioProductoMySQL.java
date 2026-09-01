package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.comercial.*;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoNoDisponibleException;
import RetailManagementSystem.dominio.puertos.RepositorioProducto;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.InventarioNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioProductoMySQL implements RepositorioProducto {

    //CREATE:

    private static final String SQL_INSERTAR_DATOS_PRODUCTO =
            "INSERT INTO productos (codigo_producto, id_inventario, id_impuesto, id_descuento, nombre, " +
            "valor_compra, porcentaje_ganancia, stock, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insertarProducto(Producto producto, int idInventario) {
        try (Connection conn = AdministradorConexion.obtenerConexion()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_PRODUCTO)) {
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
                    throw new IllegalArgumentException("Tipo de Producto NO soportado para persistencia.");
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                if (e.getErrorCode() == 1452) {
                    throw new InventarioNoEncontradoException("No se puede guardar el producto: El Inventario Destino NO Existe en la Base de Datos.");
                }
                throw new RuntimeException("Error en la transacción de inserción", e);

            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored){}
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico de infraestructura al obtener conexión", e);
        }
    }

    private static final String SQL_INSERTAR_DATOS_ROPA =
            "INSERT INTO producto_ropa (codigo_producto, talla) VALUES (?, ?)";

    private void insertarEspecificoRopa(Connection conn, ProductoRopa ropa) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_ROPA)) {
            pstmt.setString(1, ropa.getCodigo());
            pstmt.setString(2, ropa.getTalla().toString());
            pstmt.executeUpdate();
        }
    }

    private static final String SQL_INSERTAR_DATOS_PERECEDERO =
            "INSERT INTO producto_perecedero (codigo_producto, fecha_vencimiento, id_politica) VALUES (?, ?, ?)";

    private void insertarEspecificoPerecedero(Connection conn, ProductoPerecedero perecedero) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_PERECEDERO)) {
            pstmt.setString(1, perecedero.getCodigo());
            pstmt.setDate(2, java.sql.Date.valueOf(perecedero.getFechaVencimiento()));
            pstmt.setInt(3, perecedero.getPoliticaVencimiento().getIdPolitica());
            pstmt.executeUpdate();
        }
    }


    //READ:

    private static final String SQL_OBTENER_PRODUCTO_DE_INVENTARIO =
            "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, p.stock, " +
            "p.activo, " +
            "r.talla, per.fecha_vencimiento, per.id_politica, " +
            "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, " +
            "i.activo AS impuesto_activo, " +
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
            "WHERE p.id_inventario = ? AND p.codigo_producto = ? ";

    @Override
    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PRODUCTO_DE_INVENTARIO)) {

            pstmt.setInt(1, idInventario);
            pstmt.setString(2, codigoProducto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProductoDesdeResultSet(rs);
                }

                throw new ProductoNoEncontradoException("Error de negocio: El Producto con Código -" + codigoProducto +
                                "- NO existe en el Inventario con ID " + idInventario);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener el producto: " + codigoProducto, e);
        }
    }

    private Producto mapearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        String codigo = rs.getString("codigo_producto");
        String nombre = rs.getString("nombre");
        BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
        BigDecimal porcentajeGanancia = rs.getBigDecimal("porcentaje_ganancia");
        int stock = rs.getInt("stock");
        boolean activoProd = rs.getBoolean("activo");

        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                rs.getInt("id_impuesto"), rs.getString("nombre_impuesto"),
                rs.getBigDecimal("porcentaje_impuesto"), rs.getBoolean("impuesto_activo")
        );

        Descuento descuento = Descuento.reconstruirDesdeBD(
                rs.getInt("id_descuento"), rs.getString("nombre_descuento"),
                rs.getBigDecimal("porcentaje_descuento"), rs.getBoolean("descuento_activo")
        );

        String tallaString = rs.getString("talla");
        if (tallaString != null) {
            return ProductoRopa.reconstruirDesdeBD(
                    codigo, nombre, valorCompra, porcentajeGanancia, stock,
                    impuesto, descuento, activoProd, Talla.valueOf(tallaString)
            );
        }

        Date fechaSql = rs.getDate("fecha_vencimiento");
        if (fechaSql != null) {
            PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                    rs.getInt("id_politica"), rs.getString("nombre_politica"),
                    rs.getInt("dias_umbral"), rs.getBigDecimal("porcentaje_politica"), rs.getBoolean("politica_activa")
            );
            return ProductoPerecedero.reconstruirDesdeBD(
                    codigo, nombre, valorCompra, porcentajeGanancia, stock,
                    impuesto, descuento, activoProd, fechaSql.toLocalDate(), politicaVencimiento
            );
        }

        throw new IllegalStateException("Error de Integridad: El Producto " + codigo + " NO tiene un tipo definido.");
    }



    private static final String SQL_OBTENER_PRODUCTOS_DE_INVENTARIO =
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

    @Override
    public List<Producto> obtenerProductosPorInventario(int idInventario) {
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PRODUCTOS_DE_INVENTARIO)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProductoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico al listar los productos del inventario: " + idInventario, e);
        }
        return productos;
    }


    private static final String SQL_OBTENER_PRODUCTOS_ROPA_DE_INVENTARIO =
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

    @Override
    public List<Producto> obtenerProductosRopaPorInventario(int idInventario) {
        List<Producto> productosRopa = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PRODUCTOS_ROPA_DE_INVENTARIO)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productosRopa.add(mapearProductoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico al listar los productos del inventario: " + idInventario, e);
        }
        return productosRopa;
    }


    private static final String SQL_OBTENER_PERECEDEROS_DE_INVENTARIO =
            "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, " +
            "p.stock, p.activo, per.fecha_vencimiento, per.id_politica, r.talla, " +
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
            "LEFT JOIN producto_ropa r ON p.codigo_producto = r.codigo_producto " +
            "INNER JOIN politicas_vencimiento pove ON per.id_politica = pove.id_politica " +
            "WHERE p.id_inventario = ?";

    @Override
    public List<Producto> obtenerProductosPerecederoPorInventario(int idInventario) {
        List<Producto> productosPerecederos = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PERECEDEROS_DE_INVENTARIO)) {

            pstmt.setInt(1, idInventario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productosPerecederos.add(mapearProductoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error crítico al listar los productos del inventario: " + idInventario + e.getMessage(), e);
        }
        return productosPerecederos;
    }


    private static final String SQL_OBTENER_PRODUCTO_ACTIVO_POR_CODIGO =
            "SELECT p.codigo_producto, p.id_inventario, p.nombre, p.valor_compra, p.porcentaje_ganancia, p.stock, " +
            "p.activo, " +
            "r.talla, per.fecha_vencimiento, per.id_politica, " +
            "i.id_impuesto, i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, " +
            "i.activo AS impuesto_activo, " +
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
            "WHERE p.codigo_producto = ? ";

    @Override
    public Producto obtenerProductoActivoSoloPorCodigo(String codigoProducto) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_PRODUCTO_ACTIVO_POR_CODIGO)) {

            pstmt.setString(1, codigoProducto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = mapearProductoDesdeResultSet(rs);
                    if (!producto.isActivo()) {
                        throw new ProductoNoDisponibleException("Error de negocio: El Producto con Código -" +
                                codigoProducto + "- NO esta en Venta");
                    }
                    return producto;
                }

                throw new ProductoNoEncontradoException("El Producto de Código -" + codigoProducto + "- NO existe");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener el producto: " + codigoProducto, e);
        }
    }


    private static final String SQL_EXISTE_PRODUCTO =
            "SELECT 1 FROM productos WHERE codigo_producto = ?";

    @Override
    public boolean existeProducto(String codigoProducto) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTE_PRODUCTO)) {

            pstmt.setString(1, codigoProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar la existencia del producto: " + codigoProducto, e);
        }
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_PRODUCTO =
            "UPDATE productos SET nombre = ?, valor_compra = ?, porcentaje_ganancia = ?, stock = ?, " +
            "id_impuesto = ?, id_descuento = ? , activo = ? " +
            "WHERE id_inventario = ? AND codigo_producto = ?";

    @Override
    public void actualizarProducto(Producto producto, int idInventario) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_PRODUCTO)){

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

            if (producto instanceof ProductoPerecedero perecedero) {
                actualizarProductoPerecedero(conn, perecedero);
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                throw new IllegalArgumentException("No se puede actualizar el producto: " +
                        "El Impuesto, el Descuento o el Inventario destino especificado no existen.");
            }
            throw new PersistenciaException("Error al actualizar el producto: " + producto.getCodigo(), e);
        }
    }

    private static final String SQL_ACTUALIZAR_PERECEDERO =
            "UPDATE producto_perecedero SET id_politica = ? WHERE codigo_producto = ? ";

    private void actualizarProductoPerecedero(Connection conn, ProductoPerecedero perecedero) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_PERECEDERO)) {
            pstmt.setInt(1, perecedero.getPoliticaVencimiento().getIdPolitica());
            pstmt.setString(2, perecedero.getCodigo());

            pstmt.executeUpdate();
        }
    }


    private static final String SQL_CAMBIAR_INVENTARIO_PRODUCTO =
            "UPDATE productos SET id_inventario = ? WHERE id_inventario = ? AND codigo_producto = ?";

    @Override
    public void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CAMBIAR_INVENTARIO_PRODUCTO)) {

            pstmt.setInt(1, idInventarioDestino);
            pstmt.setInt(2, idInventarioOrigen);
            pstmt.setString(3, codigoProducto);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ProductoNoEncontradoException("No se pudo mover: El producto no existe en el inventario de origen.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error en la base de datos al mover el producto: " + codigoProducto, e);
        }
    }


}//===================================================================================================================//

