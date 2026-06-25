package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.ProductoVencidoException;

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

    public ProductoPerecedero(int codigo, String nombre, double valorCompra, int stock, LocalDate fechaVencimiento){
        super(codigo, nombre, valorCompra, stock);
        this.fechaVencimiento=fechaVencimiento;
        this.posibleDescuento=descuentoPorVencimiento;
    }

    //METODOS:

    public boolean estaVencido(LocalDate fechaReferencia) {
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        return diasRestantes < 0;
    }

    @Override
    protected double calcularValorVenta(LocalDate fechaReferencia) {
        double valorVenta = getValorCompra() + (getValorCompra() * (getPorcentajeGanancia() / 100));
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia,this.fechaVencimiento);
        if (diasRestantes>=0 && diasRestantes<=3){
            valorVenta-=valorVenta*((double)this.posibleDescuento/100);
            return valorVenta;
        }
        return valorVenta;
    }

    @Override
    public void validarEstadoParaVenta(LocalDate fechaReferencia){
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new ProductoVencidoException("ALERTA: El producto '" + this.getNombre() + "' está vencido. Venta bloqueada.");
        }
    }

}

