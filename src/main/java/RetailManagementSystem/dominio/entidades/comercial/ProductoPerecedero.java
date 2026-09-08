package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoVencidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class ProductoPerecedero extends Producto{

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

    //VALIDACIONES:

    private void validarFechaVencimiento(LocalDate fechaVencimiento){
        if (fechaVencimiento == null){
            throw new IllegalArgumentException("La Fecha de Vencimiento del Producto es Obligatoria");
        }
    }

    private void validarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        if (politicaVencimiento == null){
            throw new IllegalArgumentException("La Política de Vencimiento del Producto es Obligatoria");
        }
    }

    private void validarEstadoPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        if (!politicaVencimiento.isActiva()){
            throw new IllegalArgumentException("La Política de Vencimiento que quieres colocar NO esta Activa");
        }
    }

    //CONSTRUCTOR:

    private ProductoPerecedero(
            String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, LocalDate fechaVencimiento,
            PoliticaVencimiento politicaVencimiento
    ) {
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo, TipoProducto.PERECEDERO);
        validarFechaVencimiento(fechaVencimiento);
        validarPoliticaVencimiento(politicaVencimiento);
        this.fechaVencimiento = fechaVencimiento;
        this.politicaVencimiento = politicaVencimiento;
    }

    public static ProductoPerecedero reconstruirDesdeBD(
            String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, LocalDate fechaVencimiento,
            PoliticaVencimiento politicaVencimiento
    ) {
        return new ProductoPerecedero(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                activo, fechaVencimiento, politicaVencimiento);
    }

    private ProductoPerecedero(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, Integer stock,
            Impuesto impuesto, Descuento descuento, LocalDate fechaVencimiento,
            PoliticaVencimiento politicaVencimiento
    ) {
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, TipoProducto.PERECEDERO);
        validarFechaVencimiento(fechaVencimiento);
        validarPoliticaVencimiento(politicaVencimiento);
        validarEstadoPoliticaVencimiento(politicaVencimiento);
        this.fechaVencimiento = fechaVencimiento;
        this.politicaVencimiento = politicaVencimiento;
    }

    public static ProductoPerecedero crearNuevo(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, Integer stock,
            Impuesto impuesto, Descuento descuento, LocalDate fechaVencimiento,
            PoliticaVencimiento politicaVencimiento
    ) {
        return new ProductoPerecedero(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
                fechaVencimiento, politicaVencimiento);
    }

    //MÉTODOS:

    public void cambiarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        validarPoliticaVencimiento(politicaVencimiento);
        validarEstadoPoliticaVencimiento(politicaVencimiento);
        this.politicaVencimiento = politicaVencimiento;
    }

    public boolean estaVencido(LocalDate fechaReferencia) {
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        return diasRestantes < 0;
    }

    public void validarEstadoParaVenta(LocalDate fechaReferencia){
        if (estaVencido(fechaReferencia)) {
            throw new ProductoVencidoException("El Producto -" + this.getNombre() + "- está vencido.");
        }
    }

    private BigDecimal calcularDescuentoPolitica(BigDecimal precioBase, LocalDate fechaReferencia){
        PoliticaVencimiento pol = this.getPoliticaVencimiento();
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.getFechaVencimiento());
        if (diasRestantes >= 0 && diasRestantes <= pol.getDiasUmbral()) {
            return precioBase.multiply(
                            dividirEntreCien(pol.getPorcentajeDescuento())
                    )
                    .setScale(6, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getValorFinalSinImpuesto(LocalDate fechaReferencia) {
        BigDecimal precioBase = getPrecioBase();
        return precioBase.subtract(
                calcularDescuentoPolitica(precioBase, fechaReferencia)
        ).subtract(
                calcularDescuento(precioBase)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

}//===================================================================================================================//

