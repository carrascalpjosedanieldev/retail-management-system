package ProyectoPropio1;

import java.util.ArrayList;
import java.util.List;

public class Factura {

    //ATRIBUTOS:

    private final List<ItemFacturable> lineaDeCobros;

    private int idFactura;

    private static int idFacturaSiguiente=0;

    //GETTERS Y SETTERS:

    public int getIdFactura() {
        return idFactura;
    }

    private void setIdFactura(int idFactura){
        this.idFactura = idFactura;
    }

    //CONSTRUCTOR:

    public Factura() {
        this.lineaDeCobros = new ArrayList<>();
    }

    //METODOS:

    public void asignarIdFactura(){
        int idFactura = idFacturaSiguiente++;
        setIdFactura(idFactura);
    }

    public void agregarItem(ItemFacturable itemFacturable){
        this.lineaDeCobros.add(itemFacturable);
    }

    public String generarFactura(){
        StringBuilder factura = new StringBuilder();
        factura.append("--------------------------------------------------------------------------");
        factura.append(System.lineSeparator());
        factura.append("FACTURA N`");
        factura.append(getIdFactura());
        factura.append(System.lineSeparator());
        for (ItemFacturable itemFacturable :this.lineaDeCobros){
            factura.append(itemFacturable.obtenerDetalleFacturacion());
        }
        factura.append(System.lineSeparator());
        factura.append("TOTAL A PAGAR:  $");
        factura.append(calcularTotalFactura());
        factura.append(System.lineSeparator());
        factura.append("--------------------------------------------------------------------------");
        return factura.toString();
    }

    public double calcularTotalFactura() {
        double totalFactura = 0;
        for (ItemFacturable itemFacturable :this.lineaDeCobros){
            totalFactura+= itemFacturable.getValorCobrado();
        }
        return totalFactura;
    }

}

