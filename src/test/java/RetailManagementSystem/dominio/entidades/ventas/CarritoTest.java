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

    private Producto productoRopaPruebas;

    private Carrito carritoPruebas;

    @BeforeEach
    void setUp(){
        carritoPruebas = Carrito.crearNuevo();
        productoRopaPruebas = ProductoRopa.reconstruirDesdeBD(
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
    }

    //TESTS

    @Test
    void deberiaCrearCarritoCorrectamente(){
        //ACT
        Carrito carrito = Carrito.crearNuevo();
        //ASSERT
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    void deberiaAgregarItemProductoCorrectamente(){
        //ARRANGE
        int cantidad = productoRopaPruebas.getStock() - 1;
        //ACT
        carritoPruebas.agregarItem(productoRopaPruebas, cantidad);
        //ASSERT
        assertTrue(carritoPruebas.getItems().containsKey(productoRopaPruebas.getCodigo()));
        assertEquals(cantidad, carritoPruebas.getItems().get(productoRopaPruebas.getCodigo()).getCantidad());
    }

    @Test
    void deberiaAgregarItemServicioCorrectamente(){
        //ARRANGE
        int cantidad = 99;
        //ACT
        carritoPruebas.agregarItem(servicioPruebas, cantidad);
        //ASSERT
        assertTrue(carritoPruebas.getItems().containsKey(servicioPruebas.getCodigo()));
        assertEquals(cantidad, carritoPruebas.getItems().get(servicioPruebas.getCodigo()).getCantidad());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    void deberiaLanzarExcepcionSiLaCantidadEsInvalidaAlAgregarItems(int cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> carritoPruebas.agregarItem(productoRopaPruebas, cantidadInvalida)
        );
        assertEquals("La Cantidad debe ser Mayor a Cero.", exception.getMessage());
    }

    @Test
    void deberiaAumentarLaCantidadSiElItemServicioYaEstaEnElCarrito(){
        //ARRANGE
        int cantidadInicial = 5;
        int cantidadAAgregar = 5;
        int cantidadEsperada = 10;
        carritoPruebas.agregarItem(servicioPruebas, cantidadInicial);
        //ACT
        carritoPruebas.agregarItem(servicioPruebas, cantidadAAgregar);
        //ASSERT
        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(servicioPruebas.getCodigo()).getCantidad());
    }

    @Test
    void deberiaAumentarLaCantidadSiElItemProductoYaEstaEnElCarrito(){
        //ARRANGE
        productoRopaPruebas.aumentarStock(10);
        int cantidadInicial = 5;
        int cantidadAAgregar = 3;
        int cantidadEsperada = 8;
        carritoPruebas.agregarItem(productoRopaPruebas, cantidadInicial);
        //ACT
        carritoPruebas.agregarItem(productoRopaPruebas, cantidadAAgregar);
        //ASSERT
        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(productoRopaPruebas.getCodigo()).getCantidad());
    }

    @Test
    void deberiaLanzarExcepcionSiAlAgregarItemProductoNoAlcanzaElStock(){
        //ARRANGE
        int cantidadExcedida = productoRopaPruebas.getStock() + 1;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> carritoPruebas.agregarItem(productoRopaPruebas, cantidadExcedida)
        );
        assertEquals(
                "Stock del Producto -" + productoRopaPruebas.getNombre() + "- Insuficiente\n" +
                "Cantidad Solicitada:  " + cantidadExcedida + ", Cantidad Existente:  " + productoRopaPruebas.getStock(),
                exception.getMessage()
        );
    }

    @Test
    void deberiaReducirCantidadCorrectamente(){
        //ARRANGE
        int cantidadInicial = 10;
        int cantidadAReducir = 4;
        ItemFacturable item = servicioPruebas;
        carritoPruebas.agregarItem(item, cantidadInicial);
        int cantidadEsperada = cantidadInicial - cantidadAReducir;
        //ACT
        carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducir);
        //ASSERT
        assertEquals(cantidadEsperada, carritoPruebas.getItems().get(item.getCodigo()).getCantidad());
    }

    @Test
    void deberiaEliminarProductoDelCarritoSiAlReducirCantidadEsCero(){
        //ARRANGE
        int cantidadInicial = 10;
        int cantidadAReducir = 10;
        ItemFacturable item = servicioPruebas;
        carritoPruebas.agregarItem(item, cantidadInicial);
        //ACT
        carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducir);
        //ASSERT
        assertFalse(carritoPruebas.getItems().containsKey(item.getCodigo()));
    }

    @Test
    void deberiaLanzarExcepcionSiAlReducirCantidadElItemNoEstaEnElCarrito(){
        //ARRANGE
        int cantidadAReducir = 10;
        ItemFacturable item = servicioPruebas;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducir)
        );
        assertEquals("NO tienes ese Item en el Carrito", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadAReducirEsMayorALaCantidadExistente(){
        //ARRANGE
        int cantidadInicial = 10;
        int cantidadAReducirMayor = cantidadInicial + 1;
        ItemFacturable item = servicioPruebas;
        carritoPruebas.agregarItem(item, cantidadInicial);
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadAReducirMayor)
        );
        assertEquals("La Cantidad a Reducir es Mayor a la Cantidad Existente", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    void deberiaLanzarExcepcionSiLaCantidadEsInvalidaAlReducirCantidad(int cantidadInvalida){
        //ARRANGE
        int cantidadInicial = 10;
        ItemFacturable item = servicioPruebas;
        carritoPruebas.agregarItem(item, cantidadInicial);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> carritoPruebas.reducirCantidadItem(item.getCodigo(), cantidadInvalida)
        );
        assertEquals("La Cantidad debe ser Mayor a Cero.", exception.getMessage());
    }

    @Test
    void deberiaEliminarItemCorrectamente(){
        //ARRANGE
        int cantidadInicial = 10;
        ItemFacturable item = servicioPruebas;
        carritoPruebas.agregarItem(item, cantidadInicial);
        //ACT
        carritoPruebas.eliminarItem(item.getCodigo());
        //ASSERT
        assertFalse(carritoPruebas.getItems().containsKey(item.getCodigo()));
    }

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

    @Test
    void deberiaCalcularElTotalCorrectamente(){
        // ARRANGE
        LocalDate fecha = LocalDate.now();
        carritoPruebas.agregarItem(servicioPruebas, 2);
        carritoPruebas.agregarItem(productoRopaPruebas, 3);
        BigDecimal totalEsperado = new BigDecimal("41635.125000");
        // ACT
        BigDecimal totalCalculado = carritoPruebas.calcularTotal(fecha);
        // ASSERT
        assertEquals(totalEsperado, totalCalculado);
    }

    @Test
    void deberiaVaciarElCarritoCorrectamente(){
        //ARRANGE
        ItemFacturable itemProducto = productoRopaPruebas;
        productoRopaPruebas.aumentarStock(10);
        int cantidadServicio = 5;
        int cantidadProducto = 10;
        carritoPruebas.agregarItem(servicioPruebas, cantidadServicio);
        carritoPruebas.agregarItem(itemProducto, cantidadProducto);
        //ACT
        carritoPruebas.vaciarCarrito();
        //ASSERT
        assertTrue(carritoPruebas.getItems().isEmpty());
    }

}//===================================================================================================================//

