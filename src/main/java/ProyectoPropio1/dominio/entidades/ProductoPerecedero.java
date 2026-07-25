package ProyectoPropio1.dominio.entidades;

import ProyectoPropio1.dominio.excepciones.ProductoVencidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoPerecedero extends Producto{

    //ATRIBUTOS:

    private final LocalDate fechaVencimiento;

    private PoliticaVencimiento politicaVencimiento;

    //GETTERS Y SETTERS:

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public PoliticaVencimiento getPoliticaVencimiento() {
        return politicaVencimiento;
    }

    private void setPoliticaVencimiento(PoliticaVencimiento politicaVencimiento) {
        this.politicaVencimiento = politicaVencimiento;
    }

    //CONSTRUCTOR:

    private ProductoPerecedero(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                               int stock, Impuesto impuesto, Descuento descuento, boolean activo, LocalDate fechaVencimiento,
                               PoliticaVencimiento politicaVencimiento){
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo);
        this.fechaVencimiento = fechaVencimiento;
        this.politicaVencimiento = politicaVencimiento;
    }

    public static ProductoPerecedero reconstruirDesdeBD(String codigo, String nombre, BigDecimal valorCompra,
                                                        BigDecimal porcentajeGanancia, int stock, Impuesto impuesto,
                                                        Descuento descuento, boolean activo, LocalDate fechaVencimiento,
                                                        PoliticaVencimiento politicaVencimiento){
        return new ProductoPerecedero(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                activo, fechaVencimiento, politicaVencimiento);
    }

    private ProductoPerecedero(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock,
                               Impuesto impuesto, Descuento descuento, LocalDate fechaVencimiento,
                               PoliticaVencimiento politicaVencimiento){
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento);
        this.fechaVencimiento = fechaVencimiento;
        this.politicaVencimiento = politicaVencimiento;
    }

    public static ProductoPerecedero crearNuevo(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                                                int stock, Impuesto impuesto, Descuento descuento,
                                                LocalDate fechaVencimiento, PoliticaVencimiento politicaVencimiento){
        return new ProductoPerecedero(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                fechaVencimiento, politicaVencimiento);
    }

    //MÉTODOS:

    public boolean estaVencido(LocalDate fechaReferencia) {
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        return diasRestantes < 0;
    }

    @Override
    protected BigDecimal calcularValorVenta(LocalDate fechaReferencia) {
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(CIEN, 6, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal precioBase = getValorCompra().add(ganancia);
        BigDecimal descuentoPolitica = calcularDescuentoPolitica(precioBase, fechaReferencia);
        BigDecimal descuentoAplicado = calcularDescuento(precioBase, fechaReferencia);
        BigDecimal precioFinalSinImpuesto = precioBase.subtract(descuentoPolitica).subtract(descuentoAplicado);
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

    private BigDecimal calcularDescuentoPolitica(BigDecimal precioBase, LocalDate fechaReferencia){
        PoliticaVencimiento pol = this.getPoliticaVencimiento();
        if (!pol.isActiva() || pol.getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.getFechaVencimiento());
        if (diasRestantes >= 0 && diasRestantes <= pol.getDiasUmbral()) {
            BigDecimal factorDescuento = pol.getPorcentajeDescuento().divide(CIEN, 6, RoundingMode.HALF_UP);
            return precioBase.multiply(factorDescuento);
        }
        return BigDecimal.ZERO;
    }

    public void cambiarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        if (!politicaVencimiento.isActiva()){
            throw new IllegalArgumentException("La Política de Vencimiento que quieres colocar NO esta Activa");
        }
        setPoliticaVencimiento(politicaVencimiento);
    }

}

