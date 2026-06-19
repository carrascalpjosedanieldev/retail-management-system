package ProyectoPropio1;

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

    public Venta pedirProducto(int idInv, int codigoProd, int cantidad){
        Inventario inventario = this.miTienda.obtenerInventario(idInv);
        return inventario.reducirStockProductoPorVenta(codigoProd,cantidad);
    }

    public Factura procesarVentaMultiproducto(Carrito carrito) throws IllegalArgumentException{
        if (carrito.getItems().isEmpty() && carrito.getServiciosAdicionales().isEmpty()){
            throw new IllegalArgumentException("No se puede procesar una venta sin productos y sin servicios");
        }
        Factura facturaNueva = new Factura();
        List<SolicitudItem> itemsProcesadosConExito = new ArrayList<>();
        try {
            for (SolicitudItem solicitudItem: carrito.getItems()){
                Inventario inventario = this.miTienda.obtenerInventario(solicitudItem.idInventario());
                inventario.verificarStockProductoDisponible(solicitudItem.codigoProducto(),solicitudItem.cantidad());
            }
            for (SolicitudItem solicitudItem: carrito.getItems()){
                Venta venta = pedirProducto(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
                itemsProcesadosConExito.add(solicitudItem);
                facturaNueva.agregarItem(venta);
            }
            for (Servicio servicio: carrito.getServiciosAdicionales()){
                facturaNueva.agregarItem(servicio);
            }
            facturaNueva.asignarIdFactura();
            this.registroVentas.put(facturaNueva.getIdFactura(), facturaNueva);
            return facturaNueva;
        } catch (Exception e){
            for (SolicitudItem solicitudItem: itemsProcesadosConExito){
                int idInventario = solicitudItem.idInventario();
                this.miTienda.obtenerInventario(idInventario).agregarStockProducto(solicitudItem.codigoProducto(), solicitudItem.cantidad());
            }
            throw new IllegalArgumentException("Fallo al intentar generar la factura:  " + e.getMessage() + ", Cambios revertidos");
        }
    }

    //METODOS DE INFORMACION:

    public String obtenerHistorial() throws IllegalArgumentException{
        if (this.registroVentas.isEmpty()){
            throw new IllegalArgumentException("No Hay Registros");
        }
        StringBuilder historial = new StringBuilder();
        historial.append("-------------------------------------------------------------");
        historial.append(System.lineSeparator());
        for (Factura factura :this.registroVentas.values()){
            historial.append(factura.generarFactura());
        }
        historial.append(System.lineSeparator());
        historial.append("-------------------------------------------------------------");

        return historial.toString();
    }

    public double totalFacturas() {
        double totalFacturas=0;
        for (Factura factura:this.registroVentas.values()){
            totalFacturas+=factura.calcularTotalFactura();
        }
        return totalFacturas;
    }

}

