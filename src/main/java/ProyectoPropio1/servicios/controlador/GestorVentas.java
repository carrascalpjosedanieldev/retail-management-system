package ProyectoPropio1.servicios.controlador;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.excepciones.CarritoVacioException;
import ProyectoPropio1.servicios.aplicacion.ServicioFacturas;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.aplicacion.ServicioServicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorVentas {

    //ATRIBUTOS;

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    private final ServicioFacturas servicioFacturas;

    private Carrito carrito;

    //GETTERS Y SETTERS:

    public Carrito getCarrito() {
        return carrito;
    }

    //CONTRUCTOR:

    public GestorVentas(ServicioProductos servicioProductos, ServicioServicios servicioServicios, ServicioFacturas servicioFacturas) {
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
        this.servicioFacturas = servicioFacturas;
    }

    //METODOS PARA VENDER:

    public void abrirCarritoSesion(){
        this.carrito = Carrito.crearNueva();
    }

    public void agregarProductoAlCarrito(int idInventario, String codigoProducto, int cantidad){
        Producto producto = this.servicioProductos.obtenerProductoDeInventario(idInventario, codigoProducto);
        this.carrito.agregarProducto(producto, cantidad);
    }

    public void reducirCantidadProducto(String codigoProducto, int cantidadAreducir){
        this.carrito.reducirCantidadProducto(codigoProducto, cantidadAreducir);
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

    public void eliminarServicioAlCarrito(String codigoServicio){
        this.carrito.eliminarServicio(codigoServicio);
    }

    public Factura procesarVentaYObtenerFactura(LocalDate fecha){
        if (carrito.getItems().isEmpty()){
            throw new CarritoVacioException("No se puede Procesar una Venta con un Carrito Vacio");
        }
        List<ItemVendido> itemsProcesadosConExito = new ArrayList<>();
        for (ItemCarrito item:this.carrito.getItems().values()){
            ItemVendido itemVendido = ItemVendido.crearNuevo(item.getItemFacturable().getTipoItem(), item.getItemFacturable().getCodigo(),
                    item.getItemFacturable().getNombre(), item.getCantidad(), item.getItemFacturable().getValorVenta(fecha),
                    item.getItemFacturable().getPorcentajeImpuesto());
            itemsProcesadosConExito.add(itemVendido);
        }
        Factura facturaExitosa = this.servicioFacturas.registrarVentaYObtenerFactura(itemsProcesadosConExito);
        this.carrito.vaciarCarrito();
        return facturaExitosa;
    }

    public void cancelarCompraTotal(){
        this.carrito.getItems().clear();
    }

}

