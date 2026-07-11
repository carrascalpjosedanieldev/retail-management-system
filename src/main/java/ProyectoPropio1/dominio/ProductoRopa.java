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
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(CIEN, 6, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal precioBase = getValorCompra().add(ganancia);
        BigDecimal descuentoAplicado = calcularDescuento(precioBase, fecha);
        BigDecimal precioFinalSinImpuesto = precioBase.subtract(descuentoAplicado);
        BigDecimal impuesto = calcularImpuesto(precioFinalSinImpuesto, fecha);
        BigDecimal valorVenta = precioFinalSinImpuesto.add(impuesto);
        return valorVenta.setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public void validarEstadoParaVenta(LocalDate fecha) {}

    @Override
    public BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto, LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(CIEN, 6, RoundingMode.HALF_UP);
        return precioFinalSinImpuesto.multiply(factorImpuesto);
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

