package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dominio.puertos.RepositorioProducto;

import java.util.List;

public class ServicioProductos {

    private final RepositorioProducto repositorioProducto;

    public ServicioProductos(RepositorioProducto repositorioProducto) {
        this.repositorioProducto = repositorioProducto;
    }

    public String registrarProducto(int id, Producto producto){
        this.repositorioProducto.insertarProducto(producto, id);
        return producto.getCodigo();
    }

    public void eliminarProductoDeInventario(String codigo, int idInventario){
        this.repositorioProducto.desactivarProductoDeInventario(codigo, idInventario);
    }

    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.repositorioProducto.obtenerProducto(idInventario, codigoProducto);
    }

    public void actualizarProductoDeInventario(int idInventario, Producto producto){
        this.repositorioProducto.actualizarProducto(producto, idInventario);
    }

    public void moverProductoAInventario(int idInventarioOrigen, int idInventarioDestino, String codigoProducto){
        this.repositorioProducto.cambiarInventarioProducto(codigoProducto, idInventarioOrigen, idInventarioDestino);
    }

    public List<Producto> obtenerProductosDeInventario(int idInventario){
        return this.repositorioProducto.obtenerProductosPorInventario(idInventario);
    }

}

