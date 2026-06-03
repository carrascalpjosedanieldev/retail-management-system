package ProyectoUniversidad1;

import java.util.LinkedHashMap;
import java.util.Map;

public class GestorVentas {

    private final Tienda miTienda;

    private Factura facturaActiva;

    private final Map<Integer, Factura> registroVentas;


    public GestorVentas(Tienda tienda) {
        this.miTienda = tienda;
        this.registroVentas = new LinkedHashMap<>();
    }

    public void iniciarVenta(){
        this.facturaActiva = new Factura();
    }

    public void procesarVentaMultiproducto(Carrito carrito) throws IllegalArgumentException{
        for (SolicitudItem solicitudItem: carrito.getItems()){
            Inventario inventario = this.miTienda.obtenerInventario(solicitudItem.idInventario());
            inventario.verificarStockProductoDisponible(solicitudItem.codigoProducto(),solicitudItem.cantidad());
        }
        for (SolicitudItem solicitudItem: carrito.getItems()){
            pedirProducto(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
        }
    }

    public void pedirProducto(int idInv, int codigoProd, int cantidad){
        Inventario inventario = this.miTienda.obtenerInventario(idInv);
        Venta venta = inventario.reducirStockProductoPorVenta(codigoProd,cantidad);
        this.facturaActiva.agregarVenta(venta);
    }

    public void finalizarVenta(){
        this.registroVentas.put(this.facturaActiva.getIdFactura(),this.facturaActiva);
        this.facturaActiva = null;
    }

    public String obtenerHistorial(){
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

}

