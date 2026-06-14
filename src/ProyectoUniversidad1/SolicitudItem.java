package ProyectoUniversidad1;

public record SolicitudItem(int idInventario, int codigoProducto, int cantidad) {

    public String mostrarItem(){
        return  "Inventario:  " + this.idInventario + System.lineSeparator() +
                "Codigo Producto:  " + this.codigoProducto + System.lineSeparator() +
                "Cantidad Del Producto:  " + this.cantidad + System.lineSeparator();
    }

}

