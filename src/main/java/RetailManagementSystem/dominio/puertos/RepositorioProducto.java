package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Producto;
import RetailManagementSystem.dominio.entidades.ProductoPerecedero;

import java.util.List;

public interface RepositorioProducto {

    //CREATE:

    void insertarProducto(Producto producto, int idInventario);

    //READ:

    Producto obtenerProductoDeInventario(int idInventario, String  codigoProducto);

    ProductoPerecedero obtenerPerecederoDeInventario(int idInventario, String codigoProducto);

    List<Producto> obtenerProductosPorInventario(int idInventario);

    List<Producto> obtenerProductosRopaPorInventario(int idInventario);

    List<Producto> obtenerProductosPerecederoPorInventario(int idInventario);

    Producto obtenerProductoActivoSoloPorCodigo(String codigoProducto);

    boolean existeProducto(String codigoProducto);

    //UPDATE:

    void actualizarProducto(Producto producto, int idInventario);

    void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino);

}//===================================================================================================================//

