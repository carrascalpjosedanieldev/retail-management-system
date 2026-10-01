package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;

public class EstrategiaCalculoPreciosServicio implements EstrategiaCalculoPrecios<Servicio> {

    //ATRIBUTOS:

    private final MatematicaFinanciera matematicaFinanciera;

    //CONSTRUCTOR:

    public EstrategiaCalculoPreciosServicio(MatematicaFinanciera matematicaFinanciera) {
        this.matematicaFinanciera = matematicaFinanciera;
    }

    //MÉTODOS:

    @Override
    public BigDecimal calcularValorFinalSinImpuesto(Servicio itemFacturable, ContextoEvaluacion contextoEvaluacion) {
        BigDecimal precioBase = itemFacturable.getPrecioBase();
        BigDecimal valorFinalSinImpuesto = precioBase.subtract(
                this.matematicaFinanciera.calcularMontoDescuento(precioBase, itemFacturable.getDescuento())
        );
        return this.matematicaFinanciera.aplicarEscala(valorFinalSinImpuesto);
    }

    @Override
    public BigDecimal calcularValorVenta(Servicio itemFacturable, ContextoEvaluacion contextoEvaluacion) {
        BigDecimal precioFinalSinImpuesto = calcularValorFinalSinImpuesto(itemFacturable, contextoEvaluacion);
        BigDecimal valorVenta = precioFinalSinImpuesto.add(
                this.matematicaFinanciera.calcularMontoImpuesto(precioFinalSinImpuesto, itemFacturable.getImpuesto())
        );
        return this.matematicaFinanciera.aplicarEscala(valorVenta);
    }

}//===================================================================================================================//

