package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;

public class EstrategiaCalculoPreciosProductoPerecedero extends EstrategiaCalculoPreciosProductoBase<ProductoPerecedero> implements EstrategiaCalculoPrecios<ProductoPerecedero> {

    //CONSTRUCTOR:

    public EstrategiaCalculoPreciosProductoPerecedero(MatematicaFinanciera matematicaFinanciera) {
        super(matematicaFinanciera);
    }

    //MÉTODOS:

    @Override
    public BigDecimal calcularValorFinalSinImpuesto(
            ProductoPerecedero itemFacturable, ContextoEvaluacion contextoEvaluacion
    ) {
        BigDecimal precioBase = itemFacturable.getPrecioBase();
        if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
            throw new IllegalArgumentException("Se Requiere una Fecha para Calcular el Valor del Producto");
        }
        BigDecimal valorFinalSinImpuesto = precioBase.subtract(
                itemFacturable.calcularDescuentoPolitica(precioBase, contextoEvaluacion.getFechaEvaluacion().get())
        ).subtract(
                this.matematicaFinanciera.calcularMontoDescuento(precioBase, itemFacturable.getDescuento())
        );
        return this.matematicaFinanciera.aplicarEscala(valorFinalSinImpuesto);
    }

}//===================================================================================================================//

