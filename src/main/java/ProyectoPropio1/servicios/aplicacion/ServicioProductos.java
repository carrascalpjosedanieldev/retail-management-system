package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.dominio.puertos.RepositorioPoliticaVencimiento;
import ProyectoPropio1.dominio.puertos.RepositorioProducto;

import java.math.BigDecimal;
import java.util.List;

public class ServicioProductos {

    private final RepositorioProducto repositorioProducto;

    private final RepositorioImpuestos repositorioImpuestos;

    private final RepositorioDescuentos repositorioDescuentos;

    private final RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    public ServicioProductos(RepositorioProducto repositorioProducto, RepositorioImpuestos repositorioImpuestos,
                             RepositorioDescuentos repositorioDescuentos, RepositorioPoliticaVencimiento repositorioPoliticaVencimiento) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioImpuestos = repositorioImpuestos;
        this.repositorioDescuentos = repositorioDescuentos;
        this.repositorioPoliticaVencimiento = repositorioPoliticaVencimiento;
    }

    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    public void registrarProducto(int idInventario, Producto producto){
        this.repositorioProducto.insertarProducto(producto, idInventario);
    }

    public void cambiarEstadoProducto(int idInventario, String codigoProducto){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        if (producto.isActivo()){
            producto.desactivarProducto();
        } else {
            producto.activarProducto();
        }
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    private void actualizarProductoDeInventario(int idInventario, Producto producto){
        this.repositorioProducto.actualizarProducto(producto, idInventario);
    }

    public void actualizarProductoRopaDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia,int idImpuesto, int idDescuento
            ) {
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarNombreProducto(nombreNuevo);
        producto.cambiarValorCompra(valorCompra);
        producto.cambiarValorVentaPorPorcentaje(porcentajeGanancia);
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        producto.cambiarImpuesto(impuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        producto.cambiarDescuento(descuento);
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarProductoPerecederoDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento, int idPoliticaVencimiento
    ) {
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarNombreProducto(nombreNuevo);
        producto.cambiarValorCompra(valorCompra);
        producto.cambiarValorVentaPorPorcentaje(porcentajeGanancia);
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        producto.cambiarImpuesto(impuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        producto.cambiarDescuento(descuento);
        PoliticaVencimiento politicaVencimiento = this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        //cambiarPolitica
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

    public void moverProductoAInventario(int idInventarioOrigen, int idInventarioDestino, String codigoProducto){
        this.repositorioProducto.cambiarInventarioProducto(codigoProducto, idInventarioOrigen, idInventarioDestino);
    }

    public List<Producto> obtenerProductosDeInventario(int idInventario){
        return this.repositorioProducto.obtenerProductosPorInventario(idInventario);
    }

    public List<Producto> obtenerProductosRopaDeInventario(int idInventario){
        return this.repositorioProducto.obtenerProductosRopaPorInventario(idInventario);
    }

    public List<Producto> obtenerProductosPerecederoDeInventario(int idInventario){
        return this.repositorioProducto.obtenerProductosPerecederoPorInventario(idInventario);
    }

}

