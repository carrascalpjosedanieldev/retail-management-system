package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.comercial.Producto;

import java.util.List;

public interface RepositorioProducto {

    //CREATE:

    void insertarProducto(Producto producto, int idInventario);

    //READ:

    Producto obtenerProductoDeInventario(int idInventario, String  codigoProducto);

    List<Producto> obtenerProductosPorInventario(int idInventario);

    List<Producto> obtenerProductosRopaPorInventario(int idInventario);

    List<Producto> obtenerProductosPerecederoPorInventario(int idInventario);

    Producto obtenerProductoActivoSoloPorCodigo(String codigoProducto);

    boolean existeProducto(String codigoProducto);

    //UPDATE:

    void actualizarProducto(Producto producto, int idInventario);

    void actualizarStockProducto(Producto producto, int idInventario);

    void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino);

}//===================================================================================================================//

