package ProyectoPropio1;

public record SolicitudItem(int idInventario, int codigoProducto, int cantidad) {

    //METODOS:

    public String mostrarItem(){
        return  "Inventario:  " + this.idInventario + System.lineSeparator() +
                "Codigo Producto:  " + this.codigoProducto + System.lineSeparator() +
                "Cantidad Del Producto:  " + this.cantidad + System.lineSeparator();
    }

}

