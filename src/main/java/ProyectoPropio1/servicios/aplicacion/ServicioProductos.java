package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dominio.puertos.RepositorioProducto;

import java.math.BigDecimal;
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

    public void desactivarProductoDeInventario(String codigoProducto, int idInventario){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.desactivarProducto();
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void activarProductoDeInventario(String codigoProducto, int idInventario){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.activarProducto();
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void cambiarDescuentoAProducto(String codigoProducto, int idInventario, Descuento descuento){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarDescuento(descuento);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void cambiarImpuestoAProducto(String codigoProducto, int idInventario, Impuesto impuesto){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarImpuesto(impuesto);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void reducirStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.reducirStock(cantidad);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void aumentarStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.aumentarStock(cantidad);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarValorCompraDeProductoDeInventario(int idInventario, String codigoProducto, BigDecimal valorNuevo){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarValorCompra(valorNuevo);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarPorcentajeGananciaDeProductoDeInventario(int idInventario, String  codigoProducto, BigDecimal porcentaje){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarValorVentaPorPorcentaje(porcentaje);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarNombreDeProductoDeInventario(int idInventario, String  codigoProducto, String nombreNuevo){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarNombreProducto(nombreNuevo);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    private void actualizarProductoDeInventario(int idInventario, Producto producto){
        this.repositorioProducto.actualizarProducto(producto, idInventario);
    }

    public void moverProductoAInventario(int idInventarioOrigen, int idInventarioDestino, String codigoProducto){
        this.repositorioProducto.cambiarInventarioProducto(codigoProducto, idInventarioOrigen, idInventarioDestino);
    }

    public List<Producto> obtenerProductosDeInventario(int idInventario){
        return this.repositorioProducto.obtenerProductosPorInventario(idInventario);
    }

}

