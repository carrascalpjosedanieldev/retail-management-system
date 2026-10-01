package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.ventas.ItemCarrito;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        BigDecimal valorVentaUnidad = new BigDecimal("80000");
        ItemCarrito itemCarrito = ItemCarrito.crearNuevo(itemFacturable, valorVentaUnidad, cantidad);
        Carrito carrito = Carrito.crearNuevo();
        carrito.agregarItem(itemFacturable, valorVentaUnidad, cantidad);
        //ACT
        VistaPreviaCarritoDTO resultado =
                ensambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(carrito, contextoEvaluacion);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(carrito.calcularTotal(), resultado.totalAproximado());
        assertEquals(1, resultado.carritoItems().size());
        ItemCarritoDTO dto = resultado.carritoItems().getFirst();
        assertEquals(itemFacturable.getCodigo(), dto.codigoArticulo());
        assertEquals(itemFacturable.getNombre(), dto.nombreArticulo());
        assertEquals(5, dto.cantidad());
        assertEquals(itemCarrito.calcularSubtotal(), dto.subtotal());
    }

}//===================================================================================================================//

