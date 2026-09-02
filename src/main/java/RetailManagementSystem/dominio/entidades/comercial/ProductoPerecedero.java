package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
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
            throw new IllegalArgumentException("La Fecha de Vencimiento del Producto es Invalida");
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
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo);
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
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento);
        validarFechaVencimiento(fechaVencimiento);
        validarEstadoPoliticaVencimiento(politicaVencimiento);
        this.fechaVencimiento = fechaVencimiento;
        this.politicaVencimiento = politicaVencimiento;
    }

    public static ProductoPerecedero crearNuevo(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock,
            Impuesto impuesto, Descuento descuento, LocalDate fechaVencimiento,
            PoliticaVencimiento politicaVencimiento
    ) {
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
        BigDecimal precioBase = getPrecioBase();
        BigDecimal precioFinalSinImpuesto = getPrecioBase().subtract(
                calcularDescuentoPolitica(precioBase, fechaReferencia)
        ).subtract(
                calcularDescuento(precioBase)
        );
        return precioFinalSinImpuesto.add(
                calcularImpuesto(precioFinalSinImpuesto)
        )
        .setScale(6, RoundingMode.HALF_UP);
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

    @Override
    public void validarEstadoParaVenta(LocalDate fechaReferencia){
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.fechaVencimiento);
        if (diasRestantes < 0) {
            throw new ProductoVencidoException("ALERTA: El Producto -" + this.getNombre() +
                    "- está vencido. Venta bloqueada.");
        }
    }

    private BigDecimal calcularDescuentoPolitica(BigDecimal precioBase, LocalDate fechaReferencia){
        PoliticaVencimiento pol = this.getPoliticaVencimiento();
        if (!pol.isActiva() || pol.getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, this.getFechaVencimiento());
        if (diasRestantes >= 0 && diasRestantes <= pol.getDiasUmbral()) {
            return precioBase.multiply(
                    pol.getPorcentajeDescuento().divide(CIEN, 6, RoundingMode.HALF_UP)
            )
            .setScale(6, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void cambiarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        validarEstadoPoliticaVencimiento(politicaVencimiento);
        this.politicaVencimiento = politicaVencimiento;
    }

}//===================================================================================================================//

