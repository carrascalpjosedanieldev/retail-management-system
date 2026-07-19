package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Producto;
import ProyectoPropio1.dominio.ProductoPerecedero;
import ProyectoPropio1.dominio.ProductoRopa;

import java.util.List;

public interface RepositorioProducto {

    void insertarProducto(Producto producto, int idInventario);

    void desactivarProductoDeInventario(String codigoProducto, int idInventario);

    Producto obtenerProductoDeInventario(int idInventario, String  codigoProducto);

    void actualizarProducto(Producto producto, int idInventario);

    void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino);

    List<Producto> obtenerProductosPorInventario(int idInventario);

    List<Producto> obtenerProductosRopaPorInventario(int idInventario);

    List<Producto> obtenerProductosPerecederoPorInventario(int idInventario);

}
