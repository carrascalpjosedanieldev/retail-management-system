package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dto.SolicitudItemDTO;
import ProyectoPropio1.excepciones.*;
import ProyectoPropio1.dto.FacturaDTO;
import ProyectoPropio1.dto.HistorialVentasDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestorVentas {

    //ATRIBUTOS;

    private final Tienda miTienda;

    private final Map<Integer, Factura> registroVentas;

    //CONTRUCTOR:

    public GestorVentas(Tienda tienda) {
        this.miTienda = tienda;
        this.registroVentas = new LinkedHashMap<>();
    }

    //METODOS PARA VENDER:

    public Venta pedirProducto(int idInv, int codigoProd, int cantidad) throws InventarioNoEncontradoException, ProductoNoEncontradoException,StockInsuficienteException{
        return this.miTienda.reducirStockProductoParaVenta(idInv, codigoProd, cantidad);
    }

    public FacturaDTO procesarVentaMultiproducto(Carrito carrito) throws CarritoVacioException, InventarioNoEncontradoException, ProductoNoEncontradoException, StockInsuficienteException, ServicioNoEncontradoException, CapacidadExcedidaException {
        if (carrito.getItems().isEmpty() && carrito.getCodigosServiciosAdicionales().isEmpty()){
            throw new CarritoVacioException("No se puede Procesar una Venta con un Carrito Vacio");
        }
        List<ItemFacturable> itemsProcesadosConExito = new ArrayList<>();
        List<SolicitudItemDTO> rollBack = new ArrayList<>();
        try {
            for (SolicitudItemDTO solicitudItem: carrito.getItems()){
                this.miTienda.verificarStockProductoParaVenta(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
            }
            for (SolicitudItemDTO solicitudItem: carrito.getItems()){
                Venta venta = pedirProducto(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
                itemsProcesadosConExito.add(venta);
                rollBack.add(solicitudItem);
            }
            for (int codigoServicio:carrito.getCodigosServiciosAdicionales()){
                Servicio servicio = this.miTienda.obtenerServicio(codigoServicio);
                itemsProcesadosConExito.add(servicio);
            }
            Factura facturaNueva = new Factura(itemsProcesadosConExito);
            this.registroVentas.put(facturaNueva.getIdFactura(), facturaNueva);
            return facturaNueva.generarFactura();
        } catch (InventarioNoEncontradoException | ProductoNoEncontradoException | StockInsuficienteException | ServicioNoEncontradoException e){
            for (SolicitudItemDTO solicitudItem: rollBack){
                this.miTienda.aumentarStockDeProductoDeInventario(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
            }
            throw e;
        }
    }



    //METODOS DE INFORMACION:

    public boolean registroVentasEstaVacio(){
        return this.registroVentas.isEmpty();
    }

    public HistorialVentasDTO obtenerHistorial(){
        if (this.registroVentas.isEmpty()){
            throw new IllegalStateException("No Hay Registros");
        }
        List<FacturaDTO> facturasRegistradas = new ArrayList<>();
        for (Factura factura:this.registroVentas.values()){
            facturasRegistradas.add(factura.generarFactura());
        }
        return new HistorialVentasDTO(facturasRegistradas, this.totalFacturas());
    }


    public double totalFacturas() {
        double totalFacturas=0;
        for (Factura factura:this.registroVentas.values()){
            totalFacturas+=factura.calcularTotalFactura();
        }
        return totalFacturas;
    }

}

