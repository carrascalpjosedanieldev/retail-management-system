package ProyectoPropio1;

import Excepciones.ProductoVencidoException;

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

    public ProductoPerecedero(String nombre, double valorCompra, int stock, LocalDate fechaVencimiento){
        super(nombre, valorCompra, stock);
        this.fechaVencimiento=fechaVencimiento;
        this.posibleDescuento=descuentoPorVencimiento;
    }

    //METODOS:

    private boolean estaVencido() {
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), this.fechaVencimiento);
        return diasRestantes < 0;
    }

    @Override
    public DatosTotalesProductoDTO exportarDatosTotales() {
        return new DatosTotalesProductoPerecederoDTO(this.getCodigo(), this.getNombre(), this.getValorCompra(), this.getPorcentajeGanancia(), this.getValorVenta(), this.getStock(), this.getFechaVencimiento(), this.estaVencido());
    }

    @Override
    public DatosVentaProductoDTO exportarDatosVenta() {
        return new DatosVentaProductoPerecederoDTO(this.getNombre(), this.getValorVenta(), this.getFechaVencimiento());
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
    public void validarEstadoParaVenta() throws ProductoVencidoException{
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new ProductoVencidoException("ALERTA: El producto '" + this.getNombre() + "' está vencido. Venta bloqueada.");
        }
    }

}

