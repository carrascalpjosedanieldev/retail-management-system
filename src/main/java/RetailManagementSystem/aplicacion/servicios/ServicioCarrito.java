package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ServicioNoEncontradoException;

import java.time.LocalDate;

public class ServicioCarrito {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    //CONSTRUCTOR:

    public ServicioCarrito(ServicioProductos servicioProductos, ServicioServicios servicioServicios) {
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
    }

    //MÉTODOS:

    public void agregarItemNuevoAlCarrito(Carrito carrito, String codigo, int cantidad, LocalDate fecha) {
        ItemFacturable item = resolverItemPorCodigo(codigo, fecha);
        carrito.agregarItem(item, cantidad);
    }

    private ItemFacturable resolverItemPorCodigo(String codigo, LocalDate fecha) {
        try {
            return obtenerYValidarProducto(codigo, fecha);
        } catch (ProductoNoEncontradoException e) {
            try {
                return this.servicioServicios.obtenerServicioActivoParaLaVenta(codigo);
            } catch (ServicioNoEncontradoException ex) {
                throw new IllegalArgumentException(
                        "El Código -" + codigo + "- NO pertenece a un Producto ni a un Servicio Activo."
                );
            }
        }
    }

    public void aumentarCantidadItemDeCarrito(
            Carrito carrito, String codigo, int cantidad, TipoItem tipoItem, LocalDate fecha
    ){
        ItemFacturable item = obtenerItemValido(codigo, tipoItem, fecha);
        carrito.agregarItem(item, cantidad);
    }

    private ItemFacturable obtenerItemValido(String codigo, TipoItem tipo, LocalDate fecha) {
        return switch (tipo) {
            case PRODUCTO -> obtenerYValidarProducto(codigo, fecha);
            case SERVICIO -> this.servicioServicios.obtenerServicioActivoParaLaVenta(codigo);
        };
    }

    private Producto obtenerYValidarProducto(String codigo, LocalDate fecha) {
        Producto producto = this.servicioProductos.obtenerProductoActivoParaLaVenta(codigo);
        if (producto instanceof ProductoPerecedero perecedero) {
            perecedero.validarEstadoParaVenta(fecha);
        }
        return producto;
    }

    public void reducirCantidadItem(Carrito carrito, String codigo, int cantidadAReducir, TipoItem tipoItem){
        carrito.reducirCantidadItem(codigo, cantidadAReducir, tipoItem);
    }

    public void eliminarItem(Carrito carrito, String codigo, TipoItem tipoItem){
        carrito.eliminarItem(codigo, tipoItem);
    }

    public void cancelarCompraTotal(Carrito carrito){
        carrito.vaciarCarrito();
    }

}//===================================================================================================================//

