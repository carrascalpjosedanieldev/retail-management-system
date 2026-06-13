package ProyectoUniversidad1;

import java.util.LinkedHashMap;
import java.util.Map;

public class GestorVentas {

    private final Tienda miTienda;

    private final Map<Integer, Factura> registroVentas;


    public GestorVentas(Tienda tienda) {
        this.miTienda = tienda;
        this.registroVentas = new LinkedHashMap<>();
    }

    public Factura procesarVentaMultiproducto(Carrito carrito) throws IllegalArgumentException{
        if (carrito.getItems().isEmpty()){
            throw new IllegalArgumentException("No se puede procesar una venta sin productos");
        }
        for (SolicitudItem solicitudItem: carrito.getItems()){
            Inventario inventario = this.miTienda.obtenerInventario(solicitudItem.idInventario());
            inventario.verificarStockProductoDisponible(solicitudItem.codigoProducto(),solicitudItem.cantidad());
        }
        Factura facturaNueva = new Factura();
        for (SolicitudItem solicitudItem: carrito.getItems()){
            Venta venta = pedirProducto(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
            facturaNueva.agregarVenta(venta);
        }
        this.registroVentas.put(facturaNueva.getIdFactura(), facturaNueva);
        return facturaNueva;
    }

    public Venta pedirProducto(int idInv, int codigoProd, int cantidad){
        Inventario inventario = this.miTienda.obtenerInventario(idInv);
        return inventario.reducirStockProductoPorVenta(codigoProd,cantidad);
    }

    public String obtenerHistorial() throws IllegalArgumentException{
        if (this.registroVentas.isEmpty()){
            throw new IllegalArgumentException("No Hay Registros");
        }
        StringBuilder historial = new StringBuilder();
        historial.append("-------------------------------------------------------------");
        historial.append(System.lineSeparator());
        historial.append("FACTURA:  ");
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

