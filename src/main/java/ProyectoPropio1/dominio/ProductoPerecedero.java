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

    public boolean estaVencido(LocalDate fechaReferencia) {
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        return diasRestantes < 0;
    }

    @Override
    protected BigDecimal calcularValorVenta(LocalDate fechaReferencia) {
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(CIEN, 6, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal precioBase = getValorCompra().add(ganancia);
        BigDecimal descuentoAplicado = calcularDescuento(precioBase, fechaReferencia);
        BigDecimal precioFinalSinImpuesto = precioBase.subtract(descuentoAplicado);
        BigDecimal impuesto = calcularImpuesto(precioFinalSinImpuesto, fechaReferencia);
        BigDecimal valorVenta = precioFinalSinImpuesto.add(impuesto);
        return valorVenta.setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public void validarEstadoParaVenta(LocalDate fechaReferencia){
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new ProductoVencidoException("ALERTA: El producto '" + this.getNombre() + "' está vencido. Venta bloqueada.");
        }
    }

    @Override
    public BigDecimal calcularImpuesto(BigDecimal precioBase, LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(CIEN, 6, RoundingMode.HALF_UP);
        return precioBase.multiply(factorImpuesto);
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase, LocalDate fecha) {
        if (getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        BigDecimal factorDescuento = getPorcentajeDescuento().divide(CIEN, 6, RoundingMode.HALF_UP);
        return precioBase.multiply(factorDescuento);
    }
}

