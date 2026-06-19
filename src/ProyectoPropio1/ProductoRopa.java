package ProyectoPropio1;

public class ProductoRopa extends Producto implements Impuestable{

    //ATRIBUTOS:

    private final Talla talla;

    private static final int IVA_FIJO =19;

    //GETTERS Y SETTERS:

    public Talla getTalla() {
        return talla;
    }

    //CONSTRUCTOR:

    public ProductoRopa(String nombre, double valorCompra, int stock, Talla talla) throws IllegalArgumentException {
        super(nombre, valorCompra, stock);
        this.talla=talla;
    }

    //METODOS:

    @Override
    public String describirProducto() {
        return String.format("Tipo de Producto:  Ropa    Nombre del Producto:  %-12s Talla:  %-4s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d%n",
                getNombre(),getTalla(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ", getValorVenta(),getStock());
    }

    @Override
    protected double calcularValorVenta() {
        double valorVenta = getValorCompra() + (getValorCompra() * (getPorcentajeGanancia() / 100));
        valorVenta += calcularImpuesto(valorVenta);
        return valorVenta;
    }

    @Override
    public double calcularImpuesto(double precioBase) {
        return precioBase*((double) IVA_FIJO /100);
    }

}

