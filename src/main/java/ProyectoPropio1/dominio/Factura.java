package ProyectoPropio1.dominio;

import java.util.Collections;
import java.util.List;

public class Factura {

    //ATRIBUTOS:

    private final List<ItemFacturable> itemsFinales;

    private final int idFactura;

    //GETTERS Y SETTERS:

    public int getIdFactura() {
        return idFactura;
    }

    public List<ItemFacturable> getItemsFinales(){
        return Collections.unmodifiableList(this.itemsFinales);
    }

    //CONSTRUCTOR:


    public Factura(int idFactura, List<ItemFacturable> itemsFinales) {
        this.itemsFinales = List.copyOf(itemsFinales);
        this.idFactura = idFactura;
    }

    //METODOS:

    public double calcularTotalFactura() {
        double totalFactura = 0;
        for (ItemFacturable itemFacturable :this.itemsFinales){
            totalFactura+= itemFacturable.getValorCobrado();
        }
        return totalFactura;
    }

}

