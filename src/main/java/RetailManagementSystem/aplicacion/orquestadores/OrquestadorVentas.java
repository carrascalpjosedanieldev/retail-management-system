package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOCarrito;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.aplicacion.servicios.ventas.ServicioCarrito;
import RetailManagementSystem.aplicacion.servicios.ventas.ServicioFacturas;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.ventas.*;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CarritoVacioException;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrquestadorVentas {

    //ATRIBUTOS;

    private final CalculadoraPrecios calculadoraPrecios;

    private final ServicioFacturas servicioFacturas;

    private final ServicioCarrito servicioCarrito;

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    private final EnsambladorDTOCarrito ensambladorDTOCarrito;

    //CONSTRUCTOR:

    public OrquestadorVentas(
            CalculadoraPrecios calculadoraPrecios, ServicioFacturas servicioFacturas,
            ServicioCarrito servicioCarrito, EnsambladorDTOFactura ensambladorDTOFactura,
            EnsambladorDTOCarrito ensambladorDTOCarrito
    ) {
        this.calculadoraPrecios = calculadoraPrecios;
        this.servicioFacturas = servicioFacturas;
        this.servicioCarrito = servicioCarrito;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
        this.ensambladorDTOCarrito = ensambladorDTOCarrito;
    }

    //MÉTODOS:

    public SesionVenta abrirVentaSesion(){
        return SesionVenta.crearNueva();
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito(SesionVenta sesionVenta, LocalDate fecha){
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(sesionVenta.getCarrito(), contextoEvaluacion);
    }

    public VistaPreviaCarritoDTO agregarItemAlCarrito(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, String codigoItem, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        this.servicioCarrito.agregarItemNuevoAlCarrito(
                sesionVenta.getCarrito(), codigoItem, 1, contextoEvaluacion
        );
        return obtenerVistaPreviaCarrito(sesionVenta, fecha);
    }

    public VistaPreviaCarritoDTO aumentarCantidadItem(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, String codigoItem, int cantidad,
            TipoItem tipoItem, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        this.servicioCarrito.aumentarCantidadItemDeCarrito(
                sesionVenta.getCarrito(), codigoItem, cantidad, tipoItem, contextoEvaluacion
        );
        return obtenerVistaPreviaCarrito(sesionVenta, fecha);
    }

    public VistaPreviaCarritoDTO reducirCantidadItem(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, String codigoItem, int cantidadAReducir,
            LocalDate fecha
    ){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        this.servicioCarrito.reducirCantidadItem(sesionVenta.getCarrito(), codigoItem, cantidadAReducir);
        return obtenerVistaPreviaCarrito(sesionVenta, fecha);
    }

    public VistaPreviaCarritoDTO eliminarItemDelCarrito(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, String codigoItem, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        this.servicioCarrito.eliminarItem(sesionVenta.getCarrito(), codigoItem);
        return obtenerVistaPreviaCarrito(sesionVenta, fecha);
    }

    public VistaPreviaCarritoDTO cancelarCompraTotal(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        this.servicioCarrito.cancelarCompraTotal(sesionVenta.getCarrito());
        return obtenerVistaPreviaCarrito(sesionVenta, fecha);
    }

    public FacturaDTO procesarVentaYObtenerFactura(
            UsuarioDTOCompleto usuario, SesionVenta sesionVenta, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.PROCESAR_VENTA);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        Carrito carrito = sesionVenta.getCarrito();
        if (carrito.getItems().isEmpty()){
            throw new CarritoVacioException("No se puede Procesar una Venta con un Carrito Vacío");
        }
        List<ItemVendido> itemsProcesadosConExito = new ArrayList<>();
        for (ItemCarrito item:carrito.getItems().values()){
            ItemFacturable itemFacturable = item.getItemFacturable();
            BigDecimal porcentajeImpuesto = itemFacturable.getImpuesto().isActivo() ?
                    item.getItemFacturable().getImpuesto().getPorcentaje() : BigDecimal.ZERO;
            BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(itemFacturable, contextoEvaluacion);
            ItemVendido itemVendido = ItemVendido.crearNuevo(
                    itemFacturable.getTipoItem(), itemFacturable.getCodigo(), itemFacturable.getNombre(),
                    item.getCantidad(), valorVenta, porcentajeImpuesto
            );
            itemsProcesadosConExito.add(itemVendido);
        }
        FacturaDTO facturaGenerada = this.ensambladorDTOFactura.ensamblarFactura(
                this.servicioFacturas.registrarVentaYObtenerFactura(itemsProcesadosConExito)
        );
        sesionVenta.getCarrito().vaciarCarrito();
        return facturaGenerada;
    }


}//===================================================================================================================//

