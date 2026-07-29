package ProyectoPropio1.aplicacion.servicios;

import ProyectoPropio1.dominio.entidades.Carrito;
import ProyectoPropio1.dominio.entidades.Producto;
import ProyectoPropio1.dominio.entidades.Servicio;

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
        return this.carrito.getItems().containsKey(codigoProducto);
    }

    public void eliminarProductoAlCarrito(String codigoProducto){
        this.carrito.eliminarProducto(codigoProducto);
    }

    public void agregarServicioAlCarrito(String codigoServicio){
        Servicio servicio = this.servicioServicios.obtenerServicio(codigoServicio);
        this.carrito.agregarServicio(servicio);
    }

    public void reducirCantidadServicio(String codigoServicio, int cantidadAReducir){
        this.carrito.reducirCantidadServicio(codigoServicio, cantidadAReducir);
    }

    public boolean servicioEstaEnElCarrito(String codigoServicio){
        return this.carrito.getItems().containsKey(codigoServicio);
    }

    public void eliminarServicioAlCarrito(String codigoServicio){
        this.carrito.eliminarServicio(codigoServicio);
    }

    public void cancelarCompraTotal(){
        this.carrito.vaciarCarrito();
    }


}//===================================================================================================================//

