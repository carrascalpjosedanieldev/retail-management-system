package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.DatosProductoBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class EstrategiaPersistenciaRopa extends EstrategiaPersistenciaAbstracta<ProductoRopa> {

    private static final String SQL_INSERTAR_DATOS_ROPA =
            "INSERT INTO producto_ropa (codigo_producto, talla) VALUES (?, ?)";

    @Override
    public void insertarDetalleInsertar(Connection conn, ProductoRopa ropa) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_ROPA)) {
            pstmt.setString(1, ropa.getCodigo());
            pstmt.setString(2, ropa.getTalla().name());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Producto mapearDetalleYConstruirProducto(ResultSet rs, DatosProductoBase datosBase) throws SQLException {
        String tallaString = rs.getString("talla");
        return ProductoRopa.reconstruirDesdeBD(
                datosBase.codigo(), datosBase.nombre(), datosBase.valorCompra(), datosBase.porcentajeGanancia(),
                datosBase.stock(), datosBase.impuesto(), datosBase.descuento(), datosBase.activo(),
                Talla.valueOf(tallaString)
        );
    }

    @Override
    protected String generarSqlLote(int cantidadParametros) {
        String finalConsulta = String.join(", ", Collections.nCopies(cantidadParametros, "?"));
        return "SELECT r.codigo_producto, r.talla " +
                "FROM producto_ropa r " +
                "WHERE r.codigo_producto IN (" + finalConsulta + ")";
    }

}//===================================================================================================================//

