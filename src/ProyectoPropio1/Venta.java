package ProyectoPropio1;

public class Venta implements ItemFacturable{

    //ATRIBUTOS:

    private final Producto productoVendido;

    private final int cantidadVendida;

    private final double valorCobrado;

    private final int numeroDeVenta;

    private static int idVentaSiguiente=0;

    //GETTERS Y SETTERS:

    public int getNumeroDeVenta() {
        return numeroDeVenta;
    }

    public Producto getProductoVendido() {
        return productoVendido;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }


    //CONSTRUCTOR:

    public Venta(Producto producto, int cantidadVendida, double valorCobrado) {
        this.productoVendido = producto;
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
    public DatosLineaFacturaDTO obtenerDatosLinea() {
        return new DatosLineaFacturaDTO("Producto (Venta N°" + this.numeroDeVenta + ")", this.productoVendido.getNombre(), this.cantidadVendida, this.valorCobrado);
    }

}

