package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoPerecederoTest extends ProductoBaseTest<ProductoPerecedero>{

    @Mock
    protected PoliticaVencimiento politicaVActiva;

    @Override
    protected ProductoPerecedero crearNuevoProducto() {
        Mockito.lenient().when(politicaVActiva.isActiva()).thenReturn(true);
        return ProductoPerecedero.crearNuevo(
                "Perecedero",
                new BigDecimal("5000"),
                new BigDecimal("100"),
                35,
                impuestoActivo,
                descuentoActivo,
                LocalDate.of(2028,11,19),
                politicaVActiva
        );
    }

}//===================================================================================================================//

