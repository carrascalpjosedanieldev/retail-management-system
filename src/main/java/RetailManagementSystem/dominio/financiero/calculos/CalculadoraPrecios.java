package RetailManagementSystem.dominio.financiero.calculos;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPrecios;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CalculadoraPrecios {

    //ATRIBUTOS:

    private final MatematicaFinanciera matematicaFinanciera;

    private final Map<Class<? extends ItemFacturable>, EstrategiaCalculoPrecios<?>> estrategias;

    //CONSTRUCTOR:

    public CalculadoraPrecios(
            MatematicaFinanciera matematicaFinanciera,
            Map<Class<? extends ItemFacturable>, EstrategiaCalculoPrecios<?>> estrategias
    ) {
        this.matematicaFinanciera = matematicaFinanciera;
        this.estrategias = estrategias;
    }

    //MÉTODOS:

    @SuppressWarnings("unchecked")
    private <T extends ItemFacturable> EstrategiaCalculoPrecios<T> obtenerEstrategia(T item) {
        Class<?> claseItem = item.getClass();
        EstrategiaCalculoPrecios<?> estrategia = estrategias.get(claseItem);
        if (estrategia == null) {
            throw new IllegalStateException(
                    "NO Existe una Estrategia de Cálculo Registrada para la Clase " +
                            claseItem.getSimpleName()
            );
        }
        return (EstrategiaCalculoPrecios<T>) estrategia;
    }

    public BigDecimal calcularImpuesto(BigDecimal valorFinalSinImpuesto, Impuesto impuesto){
        return this.matematicaFinanciera.calcularMontoImpuesto(valorFinalSinImpuesto, impuesto);
    }

    public BigDecimal calcularValorFinalSinImpuesto(
            ItemFacturable itemFacturable, ContextoEvaluacion contextoEvaluacion
    ){
        return obtenerEstrategia(itemFacturable).calcularValorFinalSinImpuesto(itemFacturable, contextoEvaluacion);
    }

    public BigDecimal calcularValorVenta(ItemFacturable itemFacturable, ContextoEvaluacion contextoEvaluacion){
        return obtenerEstrategia(itemFacturable).calcularValorVenta(itemFacturable, contextoEvaluacion);
    }

}//===================================================================================================================//

