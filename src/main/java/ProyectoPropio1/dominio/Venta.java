package ProyectoPropio1.dominio;

public class Venta implements ItemFacturable {

    //ATRIBUTOS:

    private final Producto productoVendido;

    private final int cantidadVendida;

    private final double valorCobrado;

    private final int numeroDeVenta;

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

    public Venta(int numeroDeVenta, Producto producto, int cantidadVendida, double valorCobrado) {
        this.productoVendido = producto;
        this.cantidadVendida = cantidadVendida;
        this.valorCobrado = valorCobrado;
        this.numeroDeVenta = numeroDeVenta;
    }

    //METODOS:

    @Override
    public String getTipoItem() {
        return "Producto (Venta N°" + this.numeroDeVenta + ")";
    }

    @Override
    public String getNombre() {
        return this.productoVendido.getNombre();
    }

    @Override
    public int getCantidad() {
        return this.cantidadVendida;
    }

    @Override
    public double getValorCobrado() {
        return valorCobrado;
    }

}

