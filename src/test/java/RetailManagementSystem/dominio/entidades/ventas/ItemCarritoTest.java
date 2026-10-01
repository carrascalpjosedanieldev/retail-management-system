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
        itemCarritoServicio = ItemCarrito.crearNuevo(itemServicio, new BigDecimal("50000"), 5);
        itemCarritoProducto = ItemCarrito.crearNuevo(itemProducto, new BigDecimal("15000"), 5);
    }

    //TESTS

    @Test
    void deberiaCrearNuevoCorrectamente(){
        //ARRANGE
        int cantidadValida = 5;
        BigDecimal valorVentaUnidad = new BigDecimal("15000");
        //ACT
        ItemCarrito itemCarritoNuevo = ItemCarrito.crearNuevo(itemServicio, valorVentaUnidad, cantidadValida);
        //ASSERT
        assertEquals(itemServicio, itemCarritoNuevo.getItemFacturable());
        assertEquals(cantidadValida, itemCarritoNuevo.getCantidad());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCrearNuevoElItemFacturableEsNulo(){
        //ARRANGE
        int cantidadValida = 5;
        BigDecimal valorVentaUnidad = new BigDecimal("15000");
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ItemCarrito.crearNuevo(null, valorVentaUnidad, cantidadValida)
        );
        assertEquals("Debe Haber un Item Valido para Agregar al Carrito", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"-1", "0"})
    void deberiaLanzarExcepcionSiAlCrearNuevoLaCantidadEsInvalida(int cantidadInvalida){
        //ARRANGE
        BigDecimal valorVentaUnidad = new BigDecimal("15000");
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ItemCarrito.crearNuevo(itemProducto, valorVentaUnidad, cantidadInvalida)
        );
        assertEquals("La Cantidad del Item Carrito Debe Ser Positiva", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadEsMayorALaCantidadDelItemFacturable(){
        //ARRANGE
        int cantidadExcedida = itemProducto.getStock() + 1;
        BigDecimal valorVentaUnidad = new BigDecimal("15000");
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> ItemCarrito.crearNuevo(itemProducto, valorVentaUnidad, cantidadExcedida)
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
        BigDecimal valorVentaUnidad = new BigDecimal("15000");
        ItemCarrito itemCarritoProducto =
                ItemCarrito.crearNuevo(itemProducto, valorVentaUnidad, itemProducto.getStock());
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
          // valorServicio, cantServicio, valorProducto, cantProducto, totalServicioEsperado, totalProductoEsperado
            "15000,         1,            15000,         1,            15000.000000,          15000.000000",
            "12500.50,      2,            7300.25,       4,            25001.000000,          29201.000000",
            "333.333333,    3,            55.5,          10,           999.999999,           555.000000"
    })
    void deberiaCalcularElSubtotalCorrectamente(
            String valorVentaServicio, int cantidadServicio,
            String valorVentaProducto, int cantidadProducto,
            String totalServ, String totalProd
    ){
        // ARRANGE
        BigDecimal precioServicio = new BigDecimal(valorVentaServicio);
        BigDecimal precioProducto = new BigDecimal(valorVentaProducto);
        ItemCarrito carritoServicio = ItemCarrito.crearNuevo(itemServicio, precioServicio, cantidadServicio);
        ItemCarrito carritoProducto = ItemCarrito.crearNuevo(itemProducto, precioProducto, cantidadProducto);
        BigDecimal totalEsperadoServicio = new BigDecimal(totalServ);
        BigDecimal totalEsperadoProducto = new BigDecimal(totalProd);
        // ACT
        BigDecimal subtotalServicio = carritoServicio.calcularSubtotal();
        BigDecimal subtotalProducto = carritoProducto.calcularSubtotal();
        // ASSERT
        assertEquals(totalEsperadoServicio, subtotalServicio);
        assertEquals(totalEsperadoProducto, subtotalProducto);
    }

}//===================================================================================================================//

