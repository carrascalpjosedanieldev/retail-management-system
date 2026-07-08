package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Producto;

import java.util.List;

public interface RepositorioProducto {

    void insertarProducto(Producto producto, int idInventario);

    void eliminarProductoDeInventario(String codigoProducto, int idInventario);

    Producto obtenerProducto(int idInventario, String  codigoProducto);

    void actualizarProducto(Producto producto, int idInventario);

    void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino);

    List<Producto> obtenerProductosPorInventario(int idInventario);
}
