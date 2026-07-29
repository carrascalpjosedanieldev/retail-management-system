package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.VistaPreviaCarritoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOCarrito;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioCarrito;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.aplicacion.servicios.ServicioServicios;
import RetailManagementSystem.dominio.entidades.*;
import RetailManagementSystem.dominio.excepciones.CarritoVacioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrquestadorVentas {

    //ATRIBUTOS;

    private final ServicioFacturas servicioFacturas;
    private final ServicioCarrito servicioCarrito;
    private final ServicioProductos servicioProductos;
    private final ServicioServicios servicioServicios;

    private final EnsambladorDTOFactura ensambladorDTOFactura;
    private final EnsambladorDTOCarrito ensambladorDTOCarrito;

    //CONTRUCTOR:

    public OrquestadorVentas(
            ServicioFacturas servicioFacturas, ServicioCarrito servicioCarrito, ServicioProductos servicioProductos,
            ServicioServicios servicioServicios, EnsambladorDTOFactura ensambladorDTOFactura,
            EnsambladorDTOCarrito ensambladorDTOCarrito
    ) {
        this.servicioFacturas = servicioFacturas;
        this.servicioCarrito = servicioCarrito;
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
        this.ensambladorDTOCarrito = ensambladorDTOCarrito;
    }

    //MÉTODOS:

    public SesionVenta abrirVentaSesion(){
        return SesionVenta.crearNueva();
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito(SesionVenta sesionVenta, LocalDate fecha){
        return this.ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(
                sesionVenta.getCarrito(), fecha
        );
    }

    public void agregarItemAlCarrito(SesionVenta sesionVenta, String codigoItem, LocalDate fecha){
        if (this.servicioProductos.existeProducto(codigoItem)){
            this.servicioCarrito.agregarProductoAlCarrito(sesionVenta.getCarrito(), codigoItem, 1, fecha);
            return;
        }
        if (this.servicioServicios.existeServicio(codigoItem)){
            this.servicioCarrito.agregarServicioAlCarrito(sesionVenta.getCarrito(), codigoItem, 1);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void aumentarCantidadItem(SesionVenta sesionVenta, String codigoItem, int cantidad, LocalDate fecha){
        if (this.servicioCarrito.productoEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.agregarProductoAlCarrito(sesionVenta.getCarrito(), codigoItem, cantidad, fecha);
            return;
        }
        if (this.servicioCarrito.servicioEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.agregarServicioAlCarrito(sesionVenta.getCarrito(), codigoItem, cantidad);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void reducirCantidadItem(SesionVenta sesionVenta, String codigoItem, int cantidadAReducir){
        if (this.servicioCarrito.productoEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.reducirCantidadProducto(sesionVenta.getCarrito(), codigoItem, cantidadAReducir);
            return;
        }
        if (this.servicioCarrito.servicioEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.reducirCantidadServicio(sesionVenta.getCarrito(), codigoItem, cantidadAReducir);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");

    }

    public void eliminarItemDelCarrito(SesionVenta sesionVenta, String codigoItem){
        if (this.servicioCarrito.productoEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.eliminarProductoAlCarrito(sesionVenta.getCarrito(), codigoItem);
            return;
        }
        if (this.servicioCarrito.servicioEstaEnElCarrito(sesionVenta.getCarrito(), codigoItem)){
            this.servicioCarrito.eliminarServicioAlCarrito(sesionVenta.getCarrito(), codigoItem);
            return;
        }
        throw new IllegalArgumentException("El Código -" + codigoItem + "- NO Pertenece ni a un Producto ni a un Servicio");
    }

    public void cancelarCompraTotal(SesionVenta sesionVenta){
        this.servicioCarrito.cancelarCompraTotal(sesionVenta.getCarrito());
    }

    public FacturaDTO procesarVentaYObtenerFactura(SesionVenta sesionVenta, LocalDate fecha){
        Carrito carrito = sesionVenta.getCarrito();
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
        return this.ensambladorDTOFactura.ensamblarFactura(
                this.servicioFacturas.registrarVentaYObtenerFactura(itemsProcesadosConExito)
        );
    }


}//===================================================================================================================//

