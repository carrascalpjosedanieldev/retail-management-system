package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.comercial.*;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioProducto;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.math.BigDecimal;
import java.util.List;

public class ServicioProductos {

    //ATRIBUTOS:

    private final RepositorioProducto repositorioProducto;

    private final RepositorioImpuestos repositorioImpuestos;

    private final RepositorioDescuentos repositorioDescuentos;

    private final RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioProductos(
            RepositorioProducto repositorioProducto, RepositorioImpuestos repositorioImpuestos,
            RepositorioDescuentos repositorioDescuentos, RepositorioPoliticaVencimiento repositorioPoliticaVencimiento,
            GestorTransaccional gestorTransaccional
    ) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioImpuestos = repositorioImpuestos;
        this.repositorioDescuentos = repositorioDescuentos;
        this.repositorioPoliticaVencimiento = repositorioPoliticaVencimiento;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto)
        );
    }

    public Producto obtenerProductoActivoParaLaVenta(String codigoProducto){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductoActivoSoloPorCodigo(codigoProducto)
        );
    }

    public boolean existeProducto(String codigoProducto){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.existeProducto(codigoProducto)
        );
    }

    public void cambiarEstadoProducto(int idInventario, String codigoProducto){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarEstado();
        this.actualizarProductoDeInventario(idInventario, producto);
    }

    private void actualizarProductoDeInventario(int idInventario, Producto producto){
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioProducto.actualizarProducto(producto, idInventario)
        );
    }

    public Producto actualizarProductoRopaDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia,int idImpuesto, int idDescuento
    ) {
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarNombreProducto(nombreNuevo);
        producto.cambiarValorCompra(valorCompra);
        producto.cambiarPorcentajeGanancia(porcentajeGanancia);
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        producto.cambiarImpuesto(impuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        producto.cambiarDescuento(descuento);
        this.actualizarProductoDeInventario(idInventario, producto);
        return producto;
    }

    public ProductoPerecedero actualizarProductoPerecederoDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento, int idPoliticaVencimiento
    ) {
        ProductoPerecedero perecedero = (ProductoPerecedero) this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto);
        perecedero.cambiarNombreProducto(nombreNuevo);
        perecedero.cambiarValorCompra(valorCompra);
        perecedero.cambiarPorcentajeGanancia(porcentajeGanancia);
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        perecedero.cambiarImpuesto(impuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        perecedero.cambiarDescuento(descuento);
        PoliticaVencimiento politicaVencimiento = this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        perecedero.cambiarPoliticaVencimiento(politicaVencimiento);
        this.actualizarProductoDeInventario(idInventario, perecedero);
        return perecedero;
    }

    public void moverProductoAInventario(int idInventarioOrigen, int idInventarioDestino, String codigoProducto){
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioProducto.cambiarInventarioProducto(codigoProducto, idInventarioOrigen, idInventarioDestino)
        );
    }

    public List<Producto> obtenerProductosDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosPorInventario(idInventario)
        );
    }

    public List<Producto> obtenerProductosRopaDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.ROPA)
        );
    }

    public List<Producto> obtenerProductosPerecederoDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.PERECEDERO)
        );
    }

}//===================================================================================================================//

