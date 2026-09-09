package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.ProductoBaseDatos;

import java.sql.*;

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
    public Producto obtenerDetalleYConstruirProducto(ResultSet rs, ProductoBaseDatos datosBase)throws SQLException{
        Date fechaSql = rs.getDate("fecha_vencimiento");
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                rs.getInt("id_politica"), rs.getString("nombre_politica"),
                rs.getInt("dias_umbral"), rs.getBigDecimal("porcentaje_politica"),
                rs.getBoolean("politica_activa")
        );
        return ProductoPerecedero.reconstruirDesdeBD(
                datosBase.codigo(), datosBase.nombre(), datosBase.valorCompra(), datosBase.porcentajeGanancia(),
                datosBase.stock(), datosBase.impuesto(), datosBase.descuento(), datosBase.activo(),
                fechaSql.toLocalDate(), politicaVencimiento
        );
    }

}//===================================================================================================================//

