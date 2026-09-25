package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemCarritoTest {

    private final Impuesto impuesto =
            Impuesto.reconstruirDesdeBD(
                    1,
                    "IVA",
                    new BigDecimal("19"),
                    true
            );

    private final Descuento descuento =
            Descuento.reconstruirDesdeBD(
                    1,
                    "Promoción",
                    new BigDecimal("25"),
                    true
            );

    private Servicio itemServicio;

    private Producto itemProducto;

    private ItemCarrito itemCarritoServicio;

    private ItemCarrito itemCarritoProducto;

    @BeforeEach
    void setUp(){
        itemServicio = Servicio.reconstruirDesdeBD(
                "servicio1234567890",
                "Servicio",
                new BigDecimal("15000"),
                impuesto,
                descuento,
                true
        );
        itemProducto = ProductoRopa.reconstruirDesdeBD(
                "producto1234567890",
                "Producto",
                new BigDecimal("3000"),
                new BigDecimal("85"),
                25,
                impuesto,
                descuento,
                true,
                Talla.M
        );
        itemCarritoServicio = ItemCarrito.crearNuevo(itemServicio, 5);
        itemCarritoProducto = ItemCarrito.crearNuevo(itemProducto, 5);
    }

    //TESTS

    @Test
    void deberiaCrearNuevoCorrectamente(){
        //ARRANGE
        int cantidadValida = 5;
        //ACT
        ItemCarrito itemCarritoNuevo = ItemCarrito.crearNuevo(itemServicio, cantidadValida);
        //ASSERT
        assertEquals(itemServicio, itemCarritoNuevo.getItemFacturable());
        assertEquals(cantidadValida, itemCarritoNuevo.getCantidad());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCrearNuevoElItemFacturableEsNulo(){
        //ARRANGE
        int cantidadValida = 5;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ItemCarrito.crearNuevo(null, cantidadValida)
        );
        assertEquals("Debe Haber un Item Valido para Agregar al Carrito", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"-1", "0"})
    void deberiaLanzarExcepcionSiAlCrearNuevoLaCantidadEsInvalida(int cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ItemCarrito.crearNuevo(itemProducto, cantidadInvalida)
        );
        assertEquals("La Cantidad del Item Carrito Debe Ser Positiva", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadEsMayorALaCantidadDelItemFacturable(){
        //ARRANGE
        int cantidadExcedida = itemProducto.getStock() + 1;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> ItemCarrito.crearNuevo(itemProducto, cantidadExcedida)
        );
        assertEquals(
                "Stock del Producto -" + itemProducto.getNombre() + "- Insuficiente\n" +
                "Cantidad Solicitada:  " + cantidadExcedida + ", Cantidad Existente:  " + itemProducto.getStock()
                , exception.getMessage()
        );
    }

    @Test
    void deberiaAumentarCantidadCorrectamente(){
        //ARRANGE
        int cantidadNueva = 5;
        int cantidadEsperada = itemCarritoServicio.getCantidad() + cantidadNueva;
        //ACT
        itemCarritoServicio.aumentarCantidad(cantidadNueva);
        //ASSERT
        assertEquals(cantidadEsperada, itemCarritoServicio.getCantidad());
    }

    @ParameterizedTest
    @CsvSource({"-1", "0"})
    void deberiaLanzarExcepcionSiLaCantidadExtraEsInvalidaAlAumentarCantidad(int cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> itemCarritoProducto.aumentarCantidad(cantidadInvalida)
        );
        assertEquals("Cantidad a comprar Invalida", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlAumentarCantidadDeUnItemProductoEstaExcedeElStockDisponible(){
        //ARRANGE
        ItemCarrito itemCarritoProducto = ItemCarrito.crearNuevo(itemProducto, itemProducto.getStock());
        int cantidadExcedida = 1;
        int cantidadTotalExcedida = itemCarritoProducto.getCantidad() + cantidadExcedida;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> itemCarritoProducto.aumentarCantidad(cantidadExcedida)
        );
        assertEquals(
                "Stock del Producto -" + itemProducto.getNombre() + "- Insuficiente\n" +
                "Cantidad Solicitada:  " + cantidadTotalExcedida + ", Cantidad Existente:  " + itemProducto.getStock()
                ,exception.getMessage()
        );
    }

    @Test
    void deberiaReducirCantidadCorrectamente(){
        //ARRANGE
        int cantidadAReducir = itemCarritoProducto.getCantidad() - 1;
        int cantidadFinal = itemCarritoProducto.getCantidad() - cantidadAReducir;
        //ACT
        itemCarritoProducto.reducirCantidad(cantidadAReducir);
        //ASSERT
        assertEquals(cantidadFinal, itemCarritoProducto.getCantidad());
    }

    @ParameterizedTest
    @CsvSource({"-1", "0"})
    void deberiaLanzarExcepcionSiLaCantidadAReducirEsInvalida(int cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> itemCarritoProducto.reducirCantidad(cantidadInvalida)
        );
        assertEquals("Cantidad a Reducir Invalida", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadAReducirEsMayorALaExistente(){
        //ARRANGE
        int cantidadExcedida = itemCarritoProducto.getCantidad() + 1;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> itemCarritoProducto.reducirCantidad(cantidadExcedida)
        );
        assertEquals("La Cantidad a Reducir es Mayor a la Cantidad Existente", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
          //pServicio,    cServ, cProducto,  gProducto, cProd, imp%, desc%,      totalProducto,   totalServicio
            "15000,       2,     3000,       85,        3,     19,   25,         14860.125000,    26775.000000",
            "100.123456,  3,     50.111111,  20,        2,     19,   10,         128.805600,      321.696663",
            "999.00,      1,     999.00,     33.333333, 1,     19,   33.333333,  1056.694110,     792.540396"
    })
    void deberiaCalcularElSubtotalCorrectamente(
            String precioServicioSt, int cantidadServicio,
            String costoProductoSt, String gananciaProductoSt, int cantidadProducto,
            String porcentajeImpuesto, String porcentajeDescuento, String totalProd, String totalServ
    ){
        // ARRANGE
        LocalDate fecha = LocalDate.now();
        Impuesto impuestoDinamico = Impuesto.reconstruirDesdeBD(
                1, "Impuesto Test", new BigDecimal(porcentajeImpuesto), true
        );
        Descuento descuentoDinamico = Descuento.reconstruirDesdeBD(
                1, "Descuento Test", new BigDecimal(porcentajeDescuento), true
        );
        Servicio servicio = Servicio.reconstruirDesdeBD(
                "serv_test",
                "Servicio Test",
                new BigDecimal(precioServicioSt),
                impuestoDinamico,
                descuentoDinamico,
                true
        );
        int stockSeguro = cantidadProducto + 10;
        ProductoRopa producto = ProductoRopa.reconstruirDesdeBD(
                "prod_test",
                "Producto Test",
                new BigDecimal(costoProductoSt),
                new BigDecimal(gananciaProductoSt),
                stockSeguro,
                impuestoDinamico,
                descuentoDinamico,
                true,
                Talla.M
        );
        ItemCarrito itemCarritoProducto = ItemCarrito.crearNuevo(producto, cantidadProducto);
        ItemCarrito itemCarritoServicio = ItemCarrito.crearNuevo(servicio, cantidadServicio);
        BigDecimal totalEsperadoProducto = new BigDecimal(totalProd);
        BigDecimal totalEsperadoServicio = new BigDecimal(totalServ);
        // ACT
        BigDecimal subtotalProducto = itemCarritoProducto.calcularSubtotal(fecha);
        BigDecimal subtotalServicio = itemCarritoServicio.calcularSubtotal(fecha);
        // ASSERT
        assertEquals(totalEsperadoProducto, subtotalProducto);
        assertEquals(totalEsperadoServicio, subtotalServicio);
    }

}//===================================================================================================================//

