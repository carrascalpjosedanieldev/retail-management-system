package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.ventas.ItemCarrito;
import RetailManagementSystem.dominio.enums.Talla;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnsambladorDTOCarritoTest {

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private final EnsambladorDTOCarrito ensambladorDTOCarrito = new EnsambladorDTOCarrito();

    //TESTS

    @Test
    void deberiaEnsamblarVistaPreviaCarritoDTOCorrectamente(){
        //ARRANGE
        ItemFacturable itemFacturable = ProductoRopa.reconstruirDesdeBD(
                "codigoProducto01234567890",
                "Ropa",
                new BigDecimal("45000"),
                new BigDecimal("85"),
                25,
                impuestoPruebas,
                descuentoPruebas,
                true,
                Talla.M
        );
        int cantidad = 5;
        ItemCarrito itemCarrito = ItemCarrito.crearNuevo(itemFacturable, cantidad);
        Carrito carrito = Carrito.crearNuevo();
        carrito.agregarItem(itemFacturable, cantidad);
        LocalDate fecha = LocalDate.now();
        //ACT
        VistaPreviaCarritoDTO resultado = ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(carrito, fecha);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(carrito.calcularTotal(fecha), resultado.totalAproximado());
        assertEquals(1, resultado.carritoItems().size());
        ItemCarritoDTO dto = resultado.carritoItems().getFirst();
        assertEquals(itemFacturable.getCodigo(), dto.codigoArticulo());
        assertEquals(itemFacturable.getNombre(), dto.nombreArticulo());
        assertEquals(5, dto.cantidad());
        assertEquals(itemCarrito.calcularSubtotal(fecha), dto.subtotal());
    }

}//===================================================================================================================//

