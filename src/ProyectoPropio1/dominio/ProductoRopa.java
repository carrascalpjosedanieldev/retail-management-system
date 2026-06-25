package ProyectoPropio1.dominio;

import java.time.LocalDate;

public class ProductoRopa extends Producto implements Impuestable {

    //ATRIBUTOS:

    private final Talla talla;

    private static final int IVA_FIJO =19;

    //GETTERS Y SETTERS:

    public Talla getTalla() {
        return talla;
    }

    //CONSTRUCTOR:

    public ProductoRopa(int codigo, String nombre, double valorCompra, int stock, Talla talla){
        super(codigo, nombre, valorCompra, stock);
        this.talla=talla;
    }

    //METODOS:

    @Override
    protected double calcularValorVenta(LocalDate fecha) {
        double valorVenta = getValorCompra() + (getValorCompra() * (getPorcentajeGanancia() / 100));
        valorVenta += calcularImpuesto(valorVenta);
        return valorVenta;
    }

    @Override
    public double calcularImpuesto(double precioBase) {
        return precioBase*((double) IVA_FIJO /100);
    }

}

