package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.ProductoBaseDatos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface EstrategiaPersistenciaProducto<T extends Producto> {

    void insertarDetalleInsertar(Connection conn, T producto) throws SQLException;

    @SuppressWarnings("unchecked")
    default void ejecutarInsertar(Connection conn, Producto producto) throws SQLException {
        this.insertarDetalleInsertar(conn, (T) producto);
    }

    Producto obtenerDetalleYConstruirProducto(ResultSet rs, ProductoBaseDatos datosBase) throws SQLException;

}//===================================================================================================================//

