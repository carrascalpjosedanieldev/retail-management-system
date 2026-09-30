package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
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

import static org.junit.jupiter.api.Assertions.*;

public class CarritoTest {

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

    private final Servicio servicioPruebas =
            Servicio.reconstruirDesdeBD(
                    "servicio1234567890",
                    "Servicio",
                    new BigDecimal("15000"),
                    impuesto,
                    descuento,
                    true
            );

    private static final String CODIGO_PRODUCTO_POR_DEFECTO = "producto1234567890";

    private static final String NOMBRE_PRODUCTO_POR_DEFECTO = "Producto";

    private static final BigDecimal VALOR_COMPRA_POR_DEFECTO = new BigDecimal("3000");

    private static final BigDecimal PORCENTAJE_GANANCIA_POR_DEFECTO = new BigDecimal("85");

    private Producto productoRopaPruebas;

    private Carrito carritoPruebas;

    @BeforeEach
    void setUp(){
        carritoPruebas = Carrito.crearNuevo();
        productoRopaPruebas = ProductoRopa.reconstruirDesdeBD(
                CODIGO_PRODUCTO_POR_DEFECTO,
                NOMBRE_PRODUCTO_POR_DEFECTO,
                VALOR_COMPRA_POR_DEFECTO,
                PORCENTAJE_GANANCIA_POR_DEFECTO,
                25,
                impuesto,
                descuento,
                true,
                Talla.M
        );
    }

    //TESTS

    @Test
    void deberiaCrearCarritoCorrectamente(){
        //ACT
        Carrito carrito = Carrito.crearNuevo();
        //ASSERT
        assertTrue(carrito.getItems().isEmpty());
    }

//    @Test
//    void deberiaAgregarItemProductoCorrectamente(){
//        //ARRANGE
//        int stock = 10;
//        Producto productoRopa = ProductoRopa.reconstruirDesdeBD(
//                CODIGO_PRODUCTO_POR_DEFECTO,
//                NOMBRE_PRODUCTO_POR_DEFECTO,
//                VALOR_COMPRA_POR_DEFECTO,
//                PORCENTAJE_GANANCIA_POR_DEFECTO,
//                stock,
//                impuesto,
//                descuento,
//                true,
//                Talla.M
//        );
//        int cantidad = stock - 1;
//        //ACT
//        carritoPruebas.agregarItem(productoRopa, cantidad);
//        //ASSERT
//        assertTrue(carritoPruebas.getItems().containsKey(productoRopa.getCodigo()));
//        assertEquals(cantidad, carritoPruebas.getItems().get(productoRopa.getCodigo()).getCantidad());
//    }
//
//    @Test
//    void deberiaAgregarItemServicioCorrectamente(){
//        //ARRANGE
//        int cantidad = 99;
//        //ACT
//        carritoPruebas.agregarItem(servicioPruebas, cantidad);
//        //ASSERT
//        assertTrue(carritoPruebas.getItems().containsKey(servicioPruebas.getCodigo()));
//        assertEquals(cantidad, carritoPruebas.getItems().get(servicioPruebas.getCodigo()).getCantidad());
//    }
//
//    @ParameterizedTest
//    @CsvSource({"0", "-1"})
//    void deberiaLanzarExcepcionSiLaCantidadEsInvalidaAlAgregarItems(int cantidadInvalida){
//        //ACT AND ASSERT
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                ()-> carritoPruebas.agregarItem(productoRopaPruebas, cantidadInvalida)
//        );
//        assertEquals("La Cantidad debe ser Mayor a Cero.", exception.getMessage());
//    }
//
//    @Test
//    void deberiaAumentarLaCantidadSiElItemServicioYaEstaEnElCarrito(){
//        //ARRANGE
//        int cantidadInicial = 5;
//        int cantidadAAgregar = 5;
//        int cantidadEsperada = 10;
//        carritoPruebas.agregarItem(servicioPruebas, cantidadInicial);
//        //ACT
//        carritoPruebas.agregarItem(servicioPruebas, cantidadAAgregar);
//        //ASSERT
//        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(servicioPruebas.getCodigo()).getCantidad());
//    }
//
//    @Test
//    void deberiaAumentarLaCantidadSiElItemProductoYaEstaEnElCarrito(){
//        //ARRANGE
//        int stock = 10;
//        Producto productoRopa = ProductoRopa.reconstruirDesdeBD(
//                CODIGO_PRODUCTO_POR_DEFECTO,
//                NOMBRE_PRODUCTO_POR_DEFECTO,
//                VALOR_COMPRA_POR_DEFECTO,
//                PORCENTAJE_GANANCIA_POR_DEFECTO,
//                stock,
//                impuesto,
//                descuento,
//                true,
//                Talla.M
//        );
//        int cantidadInicial = 5;
//        int cantidadAAgregar = 3;
//        int cantidadEsperada = 8;
//        carritoPruebas.agregarItem(productoRopa, cantidadInicial);
//        //ACT
//        carritoPruebas.agregarItem(productoRopa, cantidadAAgregar);
//        //ASSERT
//        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(productoRopa.getCodigo()).getCantidad());
//    }
//
//    @Test
//    void deberiaLanzarExcepcionSiAlAgregarItemProductoNoAlcanzaElStock(){
//        //ARRANGE
//        int stock = 10;
//        Producto productoRopa = ProductoRopa.reconstruirDesdeBD(
//                CODIGO_PRODUCTO_POR_DEFECTO,
//                NOMBRE_PRODUCTO_POR_DEFECTO,
//                VALOR_COMPRA_POR_DEFECTO,
//                PORCENTAJE_GANANCIA_POR_DEFECTO,
//                stock,
//                impuesto,
//                descuento,
//                true,
//                Talla.M
//        );
//        int cantidadExcedida = stock + 1;
//        //ACT AND ASSERT
//        StockInsuficienteException exception = assertThrows(
//                StockInsuficienteException.class,
//                ()-> carritoPruebas.agregarItem(productoRopa, cantidadExcedida)
//        );
//        assertEquals(
//                "Stock del Producto -" + productoRopa.getNombre() + "- Insuficiente\n" +
//                "Cantidad Solicitada:  " + cantidadExcedida + ", Cantidad Existente:  " + productoRopa.getStock(),
//                exception.getMessage()
//        );
//    }
//
//    @Test
//    void deberiaReducirCantidadCorrectamente(){
//        //ARRANGE
//        int cantidadInicial = 10;
//        int cantidadAReducir = 4;
//        ItemFacturable item = servicioPruebas;
//        carritoPruebas.agregarItem(item, cantidadInicial);
//        int cantidadEsperada = cantidadInicial - cantidadAReducir;
//        //ACT
//        carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducir);
//        //ASSERT
//        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(item.getCodigo()).getCantidad());
//    }
//
//    @Test
//    void deberiaEliminarProductoDelCarritoSiAlReducirCantidadEsCero(){
//        //ARRANGE
//        int stock = 10;
//        Producto productoRopa = ProductoRopa.reconstruirDesdeBD(
//                CODIGO_PRODUCTO_POR_DEFECTO,
//                NOMBRE_PRODUCTO_POR_DEFECTO,
//                VALOR_COMPRA_POR_DEFECTO,
//                PORCENTAJE_GANANCIA_POR_DEFECTO,
//                stock,
//                impuesto,
//                descuento,
//                true,
//                Talla.M
//        );
//        int cantidadInicial = 8;
//        int cantidadAReducir = 8;
//        carritoPruebas.agregarItem(productoRopa, cantidadInicial);
//        //ACT
//        carritoPruebas.reducirCantidadItem(productoRopa.getCodigo(), cantidadAReducir);
//        //ASSERT
//        assertFalse(carritoPruebas.getItems().containsKey(productoRopa.getCodigo()));
//    }
//
//    @Test
//    void deberiaLanzarExcepcionSiAlReducirCantidadElItemNoEstaEnElCarrito(){
//        //ARRANGE
//        int cantidadAReducir = 10;
//        ItemFacturable item = servicioPruebas;
//        //ACT AND ASSERT
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducir)
//        );
//        assertEquals("NO tienes ese Item en el Carrito", exception.getMessage());
//    }
//
//    @Test
//    void deberiaLanzarExcepcionSiLaCantidadAReducirEsMayorALaCantidadExistente(){
//        //ARRANGE
//        int cantidadInicial = 10;
//        int cantidadAReducirMayor = cantidadInicial + 1;
//        ItemFacturable item = servicioPruebas;
//        carritoPruebas.agregarItem(item, cantidadInicial);
//        //ACT AND ASSERT
//        StockInsuficienteException exception = assertThrows(
//                StockInsuficienteException.class,
//                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducirMayor)
//        );
//        assertEquals("La Cantidad a Reducir es Mayor a la Cantidad Existente", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @CsvSource({"0", "-1"})
//    void deberiaLanzarExcepcionSiLaCantidadEsInvalidaAlReducirCantidad(int cantidadInvalida){
//        //ARRANGE
//        int cantidadInicial = 10;
//        ItemFacturable item = servicioPruebas;
//        carritoPruebas.agregarItem(item, cantidadInicial);
//        //ACT AND ASSERT
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadInvalida)
//        );
//        assertEquals("La Cantidad debe ser Mayor a Cero.", exception.getMessage());
//    }
//
//    @Test
//    void deberiaEliminarItemCorrectamente(){
//        //ARRANGE
//        int cantidadInicial = 10;
//        ItemFacturable item = servicioPruebas;
//        carritoPruebas.agregarItem(item, cantidadInicial);
//        //ACT
//        carritoPruebas.eliminarItem(item.getCodigo());
//        //ASSERT
//        assertFalse(carritoPruebas.getItems().containsKey(item.getCodigo()));
//    }

    @Test
    void deberiaLanzarExcepcionSiAlEliminarItemNoEstaEnElCarrito(){
        //ARRANGE
        ItemFacturable item = servicioPruebas;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> carritoPruebas.eliminarItem(item.getCodigo())
        );
        assertEquals("NO tienes ese Item en el Carrito", exception.getMessage());
    }

//    @ParameterizedTest
//    @CsvSource({
//          //pServicio,    cServ, cProducto,  gProducto, cProd, imp%, desc%,      totalEsperado
//            "15000,       2,     3000,       85,        3,     19,   25,         41635.125000",
//            "100.123456,  3,     50.111111,  20,        2,     19,   10,         450.502263",
//            "999.00,      1,     999.00,     33.333333, 1,     19,   33.333333,  1849.234506"
//    })
//    void deberiaCalcularElTotalCorrectamente(
//            String precioServicioSt, int cantidadServicio,
//            String costoProductoSt, String gananciaProductoSt, int cantidadProducto,
//            String porcentajeImpuesto, String porcentajeDescuento, String totalEsperadoSt
//    ){
//        // ARRANGE
//        LocalDate fecha = LocalDate.now();
//        Impuesto impuestoDinamico = Impuesto.reconstruirDesdeBD(
//                1, "Impuesto Test", new BigDecimal(porcentajeImpuesto), true
//        );
//        Descuento descuentoDinamico = Descuento.reconstruirDesdeBD(
//                1, "Descuento Test", new BigDecimal(porcentajeDescuento), true
//        );
//        Servicio servicio = Servicio.reconstruirDesdeBD(
//                "serv_test",
//                "Servicio Test",
//                new BigDecimal(precioServicioSt),
//                impuestoDinamico,
//                descuentoDinamico,
//                true
//        );
//        int stockSeguro = cantidadProducto + 10;
//        ProductoRopa producto = ProductoRopa.reconstruirDesdeBD(
//                "prod_test",
//                "Producto Test",
//                new BigDecimal(costoProductoSt),
//                new BigDecimal(gananciaProductoSt),
//                stockSeguro,
//                impuestoDinamico,
//                descuentoDinamico,
//                true,
//                Talla.M
//        );
//        carritoPruebas.agregarItem(servicio, cantidadServicio);
//        carritoPruebas.agregarItem(producto, cantidadProducto);
//        BigDecimal totalEsperado = new BigDecimal(totalEsperadoSt);
//        // ACT
//        BigDecimal totalCalculado = carritoPruebas.calcularTotal(fecha);
//        // ASSERT
//        assertEquals(totalEsperado, totalCalculado);
//    }
//
//    @Test
//    void deberiaVaciarElCarritoCorrectamente(){
//        //ARRANGE
//        int stock = 10;
//        Producto productoRopa = ProductoRopa.reconstruirDesdeBD(
//                CODIGO_PRODUCTO_POR_DEFECTO,
//                NOMBRE_PRODUCTO_POR_DEFECTO,
//                VALOR_COMPRA_POR_DEFECTO,
//                PORCENTAJE_GANANCIA_POR_DEFECTO,
//                stock,
//                impuesto,
//                descuento,
//                true,
//                Talla.M
//        );
//        int cantidadServicio = 5;
//        int cantidadProducto = 8;
//        carritoPruebas.agregarItem(servicioPruebas, cantidadServicio);
//        carritoPruebas.agregarItem(productoRopa, cantidadProducto);
//        //ACT
//        carritoPruebas.vaciarCarrito();
//        //ASSERT
//        assertTrue(carritoPruebas.getItems().isEmpty());
//    }

}//===================================================================================================================//

