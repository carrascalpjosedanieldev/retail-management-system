package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstrategiaPersistenciaPerecedero implements EstrategiaPersistenciaProducto<ProductoPerecedero> {

    private static final String SQL_INSERTAR_DATOS_PERECEDERO =
            "INSERT INTO producto_perecedero (codigo_producto, fecha_vencimiento, id_politica) VALUES (?, ?, ?)";

    @Override
    public void insertarDetalleInsertar(Connection conn, ProductoPerecedero perecedero) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_DATOS_PERECEDERO)) {
            pstmt.setString(1, perecedero.getCodigo());
            pstmt.setDate(2, Date.valueOf(perecedero.getFechaVencimiento()));
            pstmt.setInt(3, perecedero.getPoliticaVencimiento().getIdPolitica());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Producto obtenerDetalleYObtenerProducto(Connection conn, ProductoBaseDTO datosBase) {
        return null;
    }

}//===================================================================================================================//

