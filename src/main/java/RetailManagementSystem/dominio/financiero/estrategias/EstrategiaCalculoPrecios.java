package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;

import java.math.BigDecimal;

public interface EstrategiaCalculoPrecios<T extends ItemFacturable> {

    BigDecimal calcularValorFinalSinImpuesto(T itemFacturable, ContextoEvaluacion contextoEvaluacion);

    BigDecimal calcularValorVenta(T itemFacturable, ContextoEvaluacion contextoEvaluacion);

}//===================================================================================================================//

