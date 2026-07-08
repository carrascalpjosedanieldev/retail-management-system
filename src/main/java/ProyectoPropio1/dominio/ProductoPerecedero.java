package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.ProductoVencidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoPerecedero extends Producto{

    //ATRIBUTOS:

    private final LocalDate fechaVencimiento;

    private static final BigDecimal descuentoPorVencimiento = BigDecimal.valueOf(30);

    private final BigDecimal descuento;

    //GETTERS Y SETTERS:

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    //CONSTRUCTOR:

    public ProductoPerecedero(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto, boolean activo, LocalDate fechaVencimiento){
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, activo);
        this.fechaVencimiento=fechaVencimiento;
        this.descuento = descuentoPorVencimiento;
    }

    public ProductoPerecedero(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto, LocalDate fechaVencimiento){
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto);
        this.fechaVencimiento = fechaVencimiento;
        this.descuento = descuentoPorVencimiento;
    }

    //METODOS:

    public String estaVencido(LocalDate fechaReferencia) {
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes < 0){
            return "Vencido";
        }
        return "Disponible";
    }

    @Override
    protected BigDecimal calcularValorVenta(LocalDate fechaReferencia) {
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal valorVenta = getValorCompra().add(ganancia).add(calcularImpuesto(fechaReferencia));
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes >= 0 && diasRestantes <= 3) {
            BigDecimal factorDescuento = this.descuento.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal descuentoAplicado = valorVenta.multiply(factorDescuento);
            valorVenta = valorVenta.subtract(descuentoAplicado);
        }
        return valorVenta.setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public void validarEstadoParaVenta(LocalDate fechaReferencia){
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new ProductoVencidoException("ALERTA: El producto '" + this.getNombre() + "' está vencido. Venta bloqueada.");
        }
    }

    @Override
    public BigDecimal calcularImpuesto(LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return getValorCompra().multiply(factorImpuesto);
    }

}

