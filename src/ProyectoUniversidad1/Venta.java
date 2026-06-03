package ProyectoUniversidad1;

public class Venta {

    private final String nombreProducto;

    private final int cantidadVendida;

    private final double valorCobrado;

    private final int numeroDeVenta;

    private static int idVentaSiguiente=0;

    public int getNumeroDeVenta() {
        return numeroDeVenta;
    }

    public Venta(String nombreProducto, int cantidadVendida, double valorCobrado) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.valorCobrado = valorCobrado;
        idVentaSiguiente++;
        this.numeroDeVenta =idVentaSiguiente;
    }

    public String obtenerVenta(){
        return  "Numero De Venta:  " + this.numeroDeVenta + System.lineSeparator() +
                "Producto Vendido:  " + this.nombreProducto + System.lineSeparator() +
                "Cantidad Vendida:  " + this.cantidadVendida + System.lineSeparator() +
                "Valor A Pagar:  " + this.valorCobrado + System.lineSeparator() ;
    }

}

