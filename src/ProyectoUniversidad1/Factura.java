package ProyectoUniversidad1;

import java.util.ArrayList;
import java.util.List;

public class Factura {

    private final List<Venta> lineaDeVentas;

    private final int idFactura;

    private static int idFacturaSiguiente=0;

    public int getIdFactura() {
        return idFactura;
    }

    public Factura() {
        this.lineaDeVentas = new ArrayList<>();
        idFacturaSiguiente++;
        this.idFactura = idFacturaSiguiente;
    }

    public void agregarVenta(Venta venta){
        this.lineaDeVentas.add(venta);
    }

    public String generarFactura(){
        StringBuilder factura = new StringBuilder();
        factura.append("--------------------------------------------------------------------------");
        factura.append(System.lineSeparator());
        for (Venta venta:this.lineaDeVentas){
            factura.append(venta.obtenerVenta());
        }
        factura.append(System.lineSeparator());
        factura.append("--------------------------------------------------------------------------");
        return factura.toString();
    }

    public double calcularTotalFactura() {
        double totalFactura = 0;
        for (Venta venta:this.lineaDeVentas){
            totalFactura+=venta.getValorCobrado();
        }
        return totalFactura;
    }

}

