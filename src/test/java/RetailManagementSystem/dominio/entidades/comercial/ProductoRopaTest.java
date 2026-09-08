package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductoRopaTest extends ProductoBaseTest<ProductoRopa> {

    @Override
    protected TipoProducto getTipoProducto() {
        return TipoProducto.ROPA;
    }

    @Override
    protected ProductoRopa crearNuevoProducto() {
        return ProductoRopa.crearNuevo(
                "Producto Ropa",
                new BigDecimal("50000"),
                new BigDecimal("80"),
                25,
                impuestoActivo,
                descuentoActivo,
                Talla.M
        );
    }

    @Test
    void deberiaLanzarExcepcionSiLaTallaEsNulaAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ProductoRopa.crearNuevo(
                        "Producto Ropa",
                        new BigDecimal("50000"),
                        new BigDecimal("80"),
                        25,
                        impuestoActivo,
                        descuentoActivo,
                        null
                )
        );
        assertEquals("La Talla de la Prenda es Obligatoria", exception.getMessage());
    }

}//===================================================================================================================//

