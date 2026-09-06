package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.enums.Talla;

import java.math.BigDecimal;

public class ProductoRopaTest extends ProductoBaseTest<ProductoRopa> {

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

}//===================================================================================================================//

