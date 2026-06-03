package ProyectoUniversidad1;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Carrito {

    private final Map<String,SolicitudItem> carritoFinal;

    public Collection<SolicitudItem> getItems(){
        return this.carritoFinal.values();
    }

    public Carrito() {
        this.carritoFinal = new HashMap<>();
    }

    public void agregarItem(SolicitudItem solicitudItem){
        String key = solicitudItem.idInventario() + "-" + solicitudItem.codigoProducto();
        if (this.carritoFinal.containsKey(key)){
            int cantidad = solicitudItem.cantidad();
            cantidad += this.carritoFinal.get(key).cantidad();
            SolicitudItem solicitudItemFinal = new SolicitudItem(solicitudItem.idInventario(), solicitudItem.codigoProducto(), cantidad);
            this.carritoFinal.put(key, solicitudItemFinal);
            return;
        }
        this.carritoFinal.put(key, solicitudItem);
    }

}

