package ProyectoPropio1.aplicacion.orquestadores;

import ProyectoPropio1.aplicacion.dto.VistaPreviaCarritoDTO;
import ProyectoPropio1.aplicacion.ensambladores.EnsambladorDTOCarrito;
import ProyectoPropio1.aplicacion.servicios.ServicioCarrito;
import ProyectoPropio1.aplicacion.servicios.ServicioFacturas;
import ProyectoPropio1.aplicacion.servicios.ServicioProductos;
import ProyectoPropio1.aplicacion.servicios.ServicioServicios;
import ProyectoPropio1.dominio.entidades.*;
import ProyectoPropio1.dominio.excepciones.CarritoVacioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrquestadorVentas {

    //ATRIBUTOS;

    private final ServicioFacturas servicioFacturas;
    private final ServicioCarrito servicioCarrito;
    private final ServicioProductos servicioProductos;
    private final ServicioServicios servicioServicios;

    private final EnsambladorDTOCarrito ensambladorDTOCarrito;

    //CONTRUCTOR:

    public OrquestadorVentas(
            ServicioFacturas servicioFacturas, ServicioCarrito servicioCarrito, ServicioProductos servicioProductos,
            ServicioServicios servicioServicios, EnsambladorDTOCarrito ensambladorDTOCarrito
    ) {
        this.servicioFacturas = servicioFacturas;
        this.servicioCarrito = servicioCarrito;
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
        this.ensambladorDTOCarrito = ensambladorDTOCarrito;
    }

    //MÉTODOS:

    public void abrirCarritoSesion(){
        this.servicioCarrito.abrirCarritoSesion();
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito(LocalDate fecha){
        return this.ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(
                this.servicioCarrito.getCarrito(), fecha
        );
    }

    public void agregarItemAlCarrito(String codigoItem, LocalDate fecha){
        if (this.servicioProductos.existeProducto(codigoItem)){
            this.servicioCarrito.agregarProductoAlCarrito(codigoItem, 1, fecha);
            return;
        }
        if (this.servicioServicios.existeServicio(codigoItem)){
            this.servicioCarrito.agregarServicioAlCarrito(codigoItem, 1);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void aumentarCantidadItem(String codigoItem, int cantidad, LocalDate fecha){
        if (this.servicioCarrito.productoEstaEnElCarrito(codigoItem)){
            this.servicioCarrito.agregarProductoAlCarrito(codigoItem, cantidad, fecha);
            return;
        }
        if (this.servicioCarrito.servicioEstaEnElCarrito(codigoItem)){
            this.servicioCarrito.agregarServicioAlCarrito(codigoItem, cantidad);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void eliminarItemDelCarrito(String codigoItem){
        if (this.servicioCarrito.productoEstaEnElCarrito(codigoItem)){
            this.servicioCarrito.eliminarProductoAlCarrito(codigoItem);
            return;
        }
        if (this.servicioCarrito.servicioEstaEnElCarrito(codigoItem)){
            this.servicioCarrito.eliminarServicioAlCarrito(codigoItem);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void cancelarCompraTotal(){
        this.servicioCarrito.cancelarCompraTotal();
    }

    public Factura procesarVentaYObtenerFactura(Carrito carrito, LocalDate fecha){
        if (carrito.getItems().isEmpty()){
            throw new CarritoVacioException("No se puede Procesar una Venta con un Carrito Vacío");
        }
        List<ItemVendido> itemsProcesadosConExito = new ArrayList<>();
        for (ItemCarrito item:carrito.getItems().values()){
            ItemVendido itemVendido = ItemVendido.crearNuevo(
                    item.getItemFacturable().getTipoItem(), item.getItemFacturable().getCodigo(),
                    item.getItemFacturable().getNombre(), item.getCantidad(),
                    item.getItemFacturable().getValorVenta(fecha), item.getItemFacturable().getPorcentajeImpuesto()
            );
            itemsProcesadosConExito.add(itemVendido);
        }
        return this.servicioFacturas.registrarVentaYObtenerFactura(itemsProcesadosConExito);
    }


}//===================================================================================================================//

