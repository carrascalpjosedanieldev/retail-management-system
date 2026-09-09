package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.DatosProductoBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface EstrategiaPersistenciaProducto<T extends Producto> {

    void insertarDetalleInsertar(Connection conn, T producto) throws SQLException;

    @SuppressWarnings("unchecked")
    default void ejecutarInsertar(Connection conn, Producto producto) throws SQLException {
        this.insertarDetalleInsertar(conn, (T) producto);
    }

    Producto obtenerDetalleYConstruirProducto(ResultSet rs, DatosProductoBase datosBase) throws SQLException;

    List<Producto> obtenerDetallesYConstruirEnLote(Connection conn, List<DatosProductoBase> loteBase) throws SQLException;

}//===================================================================================================================//

