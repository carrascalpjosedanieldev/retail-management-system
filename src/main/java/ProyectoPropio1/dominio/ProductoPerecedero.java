package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.ProductoVencidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoPerecedero extends Producto{

    //ATRIBUTOS:

    private final LocalDate fechaVencimiento;

    //GETTERS Y SETTERS:

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    //CONSTRUCTOR:

    private ProductoPerecedero(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                               int stock, Impuesto impuesto, Descuento descuento, boolean activo, LocalDate fechaVencimiento){
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo);
        this.fechaVencimiento=fechaVencimiento;
    }

    public static ProductoPerecedero reconstruirDesdeBD(String codigo, String nombre, BigDecimal valorCompra,
                                                        BigDecimal porcentajeGanancia, int stock, Impuesto impuesto,
                                                        Descuento descuento, boolean activo, LocalDate fechaVencimiento){
        return new ProductoPerecedero(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                activo, fechaVencimiento);
    }

    private ProductoPerecedero(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock,
                               Impuesto impuesto, Descuento descuento, LocalDate fechaVencimiento){
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento);
        this.fechaVencimiento = fechaVencimiento;
    }

    public static ProductoPerecedero crearNuevo(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                                                int stock, Impuesto impuesto, Descuento descuento,
                                                LocalDate fechaVencimiento){
        return new ProductoPerecedero(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                fechaVencimiento);
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
        BigDecimal descuentoAplicado = calcularDescuento(valorVenta, fechaReferencia);
        valorVenta = valorVenta.subtract(descuentoAplicado);
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

    @Override
    public BigDecimal calcularDescuento(BigDecimal valorVenta, LocalDate fecha) {
        if (getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        BigDecimal factorDescuento = getPorcentajeDescuento().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return valorVenta.multiply(factorDescuento);
    }
}

