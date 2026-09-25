package RetailManagementSystem.dominio.entidades.ventas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SesionVentaTest {

    @Test
    void deberiaCrearNuevaSesionConCarritoInicializado() {
        // ACT
        SesionVenta sesion = SesionVenta.crearNueva();
        // ASSERT
        assertNotNull(sesion);
        assertNotNull(sesion.getCarrito());
    }

}//===================================================================================================================//

