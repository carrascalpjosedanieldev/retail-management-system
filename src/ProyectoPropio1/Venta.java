package ProyectoPropio1;

public class Venta implements ItemFacturable{

    //ATRIBUTOS:

    private final String nombreProducto;

    private final int cantidadVendida;

    private final double valorCobrado;

    private final int numeroDeVenta;

    private static int idVentaSiguiente=0;

    //GETTERS Y SETTERS:

    public int getNumeroDeVenta() {
        return numeroDeVenta;
    }

    //CONSTRUCTOR:

    public Venta(String nombreProducto, int cantidadVendida, double valorCobrado) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.valorCobrado = valorCobrado;
        idVentaSiguiente++;
        this.numeroDeVenta =idVentaSiguiente;
    }

    //METODOS:

    @Override
    public double getValorCobrado() {
        return valorCobrado;
    }

    @Override
    public String obtenerDetalleFacturacion(){
        return  "Numero De Venta:  " + this.numeroDeVenta + System.lineSeparator() +
                "Producto Vendido:  " + this.nombreProducto + System.lineSeparator() +
                "Cantidad Vendida:  " + this.cantidadVendida + System.lineSeparator() +
                "Valor A Pagar:  " + this.valorCobrado + System.lineSeparator() ;
    }

}

