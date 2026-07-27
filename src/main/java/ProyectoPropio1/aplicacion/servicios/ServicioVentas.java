package ProyectoPropio1.aplicacion.servicios;

import ProyectoPropio1.dominio.entidades.*;
import ProyectoPropio1.dominio.excepciones.CarritoVacioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioVentas {

    //ATRIBUTOS;

    private final ServicioFacturas servicioFacturas;

    //CONTRUCTOR:

    public ServicioVentas(ServicioFacturas servicioFacturas) {
        this.servicioFacturas = servicioFacturas;
    }

    //MÉTODOS:

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

