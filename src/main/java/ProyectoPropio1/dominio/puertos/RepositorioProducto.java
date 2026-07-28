package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.entidades.Producto;
import ProyectoPropio1.dominio.entidades.ProductoPerecedero;

import java.util.List;

public interface RepositorioProducto {

    void insertarProducto(Producto producto, int idInventario);

    Producto obtenerProductoDeInventario(int idInventario, String  codigoProducto);

    ProductoPerecedero obtenerPerecederoDeInventario(int idInventario, String codigoProducto);

    void actualizarProducto(Producto producto, int idInventario);

    void cambiarInventarioProducto(String codigoProducto, int idInventarioOrigen, int idInventarioDestino);

    List<Producto> obtenerProductosPorInventario(int idInventario);

    List<Producto> obtenerProductosRopaPorInventario(int idInventario);

    List<Producto> obtenerProductosPerecederoPorInventario(int idInventario);

    Producto obtenerProductoActivoSoloPorCodigo(String codigoProducto);

    boolean existeProducto(String codigoProducto);

}
