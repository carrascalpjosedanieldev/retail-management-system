package ProyectoPropio1.dominio;

import ProyectoPropio1.dto.DatosLineaFacturaDTO;
import ProyectoPropio1.dto.FacturaDTO;

import java.util.ArrayList;
import java.util.List;

public class Factura {

    //ATRIBUTOS:

    private final List<ItemFacturable> itemsFinales;

    private final int idFactura;

    private static int idFacturaSiguiente=1;

    //GETTERS Y SETTERS:

    public int getIdFactura() {
        return idFactura;
    }

    //CONSTRUCTOR:


    public Factura(List<ItemFacturable> itemsFinales) {
        this.itemsFinales = itemsFinales;
        this.idFactura = idFacturaSiguiente++;
    }

    //METODOS:

    public FacturaDTO generarFactura(){
        List<DatosLineaFacturaDTO> datosItemsFinales = new ArrayList<>();
        for (ItemFacturable itemFacturable :this.itemsFinales){
            datosItemsFinales.add(itemFacturable.obtenerDatosLinea());
        }
        return new FacturaDTO(this.idFactura, datosItemsFinales, this.calcularTotalFactura());
    }

    public double calcularTotalFactura() {
        double totalFactura = 0;
        for (ItemFacturable itemFacturable :this.itemsFinales){
            totalFactura+= itemFacturable.getValorCobrado();
        }
        return totalFactura;
    }

}

