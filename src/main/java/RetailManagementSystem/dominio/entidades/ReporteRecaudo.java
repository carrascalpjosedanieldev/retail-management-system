package RetailManagementSystem.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteRecaudo {

    //ATRIBUTOS:

    private final LocalDate fechaInicio;

    private final LocalDate fechaFin;

    private final int cantidadFacturasEmitidas;

    private final BigDecimal subTotal;

    private final BigDecimal totalImpuestos;

    private final BigDecimal totalRecaudo;

    //GETTERS Y SETTERS:

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCantidadFacturasEmitidas() {
        return cantidadFacturasEmitidas;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public BigDecimal getTotalRecaudo() {
        return totalRecaudo;
    }

    //CONSTRUCTORES:

    private ReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin, int cantidadFacturasEmitidas,
                           BigDecimal subTotal, BigDecimal totalImpuestos, BigDecimal totalRecaudo) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadFacturasEmitidas = cantidadFacturasEmitidas;
        this.subTotal = subTotal;
        this.totalImpuestos = totalImpuestos;
        this.totalRecaudo = totalRecaudo;
    }

    public static ReporteRecaudo reconstruirDesdeBD(
            LocalDate fechaInicio, LocalDate fechaFin, int cantidadFacturasEmitidas,
            BigDecimal subTotal, BigDecimal totalImpuestos, BigDecimal totalRecaudo
    ){
        return new ReporteRecaudo(
                fechaInicio, fechaFin, cantidadFacturasEmitidas, subTotal, totalImpuestos, totalRecaudo
        );
    }

}//===================================================================================================================//

