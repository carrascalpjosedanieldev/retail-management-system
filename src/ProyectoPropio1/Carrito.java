package ProyectoPropio1;

import java.util.*;

public class Carrito {

    //ATRIBUTOS:

    private final Map<String,SolicitudItem> carritoFinal;

    private final List<Servicio> serviciosAdicionales;

    //GETTERS Y SETTERS:

    public Collection<SolicitudItem> getItems(){
        return this.carritoFinal.values();
    }

    public List<Servicio> getServiciosAdicionales(){
        return Collections.unmodifiableList(this.serviciosAdicionales);
    }

    //CONSTRUCTOR:

    public Carrito() {
        this.carritoFinal = new LinkedHashMap<>();
        this.serviciosAdicionales = new ArrayList<>();
    }

    //METODOS:

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

    public void agregarServicio(Servicio servicio){
        this.serviciosAdicionales.add(servicio);
    }

    //METODOS DE MOSTRAR INFORMACION:

    public String mostrarDatosServiciosDeCarrito(){
        StringBuilder datosServicios = new StringBuilder();
        datosServicios.append("----------------------------------------------------------");
        datosServicios.append(System.lineSeparator());
        for (Servicio servicio:this.serviciosAdicionales){
            datosServicios.append(servicio.obtenerInfoServicio());
            datosServicios.append(System.lineSeparator());
        }
        datosServicios.append("----------------------------------------------------------");
        return datosServicios.toString();
    }

    public String mostrarCarrito(){
        StringBuilder carrito = new StringBuilder();
        carrito.append("----------------------------------------------------------");
        carrito.append(System.lineSeparator());
        carrito.append("                   CARRITO ACTUAL:");
        carrito.append(System.lineSeparator());
        for (SolicitudItem item:getItems()){
            carrito.append(item.mostrarItem());
            carrito.append("............................");
            carrito.append(System.lineSeparator());
        }
        carrito.append("----------------------------------------------------------");
        return carrito.toString();
    }

}

