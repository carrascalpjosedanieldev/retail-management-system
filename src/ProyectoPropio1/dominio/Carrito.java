package ProyectoPropio1.dominio;

import ProyectoPropio1.dto.SolicitudItemDTO;

import java.util.*;

public class Carrito {

    //ATRIBUTOS:

    private final Map<String, SolicitudItemDTO> carritoFinal;

    private final List<Integer> codigosServiciosAdicionales;

    //GETTERS Y SETTERS:

    public Collection<SolicitudItemDTO> getItems(){
        return this.carritoFinal.values();
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

    public void agregarItem(SolicitudItemDTO solicitudItem){
        String key = solicitudItem.idInventario() + "-" + solicitudItem.codigoProducto();
        if (this.carritoFinal.containsKey(key)){
            int cantidad = solicitudItem.cantidad();
            cantidad += this.carritoFinal.get(key).cantidad();
            SolicitudItemDTO solicitudItemFinal = new SolicitudItemDTO(solicitudItem.idInventario(), solicitudItem.codigoProducto(), cantidad);
            this.carritoFinal.put(key, solicitudItemFinal);
            return;
        }
        this.carritoFinal.put(key, solicitudItem);
    }

    public void agregarServicio(int codigoServicio){
        this.codigosServiciosAdicionales.add(codigoServicio);
    }

}

