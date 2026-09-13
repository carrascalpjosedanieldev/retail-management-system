package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.DatosProductoBase;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorPoliticasVencimiento;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EstrategiaPersistenciaPerecedero extends EstrategiaPersistenciaAbstracta<ProductoPerecedero>{

    //ATRIBUTOS:

    private final MapeadorPoliticasVencimiento mapeadorPoliticasVencimiento;

    //CONSTRUCTOR:

    public EstrategiaPersistenciaPerecedero(MapeadorPoliticasVencimiento mapeadorPoliticasVencimiento) {
        this.mapeadorPoliticasVencimiento = mapeadorPoliticasVencimiento;
    }

    //MÉTODOS:

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
    public Producto mapearDetalleYConstruirProducto(ResultSet rs, DatosProductoBase datosBase)throws SQLException{
        Date fechaSql = rs.getDate("fecha_vencimiento");
        PoliticaVencimiento politicaVencimiento = this.mapeadorPoliticasVencimiento.mapearPoliticaVencimiento(rs);

        return ProductoPerecedero.reconstruirDesdeBD(
                datosBase.codigo(), datosBase.nombre(), datosBase.valorCompra(), datosBase.porcentajeGanancia(),
                datosBase.stock(), datosBase.impuesto(), datosBase.descuento(), datosBase.activo(),
                fechaSql.toLocalDate(), politicaVencimiento
        );
    }

    @Override
    protected String generarSqlLote(int cantidadParametros) {
        String finalConsulta = String.join(", ", Collections.nCopies(cantidadParametros, "?"));
        return "SELECT per.codigo_producto, per.fecha_vencimiento, per.id_politica, " +
                "pove.nombre_politica, pove.dias_umbral, pove.porcentaje_descuento AS porcentaje_politica, " +
                "pove.activa AS politica_activa " +
                "FROM producto_perecedero per " +
                "INNER JOIN politicas_vencimiento pove ON per.id_politica = pove.id_politica " +
                "WHERE per.codigo_producto IN (" + finalConsulta + ")";
    }

}//===================================================================================================================//

