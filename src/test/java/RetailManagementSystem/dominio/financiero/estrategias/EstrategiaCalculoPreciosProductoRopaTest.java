package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;
import org.mockito.Mockito;

public class EstrategiaCalculoPreciosProductoRopaTest extends EstrategiaCalculoPreciosProductoBaseAbstractaTest<ProductoRopa> {

    @Override
    protected EstrategiaCalculoPreciosProductoBase<ProductoRopa> instanciarEstrategia(MatematicaFinanciera matematicaFinanciera) {
        return new EstrategiaCalculoPreciosProductoRopa(matematicaFinanciera);
    }

    @Override
    protected ProductoRopa mockearProducto() {
        return Mockito.mock(ProductoRopa.class);
    }

}//===================================================================================================================//

