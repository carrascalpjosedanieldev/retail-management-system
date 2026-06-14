package ProyectoUniversidad1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoPerecedero extends Producto{

    private final LocalDate fechaVencimiento;

    private static final int descuentoPorVencimiento=30;

    private final int posibleDescuento;

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public ProductoPerecedero(String nombre, double valorCompra, int stock, LocalDate fechaVencimiento) throws IllegalArgumentException {
        super(nombre, valorCompra, stock);
        this.fechaVencimiento=fechaVencimiento;
        this.posibleDescuento=descuentoPorVencimiento;
    }

    @Override
    public String describirProducto() {
        return String.format("Tipo de Producto:  Comida    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d%n",
                getNombre(),getFechaVencimiento(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ",getValorVenta(),getStock());
    }

    @Override
    protected double calcularValorVenta() {
        double valorVenta = getValorCompra() + (getValorCompra() * (getPorcentajeGanancia() / 100));
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(),this.fechaVencimiento);
        if (diasRestantes>=0 && diasRestantes<=3){
            valorVenta-=valorVenta*((double)this.posibleDescuento/100);
            return valorVenta;
        }
        return valorVenta;
    }

}

