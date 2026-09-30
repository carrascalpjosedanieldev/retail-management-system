package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class EnsambladorDTOCarritoTest {

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    @Mock
    private CalculadoraPrecios calculadoraPreciosFalso;

    @InjectMocks
    private EnsambladorDTOCarrito ensambladorDTOCarrito;

    //TESTS

//    @Test
//    void deberiaEnsamblarVistaPreviaCarritoDTOCorrectamente(){
//        //ARRANGE
//        ItemFacturable itemFacturable = ProductoRopa.reconstruirDesdeBD(
//                "codigoProducto01234567890",
//                "Ropa",
//                new BigDecimal("45000"),
//                new BigDecimal("85"),
//                25,
//                impuestoPruebas,
//                descuentoPruebas,
//                true,
//                Talla.M
//        );
//        int cantidad = 5;
//        ItemCarrito itemCarrito = ItemCarrito.crearNuevo(itemFacturable, cantidad);
//        Carrito carrito = Carrito.crearNuevo();
//        carrito.agregarItem(itemFacturable, cantidad);
//        LocalDate fecha = LocalDate.now();
//        //ACT
//        VistaPreviaCarritoDTO resultado = ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(carrito, fecha);
//        //ASSERT
//        assertNotNull(resultado);
//        assertEquals(carrito.calcularTotal(fecha), resultado.totalAproximado());
//        assertEquals(1, resultado.carritoItems().size());
//        ItemCarritoDTO dto = resultado.carritoItems().getFirst();
//        assertEquals(itemFacturable.getCodigo(), dto.codigoArticulo());
//        assertEquals(itemFacturable.getNombre(), dto.nombreArticulo());
//        assertEquals(5, dto.cantidad());
//        assertEquals(itemCarrito.calcularSubtotal(), dto.subtotal());
//    }

}//===================================================================================================================//

