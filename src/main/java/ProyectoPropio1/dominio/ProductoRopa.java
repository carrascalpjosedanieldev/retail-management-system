package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.Talla;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ProductoRopa extends Producto{

    //ATRIBUTOS:

    private final Talla talla;

    //GETTERS Y SETTERS:

    public Talla getTalla() {
        return talla;
    }

    //CONSTRUCTOR:

    private ProductoRopa(String  codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                         int stock, Impuesto impuesto, Descuento descuento, boolean activo, Talla talla){
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo);
        this.talla=talla;
    }

    public static ProductoRopa reconstruirDesdeBD(String  codigo, String nombre, BigDecimal valorCompra,
                                                  BigDecimal porcentajeGanancia, int stock, Impuesto impuesto,
                                                  Descuento descuento, boolean activo, Talla talla){
        return new ProductoRopa(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo, talla);
    }

    private ProductoRopa(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock,
                         Impuesto impuesto, Descuento descuento, Talla talla){
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento);
        this.talla = talla;
    }

    public static ProductoRopa crearNuevo(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                                          int stock, Impuesto impuesto, Descuento descuento, Talla talla){
        return new ProductoRopa(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, talla);
    }

    //METODOS:

    @Override
    protected BigDecimal calcularValorVenta(LocalDate fecha) {
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal impuesto = calcularImpuesto(fecha);
        BigDecimal valorVenta = getValorCompra().add(ganancia).add(impuesto);
        BigDecimal factorDescuento = getPorcentajeDescuento().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal descuentoAplicado = valorVenta.multiply(factorDescuento);
        valorVenta = valorVenta.subtract(descuentoAplicado);
        return valorVenta.setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public void validarEstadoParaVenta(LocalDate fecha) {}

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

