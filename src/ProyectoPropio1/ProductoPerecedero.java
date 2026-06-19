package ProyectoPropio1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoPerecedero extends Producto{

    //ATRIBUTOS:

    private final LocalDate fechaVencimiento;

    private static final int descuentoPorVencimiento=30;

    private final int posibleDescuento;

    //GETTERS Y SETTERS:

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    //CONSTRUCTOR:

    public ProductoPerecedero(String nombre, double valorCompra, int stock, LocalDate fechaVencimiento) throws IllegalArgumentException {
        super(nombre, valorCompra, stock);
        this.fechaVencimiento=fechaVencimiento;
        this.posibleDescuento=descuentoPorVencimiento;
    }

    //METODOS:


    private String vencidoONo() {
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), this.fechaVencimiento);
        String vencidoONo = "";
        if (diasRestantes < 0) {
            return vencidoONo + "VENCIDO - NO DISPONIBLE";
        }
        return vencidoONo + "VIGENTE - DISPONIBLE";

    }

    @Override
    public String describirProducto() {
        String vencidoONo = vencidoONo();
        return String.format("Tipo de Producto:  Comida    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                getNombre(),getFechaVencimiento(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ",getValorVenta(),getStock(),vencidoONo);
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

    @Override
    public void validarEstadoParaVenta() throws IllegalStateException {
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new IllegalStateException("ALERTA: El producto '" + this.getNombre() + "' está vencido. Venta bloqueada.");
        }
    }

}

