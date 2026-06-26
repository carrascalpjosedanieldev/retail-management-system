package ProyectoPropio1.dominio;

import java.util.*;

public class Carrito {

    //ATRIBUTOS:

    private final Map<ReferenciaItem, Integer> carritoFinal;

    private final List<Integer> codigosServiciosAdicionales;

    //GETTERS Y SETTERS:

    public Map<ReferenciaItem,Integer> getItems(){
        return Map.copyOf(this.carritoFinal);
    }

    public List<Integer> getCodigosServiciosAdicionales(){
        return Collections.unmodifiableList(this.codigosServiciosAdicionales);
    }

    //CONSTRUCTOR:

    public Carrito() {
        this.carritoFinal = new LinkedHashMap<>();
        this.codigosServiciosAdicionales = new ArrayList<>();
    }

    //METODOS:

    public void agregarItem(ReferenciaItem referenciaItem, int cantidad){
        if (this.carritoFinal.containsKey(referenciaItem)){
            this.carritoFinal.put(referenciaItem, this.carritoFinal.get(referenciaItem)+cantidad);
            return;
        }
        this.carritoFinal.put(referenciaItem, cantidad);
    }

    public void agregarServicio(int codigoServicio){
        this.codigosServiciosAdicionales.add(codigoServicio);
    }

}

