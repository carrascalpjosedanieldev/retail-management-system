package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;

import java.sql.Connection;
import java.sql.SQLException;

public interface EstrategiaPersistenciaProducto<T extends Producto> {

    void insertarDetalleInsertar(Connection conn, T producto) throws SQLException;

    Producto obtenerDetalleYObtenerProducto(Connection conn, ProductoBaseDTO datosBase);

}

