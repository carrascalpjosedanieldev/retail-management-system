package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

public class EstrategiaProductoRopa extends EstrategiaProductoBase<ProductoRopa> implements EstrategiaCalculoPrecios<ProductoRopa> {

    //CONSTRUCTOR:

    public EstrategiaProductoRopa(MatematicaFinanciera matematicaFinanciera) {
        super(matematicaFinanciera);
    }

}//===================================================================================================================//

