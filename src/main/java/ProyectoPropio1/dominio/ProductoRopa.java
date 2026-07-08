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

    public ProductoRopa(String  codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto, boolean activo, Talla talla){
        super(codigo, nombre, valorCompra, porcentajeGanancia,  stock, impuesto, activo);
        this.talla=talla;
    }

    public ProductoRopa(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto, Talla talla){
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto);
        this.talla = talla;
    }

    //METODOS:

    @Override
    protected BigDecimal calcularValorVenta(LocalDate fecha) {
        BigDecimal factorGanancia = getPorcentajeGanancia().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal ganancia = getValorCompra().multiply(factorGanancia);
        BigDecimal impuesto = calcularImpuesto(fecha);
        BigDecimal valorVenta = getValorCompra().add(ganancia).add(impuesto);
        return valorVenta.setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularImpuesto(LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return getValorCompra().multiply(factorImpuesto);
    }

}

