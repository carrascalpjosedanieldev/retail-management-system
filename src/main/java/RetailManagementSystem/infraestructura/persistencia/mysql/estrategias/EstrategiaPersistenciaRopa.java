package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstrategiaPersistenciaRopa implements EstrategiaPersistenciaProducto<ProductoRopa>{

    private static final String SQL_INSERTAR_DATOS_ROPA =
            "INSERT INTO producto_ropa (codigo_producto, talla) VALUES (?, ?)";

    @Override
    public void insertarDetalle(Connection conn, ProductoRopa ropa) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_ROPA)) {
            pstmt.setString(1, ropa.getCodigo());
            pstmt.setString(2, ropa.getTalla().name());
            pstmt.executeUpdate();
        }
    }

}

