package ProyectoUniversidad1;

public record SolicitudItem(int idInventario, int codigoProducto, int cantidad) {

    public String mostrarItem(){
        String item = "Inventario:  " + this.idInventario + System.lineSeparator() +
                "Codigo Producto:  " + this.codigoProducto + System.lineSeparator() +
                "Cantidad Del Producto:  " + this.cantidad + System.lineSeparator();
        return item;
    }

}

