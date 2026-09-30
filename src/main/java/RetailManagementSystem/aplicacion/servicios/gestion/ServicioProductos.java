package RetailManagementSystem.aplicacion.servicios.gestion;

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

    private Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    private void actualizarProductoDeInventario(int idInventario, Producto producto){
        this.repositorioProducto.actualizarProducto(producto, idInventario);
    }

    public Producto obtenerProductoActivoParaLaVenta(String codigoProducto){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductoActivoSoloPorCodigo(codigoProducto)
        );
    }

    public void cambiarEstadoProducto(int idInventario, String codigoProducto){
        this.gestorTransaccional.ejecutarEnTransaccion(()-> {
            Producto producto = obtenerProductoDeInventario(idInventario, codigoProducto);
            producto.cambiarEstado();
            actualizarProductoDeInventario(idInventario, producto);
        });
    }

    public Producto actualizarProductoRopaDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia,int idImpuesto, int idDescuento
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()-> {
            Producto producto = obtenerProductoDeInventario(idInventario, codigoProducto);
            producto.cambiarNombreProducto(nombreNuevo);
            producto.cambiarValorCompra(valorCompra);
            producto.cambiarPorcentajeGanancia(porcentajeGanancia);
            Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
            producto.cambiarImpuesto(impuesto);
            Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
            producto.cambiarDescuento(descuento);
            actualizarProductoDeInventario(idInventario, producto);
            return producto;
        });
    }

    public ProductoPerecedero actualizarProductoPerecederoDeInventario(
            int idInventario, String codigoProducto, String nombreNuevo, BigDecimal valorCompra,
            BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento, int idPoliticaVencimiento
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()-> {
            ProductoPerecedero perecedero =
                    (ProductoPerecedero) obtenerProductoDeInventario(idInventario, codigoProducto);
            perecedero.cambiarNombreProducto(nombreNuevo);
            perecedero.cambiarValorCompra(valorCompra);
            perecedero.cambiarPorcentajeGanancia(porcentajeGanancia);
            Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
            perecedero.cambiarImpuesto(impuesto);
            Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
            perecedero.cambiarDescuento(descuento);
            PoliticaVencimiento politicaVencimiento =
                    this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
            perecedero.cambiarPoliticaVencimiento(politicaVencimiento);
            actualizarProductoDeInventario(idInventario, perecedero);
            return perecedero;
        });
    }

    public List<Producto> obtenerTodosLosProductosDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosPorInventario(idInventario)
        );
    }

    public List<Producto> obtenerTodosLosProductosRopaDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.ROPA)
        );
    }

    public List<Producto> obtenerTodosLosProductosPerecederoDeInventario(int idInventario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioProducto.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.PERECEDERO)
        );
    }

}//===================================================================================================================//

