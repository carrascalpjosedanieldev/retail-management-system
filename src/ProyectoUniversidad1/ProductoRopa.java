package ProyectoUniversidad1;

public class ProductoRopa extends Producto{

    private final Talla talla;

    private static final int IVAFijo=19;

    private final int impuesto;

    public Talla getTalla() {
        return talla;
    }

    public int getImpuesto() {
        return impuesto;
    }

    public ProductoRopa(String nombre, double valorCompra, int stock, Talla talla) throws IllegalArgumentException {
        super(nombre, valorCompra, stock);
        this.talla=talla;
        this.impuesto=IVAFijo;
    }

    @Override
    public String describirProducto() {
        return String.format("Tipo de Producto:  Ropa    Nombre del Producto:  %-10s Talla:  %-4s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d%n",
                getNombre(),getTalla(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ", getValorVenta(),getStock());
    }

    @Override
    protected double calcularValorVenta() {
        double valorVenta = getValorCompra() + (getValorCompra() * (getPorcentajeGanancia() / 100));
        valorVenta += valorVenta*((double)this.impuesto /100);
        return valorVenta;
    }

}

