package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.excepciones.*;

import java.time.LocalDate;
import java.util.*;

public class GestorVentas {

    //ATRIBUTOS;

    private final Tienda miTienda;

    private final Map<Integer, Factura> registroVentas;

    private int contadorVentas = 1;

    private int contadorFacturas = 0;

    //GETTERS Y SETTERS:

    public List<Factura> getFacturas(){
        return List.copyOf(this.registroVentas.values());
    }

    //CONTRUCTOR:

    public GestorVentas(Tienda tienda) {
        this.miTienda = tienda;
        this.registroVentas = new LinkedHashMap<>();
    }

    //METODOS PARA VENDER:

    public Venta pedirProducto(int idInv, int codigoProd, int cantidad, LocalDate fecha){
        Producto producto = this.miTienda.pedirProducto(idInv, codigoProd, cantidad, fecha);
        double valorCobrado = producto.getValorVenta(fecha)*cantidad;
        return new Venta(this.contadorVentas++, producto, cantidad, valorCobrado);
    }

    public Factura procesarVentaMultiproducto(Carrito carrito, LocalDate fecha){
        if (carrito.getItems().isEmpty() && carrito.getCodigosServiciosAdicionales().isEmpty()){
            throw new CarritoVacioException("No se puede Procesar una Venta con un Carrito Vacio");
        }
        List<ItemFacturable> itemsProcesadosConExito = new ArrayList<>();
        Map<ReferenciaItem,Integer> rollBack = new LinkedHashMap<>();
        try {
            for (Map.Entry<ReferenciaItem,Integer> entry :carrito.getItems().entrySet()){
                this.miTienda.verificarStockProductoParaVenta(entry.getKey().idInventario(), entry.getKey().codigoProducto(), entry.getValue(), fecha);
            }
            for (Map.Entry<ReferenciaItem,Integer> entry :carrito.getItems().entrySet()){
                Venta venta = pedirProducto(entry.getKey().idInventario(), entry.getKey().codigoProducto(), entry.getValue(), fecha);
                itemsProcesadosConExito.add(venta);
                rollBack.put(entry.getKey(), entry.getValue());
            }
            for (int codigoServicio:carrito.getCodigosServiciosAdicionales()){
                Servicio servicio = this.miTienda.obtenerServicio(codigoServicio);
                itemsProcesadosConExito.add(servicio);
            }
            Factura facturaNueva = new Factura(this.contadorFacturas , itemsProcesadosConExito);
            this.contadorFacturas++;
            this.registroVentas.put(facturaNueva.getIdFactura(), facturaNueva);
            return facturaNueva;
        } catch (InventarioNoEncontradoException | ProductoNoEncontradoException | StockInsuficienteException | ServicioNoEncontradoException e){
            for (Map.Entry<ReferenciaItem,Integer> entry : rollBack.entrySet()){
                this.miTienda.aumentarStockDeProductoDeInventario(entry.getKey().idInventario(), entry.getKey().codigoProducto(), entry.getValue());
            }
            throw e;
        }
    }

    //METODOS DE INFORMACION:

    public Factura obtenerFactura(int idFactura){
        Factura factura = this.registroVentas.get(idFactura);
        if (factura==null){
            throw new FacturaNoEncontradaException("En el registro de facturas no se encuentra la Factura de ID: " + idFactura);
        }
        return factura;
    }

    public boolean registroVentasEstaVacio(){
        return this.registroVentas.isEmpty();
    }

    public List<Factura> obtenerHistorial(){
        if (this.registroVentas.isEmpty()){
            throw new IllegalStateException("No Hay Registros");
        }
        List<Factura> facturasRegistradas = new ArrayList<>();
        for (Factura factura:this.registroVentas.values()){
            facturasRegistradas.add(factura);
        }
        return facturasRegistradas;
    }

    public double totalFacturas() {
        double totalFacturas=0;
        for (Factura factura:this.registroVentas.values()){
            totalFacturas+=factura.calcularTotalFactura();
        }
        return totalFacturas;
    }

}

