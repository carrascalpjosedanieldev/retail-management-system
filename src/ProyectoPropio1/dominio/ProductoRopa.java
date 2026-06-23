package ProyectoPropio1.dominio;

import ProyectoPropio1.dto.DatosTotalesProductoDTO;
import ProyectoPropio1.dto.DatosTotalesProductoRopaDTO;
import ProyectoPropio1.dto.DatosVentaProductoDTO;
import ProyectoPropio1.dto.DatosVentaProductoRopaDTO;

public class ProductoRopa extends Producto implements Impuestable {

    //ATRIBUTOS:

    private final Talla talla;

    private static final int IVA_FIJO =19;

    //GETTERS Y SETTERS:

    public Talla getTalla() {
        return talla;
    }

    //CONSTRUCTOR:

    public ProductoRopa(String nombre, double valorCompra, int stock, Talla talla){
        super(nombre, valorCompra, stock);
        this.talla=talla;
    }

    //METODOS:

    @Override
    public DatosTotalesProductoDTO exportarDatosTotales() {
        return new DatosTotalesProductoRopaDTO(this.getCodigo(), this.getNombre(), this.getValorCompra(), this.getPorcentajeGanancia(), this.getValorVenta(), this.getStock(), this.getTalla());
    }

    @Override
    public DatosVentaProductoDTO exportarDatosVenta() {
        return new DatosVentaProductoRopaDTO(this.getNombre(), this.getValorVenta(), this.getTalla());
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

