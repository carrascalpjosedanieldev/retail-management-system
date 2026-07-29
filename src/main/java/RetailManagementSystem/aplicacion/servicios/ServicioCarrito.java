package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.Carrito;
import RetailManagementSystem.dominio.entidades.Producto;
import RetailManagementSystem.dominio.entidades.Servicio;
import RetailManagementSystem.dominio.enums.TipoItem;

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

    public void agregarProductoAlCarrito(Carrito carrito, String codigoProducto, int cantidad, LocalDate fecha){
        Producto producto = this.servicioProductos.obtenerProductoActivoParaLaVenta(codigoProducto);
        producto.validarEstadoParaVenta(fecha);
        carrito.agregarProducto(producto, cantidad);
    }

    public void reducirCantidadProducto(Carrito carrito, String codigoProducto, int cantidadAreducir){
        carrito.reducirCantidadProducto(codigoProducto, cantidadAreducir);
    }

    public boolean productoEstaEnElCarrito(Carrito carrito, String codigoProducto){
        if (carrito.getItems().containsKey(codigoProducto)){
            return carrito.getItems().get(codigoProducto).getItemFacturable().getTipoItem() == TipoItem.PRODUCTO;
        }
        return false;
    }

    public void eliminarProductoAlCarrito(Carrito carrito, String codigoProducto){
        carrito.eliminarProducto(codigoProducto);
    }

    public void agregarServicioAlCarrito(Carrito carrito, String codigoServicio, int cantidad){
        Servicio servicio = this.servicioServicios.obtenerServicio(codigoServicio);
        carrito.agregarServicio(servicio, cantidad);
    }

    public void reducirCantidadServicio(Carrito carrito, String codigoServicio, int cantidadAReducir){
        carrito.reducirCantidadServicio(codigoServicio, cantidadAReducir);
    }

    public boolean servicioEstaEnElCarrito(Carrito carrito, String codigoServicio){
        if (carrito.getItems().containsKey(codigoServicio)){
            return carrito.getItems().get(codigoServicio).getItemFacturable().getTipoItem() == TipoItem.SERVICIO;
        }
        return false;
    }

    public void eliminarServicioAlCarrito(Carrito carrito, String codigoServicio){
        carrito.eliminarServicio(codigoServicio);
    }

    public void cancelarCompraTotal(Carrito carrito){
        carrito.vaciarCarrito();
    }


}//===================================================================================================================//

