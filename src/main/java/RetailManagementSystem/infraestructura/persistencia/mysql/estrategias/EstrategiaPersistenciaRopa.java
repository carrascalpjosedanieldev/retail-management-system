package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.ProductoBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstrategiaPersistenciaRopa implements EstrategiaPersistenciaProducto<ProductoRopa>{

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
    public Producto obtenerDetalleYConstruirProducto(ResultSet rs, ProductoBaseDatos datosBase) throws SQLException {
        String tallaString = rs.getString("talla");
        return ProductoRopa.reconstruirDesdeBD(
                datosBase.codigo(), datosBase.nombre(), datosBase.valorCompra(), datosBase.porcentajeGanancia(),
                datosBase.stock(), datosBase.impuesto(), datosBase.descuento(), datosBase.activo(),
                Talla.valueOf(tallaString)
        );
    }

}//===================================================================================================================//

