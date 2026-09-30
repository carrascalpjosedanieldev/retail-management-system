package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;

public class EstrategiaProductoPerecedero implements EstrategiaCalculoPrecios<ProductoPerecedero> {

    //ATRIBUTOS:

    private final MatematicaFinanciera matematicaFinanciera;

    //CONSTRUCTOR:

    public EstrategiaProductoPerecedero(MatematicaFinanciera matematicaFinanciera) {
        this.matematicaFinanciera = matematicaFinanciera;
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

    @Override
    public BigDecimal calcularValorVenta(ProductoPerecedero itemFacturable, ContextoEvaluacion contextoEvaluacion) {
        BigDecimal valorFinalSinImpuesto = calcularValorFinalSinImpuesto(itemFacturable, contextoEvaluacion);
        BigDecimal valorVenta = valorFinalSinImpuesto.add(
                this.matematicaFinanciera.calcularMontoImpuesto(valorFinalSinImpuesto, itemFacturable.getImpuesto())
        );
        return this.matematicaFinanciera.aplicarEscala(valorVenta);
    }

}//===================================================================================================================//

