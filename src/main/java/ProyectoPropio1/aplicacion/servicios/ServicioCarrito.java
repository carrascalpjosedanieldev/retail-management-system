package ProyectoPropio1.aplicacion.servicios;

import ProyectoPropio1.dominio.entidades.Carrito;
import ProyectoPropio1.dominio.entidades.Producto;
import ProyectoPropio1.dominio.entidades.Servicio;
import ProyectoPropio1.dominio.enums.TipoItem;

import java.time.LocalDate;

public class ServicioCarrito {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    private Carrito carrito;

    //GETTERS Y SETTERS:

    public Carrito getCarrito() {
        return carrito;
    }

    //CONSTRUCTOR:

    public ServicioCarrito(ServicioProductos servicioProductos, ServicioServicios servicioServicios) {
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
    }

    //MÉTODOS:

    public void abrirCarritoSesion(){
        this.carrito = Carrito.crearNueva();
    }

    public void agregarProductoAlCarrito(String codigoProducto, int cantidad, LocalDate fecha){
        Producto producto = this.servicioProductos.obtenerProductoActivoParaLaVenta(codigoProducto);
        producto.validarEstadoParaVenta(fecha);
        this.carrito.agregarProducto(producto, cantidad);
    }

    public void reducirCantidadProducto(String codigoProducto, int cantidadAreducir){
        this.carrito.reducirCantidadProducto(codigoProducto, cantidadAreducir);
    }

    public boolean productoEstaEnElCarrito(String codigoProducto){
        if (this.carrito.getItems().containsKey(codigoProducto)){
            return this.carrito.getItems().get(codigoProducto).getItemFacturable().getTipoItem() == TipoItem.PRODUCTO;
        }
        return false;
    }

    public void eliminarProductoAlCarrito(String codigoProducto){
        this.carrito.eliminarProducto(codigoProducto);
    }

    public void agregarServicioAlCarrito(String codigoServicio, int cantidad){
        Servicio servicio = this.servicioServicios.obtenerServicio(codigoServicio);
        this.carrito.agregarServicio(servicio, cantidad);
    }

    public void reducirCantidadServicio(String codigoServicio, int cantidadAReducir){
        this.carrito.reducirCantidadServicio(codigoServicio, cantidadAReducir);
    }

    public boolean servicioEstaEnElCarrito(String codigoServicio){
        if (this.carrito.getItems().containsKey(codigoServicio)){
            return this.carrito.getItems().get(codigoServicio).getItemFacturable().getTipoItem() == TipoItem.SERVICIO;
        }
        return false;
    }

    public void eliminarServicioAlCarrito(String codigoServicio){
        this.carrito.eliminarServicio(codigoServicio);
    }

    public void cancelarCompraTotal(){
        this.carrito.vaciarCarrito();
    }


}//===================================================================================================================//

