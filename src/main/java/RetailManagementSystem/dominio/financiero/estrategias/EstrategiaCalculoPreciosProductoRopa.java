package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

public class EstrategiaCalculoPreciosProductoRopa extends EstrategiaCalculoPreciosProductoBase<ProductoRopa> implements EstrategiaCalculoPrecios<ProductoRopa> {

    //CONSTRUCTOR:

    public EstrategiaCalculoPreciosProductoRopa(MatematicaFinanciera matematicaFinanciera) {
        super(matematicaFinanciera);
    }

}//===================================================================================================================//

