package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;

public abstract class EstrategiaProductoBase<T extends Producto> implements EstrategiaCalculoPrecios<T>{

    //ATRIBUTOS:

    protected final MatematicaFinanciera matematicaFinanciera;

    //CONSTRUCTOR:

    public EstrategiaProductoBase(MatematicaFinanciera matematicaFinanciera) {
        this.matematicaFinanciera = matematicaFinanciera;
    }

    //MÉTODOS:

    @Override
    public BigDecimal calcularValorFinalSinImpuesto(
            T itemFacturable, ContextoEvaluacion contextoEvaluacion
    ) {
        BigDecimal precioBase = itemFacturable.getPrecioBase();
        BigDecimal valorFinalSinImpuesto = precioBase.subtract(
                this.matematicaFinanciera.calcularMontoDescuento(precioBase, itemFacturable.getDescuento())
        );
        return this.matematicaFinanciera.aplicarEscala(valorFinalSinImpuesto);
    }

    @Override
    public BigDecimal calcularValorVenta(T itemFacturable, ContextoEvaluacion contextoEvaluacion) {
        BigDecimal valorFinalSinImpuesto = calcularValorFinalSinImpuesto(itemFacturable, contextoEvaluacion);
        BigDecimal valorVenta = valorFinalSinImpuesto.add(
                this.matematicaFinanciera.calcularMontoImpuesto(valorFinalSinImpuesto, itemFacturable.getImpuesto())
        );
        return this.matematicaFinanciera.aplicarEscala(valorVenta);
    }

}//===================================================================================================================//

