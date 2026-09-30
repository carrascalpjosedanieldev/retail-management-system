package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;
import org.mockito.Mockito;

public class EstrategiaProductoRopaTest extends EstrategiaProductoBaseAbstractaTest<ProductoRopa> {

    @Override
    protected EstrategiaProductoBase<ProductoRopa> instanciarEstrategia(MatematicaFinanciera matematicaFinanciera) {
        return new EstrategiaProductoRopa(matematicaFinanciera);
    }

    @Override
    protected ProductoRopa mockearProducto() {
        return Mockito.mock(ProductoRopa.class);
    }

}//===================================================================================================================//

